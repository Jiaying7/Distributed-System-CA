import com.google.protobuf.Timestamp;

public class WaterPollutionLevel {
    private float ph;
    private float temperature;
    private float turbidity;
    private Timestamp timestamp;

    public WaterPollutionLevel(float ph, float temperature, float turbidity, Timestamp timestamp) {
        this.ph = ph;
        this.temperature = temperature;
        this.turbidity = turbidity;
        this.timestamp = timestamp;
    }

    public float getPh() {
        return ph;
    }

    public float getTemperature() {
        return temperature;
    }

    public float getTurbidity() {
        return turbidity;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setPh(float ph) {
        this.ph = ph;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public void setTurbidity(float turbidity) {
        this.turbidity = turbidity;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
