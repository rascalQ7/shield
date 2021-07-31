package request;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import profile.PolicyType;
import profile.Profile;
import profile.ProfilesCacheService;
import statistics.StatisticsService;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

  private static final String MODEL_NAME = "modelName";
  private static final String URL = "url";
  private static final List<String> URLS = List.of(URL);
  private static final List<String> EMPTY_LIST = List.of();

  @Mock
  private ProfilesCacheService profilesCacheService;
  @Mock
  private StatisticsService statisticsService;
  @Mock
  private Request request;
  @Mock
  private Profile profile;

  private RequestService requestService;

  @BeforeEach
  void setUp() {
    this.requestService = new RequestServiceImpl(this.profilesCacheService, this.statisticsService);
    when(this.request.getModelName()).thenReturn(MODEL_NAME);
  }

  @Test
  void shouldBlockWhenPolicyAllow() {
    when(this.profilesCacheService.getProfile(MODEL_NAME)).thenReturn(Optional.of(this.profile));
    when(this.profile.getPolicyType()).thenReturn(PolicyType.ALLOW);

    when(this.profile.getUrls()).thenReturn(URLS);
    when(this.request.getUrl()).thenReturn(URL);

    this.requestService.verify(this.request);

    verify(this.statisticsService).logBlock(this.request);
  }

  @Test
  void shouldAllowWhenPolicyAllow() {
    when(this.profilesCacheService.getProfile(MODEL_NAME)).thenReturn(Optional.of(this.profile));
    when(this.profile.getPolicyType()).thenReturn(PolicyType.ALLOW);

    when(this.profile.getUrls()).thenReturn(EMPTY_LIST);
    when(this.request.getUrl()).thenReturn(URL);

    this.requestService.verify(this.request);

    verify(this.statisticsService).logAllow(this.request);
  }

  @Test
  void shouldAllowWhenPolicyBlock() {
    when(this.profilesCacheService.getProfile(MODEL_NAME)).thenReturn(Optional.of(this.profile));
    when(this.profile.getPolicyType()).thenReturn(PolicyType.BLOCK);

    when(this.profile.getUrls()).thenReturn(URLS);
    when(this.request.getUrl()).thenReturn(URL);

    this.requestService.verify(this.request);

    verify(this.statisticsService).logAllow(this.request);
  }

  @Test
  void shouldBlockWhenPolicyBlock() {
    when(this.profilesCacheService.getProfile(MODEL_NAME)).thenReturn(Optional.of(this.profile));
    when(this.profile.getPolicyType()).thenReturn(PolicyType.BLOCK);

    when(this.profile.getUrls()).thenReturn(EMPTY_LIST);
    when(this.request.getUrl()).thenReturn(URL);

    this.requestService.verify(this.request);

    verify(this.statisticsService).logBlock(this.request);
    verify(this.statisticsService).logQuarantine(this.request);
  }

  @Test
  void shouldSkipRequestWhenProfileIsNotInTheCache() {
    when(this.profilesCacheService.getProfile(MODEL_NAME)).thenReturn(Optional.ofNullable(null));

    this.requestService.verify(this.request);

    verify(this.statisticsService, never()).logAllow(any());
    verify(this.statisticsService, never()).logBlock(any());
    verify(this.statisticsService, never()).logQuarantine(any());
  }
}