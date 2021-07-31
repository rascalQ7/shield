package request;

import java.util.Objects;
import profile.PolicyType;
import profile.ProfilesCacheService;
import statistics.StatisticsService;

public class RequestServiceImpl implements RequestService {

  private final ProfilesCacheService profilesCache;
  private final StatisticsService statisticsService;

  public RequestServiceImpl(
      ProfilesCacheService profilesCacheService,
      StatisticsService statisticsService
  ) {
    this.profilesCache = Objects.requireNonNull(profilesCacheService);
    this.statisticsService = Objects.requireNonNull(statisticsService);
  }

  /**
   * Looks for profile in the cache, if profile exists handles request according to policy
   *
   * @param request - http request from specific device
   */
  @Override
  public void verify(Request request) {
    this.profilesCache.getProfile(request.getModelName())
        .ifPresent(profile -> {
              if (profile.getPolicyType() == PolicyType.ALLOW) {
                if (profile.getUrls().contains(request.getUrl())) {
                  this.statisticsService.logBlock(request);
                } else {
                  this.statisticsService.logAllow(request);
                }
              } else if (profile.getPolicyType() == PolicyType.BLOCK) {
                if (profile.getUrls().contains(request.getUrl())) {
                  this.statisticsService.logAllow(request);
                } else {
                  this.statisticsService.logBlock(request);
                  this.statisticsService.logQuarantine(request);
                }
              }
            }
        );
  }
}
