package request;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Objects;
import java.util.stream.Stream;
import profile.Profile;
import profile.ProfileService;
import utils.JsonParser;

/**
 * Events router to service for further execution
 */
public record RequestBatch(Stream<String> events,
                           ProfileService profileService,
                           RequestService requestService) {

  /**
   * @param events         - stream of events
   * @param profileService - service for profile events handling
   * @param requestService - service for request events handling
   */
  public RequestBatch(
      Stream<String> events,
      ProfileService profileService,
      RequestService requestService
  ) {
    this.events = Objects.requireNonNull(events);
    this.profileService = Objects.requireNonNull(profileService);
    this.requestService = Objects.requireNonNull(requestService);
  }

  /**
   * handle events
   */
  public void execute() {
    this.events
        .map(JsonParser::toJsonNode)
        .forEach(this::executeEvent);
  }

  private void executeEvent(JsonNode event) {
    var requestType = RequestType.get(event.get("type").asText());
    switch (requestType) {
      case CREATE -> this.profileService.createProfile(Profile.of(event));
      case UPDATE -> this.profileService.updateProfile(event);
      case REQUEST -> this.requestService.verify(Request.of(event));
    }
  }
}