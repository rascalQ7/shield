package profile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.List;
import utils.JsonParser;

public class Profile {

  private final String modelName;
  private final PolicyType policyType;
  private List<String> urls;

  private Profile(JsonNode event) {
    this.modelName = event.get("model_name").asText();
    this.policyType = PolicyType.valueOf(event.get("default").asText().toUpperCase());
    setUrlsFromEvent(event);
  }

  public static Profile of(JsonNode event) {
    return new Profile(event);
  }

  public String getModelName() {
    return modelName;
  }

  public PolicyType getPolicyType() {
    return policyType;
  }

  public List<String> getUrls() {
    return urls;
  }

  public void setUrlsFromEvent(JsonNode event) {
    this.urls = switch (this.policyType) {
      case ALLOW -> JsonParser.extractList((ArrayNode) event.get("blacklist"));
      case BLOCK -> JsonParser.extractList((ArrayNode) event.get("whitelist"));
    };
  }

  //TODO
  @Override
  public String toString() {
    return "Profile{" +
        "modelName='" + modelName + '\'' +
        ", policyType=" + policyType +
        ", urls=" + urls +
        '}';
  }
}
