package Service2Air;

public class AirPollutionTrackerGrpc extends AirPollutionTrackerGrpc.AirPollutionTrackerImplBase{
    private static final int MAX_HISTORY_SIZE = 1000;
    private static final Map<String, Double> CURRENT_VALUES = new ConcurrentHashMap<>();
    private static final Queue<Map<String, Double>> HISTORY = new ConcurrentLinkedQueue<>();
    private static final Metadata.Key<String> TOKEN_KEY = Metadata.Key.of("token", Metadata.ASCII_STRING_MARSHALLER);
    private static final Map<String, String> VALID_TOKENS = new HashMap<>(); // for demo purposes only

    static {
        VALID_TOKENS.put("user1", "password1");
        VALID_TOKENS.put("user2", "password2");
        VALID_TOKENS.put("user3", "password3");
    }

    @Override
    public void getAirQuality(AirQualityRequest request, StreamObserver<AirQualityResponse> responseObserver) {
        // Retrieve current values for requested parameters
        Map<String, Double> currentValues = new HashMap<>();
        for (String param : request.getParamsList()) {
            Double value = CURRENT_VALUES.get(param);
            if (value != null) {
                currentValues.put(param, value);
            }
        }
        // Build and send response
        AirQualityResponse response = AirQualityResponse.newBuilder().putAllValues(currentValues).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void setAirQualityThreshold(AirQualityThresholdRequest request, StreamObserver<Empty> responseObserver) {
        // Validate authentication token
        if (!isValidToken(request.getToken())) {
            responseObserver.onError(Status.UNAUTHENTICATED.withDescription("Invalid authentication token").asRuntimeException());
            return;
        }
        // Store threshold values for requested parameters
        for (String param : request.getParamsMap().keySet()) {
            Double value = request.getParamsMap().get(param);
            CURRENT_VALUES.put(param + "_threshold", value);
        }
        // Send empty response
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void getAirQualityHistory(AirQualityHistoryRequest request, StreamObserver<AirQualityHistoryResponse> responseObserver) {
        // Validate authentication token
        if (!isValidToken(request.getToken())) {
            responseObserver.onError(Status.UNAUTHENTICATED.withDescription("Invalid authentication token").asRuntimeException());
            return;
        }
        // Retrieve historical data for requested parameters and time frame
        List<Map<String, Double>> history = new ArrayList<>();
        for (Map<String, Double> entry : HISTORY) {
            if (isWithinTimeFrame(entry, request.getStartTime(), request.getEndTime())) {
                history.add(entry);
            }
        }
        // Build and send response
        AirQualityHistoryResponse.Builder responseBuilder = AirQualityHistoryResponse.newBuilder();
        for (Map<String, Double> entry : history) {
            responseBuilder.addValues(entry);
        }
        AirQualityHistoryResponse response = responseBuilder.build();
        responseObserver.onNext(response);
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

            // Perform updates
            try {
                airQualityData.updateAirQuality(update);
            } catch (StatusRuntimeException e) {
                responseObserver.onError(e);
                return;
            }

            // Send acknowledgement
            responseObserver.onNext(Empty.newBuilder().build());
        }

        @Override
        public void onError(Throwable t) {
            // Log error
            logger.log(Level.WARNING, "Error updating air quality: " + t.getMessage());

            // Send error response
            responseObserver.onError(t);
        }

        @Override
        public void onCompleted() {
            // Log completion
            logger.info("Air quality updates completed");

            // Send completion response
            responseObserver.onCompleted();
        }
    };
}
}
