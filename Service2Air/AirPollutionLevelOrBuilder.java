package Service2Air;

public interface AirPollutionLevelOrBuilder extends
// @@protoc_insertion_point(interface_extends:airpollutiontracker.AirPollutionLevel)
com.google.protobuf.MessageOrBuilder {

/**
* <code>string location = 1;</code>
*/
java.lang.String getLocation();
/**
* <code>string location = 1;</code>
*/
com.google.protobuf.ByteString
  getLocationBytes();

/**
* <code>string pollution_type = 2;</code>
*/
java.lang.String getPollutionType();
/**
* <code>string pollution_type = 2;</code>
*/
com.google.protobuf.ByteString
  getPollutionTypeBytes();

/**
* <code>float pollution_level = 3;</code>
*/
float getPollutionLevel();

/**
* <code>string timestamp = 4;</code>
*/
java.lang.String getTimestamp();
/**
* <code>string timestamp = 4;</code>
*/
com.google.protobuf.ByteString
  getTimestampBytes();
}