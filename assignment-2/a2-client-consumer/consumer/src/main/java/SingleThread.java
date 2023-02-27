import com.google.gson.*;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import Entities.SwiperDetails;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SingleThread implements Runnable{
  Gson gson = new Gson();
  private final Connection connection;
  private final String queue;
  private final Map<String, List<String>> hashmap;

  public SingleThread(Connection connection, String queue, Map<String, List<String>> hashmap) {
    this.connection = connection;
    this.queue = queue;
    this.hashmap = hashmap;
  }

  @Override
  public void run() {
    try {
      Channel channel = connection.createChannel();
      channel.queueDeclare(queue, false, false, false, null);
      System.out.println("Thread " + Thread.currentThread().getId() + " waiting for messages.");

      channel.basicQos(1);

      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), "UTF-8");
        SwiperDetails swiperDetails = gson.fromJson(message, SwiperDetails.class);
        System.out.println("Received '" + message + "'");
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
    String like = swiperDetails.getComment();
    if (!hashmap.containsKey(userId)) {
      hashmap.put(userId, Collections.synchronizedList(new ArrayList<>()));
    }
    hashmap.get(userId).add(like);
    System.out.println("\nhashmap size is: ");
    System.out.println(hashmap.size());
  }

}
