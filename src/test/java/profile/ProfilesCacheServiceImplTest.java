package profile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProfilesCacheServiceImplTest {

  private static final String MODEL_NAME = "modelName";

  @Mock
  private Profile profile;

  private ProfilesCacheService profilesCacheService;

  @BeforeEach
  void setUp() {
    this.profilesCacheService = new ProfilesCacheServiceImpl();
  }

  @Test
  void shouldCacheProfile() {
    when(this.profile.getModelName()).thenReturn(MODEL_NAME);

    this.profilesCacheService.addProfile(this.profile);
    var actual = this.profilesCacheService.getProfile(MODEL_NAME).get();

    assertEquals(this.profile, actual);
  }

}