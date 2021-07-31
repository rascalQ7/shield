import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import profile.ProfileService;
import request.RequestService;

@ExtendWith(MockitoExtension.class)
class RequestBatchTest {
  private static final ObjectMapper mapper = new ObjectMapper();

  @Mock
  private ProfileService profileService;
  @Mock
  private RequestService requestService;

  private RequestBatch requestBatch;
  private Stream<String> events;

  @BeforeEach
  void setUp() {
    this.requestBatch = new RequestBatch(this.profileService, this.requestService);

    this.events = List.of(
        "{\"type\": \"profile_create\", \"model_name\": \"Thinkpad\", \"default\": \"allow\", \"whitelist\": [], \"blacklist\": [], \"timestamp\": 1563951287812}",
        "{\"type\": \"profile_update\", \"model_name\": \"iPhone\", \"blacklist\": [1563951307799], \"timestamp\": \"bad_site.com\"}",
        "{\"type\": \"request\", \"request_id\": \"00ee3fde22f24384a21fbfcd2ba2ddc8\", \"model_name\": \"iPhone\", \"device_id\": \"d7c29fd60cee4e5f848bd198bcc2e100\", \"url\": \"bbc.co.uk\", \"timestamp\": 1563951288854}"
    ).stream();
  }

  @Test
  void shouldExecuteRequestsInOrder() {
    InOrder inOrder = inOrder(this.profileService, this.requestService);

    this.requestBatch.execute(this.events);

    inOrder.verify(this.profileService).createProfile(any());
    inOrder.verify(this.profileService).updateProfile(any());
    inOrder.verify(this.requestService).verify(any());
  }
}