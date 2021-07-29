import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import request.RequestBatch;

public class App {

  public static void main(String[] args) {

    try (Stream<String> eventsStream = Files.lines(Paths.get("src/main/resources/input.json"))) {
      new RequestBatch(eventsStream).execute();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
