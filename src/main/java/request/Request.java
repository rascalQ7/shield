package request;

import com.fasterxml.jackson.databind.JsonNode;

public record Request(String modelName, String requestId,
                      String deviceId, String url) {

  public static Request of(JsonNode event) {
    return new Request(
        event.get("model_name").asText(),
        event.get("request_id").asText(),
        event.get("device_id").asText(),
        event.get("url").asText()
    );
  }
}
