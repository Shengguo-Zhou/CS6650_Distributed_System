import com.google.gson.*;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import Entities.SwiperDetails;
import Entities.LikeAndDislike;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

public class SingleThread implements Runnable{
  Gson gson = new Gson();
  private final Connection connection;
  private final String queue;
  private final String exchange;
  private final ConcurrentHashMap<String, LikeAndDislike> hashmap1;
  private final JedisPool jedisPool;

  public SingleThread(Connection connection, String queue, String exchange,
      ConcurrentHashMap<String, LikeAndDislike> hashmap1, JedisPool jedisPool) {
    this.connection = connection;
    this.queue = queue;
    this.exchange = exchange;
    this.hashmap1 = hashmap1;
    this.jedisPool = jedisPool;
  }

  @Override
  public void run() {
    try {
      Channel channel = connection.createChannel();
      channel.exchangeDeclare(exchange, BuiltinExchangeType.FANOUT);
      channel.queueDeclare(queue, false, false, false, null);
      channel.queueBind(queue, exchange, "");
      System.out.println("Thread " + Thread.currentThread().getId() + " waiting for messages.");

      channel.basicQos(1);

      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), "UTF-8");
        SwiperDetails swiperDetails = gson.fromJson(message, SwiperDetails.class);
        System.out.println("Received: " + message);
        try {
           GetData(swiperDetails);
        } finally {
          System.out.println("Finished");
          channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        }
      };

      channel.basicConsume(queue, false, deliverCallback, consumerTag -> { });

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void GetData(SwiperDetails swiperDetails) {
    String userId = swiperDetails.getSwiper();
    String leftOrRight = swiperDetails.getLeftOrRight();
    boolean contain = false;
    if (!hashmap1.containsKey(userId)) {
      hashmap1.put(userId, new LikeAndDislike());
      contain = true;
    }

    if (leftOrRight.equalsIgnoreCase("left")) {
      AtomicInteger ret = new AtomicInteger(hashmap1.get(userId).getDislikeNumber().incrementAndGet());
      hashmap1.get(userId).setDislikeNumber(ret);
    } else {
      AtomicInteger ret = new AtomicInteger(hashmap1.get(userId).getLikeNumber().incrementAndGet());
      hashmap1.get(userId).setLikeNumber(ret);
    }


    int[] likeDislike = new int[] {hashmap1.get(userId).getLikeNumber().get(), hashmap1.get(userId).getDislikeNumber().get()};
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.select(0);
      String s = new String("[" + likeDislike[0] + ", " + likeDislike[1] + "]");
      if(contain){
        jedis.sadd(userId, s);
      } else {
        jedis.set(userId, s);
      }
    } catch (JedisException e) {
      // Handle the exception
    }

  }

}
