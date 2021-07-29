package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonParser {
  private final static ObjectMapper mapper = new ObjectMapper();

  public static JsonNode toJsonNode(String event) {
    JsonNode jsonNode = null;
    try {
      jsonNode = mapper.readTree(event);
    } catch (JsonMappingException e) {
      e.printStackTrace();
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return jsonNode;
  }

  public static List<String> extractList(ArrayNode arrayNode) {
    var reader = mapper.readerFor(new TypeReference<List<String>>() {});
    List<String> urls = new ArrayList<>();
    try {
      urls = reader.readValue(arrayNode);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return urls;
  }
}
