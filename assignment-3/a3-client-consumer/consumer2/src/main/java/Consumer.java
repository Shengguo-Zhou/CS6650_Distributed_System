import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Consumer {

  private static final int NUM_THREADS = 200;
  private static final ConcurrentHashMap<String, List<String>> HASH_MAP2 = new ConcurrentHashMap<>();
  private static final String QUEUE = "ResultMQ2";
  private static final String EXCHANGE = "SwipeExchange";
  private static JedisPool jedisPool;

  public static void main(String[] args) {
    ConnectionFactory connectionFactory = new ConnectionFactory();
//    connectionFactory.setHost("localhost");
//    connectionFactory.setUsername("guest");
//    connectionFactory.setPassword("guest");

    connectionFactory.setHost("54.201.155.64");
    connectionFactory.setVirtualHost("cherry_broker");
    connectionFactory.setUsername("user");
    connectionFactory.setPassword("user");

    jedisPool = new JedisPool("34.219.132.175", 6379);
    JedisPoolConfig config = new JedisPoolConfig();
    config.setMaxTotal(200);
    config.setBlockWhenExhausted(true);
    config.setMaxIdle(100);
    jedisPool.setConfig(config);

    try {
      Connection connection = connectionFactory.newConnection();
      ExecutorService threadPool = Executors.newFixedThreadPool(NUM_THREADS);
      for (int i = 0; i < NUM_THREADS; i++) {
        threadPool.execute(new SingleThread(connection, QUEUE, EXCHANGE, HASH_MAP2, jedisPool));
      }
    } catch (TimeoutException | IOException e) {
      e.printStackTrace();
    }
  }

}
