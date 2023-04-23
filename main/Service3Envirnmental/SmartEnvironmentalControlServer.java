package Service3Envirnment;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.Metadata;
import java.io.IOException;
import java.util.logging.Logger;
import DS.Service2Air.*;
import io.grpc.protobuf.StatusProto;

public class SmartEnvironmentalControlServer {
    private static final Logger logger = Logger.getLogger(SmartEnvironmentalControlServer.class.getName());

    private Server server;

    private void start() throws IOException {
        int port = 50052;
        server = ServerBuilder.forPort(port)
                .addService(new SmartEnvironmentalControlImpl())
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            SmartEnvironmentalControlServer.this.stop();
            System.err.println("*** server shut down");
        }));
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
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
        final SmartEnvironmentalControlServer server = new SmartEnvironmentalControlServer();
        server.start();
        server.blockUntilShutdown();
    }

    static class SmartEnvironmentalControlImpl extends SmartEnvironmentalControlGrpc.SmartEnvironmentalControlImplBase {

        @Override
        public void turnOnDevice(DeviceRequest request, StreamObserver<DeviceResponse> responseObserver) {
            String deviceId = request.getDeviceId();
            // TODO: implement the device turn-on logic here
            logger.info("Device " + deviceId + " turned on");
            DeviceResponse response = DeviceResponse.newBuilder().setSuccess(true).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void turnOffDevice(DeviceRequest request, StreamObserver<DeviceResponse> responseObserver) {
            String deviceId = request.getDeviceId();
            // TODO: implement the device turn-off logic here
            logger.info("Device " + deviceId + " turned off");
            DeviceResponse response = DeviceResponse.newBuilder().setSuccess(true).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void updateDeviceSettings(DeviceSettingsUpdate request, StreamObserver<DeviceResponse> responseObserver) {
            String deviceId = request.getDeviceId();
            // TODO: implement the device settings update logic here
            logger.info("Device " + deviceId + " settings updated");
            DeviceResponse response = DeviceResponse.newBuilder().setSuccess(true).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
        @Override
public void turnOnDevice(TurnOnOffRequest request, StreamObserver<TurnOnOffResponse> responseObserver) {
    String deviceId = request.getDeviceId();
    logger.info("Turning on device " + deviceId);
    // TODO: Implement the logic to turn on the device here
    TurnOnOffResponse response = TurnOnOffResponse.newBuilder()
            .setDeviceId(deviceId)
            .setStatus(Status.newBuilder().setCode(0).setMessage("Device turned on successfully"))
            .build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
}

@Override
public void turnOffDevice(TurnOnOffRequest request, StreamObserver<TurnOnOffResponse> responseObserver) {
    String deviceId = request.getDeviceId();
    logger.info("Turning off device " + deviceId);
    // TODO: Implement the logic to turn off the device here
    TurnOnOffResponse response = TurnOnOffResponse.newBuilder()
            .setDeviceId(deviceId)
            .setStatus(Status.newBuilder().setCode(0).setMessage("Device turned off successfully"))
            .build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
}

@Override
public void updateDeviceSettings(DeviceSettingsRequest request, StreamObserver<Status> responseObserver) {
    String deviceId = request.getDeviceId();
    logger.info("Updating settings for device " + deviceId);
    // TODO: Implement the logic to update the device settings here
    Status status = Status.newBuilder().setCode(0).setMessage("Device settings updated successfully").build();
    responseObserver.onNext(status);
    responseObserver.onCompleted();
}
}
