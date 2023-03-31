import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.URLConnection.guessContentTypeFromStream;

import com.google.gson.Gson;
import event.Result;
import event.ResultList;
import event.SwipeEvent;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.MatchesApi;
import io.swagger.client.api.StatsApi;
import io.swagger.client.api.SwipeApi;
import io.swagger.client.model.MatchStats;
import io.swagger.client.model.Matches;
import io.swagger.client.model.SwipeDetails;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ReceiveThread implements Runnable{
  private int MAX_RETRY = 5;
  String ipAddress;
  AtomicInteger successCallCount;
  AtomicInteger failCallCount;
  CountDownLatch latch;
  ResultList resultListGet;
  BlockingQueue<SwipeEvent> queue;
  CountDownLatch postingThreadsStarted;
  private volatile boolean running  = true;
  Gson gson = new Gson();

  public ReceiveThread(String ipAddress, BlockingQueue<SwipeEvent> queue,
      AtomicInteger successCallCount, AtomicInteger failCallCount,
      CountDownLatch latch, ResultList resultListGet, CountDownLatch postingThreadsStarted) {
    this.ipAddress = ipAddress;
    this.queue = queue;
    this.successCallCount = successCallCount;
    this.failCallCount = failCallCount;
    this.latch = latch;
    this.resultListGet = resultListGet;
    this.postingThreadsStarted = postingThreadsStarted;
  }

  @Override
  public void run() {
    try{
      postingThreadsStarted.await();
    } catch (InterruptedException e){
      e.printStackTrace();
    }
    SwipeApi api = new SwipeApi();
    api.getApiClient().setBasePath(this.ipAddress);
    MatchesApi matchesApi = new MatchesApi();
    matchesApi.getApiClient().setBasePath(this.ipAddress);
    StatsApi statsApi = new StatsApi();
    statsApi.getApiClient().setBasePath(this.ipAddress);
    List<Result> curResultList = new ArrayList<>();
    int winCount = 0;
    int loseCount = 0;
    int count = 92;
//
//    while (true) {
//      if (queue.isEmpty()) break;
//      try {
//        if (receiveData(matchesApi, statsApi, curResultList)) winCount++;
//        else loseCount++;
//      } catch (ApiException e) {
//        e.printStackTrace();
//      }
//    }
    while(running){
      try{
        for(int i = 0; i < 5; i++){
          long startTime = System.currentTimeMillis();
          String randomUser = String.valueOf(ThreadLocalRandom.current().nextInt(1, 50000));
          System.out.println("Number: " + i);
          System.out.println("RandomNumber: " + randomUser);

          ApiResponse<Matches> matchesApiResponse = matchesApi.matchesWithHttpInfo(randomUser);
          System.out.println("MatchesApiResponse is: ");
          System.out.println(matchesApiResponse.getData().toString());

          ApiResponse<MatchStats> statsApiResponse = statsApi.matchStatsWithHttpInfo(randomUser);
          System.out.println("StatsApiResponse is ");
          System.out.println(statsApiResponse.getData().toString());

          winCount++;
          long endTime = System.currentTimeMillis();
          curResultList.add(new Result(startTime, endTime - startTime, 200));
          System.out.println("Last time is:" + (endTime - startTime));
        }
        Thread.sleep(1000);
//        if(postingThreadsStarted.getCount() == 0) running = false;
        if(count <= 0) running = false;
        count--;
      } catch (ApiException | InterruptedException e ){
        e.getCause().printStackTrace();
        e.printStackTrace();
        loseCount++;
      } catch (NullPointerException e){
        // do nothing
      }
//      running = false;
    }

    this.successCallCount.getAndAdd(winCount);
    this.failCallCount.getAndAdd(loseCount);
    this.latch.countDown();
    this.resultListGet.addResult(curResultList);

  }

  private boolean receiveData(MatchesApi matchesApi, StatsApi statsApi, List<Result> resultList) throws ApiException {


    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    executor.scheduleAtFixedRate(() -> {
      for (int i = 0; i < 5; i++) {

        // send request here
        long startTime = System.currentTimeMillis();
        String randomUser = String.valueOf(ThreadLocalRandom.current().nextInt(1, 5000));
        System.out.println("Number: " + i);
        try {
          ApiResponse<Matches> matchesApiResponse = matchesApi.matchesWithHttpInfo(randomUser);
          System.out.println("MatchesApiResponse: " + matchesApiResponse.toString());
        } catch (ApiException e) {
          e.printStackTrace();
        }

        try {
          ApiResponse<MatchStats> statsApiResponse = statsApi.matchStatsWithHttpInfo(randomUser);
          System.out.println("StatsApiResponse: " + statsApiResponse.toString());
        } catch (ApiException e) {
          e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        resultList.add(new Result(startTime, endTime - startTime, 200));
        System.out.println("Last time is:" + (endTime - startTime));
      }
    }, 0, 200, TimeUnit.MILLISECONDS);

    return true;
  }

}
