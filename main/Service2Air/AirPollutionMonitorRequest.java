package Service2Air;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.examples.airquality.AirQualityServiceGrpc.AirQualityServiceBlockingStub;
import io.grpc.examples.airquality.AirQualityServiceGrpc.AirQualityServiceStub;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AirPollutionMonitorRequest {
    private static final Logger logger = Logger.getLogger(AirPollutionMonitorRequest.class.getName());

    private final ManagedChannel channel;
    private final AirQualityServiceBlockingStub blockingStub;
    private final AirQualityServiceStub asyncStub;

    public AirPollutionMonitorRequest(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext());
    }

    public AirPollutionMonitorRequest(ManagedChannelBuilder<?> channelBuilder) {
        channel = channelBuilder.build();
        blockingStub = AirQualityServiceGrpc.newBlockingStub(channel);
        asyncStub = AirQualityServiceGrpc.newStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void getAirQuality() {
        logger.info("Sending GetAirQuality request to server");
        AirQualityRequest request = AirQualityRequest.newBuilder().build();
        AirQualityResponse response;
        try {
            response = blockingStub.getAirQuality(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("Air Quality: " + response.getAirQuality());
    }

    public void setAirQualityThreshold(double pmThreshold, double coThreshold, double noxThreshold) {
        logger.info("Sending SetAirQualityThreshold request to server");
        AirQualityThresholdRequest request = AirQualityThresholdRequest.newBuilder()
                .setPmThreshold(pmThreshold)
                .setCoThreshold(coThreshold)
                .setNoxThreshold(noxThreshold)
                .build();
        AirQualityThresholdResponse response;
        try {
            response = blockingStub.setAirQualityThreshold(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("Air Quality Threshold set successfully");
    }

    public void getAirQualityHistory(long startTime, long endTime) {
        logger.info("Sending GetAirQualityHistory request to server");
        AirQualityHistoryRequest request = AirQualityHistoryRequest.newBuilder()
                .setStartTime(startTime)
                .setEndTime(endTime)
                .build();
        AirQualityHistoryResponse response;
        try {
            response = blockingStub.getAirQualityHistory(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("Air Quality History: " + response.getAirQualityHistoryList());
    }

    public static void main(String[] args) throws InterruptedException {
        AirPollutionMonitorRequest client = new AirPollutionMonitorRequest("localhost", 50051);
        try {
            client.getAirQuality();
            client.setAirQualityThreshold(50.0, 10.0, 20.0);
            client.getAirQualityHistory(System.currentTimeMillis() - 3600_000, System.currentTimeMillis());
        } finally {
            client.shutdown();
        }
    }
}
