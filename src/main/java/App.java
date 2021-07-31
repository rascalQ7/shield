import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import profile.ProfileService;
import profile.ProfileServiceImpl;
import profile.ProfilesCacheService;
import profile.ProfilesCacheServiceImpl;
import request.RequestBatch;
import request.RequestService;
import request.RequestServiceImpl;
import statistics.StatisticsService;
import statistics.StatisticsServiceImpl;

public class App {

  public static void main(String[] args) {
    ProfilesCacheService profilesCacheService = new ProfilesCacheServiceImpl();
    StatisticsService statisticsService = new StatisticsServiceImpl();

    ProfileService profileService = new ProfileServiceImpl(profilesCacheService, statisticsService);
    RequestService requestService = new RequestServiceImpl(profilesCacheService, statisticsService);

    try (Stream<String> eventsStream = Files.lines(Paths.get("src/main/resources/input.json"))) {
      new RequestBatch(
          eventsStream,
          profileService,
          requestService
      ).execute();
    } catch (IOException e) {
      e.printStackTrace();
    }

    statisticsService.displayStatistics();
  }
}
