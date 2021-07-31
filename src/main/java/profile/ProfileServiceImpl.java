package profile;

import static profile.PolicyType.ALLOW;
import static profile.PolicyType.BLOCK;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Objects;
import statistics.StatisticsService;

public class ProfileServiceImpl implements ProfileService {

  private final ProfilesCacheService profilesCache;
  private final StatisticsService statisticsService;

  public ProfileServiceImpl(
      ProfilesCacheService profilesCacheService,
      StatisticsService statisticsService
  ) {
    this.profilesCache = Objects.requireNonNull(profilesCacheService);
    this.statisticsService = Objects.requireNonNull(statisticsService);
  }

  @Override
  public void createProfile(Profile profile) {
    this.profilesCache.addProfile(profile);
  }

  @Override
  public void updateProfile(JsonNode profileUpdate) {
    var modelName = profileUpdate.get("model_name").asText();
    var policyType = profileUpdate.at("/blacklist").isMissingNode() ? BLOCK : ALLOW;

    this.profilesCache.getProfile(modelName)
        .filter(profile -> profile.getPolicyType().equals(policyType))
        .ifPresent(profile -> {
          profile.setUrlsFromEvent(profileUpdate);
          this.statisticsService.logProfileChange(profileUpdate);
        });
  }
}
