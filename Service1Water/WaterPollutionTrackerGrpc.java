import io.grpc.stub.StreamObserver;
import io.grpc.waterpollutiontracker.WaterPollutionTrackerServiceGrpc.WaterPollutionTrackerServiceImplBase;
import io.grpc.waterpollutiontracker.WaterPollutionTrackerOuterClass.GetWaterQualityHistoryRequest;
import io.grpc.waterpollutiontracker.WaterPollutionTrackerOuterClass.GetWaterQualityHistoryResponse;
import io.grpc.waterpollutiontracker.WaterPollutionTrackerOuterClass.GetWaterQualityRequest;
import io.grpc.waterpollutiontracker.WaterPollutionTrackerOuterClass.GetWaterQualityResponse;
import io.grpc.waterpollutiontracker.WaterPollutionTrackerOuterClass.SetWaterQualityThresholdRequest;
import io.grpc.waterpollutiontracker.WaterPollutionTrackerOuterClass.SetWaterQualityThresholdResponse;
import io.grpc.waterpollutiontracker.WaterQualityParameter.TemperatureParameter;
import io.grpc.waterpollutiontracker.WaterQualityParameter.TurbidityParameter;
import io.grpc.waterpollutiontracker.WaterQualityParameter.PHParameter;
import io.grpc.waterpollutiontracker.WaterQualityParameter.DissolvedOxygenParameter;
import java.util.ArrayList;
import java.util.List;

public class WaterPollutionTrackerGrpc extends WaterPollutionTrackerServiceImplBase {

    private static List<WaterQualityParameter> waterQualityHistory = new ArrayList<>();
    private static List<StreamObserver<WaterQualityParameter>> observers = new ArrayList<>();
  
    @Override
    public void getWaterQuality(GetWaterQualityRequest request, StreamObserver<GetWaterQualityResponse> responseObserver) {
      TemperatureParameter temperature = TemperatureParameter.newBuilder().setValue(25.6).build();
      TurbidityParameter turbidity = TurbidityParameter.newBuilder().setValue(20.0).build();
      PHParameter ph = PHParameter.newBuilder().setValue(7.2).build();
      DissolvedOxygenParameter dissolvedOxygen = DissolvedOxygenParameter.newBuilder().setValue(7.9).build();
  
      GetWaterQualityResponse response = GetWaterQualityResponse.newBuilder()
          .setTemperature(temperature)
          .setTurbidity(turbidity)
          .setPh(ph)
          .setDissolvedOxygen(dissolvedOxygen)
          .build();
  
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    }
  
    @Override
    public void setWaterQualityThreshold(SetWaterQualityThresholdRequest request, StreamObserver<SetWaterQualityThresholdResponse> responseObserver) {
      String message = "Water quality threshold values set successfully!";
      SetWaterQualityThresholdResponse response = SetWaterQualityThresholdResponse.newBuilder()
          .setMessage(message)
          .build();
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    }
  
    @Override
    public void getWaterQualityHistory(GetWaterQualityHistoryRequest request, StreamObserver<GetWaterQualityHistoryResponse> responseObserver) {
      int start = request.getStartTime();
      int end = request.getEndTime();
  
      List<WaterQualityParameter> result = new ArrayList<>();
      for (WaterQualityParameter parameter : waterQualityHistory) {
        int timestamp = parameter.getTimestamp();
        if (timestamp >= start && timestamp <= end) {
          result.add(parameter);
        }
      }
  
      GetWaterQualityHistoryResponse response = GetWaterQualityHistoryResponse.newBuilder()
          .addAllWaterQualityParameters(result)
          .build();
  
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    }
    // Server-side streaming RPC method
  @Override
  public StreamObserver<WaterQualityParameter> monitorWaterQuality(StreamObserver<WaterQualityParameter> responseObserver) {
    observers.add(responseObserver);
    return new StreamObserver<WaterQualityParameter>() {
      @Override
      public void onNext(WaterQualityParameter value) {
        // Receive the latest water quality parameter from the client
        System.out.println("Received water quality parameter from client: " + value.toString());

        // Check if the latest parameter violates any threshold
        checkThreshold(value);

        // Send the latest water quality parameter to all the clients who have subscribed
        for (StreamObserver<WaterQualityParameter> observer : observers) {
          observer.onNext(value);
        }
      }

      @Override
      public void onError(Throwable t) {
        System.out.println("Error in monitorWaterQuality: " + t.getMessage());
      }

      @Override
      public void onCompleted() {
        observers.remove(responseObserver);
        responseObserver.onCompleted();
      }
    };
  }

