package Entites;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.apache.commons.pool.BasePoolableObjectFactory;
public class GetChannel extends BasePoolableObjectFactory<Channel>{
  public Connection connection;

  public GetChannel(Connection connection) {
    this.connection = connection;
  }

  @Override
  public Channel makeObject() throws Exception {
    return this.connection.createChannel();
  }
}
