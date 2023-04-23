package Service3Envirnment;
import com.example.grpc.smartenvironmentalcontrol.DeviceRequest;
import com.example.grpc.smartenvironmentalcontrol.DeviceResponse;
import com.example.grpc.smartenvironmentalcontrol.DeviceServiceGrpc;
import io.grpc.stub.StreamObserver;

public class SmartEnvironmentalControlServiceImpl extends DeviceServiceGrpc.DeviceServiceImplBase{
    @Override
    public void turnOnDevice(DeviceRequest request, StreamObserver<DeviceResponse> responseObserver) {
        // Implement turnOnDevice logic here
        DeviceResponse response = DeviceResponse.newBuilder()
                .setMessage("Device turned on successfully")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void turnOffDevice(DeviceRequest request, StreamObserver<DeviceResponse> responseObserver) {
        // Implement turnOffDevice logic here
        DeviceResponse response = DeviceResponse.newBuilder()
                .setMessage("Device turned off successfully")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateDeviceSettings(DeviceRequest request, StreamObserver<DeviceResponse> responseObserver) {
        // Implement updateDeviceSettings logic here
        DeviceResponse response = DeviceResponse.newBuilder()
                .setMessage("Device settings updated successfully")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
