package profile;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProfileTest {
  private static final ObjectMapper mapper = new ObjectMapper();
  private static final String MODEL_NAME = "model_name";
  private static final String POLICY = "default";
  private static final String BLACKLIST = "blacklist";
  private static final String WHITELIST = "whitelist";
  private static final ArrayNode BLACKLIST_LIST = mapper.createArrayNode().add("blacklist");
  private static final ArrayNode WHITELIST_LIST = mapper.createArrayNode().add("whitelist");

  private Profile profile;
  private ArrayNode event;

  @BeforeEach
  void setUp() {
    this.event = mapper.createObjectNode()
        .put(MODEL_NAME, MODEL_NAME)
        .put(POLICY, "ALLOW")
        .withArray(BLACKLIST)
        .add(BLACKLIST);
  }

  @Test
  void shouldConstructProfileWithModelName() {

    this.profile = Profile.of(event);

    assertEquals(MODEL_NAME, this.profile.getModelName());
  }
}