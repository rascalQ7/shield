package profile;

import java.util.Optional;

/**
 * Service mocks persistence layer and acts as a Profiles cache for a single batch run
 */
public interface ProfilesCacheService {

  Optional<Profile> getProfile(String modeName);

  void addProfile(Profile profile);
}
