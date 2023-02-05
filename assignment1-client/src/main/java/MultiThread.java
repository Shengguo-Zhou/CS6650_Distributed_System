import event.SwipeEvent;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThread {
  private static String IP;
  private static BlockingQueue<SwipeEvent> queue;
  private static AtomicInteger winCount;
  private static AtomicInteger loseCount;
  private static Integer numThreads = 16;
  private static final Integer totalCount = 8000;

  public static void main(String[] args) throws InterruptedException {
    System.out.println("Start from here");

    IP = "http://localhost:8080/assignment1_war_exploded/skiers/";
//    IP = "http://34.219.176.28:8080/assignment1_war/skiers/";
    queue = new LinkedBlockingQueue<>();
    winCount = new AtomicInteger(0);
    loseCount = new AtomicInteger(0);
    CountDownLatch latch1 = new CountDownLatch(numThreads);
    long start = System.currentTimeMillis();
    BodyList bodyList = new BodyList(queue, totalCount);
    bodyList.generateList();

    for (int i = 0; i < numThreads; i++) {
      RunInOneThread r1 = new RunInOneThread(IP, queue, winCount, loseCount, latch1);
      Thread thread1 = new Thread(r1);
      thread1.start();
      System.out.println(i);
    }

    latch1.await();

    long end = System.currentTimeMillis();
    long lastTime = end - start;

    System.out.println("End");
    System.out.println("Number of successful requests:" + winCount.get());
    System.out.println("Number of failed requests:" + loseCount.get());
    System.out.println("Total lasting time: " + lastTime);
    System.out.println( "Throughput: " + (int)((winCount.get() + loseCount.get()) /
        (double)(lastTime / 1000) )+ " requests/second");
  }

}
