package request;

/**
 * Request handler
 */
public interface RequestService {

  /**
   * Verifies request and displays result
   * @param request - http request from specific device
   */
  void verify(Request request);
}
