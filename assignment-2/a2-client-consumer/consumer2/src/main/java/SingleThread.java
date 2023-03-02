import com.google.gson.*;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import Entities.SwiperDetails;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SingleThread implements Runnable{
  Gson gson = new Gson();
  private final Connection connection;
  private final String queue;
  private final String exchange;
  private final ConcurrentHashMap<String, List<String>> hashmap2;

  public SingleThread(Connection connection, String queue, String exchange,
      ConcurrentHashMap<String, List<String>> hashmap2) {
    this.connection = connection;
    this.queue = queue;
    this.exchange = exchange;
    this.hashmap2 = hashmap2;
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
    String target = swiperDetails.getSwipee();
    String leftOrRight = swiperDetails.getLeftOrRight();

    if (!hashmap2.containsKey(userId)) {
      hashmap2.put(userId, Collections.synchronizedList(new ArrayList<>()));
    }

    if (leftOrRight.equalsIgnoreCase("right")) {
      if(hashmap2.get(userId).size() < 100) {
        hashmap2.get(userId).add(target);
      }
    }

  }

}
