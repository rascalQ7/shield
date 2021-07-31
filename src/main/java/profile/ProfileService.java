package profile;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Service responsible for profile creation and update
 */
public interface ProfileService {
  void createProfile(Profile profile);
  void updateProfile(JsonNode profileUpdate);
}
