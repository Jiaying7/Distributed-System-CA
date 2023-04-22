import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import java.util.ArrayList;
import java.util.List;


public class WaterPollutionHistoryRequestOrBuilder extends com.google.protobuf.MessageOrBuilder{
     /**
   * <code>repeated bytes id = 1;</code>
   */
  java.util.List<ByteString> getIdList();
  /**
   * <code>repeated bytes id = 1;</code>
   */
  int getIdCount();
  /**
   * <code>repeated bytes id = 1;</code>
   */
  ByteString getId(int index);
  
  /**
   * <code>int64 start_time = 2;</code>
   */
  long getStartTime();
  
  /**
   * <code>int64 end_time = 3;</code>
   */
  long getEndTime();

  /**
   * <code>repeated .WaterPollutionParameterType parameter_types = 4;</code>
   */
  java.util.List<WaterPollutionParameterType> getParameterTypesList();
  /**
   * <code>repeated .WaterPollutionParameterType parameter_types = 4;</code>
   */
  int getParameterTypesCount();
  /**
   * <code>repeated .WaterPollutionParameterType parameter_types = 4;</code>
   */
  WaterPollutionParameterType getParameterTypes(int index);

  /**
   * <code>bool average = 5;</code>
   */
  boolean getAverage();

  /**
   * <code>int32 interval = 6;</code>
   */
  int getInterval();

  /**
   * <code>repeated int32 percentiles = 7;</code>
   */
  java.util.List<java.lang.Integer> getPercentilesList();
  /**
   * <code>repeated int32 percentiles = 7;</code>
   */
  int getPercentilesCount();
  /**
   * <code>repeated int32 percentiles = 7;</code>
   */
  int getPercentiles(int index);
}
