import io.grpc.stub.StreamObserver;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.grpc.services.HealthStatusManager;
import io.grpc.stub.StreamObserver;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class WaterPollutionTrackerImpl extends WaterPollutionTrackerGrpc.WaterPollutionTrackerImplBase{
    private static final Logger logger = Logger.getLogger(WaterPollutionTrackerImpl.class.getName());

    private final List<StreamObserver<WaterPollutionLevel>> observers = new ArrayList<>();
    private final Map<String, List<WaterPollutionLevel>> history = new HashMap<>();

    private final HealthStatusManager healthStatusManager;
    private final Server server;

    private final String SERVICE_TYPE = "_http._tcp.local.";
    private final String SERVICE_NAME = "WaterPollutionTracker";
    private final int SERVICE_PORT = 50051;
    private final String SERVICE_DESCRIPTION = "Water Pollution Tracker Service";

    private JmDNS jmdns;
    private String serviceId;

    public WaterPollutionTrackerImpl(int port) throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(this)
                .addService(ProtoReflectionService.newInstance())
                .build();

        healthStatusManager = new HealthStatusManager();
        server.start();
        logger.info("Server started, listening on " + port);

        // Register the service with jmdns
        try {
            jmdns = JmDNS.create(InetAddress.getLocalHost());
            serviceId = UUID.randomUUID().toString();

            ServiceInfo serviceInfo = ServiceInfo.create(SERVICE_TYPE, SERVICE_NAME + "-" + serviceId, SERVICE_PORT, SERVICE_DESCRIPTION);
            jmdns.registerService(serviceInfo);
            logger.info("Registered service with type " + SERVICE_TYPE + " and name " + SERVICE_NAME + "-" + serviceId);
        } catch (UnknownHostException e) {
            logger.warning("Failed to create JmDNS instance: " + e.getMessage());
        } catch (IOException e) {
            logger.warning("Failed to register service with JmDNS: " + e.getMessage());
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Use stderr here since the logger may have been reset by its JVM shutdown hook.
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            try {
                WaterPollutionTrackerImpl.this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.err.println("*** server shut down");
        }));
    }

    public void stop() throws InterruptedException {
        if (jmdns != null) {
            jmdns.unregisterAllServices();
            jmdns.close();
        }

        server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
    @Override
public void getCurrentWaterQuality(WaterPollutionMonitorRequest request, StreamObserver<WaterPollutionLevel> responseObserver) {
    logger.info("Received request for current water quality");

    // Add the observer to the list of observers to be notified
    observers.add(responseObserver);

    // Set up a callback to remove the observer from the list when the RPC is complete
    responseObserver.setOnCancelHandler(() -> {
        logger.info("Observer was cancelled, removing from observer list");
        observers.remove(responseObserver);
    });

    // Add metadata to the response
    WaterPollutionLevel.Builder responseBuilder = WaterPollutionLevel.newBuilder()
            .setTimestamp(System.currentTimeMillis())
            .setLocation(request.getLocation())
            .setStation(request.getStation())
            .setPh(generateRandomFloat(6.0f, 8.5f))
            .setTemperature(generateRandomFloat(20.0f, 30.0f))
            .setDissolvedOxygen(generateRandomFloat(6.0f, 8.0f))
            .setTurbidity(generateRandomFloat(2.0f, 10.0f))
            .setConductivity(generateRandomFloat(0.4f, 2.0f));

    // Use authentication to validate the request
    if (!isValidUser(request)) {
        responseBuilder.setErrorMessage("Invalid user");
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
        return;
    }

    // Check if a monitoring alert should be triggered
    checkMonitoringAlert(responseBuilder.build(), request.getThresholdList());

    // Send the response to the client
    responseObserver.onNext(responseBuilder.build());
    responseObserver.onCompleted();
}

// Helper method to generate a random float between min and max
private float generateRandomFloat(float min, float max) {
    return (float) (min + Math.random() * (max - min));
}

// Helper method to check if the user is valid
private boolean isValidUser(WaterPollutionMonitorRequest request) {
    // Check if the username and password are valid
    return request.hasUsername() && request.hasPassword() && request.getUsername().equals("admin") && request.getPassword().equals("password");
}

// Helper method to check if a monitoring alert should be triggered
private void checkMonitoringAlert(WaterPollutionLevel currentLevel, List<WaterQualityThreshold> thresholdList) {
    // Check if any threshold is violated
    for (WaterQualityThreshold threshold : thresholdList) {
        switch (threshold.getParameterCase()) {
            case PH:
                if (currentLevel.getPh() > threshold.getPh().getValue()) {
                    // Trigger monitoring alert
                    logger.info("PH level exceeded threshold: " + threshold.getPh().getValue());
                    notifyObservers("PH level exceeded threshold: " + threshold.getPh().getValue());
                }
                break;
            case TEMPERATURE:
                if (currentLevel.getTemperature() > threshold.getTemperature().getValue()) {
                    // Trigger monitoring alert
                    logger.info("Temperature exceeded threshold: " + threshold.getTemperature().getValue());
                    notifyObservers("Temperature exceeded threshold: " + threshold.getTemperature().getValue());
                }
                break;
            case DISSOLVED_OXYGEN:
                if (currentLevel.getDissolvedOxygen() < threshold.getDissolvedOxygen().getValue()) {
                    // Trigger monitoring alert
                    logger.info("Dissolved oxygen level exceeded threshold: " + threshold.getDissolvedOxygen().getValue());
                    notifyObservers("Dissolved oxygen level exceeded threshold: " + threshold.getDissolvedOxygen().getValue());
                }