  // Client-side streaming RPC method
  @Override
  public void setWaterQualityThreshold(StreamObserver<ThresholdResponse> responseObserver) {
    StreamObserver<ThresholdRequest> requestObserver = new StreamObserver<ThresholdRequest>() {
      private List<WaterQualityThreshold> thresholds = new ArrayList<>();

      @Override
      public void onNext(ThresholdRequest value) {
        // Receive the latest threshold values from the client
        WaterQualityThreshold threshold = value.getThreshold();
        System.out.println("Received threshold from client: " + threshold.toString());

        // Add the latest threshold to the list of thresholds
        thresholds.add(threshold);
      }

      @Override
      public void onError(Throwable t) {
        System.out.println("Error in setWaterQualityThreshold: " + t.getMessage());
      }

      @Override
      public void onCompleted() {
        // Send the response to the client
        responseObserver.onNext(ThresholdResponse.newBuilder().setStatus("Threshold set successfully").build());
        responseObserver.onCompleted();

        // Update the current threshold values with the latest received values
        thresholdList = thresholds;
        System.out.println("Water quality threshold values updated: " + thresholdList.toString());
      }
    };

    // Return the request observer to the client
    responseObserver.onNext(ThresholdResponse.newBuilder().setStatus("Ready to receive threshold values").build());
    responseObserver.onCompleted();
  }

  // Bidirectional streaming RPC method
  @Override
  public StreamObserver<WaterQualityParameter> monitorWaterQualityBi(StreamObserver<WaterQualityAlert> responseObserver) {
    return new StreamObserver<WaterQualityParameter>() {
      private boolean alertSent = false;

      @Override
      public void onNext(WaterQualityParameter value) {
        // Receive the latest water quality parameter from the client
        System.out.println("Received water quality parameter from client: " + value.toString());

        // Check if the latest parameter violates any threshold
        checkThreshold(value);
      };
    }
    @Override
public void onNext(WaterQualityParameter value) {
  System.out.println("Received water quality parameter: " + value.toString());
  
  boolean alertSent = alertHistory.contains(value.getParameterCase().toString());
  
  // Check if an alert has already been sent for the current parameter
  if (alertSent) {
    System.out.println("Alert has already been sent for the current parameter");
  } else {
    // Check if the latest parameter violates any threshold
    boolean thresholdViolated = false;
    for (WaterQualityThreshold threshold : thresholdList) {
      switch (threshold.getParameterCase()) {
        case PH:
          if (value.getPh() > threshold.getPh().getValue()) {
            thresholdViolated = true;
          }
          break;
        case TEMPERATURE:
          if (value.getTemperature() > threshold.getTemperature().getValue()) {
            thresholdViolated = true;
          }
          break;
        case TURBIDITY:
          if (value.getTurbidity() > threshold.getTurbidity().getValue()) {
            thresholdViolated = true;
          }
          break;
        case DISSOLVED_OXYGEN:
          if (value.getDissolvedOxygen() < threshold.getDissolvedOxygen().getValue()) {
            thresholdViolated = true;
          }
          break;
        default:
          break;
      }
    }
    
    // If a threshold is violated, send an alert
    if (thresholdViolated) {
      System.out.println("Water quality threshold violated. Sending alert to observers.");
      alertHistory.add(value.getParameterCase().toString());
      for (StreamObserver<WaterQualityParameter> observer : observers) {
        observer.onNext(value);
      }
    }
  }
}