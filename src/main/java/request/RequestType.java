package request;

import java.util.HashMap;
import java.util.Map;

public enum RequestType {
  CREATE("profile_create"),
  UPDATE("profile_update"),
  REQUEST("request");

  private static final Map<String, RequestType> lookup = new HashMap<>();

  static {
    for (RequestType type : RequestType.values()) {
      lookup.put(type.getValue(), type);
    }
  }

  public static RequestType get(String value) {
    return lookup.get(value);
  }

  public final String value;

  RequestType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}

