package Service2Air;

import io.grpc.Context;
import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AirPollutionTrackerImpl extends AirPollutionTrackerGrpc.AirPollutionTrackerImplBase{
    private final Map<String, List<AirPollutionLevel>> pollutionData = new ConcurrentHashMap<>();

    @Override
    public void getAirQuality(AirQualityRequest request, StreamObserver<AirQualityResponse> responseObserver) {
        String city = request.getCity();
        List<AirPollutionLevel> pollutionLevels = pollutionData.get(city);

        if (pollutionLevels == null) {
            responseObserver.onError(new RuntimeException("Pollution data not found for " + city));
            return;
        }

        AirQualityResponse.Builder responseBuilder = AirQualityResponse.newBuilder()
                .setCity(city);

        if (!pollutionLevels.isEmpty()) {
            AirPollutionLevel latest = pollutionLevels.get(pollutionLevels.size() - 1);
            responseBuilder.setAirPollutionLevel(latest);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<AirQualityUpdate> updateAirQuality(StreamObserver<Empty> responseObserver) {
        return new StreamObserver<AirQualityUpdate>() {
            private volatile boolean cancelled = false;
            private final Context.CancellableContext context = Context.current().withCancellation();

            @Override
            public void onNext(AirQualityUpdate update) {
                // Check if cancelled
                if (cancelled) {
                    return;
                }

                String city = update.getCity();
                AirPollutionLevel pollutionLevel = update.getAirPollutionLevel();
                List<AirPollutionLevel> pollutionLevels = pollutionData.computeIfAbsent(city, k -> new ArrayList<>());
                pollutionLevels.add(pollutionLevel);

                // Log the update
                System.out.println("Received pollution update for " + city + ": " + pollutionLevel);

                // Notify the client that the update was processed successfully
                responseObserver.onNext(Empty.newBuilder().build());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Error in updateAirQuality: " + t.getMessage());
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                System.out.println("Client finished streaming updates");
                responseObserver.onCompleted();
            }
        };
    }
}
