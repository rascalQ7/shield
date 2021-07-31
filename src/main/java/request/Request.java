package request;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Objects;

public class Request {

  private final String modelName;
  private final String requestId;
  private final String deviceId;
  private final String url;

  private Request(String modelName, String requestId, String deviceId, String url) {
    this.modelName = Objects.requireNonNull(modelName);
    this.requestId = Objects.requireNonNull(requestId);
    this.deviceId = Objects.requireNonNull(deviceId);
    this.url = Objects.requireNonNull(url);
  }

  public static Request of(JsonNode event) {
    return new Request(
        event.get("model_name").asText(),
        event.get("request_id").asText(),
        event.get("device_id").asText(),
        event.get("url").asText()
    );
  }

  public String getModelName() {
    return this.modelName;
  }

  public String getRequestId() {
    return this.requestId;
  }

  public String getDeviceId() {
    return this.deviceId;
  }

  public String getUrl() {
    return this.url;
  }
}
