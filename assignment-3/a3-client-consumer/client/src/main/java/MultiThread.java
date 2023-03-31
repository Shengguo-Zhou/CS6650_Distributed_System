import event.GraphPlot;
import event.OutputCSV;
import event.ResultList;
import event.ResultProcess;
import event.SwipeEvent;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThread {
  private static String IP;
  private static BlockingQueue<SwipeEvent> queue;
  private static AtomicInteger winCount;
  private static AtomicInteger loseCount;
  private static Integer numThreads = 200;
  private static Integer totalCount = 500_000;

  private static AtomicInteger numberOfSuccessGetCount;
  private static AtomicInteger numberOfLostCount;

  public static void main(String[] args) throws InterruptedException {
    System.out.println("Start from here");

//    IP = "http://localhost:8080/server_a2_war_exploded/skiers/";
    IP = "http://52.10.99.67:8080/server-a2_war/skiers/";
//    IP = "http://a3-lb-709645041.us-west-2.elb.amazonaws.com:8080/server-a2_war/skiers/";
    queue = new LinkedBlockingQueue<>();
    winCount = new AtomicInteger(0);
    loseCount = new AtomicInteger(0);

    numberOfSuccessGetCount = new AtomicInteger(0);
    numberOfLostCount = new AtomicInteger(0);

    CountDownLatch latch1 = new CountDownLatch(numThreads);
    CountDownLatch latchGet = new CountDownLatch(1);

    long start = System.currentTimeMillis();
    long startGet = System.currentTimeMillis();

    BodyList bodyList = new BodyList(queue, totalCount);
    bodyList.generateList();
    ResultList resultList = new ResultList();
    ResultList resultListGet = new ResultList();

    for (int i = 0; i < numThreads; i++) {
      RunInOneThread r1 = new RunInOneThread(IP, queue, winCount, loseCount, latch1, resultList);
      Thread thread1 = new Thread(r1);
      thread1.start();
      System.out.println(i);
    }

    // receive response from server
    for(int i = 0; i < 1; i++){
      ReceiveThread receiveThread = new ReceiveThread(IP, queue, numberOfSuccessGetCount, numberOfLostCount, latchGet, resultListGet, latch1);
      Thread threadGet = new Thread(receiveThread);
      threadGet.start();
      System.out.println("Get Thread is started");

    }

    latch1.await();
    latchGet.await();
//    threadGet.interrupt();

    long end = System.currentTimeMillis();
    long endGet = System.currentTimeMillis();
    long lastTime = end - start;
    long lastTimeGet = endGet - startGet;
//    OutputCSV outputCSV = new OutputCSV();
//    try {
//      outputCSV.outPutCSVToPath(resultList.getResultList(),
//          "/Users/shengguozhou/Desktop/6650distributed_system/");
//    } catch (IOException e) {
//      e.printStackTrace();
//    }

    GraphPlot graphPlot = new GraphPlot(resultList);
    List<Integer> timeCount = graphPlot.getList();
//    try {
//      outputCSV.outPutCSVToPath2(timeCount,
//          "/Users/shengguozhou/Desktop/6650distributed_system/");
//    } catch (IOException e) {
//      e.printStackTrace();
//    }

    ResultProcess resultProcess = new ResultProcess(resultList.getResultList());
    double mean = resultProcess.mean();
    double median = resultProcess.median();
    double p99 = resultProcess.get99p();
    double max = resultProcess.max();
    double min = resultProcess.min();

    System.out.println();
    System.out.println();
    System.out.println("This is the End");
    System.out.println("Number of successful requests:" + winCount.get());
    System.out.println("Number of failed requests:" + loseCount.get());
    System.out.println("Total lasting time: " + lastTime);
    System.out.println( "Throughput: " + (int)((winCount.get() + loseCount.get()) /
        (double)(lastTime / 1000) )+ " requests/second");
    System.out.println("Mean time: " + mean);
    System.out.println("Median response time: " + median);
    System.out.println("99th percentile response time: " + p99);
    System.out.println("Max response time: " + max);
    System.out.println("Min response time: " + min);

    ResultProcess resultProcessGet = new ResultProcess(resultListGet.getResultList());
    double meanGet = resultProcessGet.mean();
    double medianGet = resultProcessGet.median();
    double p99Get = resultProcessGet.get99p();
    double maxGet = resultProcessGet.max();
    double minGet = resultProcessGet.min();
    System.out.println();
    System.out.println();
    System.out.println("This is the End in receiving data");
    System.out.println("Number of successful requests in receiving data:" + numberOfSuccessGetCount.get());
    System.out.println("Number of failed requests in receiving data:" + numberOfLostCount.get());
    System.out.println("Total lasting time in receiving data: " + lastTimeGet);
    System.out.println( "Throughput in receiving data: " + (int)((numberOfSuccessGetCount.get() + numberOfLostCount.get()) /
        (double)(lastTimeGet / 1000) )+ " requests/second");
    System.out.println("Mean time in receiving data: " + meanGet);
    System.out.println("Median response time in receiving data: " + medianGet);
    System.out.println("99th percentile response time in receiving data: " + p99Get);
    System.out.println("Max response time in receiving data: " + maxGet);
    System.out.println("Min response time in receiving data: " + minGet);
    System.exit(0);
  }

}