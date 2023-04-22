package Service2Air;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AirPollutionLevel extends AirPollutionTrackerGrpc.AirPollutionTrackerImplBase{
    private final ConcurrentHashMap<Integer, AirQualityUpdate> airQualityUpdates = new ConcurrentHashMap<>();

    @Override
    public void getAirPollutionLevel(AirPollutionRequest request, StreamObserver<AirPollutionResponse> responseObserver) {
        int locationId = request.getLocationId();

        if (!airQualityUpdates.containsKey(locationId)) {
            responseObserver.onError(new StatusRuntimeException(Status.NOT_FOUND));
            return;
        }

        AirQualityUpdate airQualityUpdate = airQualityUpdates.get(locationId);
        AirPollutionResponse response = AirPollutionResponse.newBuilder()
                .setLocationId(locationId)
                .setParticulateMatter(airQualityUpdate.getParticulateMatter())
                .setCarbonMonoxide(airQualityUpdate.getCarbonMonoxide())
                .setNitrogenOxides(airQualityUpdate.getNitrogenOxides())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateAirQuality(AirQualityUpdate request, StreamObserver<Empty> responseObserver) {
        int locationId = request.getLocationId();
        airQualityUpdates.put(locationId, request);
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<AirQualityUpdate> updateAirQualityStream(StreamObserver<Empty> responseObserver) {
        return new StreamObserver<AirQualityUpdate>() {
            private volatile boolean cancelled = false;

            @Override
            public void onNext(AirQualityUpdate value) {
                if (cancelled) {
                    return;
                }
                int locationId = value.getLocationId();
                airQualityUpdates.put(locationId, value);
            }

            @Override
            public void onError(Throwable t) {
                if (cancelled) {
                    return;
                }
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                if (cancelled) {
                    return;
                }
                responseObserver.onNext(Empty.newBuilder().build());
                responseObserver.onCompleted();
            }
        };
    }
}
