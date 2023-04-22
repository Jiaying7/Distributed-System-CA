package Service2Air;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AirPollutionTrackerServer {
    private static final Logger logger = Logger.getLogger(AirPollutionTrackerServer.class.getName());

    private Server server;

    private void start() throws IOException {
        /* The port on which the server should run */
        int port = 50051;
        server = ServerBuilder.forPort(port)
                .addService(new AirPollutionTrackerImpl())
                .intercept(new AuthenticationInterceptor())
                .build()
                .start();
        logger.log(Level.INFO, "Server started, listening on " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Use stderr here since the logger may have been reset by its JVM shutdown hook.
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            try {
                AirPollutionTrackerServer.this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.err.println("*** server shut down");
        }));
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final AirPollutionTrackerServer server = new AirPollutionTrackerServer();
        server.start();
        server.blockUntilShutdown();
    }

    static class AirPollutionTrackerImpl extends AirPollutionTrackerGrpc.AirPollutionTrackerImplBase {
        @Override
        public void getAirQuality(AirQualityRequest request, StreamObserver<AirQualityResponse> responseObserver) {
            // Perform necessary checks and retrieve the air quality
            try {
                // Check if user is authorized to access the resource
                checkAuthorization(request);

                // Check if request deadline has expired
                checkDeadline(request);

                // Retrieve air quality
                AirQuality airQuality = retrieveAirQuality(request);

                // Build response and send it to the client
                AirQualityResponse response = AirQualityResponse.newBuilder()
                        .setAirQuality(airQuality)
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } catch (Exception e) {
                // Send an error response to the client
                responseObserver.onError(e);
            }
        }

        @Override
        public void setAirQualityThreshold(AirQualityThresholdRequest request, StreamObserver<Empty> responseObserver) {
            // Perform necessary checks and set the air quality threshold
            try {
                // Check if user is authorized to access the resource
                checkAuthorization(request);

                // Check if request deadline has expired
                checkDeadline(request);

                // Set air quality threshold
                setAirQualityThreshold(request);

                // Send an empty response to the client
                responseObserver.onNext(Empty.getDefaultInstance());
                responseObserver.onCompleted();
            } catch (Exception e) {
                // Send an error response to the client
                responseObserver.onError(e);
            }
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

            try {
                // Process the update
                double longitude = update.getLongitude();
                double latitude = update.getLatitude();
                double pollutionLevel = update.getPollutionLevel();
                airPollutionDatabase.updateAirQuality(longitude, latitude, pollutionLevel);

                // Send acknowledgement
                responseObserver.onNext(Empty.newBuilder().build());
            } catch (Exception e) {
                // Handle remote invocation error
                responseObserver.onError(Status.INTERNAL.withDescription("Error updating air quality").withCause(e).asRuntimeException());
                context.cancel(new Exception("Air quality update cancelled"));
            }
        }

        @Override
        public void onError(Throwable t) {
            // Handle remote invocation error
            responseObserver.onError(t);
            context.cancel(new Exception("Air quality update cancelled"));
        }

        @Override
        public void onCompleted() {
            // Complete the RPC
            responseObserver.onCompleted();
        }

        @Override
        public void beforeStart(CancellableContext context) throws Exception {
            // Check if cancelled
            if (cancelled) {
                throw new CancellationException();
            }
        }
    };
}
}
