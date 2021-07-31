package profile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ProfilesCacheServiceImpl implements ProfilesCacheService {

  private final Map<String, Profile> profiles = new HashMap<>();

  @Override
  public Optional<Profile> getProfile(String modelName) {
    return Optional.ofNullable(this.profiles.get(modelName));
  }

  @Override
  public void addProfile(Profile profile) {
    this.profiles.put(profile.getModelName(), profile);
  }
}
