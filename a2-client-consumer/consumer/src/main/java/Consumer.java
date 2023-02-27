import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Consumer {

  private static final int NUM_THREADS = 10;
  private static final Map<String, List<String>> HASH_MAP = new ConcurrentHashMap<>();
  private static final String QUEUE = "ResultMQ";

  public static void main(String[] args) {
    ConnectionFactory connectionFactory = new ConnectionFactory();
    connectionFactory.setHost("localhost");
//    connectionFactory.setHost("172.31.10.32");
    connectionFactory.setUsername("guest");
    connectionFactory.setPassword("guest");
    try {
      Connection connection = connectionFactory.newConnection();
      ExecutorService threadPool = Executors.newFixedThreadPool(NUM_THREADS);
      for (int i = 0; i < NUM_THREADS; i++) {
        threadPool.execute(new SingleThread(connection, QUEUE, HASH_MAP));
      }
    } catch (TimeoutException | IOException e) {
      e.printStackTrace();
    }

  }

}
