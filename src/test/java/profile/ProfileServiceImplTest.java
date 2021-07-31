package profile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import statistics.StatisticsService;
import utils.JsonParser;

@ExtendWith(MockitoExtension.class)
class ProfileServiceImplTest {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static final String MODEL_NAME = "model_name";
  private static final String BLACKLIST = "blacklist";
  private static final ArrayNode BLACKLIST_ARRAY = mapper.createArrayNode().add(BLACKLIST);

  @Mock
  private ProfilesCacheService profilesCache;
  @Mock
  private StatisticsService statisticsService;
  @Mock
  private Profile profile;

  private JsonNode update;

  private ProfileService profileService;

  @BeforeEach
  void setUp() {
    this.profileService = new ProfileServiceImpl(this.profilesCache, this.statisticsService);
  }

  @Test
  void shouldAddProfileToTheCache() {
    this.profileService.createProfile(this.profile);

    verify(this.profilesCache).addProfile(this.profile);
  }

  @Test
  void shouldUpdateBlacklistProfile() {
    this.update = JsonParser.toJsonNode("{\"type\": \"profile_update\", "
        + "\"" + MODEL_NAME + "\": \"" + MODEL_NAME + "\", "
        + "\"blacklist\": [\"api.lightbulb.io\"], \"timestamp\": 1563951341680}");
    when(this.profilesCache.getProfile(MODEL_NAME)).thenReturn(Optional.of(this.profile));
    when(this.profile.getPolicyType()).thenReturn(PolicyType.ALLOW);
    this.profileService.updateProfile(this.update);

    verify(this.profile).setUrlsFromEvent(this.update);
    verify(this.statisticsService).logProfileChange(this.update);
  }

  @Test
  void shouldUpdateWhitelistProfile() {
    this.update = JsonParser.toJsonNode("{\"type\": \"profile_update\", "
        + "\"" + MODEL_NAME + "\": \"" + MODEL_NAME + "\", "
        + "\"whitelist\": [\"api.lightbulb.io\"], \"timestamp\": 1563951341680}");
    when(this.profilesCache.getProfile(MODEL_NAME)).thenReturn(Optional.of(this.profile));
    when(this.profile.getPolicyType()).thenReturn(PolicyType.BLOCK);
    this.profileService.updateProfile(this.update);

    verify(this.profile).setUrlsFromEvent(this.update);
    verify(this.statisticsService).logProfileChange(this.update);
  }

  @Test
  void shouldSkipUpdateWhenProfilePolicyDiffersFromUpdate() {
    this.update = JsonParser.toJsonNode("{\"type\": \"profile_update\", "
        + "\"" + MODEL_NAME + "\": \"" + MODEL_NAME + "\", "
        + "\"whitelist\": [\"api.lightbulb.io\"], \"timestamp\": 1563951341680}");
    when(this.profilesCache.getProfile(MODEL_NAME)).thenReturn(Optional.of(this.profile));
    when(this.profile.getPolicyType()).thenReturn(PolicyType.ALLOW);
    this.profileService.updateProfile(this.update);

    verify(this.profile, never()).setUrlsFromEvent(any());
    verify(this.statisticsService, never()).logProfileChange(any());
  }

  @Test
  void shouldSkipUpdateWhenModelProfileDoesNotExist() {
    this.update = JsonParser.toJsonNode("{\"type\": \"profile_update\", "
        + "\"" + MODEL_NAME + "\": \"" + MODEL_NAME + "\", "
        + "\"whitelist\": [\"api.lightbulb.io\"], \"timestamp\": 1563951341680}");
    when(this.profilesCache.getProfile(MODEL_NAME)).thenReturn(Optional.ofNullable(null));
    this.profileService.updateProfile(this.update);

    verify(this.profile, never()).setUrlsFromEvent(any());
    verify(this.statisticsService, never()).logProfileChange(any());
  }


}