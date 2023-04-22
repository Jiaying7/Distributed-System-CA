package Service2Air;

import Service2Air.AirPollutionLevel;
import Service2Air.AirPollutionRequest;
import Service2Air.AirQualityUpdate;
import Service2Air.AirPollutionTrackerGrpc.AirPollutionTrackerImplBase;
import Service2Air.AirPollutionTrackerOuterClass.AirPollutionHistoryRequest;
import Service2Air.AirPollutionTrackerOuterClass.Empty;
import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.List;

public class AirPollutionTrackerImpl extends AirPollutionTrackerImplBase {

    private List<AirPollutionLevel> airPollutionHistory = new ArrayList<>();

    @Override
    public void getAirPollution(AirPollutionRequest request, StreamObserver<AirPollutionLevel> responseObserver) {
        System.out.println("Received air pollution request for location " + request.getLocation());
        for (AirPollutionLevel level : airPollutionHistory) {
            if (level.getLocation().equals(request.getLocation())) {
                responseObserver.onNext(level);
            }
        }
        responseObserver.onCompleted();
    }

    @Override
    public void updateAirQuality(AirQualityUpdate request, StreamObserver<Empty> responseObserver) {
        String location = request.getLocation();
        String pollutionType = request.getPollutionType();
        float pollutionLevel = request.getPollutionLevel();
        String timestamp = request.getTimestamp();

        System.out.println("Received air quality update: " + location + ", " + pollutionType + ", " + pollutionLevel + ", " + timestamp);
        AirPollutionLevel airPollutionLevel = AirPollutionLevel.newBuilder()
            .setLocation(location)
            .setPollutionType(pollutionType)
            .setPollutionLevel(pollutionLevel)
            .setTimestamp(timestamp)
            .build();

        airPollutionHistory.add(airPollutionLevel);
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void getAirPollutionHistory(AirPollutionHistoryRequest request, StreamObserver<AirPollutionLevel> responseObserver) {
        System.out.println("Received air pollution history request for location " + request.getLocation());
        for (AirPollutionLevel level : airPollutionHistory) {
            if (level.getLocation().equals(request.getLocation())) {
                responseObserver.onNext(level);
            }
        }
        responseObserver.onCompleted();
    }

}
