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
  private static Integer numThreads = 10;
  private static Integer totalCount = 500;

  public static void main(String[] args) throws InterruptedException {
    System.out.println("Start from here");

    IP = "http://localhost:8080/server_a2_war/skiers/";
//    IP = "http://ec2-54-244-204-254.us-west-2.compute.amazonaws.com:8080/server-a2_war/skiers/";
    queue = new LinkedBlockingQueue<>();
    winCount = new AtomicInteger(0);
    loseCount = new AtomicInteger(0);
    CountDownLatch latch1 = new CountDownLatch(numThreads);
    long start = System.currentTimeMillis();
    BodyList bodyList = new BodyList(queue, totalCount);
    bodyList.generateList();
    ResultList resultList = new ResultList();

    for (int i = 0; i < numThreads; i++) {
      RunInOneThread r1 = new RunInOneThread(IP, queue, winCount, loseCount, latch1, resultList);
      Thread thread1 = new Thread(r1);
      thread1.start();
      System.out.println(i);
    }

    latch1.await();
    long end = System.currentTimeMillis();
    long lastTime = end - start;
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
  }

}