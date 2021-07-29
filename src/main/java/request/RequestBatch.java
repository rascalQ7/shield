package request;

import static profile.PolicyType.ALLOW;
import static profile.PolicyType.BLOCK;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import profile.Profile;
import utils.JsonParser;

public class RequestBatch {

  private final Stream<String> events;
  private final List<Profile> profiles = new ArrayList<>();

  private record Request(String modelName, String requestId, String deviceId, String url) {

  }

  public RequestBatch(Stream<String> events) {
    this.events = Objects.requireNonNull(events);
  }

  public void execute() {
    this.events
        .map(JsonParser::toJsonNode)
        .forEach(this::executeEvent);
    System.out.println(this.profiles);
  }

  private void executeEvent(JsonNode event) {
    var requestType = RequestType.get(event.get("type").asText());
    switch (requestType) {
      case CREATE -> createProfile(event);
      case UPDATE -> updateProfile(event);
      case REQUEST -> verifyRequest(event);
    }
  }

  private void createProfile(JsonNode event) {
    this.profiles.add(Profile.of(event));
  }

  private void updateProfile(JsonNode event) {
    var modelName = event.get("model_name").asText();
    var policyType = event.at("/blacklist").isMissingNode() ? BLOCK : ALLOW;

    this.profiles.stream()
        .filter(profile -> profile.getModelName().equals(modelName))
        .filter(profile -> profile.getPolicyType().equals(policyType))
        .findFirst()
        .ifPresent(profile -> profile.setUrlsFromEvent(event));
  }

  private void verifyRequest(JsonNode event) {
    var request = new Request(
        event.get("model_name").asText(),
        event.get("request_id").asText(),
        event.get("device_id").asText(),
        event.get("url").asText()
    );

    this.profiles.stream()
        .filter(profile -> profile.getModelName().equals(request.modelName))
        .findFirst()
        .ifPresent(profile -> {
          switch (profile.getPolicyType()) {
            case ALLOW -> {
              if (profile.getUrls().contains(request.url)) {
                System.out.println(
                    "{\"request_id\": \"" + request.requestId + "\", \"action\": " + "\"block\"}");
              } else {
                System.out.println(
                    "{\"request_id\": \"" + request.requestId + "\", \"action\": " + "\"allow\"}");
              }
            }
            case BLOCK -> {
              if (profile.getUrls().contains(request.url)) {
                System.out.println(
                    "{\"request_id\": \"" + request.requestId + "\", \"action\": " + "\"allow\"}");
              } else {
                System.out.println(
                    "{\"request_id\": \"" + request.requestId + "\", \"action\": " + "\"block\"}");
                System.out.println("{\"device_id\": \"" + request.deviceId + "\", \"action\": "
                    + "\"quarantine\"}");
              }
            }
          }
        });
  }
}