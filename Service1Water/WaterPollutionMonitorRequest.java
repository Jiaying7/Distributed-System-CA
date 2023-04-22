public class WaterPollutionMonitorRequest extends WaterPollutionMonitorRequestGrpc.WaterPollutionMonitorRequestImplBase{
    private final Map<String, List<StreamObserver<WaterQualityParameter>>> observersMap = new HashMap<>();

    @Override
    public StreamObserver<WaterQualityRequest> monitorWaterQuality(StreamObserver<WaterQualityParameter> responseObserver) {
        String region = "";
        List<WaterQualityThreshold> thresholdList = new ArrayList<>();

        return new StreamObserver<WaterQualityRequest>() {
            @Override
            public void onNext(WaterQualityRequest request) {
                switch (request.getRequestCase()) {
                    case REGION:
                        region = request.getRegion().getName();
                        break;
                    case THRESHOLD:
                        thresholdList.add(request.getThreshold());
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onError(Throwable t) {
                observersMap.get(region).remove(responseObserver);
            }

            @Override
            public void onCompleted() {
                // Remove the observer when the client is done
                observersMap.get(region).remove(responseObserver);
            }
        };
    }

    @Override
    public void reportWaterQuality(WaterQualityParameter request, StreamObserver<Empty> responseObserver) {
        String region = request.getRegion().getName();

        // Notify all the observers in the region
        if (observersMap.containsKey(region)) {
            for (StreamObserver<WaterQualityParameter> observer : observersMap.get(region)) {
                observer.onNext(request);
            }
        }

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void subscribeWaterQuality(Region request, StreamObserver<WaterQualityParameter> responseObserver) {
        String region = request.getName();

        // Add the observer to the observers list for the region
        if (!observersMap.containsKey(region)) {
            observersMap.put(region, new ArrayList<>());
        }
        observersMap.get(region).add(responseObserver);

        // Send the current water quality parameter for the region to the client
        WaterQualityParameter latestParam = getLatestWaterQualityParameter(region);
        if (latestParam != null) {
            responseObserver.onNext(latestParam);
        }
    }

    private WaterQualityParameter getLatestWaterQualityParameter(String region) {
        // Your code to get the latest water quality parameter for the region
        return null;
    }
}
