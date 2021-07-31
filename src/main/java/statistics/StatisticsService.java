package statistics;

import com.fasterxml.jackson.databind.JsonNode;
import request.Request;

/**
 *
 */
public interface StatisticsService {

  /**
   * logs blocked request
   */
  void logBlock(Request request);

  /**
   * logs allowed request
   */
  void logAllow(Request request);

  /**
   * logs blocked request and sends device to quarantine
   */
  void logQuarantine(Request request);

  /**
   * According to change reads history and logs missed blocks and false positive events
   */
  void logProfileChange(JsonNode event);

  /**
   * Displays logged statistics for:
   *   protected_devices, suspicious_devices, missed, false_positive
   */
  void displayStatistics();
}
