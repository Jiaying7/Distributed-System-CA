import io.grpc.stub.StreamObserver;
import proto.*;

public class SmartWaterMonitoringService extends WaterMonitoringServiceGrpc.WaterMonitoringServiceImplBase {
    
    private WaterQualityParameters waterQualityParameters;
    private WaterQualityThresholds waterQualityThresholds;
    private WaterQualityHistory waterQualityHistory;
    
    public SmartWaterMonitoringService() {
        waterQualityParameters = WaterQualityParameters.newBuilder()
                .setPh(0.0)
                .setTemperature(0.0)
                .setTurbidity(0.0)
                .setDissolvedOxygenLevel(0.0)
                .build();
        
        waterQualityThresholds = WaterQualityThresholds.newBuilder()
                .setPhThreshold(7.0)
                .setTemperatureThreshold(25.0)
                .setTurbidityThreshold(10.0)
                .setDissolvedOxygenLevelThreshold(7.0)
                .build();
        
        waterQualityHistory = WaterQualityHistory.newBuilder()
                .addWaterQualityParameters(waterQualityParameters)
                .build();
    }
    
    @Override
    public void getWaterQuality(GetWaterQualityRequest request, StreamObserver<WaterQualityParameters> responseObserver) {
        responseObserver.onNext(waterQualityParameters);
        responseObserver.onCompleted();
    }
    
    @Override
    public void setWaterQualityThreshold(SetWaterQualityThresholdRequest request, StreamObserver<SetWaterQualityThresholdResponse> responseObserver) {
        waterQualityThresholds = request.getWaterQualityThresholds();
        responseObserver.onNext(SetWaterQualityThresholdResponse.newBuilder().setStatus("Success").build());
        responseObserver.onCompleted();
    }
    
    @Override
    public void getWaterQualityHistory(GetWaterQualityHistoryRequest request, StreamObserver<WaterQualityHistory> responseObserver) {
        // Here you can implement the logic to retrieve the historical data for the specified time frame
        responseObserver.onNext(waterQualityHistory);
        responseObserver.onCompleted();
    }
    
}