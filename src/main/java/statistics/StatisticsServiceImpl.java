package statistics;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import request.Request;

public class StatisticsServiceImpl implements StatisticsService {

  private static final String BLOCK = "block";
  private static final String ALLOW = "allow";
  private static final String MODEL_NAME_FIELD = "model_name";
  private Set<String> suspiciousDevices = new HashSet<>();
  private Set<String> protectedDevices = new HashSet<>();
  private HashMap<String, Integer> actionsStatistics = new HashMap<>();
  private Integer missed = 0;
  private Integer falsePositive = 0;
  private List<String> actions = new ArrayList<>();


  @Override
  public void logBlock(Request request) {
    this.protectedDevices.add(request.deviceId());
    logAction(request, BLOCK);
  }

  @Override
  public void logAllow(Request request) {
    logAction(request, ALLOW);
  }

  @Override
  public void logQuarantine(Request request) {
    this.protectedDevices.add(request.deviceId());
    this.suspiciousDevices.add(request.deviceId());

    logAction(request, BLOCK, true);
  }

  @Override
  public void logProfileChange(JsonNode event) {
    if (event.at("/blacklist").isMissingNode()) {
      collectStatisticsForWhitelistHistory(event);
    } else {
      collectStatisticsForBlacklistHistory(event);
    }
  }

  @Override
  public void displayStatistics() {
    this.actions.forEach(System.out::println);
    System.out.println(
        "{\"protected_devices\": " + this.protectedDevices.size() +
            ", \"suspicious_devices\": " + this.suspiciousDevices.size() +
            ", \"missed\": " + this.missed +
            ", \"false_positive\": " + this.falsePositive + "}"
    );
  }

  private void collectStatisticsForBlacklistHistory(JsonNode event) {
    this.missed += calculateHistoryByRecord(
        event.get(MODEL_NAME_FIELD).asText(),
        (ArrayNode) event.get("blacklist"),
        ALLOW);
    this.falsePositive += calculateMissedRequests(event, BLOCK);
  }

  private void collectStatisticsForWhitelistHistory(JsonNode event) {
    this.falsePositive += calculateHistoryByRecord(
        event.get(MODEL_NAME_FIELD).asText(),
        (ArrayNode) event.get("whitelist"),
        BLOCK);
    this.missed += calculateMissedRequests(event, ALLOW);
  }

  private Integer calculateHistoryByRecord(
      String modelName,
      ArrayNode urls,
      String action
  ) {
    var count = new AtomicInteger();
    urls.forEach(url -> {
      var actionId = modelName + url.asText() + action;

      if (this.actionsStatistics.containsKey(actionId)) {
        count.addAndGet(this.actionsStatistics.get(actionId));
        this.actionsStatistics.remove(actionId);
      }
    });
    return count.get();
  }

  private Integer calculateMissedRequests(JsonNode event, String actionType) {
    var count = 0;
    for (Map.Entry<String, Integer> action : this.actionsStatistics.entrySet()) {
      if (action.getKey().startsWith(event.get(MODEL_NAME_FIELD).asText())
          && action.getKey().endsWith(actionType)) {
        this.actionsStatistics.put(action.getKey(), 0);
        count += this.actionsStatistics.get(action.getKey());
      }
    }

    return count;
  }

  private void logAction(Request request, String action) {
    logAction(request, action, false);
  }

  private void logAction(Request request, String action, boolean quarantine) {
    collectStatistic(request, action);
    collectRequestAction(request, action, quarantine);
  }

  private void collectRequestAction(Request request, String action, boolean quarantine) {
    this.actions.add("{\"request_id\": \"" + request.requestId() + "\", \"action\": "
        + "\"" + action + "\"}");
    if (quarantine) {
      this.actions.add("{\"device_id\": \"" + request.deviceId() + "\", \"action\": "
          + "\"quarantine\"}");
    }
  }

  private void collectStatistic(Request request, String action) {
    var actionId = request.modelName() + request.url() + action;
    if (this.actionsStatistics.containsKey(actionId)) {
      this.actionsStatistics.put(actionId, this.actionsStatistics.get(actionId) + 1);
    } else {
      this.actionsStatistics.put(actionId, 1);
    }
  }
}
