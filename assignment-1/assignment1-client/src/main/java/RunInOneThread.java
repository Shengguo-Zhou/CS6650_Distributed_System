import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;

import event.SwipeEvent;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SwipeApi;
import io.swagger.client.model.SwipeDetails;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class RunInOneThread implements Runnable {
  private int MAX_RETRY = 5;
  String ipAddress;
  BlockingQueue<SwipeEvent> queue;
  AtomicInteger successCallCount;
  AtomicInteger failCallCount;
  CountDownLatch latch;

  public RunInOneThread(String ipAddress,
      BlockingQueue<SwipeEvent> queue, AtomicInteger successCallCount,
      AtomicInteger failCallCount, CountDownLatch latch) {
    this.ipAddress = ipAddress;
    this.queue = queue;
    this.successCallCount = successCallCount;
    this.failCallCount = failCallCount;
    this.latch = latch;
  }

  @Override
  public void run() {
    SwipeApi api = new SwipeApi();
    api.getApiClient().setBasePath(this.ipAddress);
    int winCount = 0;
    int loseCount = 0;

    while (true) {
      if(queue.isEmpty()) break;
      SwipeEvent event = queue.poll();
      try {
        if(sentEvent(api, event)) winCount++;
        else loseCount++;
      } catch (ApiException e) {
        e.printStackTrace();
      }
    }

    this.successCallCount.getAndAdd(winCount);
    this.failCallCount.getAndAdd(loseCount);
    this.latch.countDown();
  }

  private boolean sentEvent(SwipeApi api, SwipeEvent event) throws ApiException {
    long startTime = System.currentTimeMillis();
    int time = 0;

    SwipeDetails swipeDetails = new SwipeDetails();
    swipeDetails.setSwiper(event.getSwiper());
    swipeDetails.setSwipee(event.getSwipee());
    swipeDetails.setComment(event.getComment());

    while (time < MAX_RETRY){
      try{
        ApiResponse<Void> response = api.swipeWithHttpInfo(swipeDetails, event.getLeftOrRight());

        if(response.getStatusCode() == HTTP_OK || response.getStatusCode() == HTTP_CREATED) {
          long endTime = System.currentTimeMillis();
          System.out.println(endTime - startTime);
          return true;
        }

        if (response.getStatusCode() / 100 >= 4) {
          time++;
        }

      } catch (ApiException e){
        time++;
        e.printStackTrace();
      }
    }
    return false;
  }

}
