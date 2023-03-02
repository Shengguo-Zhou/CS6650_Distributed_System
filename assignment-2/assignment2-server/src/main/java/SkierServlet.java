import Entites.SwiperBody;
import Entites.SwiperDetails;
import Entites.GetChannel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.BufferedReader;
import java.util.concurrent.TimeoutException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

@WebServlet(name = "SkierServlet", value = "/SkierServlet")
public class SkierServlet extends HttpServlet {

  private Gson gson = new Gson();
  private ObjectPool<Channel> pool;
  private String QUEUE = "ResultMQ";
  private String EXCHANGE = "SwipeExchange";

  @Override
  public void init() {
    ConnectionFactory connectionFactory = new ConnectionFactory();
    connectionFactory.setHost("localhost");
    connectionFactory.setUsername("guest");
    connectionFactory.setPassword("guest");
//    connectionFactory.setHost("54.245.192.226");
//    connectionFactory.setVirtualHost("cherry_broker");
//    connectionFactory.setUsername("user");
//    connectionFactory.setPassword("user");
    Connection connection;
    try {
      connection = connectionFactory.newConnection();
      pool = new GenericObjectPool<>(new GetChannel(connection));
    } catch (IOException | TimeoutException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    String urlPath = request.getPathInfo();
    String[] urls = urlPath.split("/");

    if (!isUrlValid(urls, response)) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    } else  {
      response.setStatus(HttpServletResponse.SC_OK);
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    String urlPath = request.getPathInfo();
    String[] urlParts = urlPath.split("/");

    if (!isUrlValid(urlParts, response)) {
      response.getWriter().write("Failed in valid check, line 63\n");
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } else {
      processRequest(request, response, urlParts[2]);
      response.getWriter().write("\nAssignment 2 doPost is working.\n");
//      response.getWriter().write(urlParts[2]); // left or right
      response.setStatus(HttpServletResponse.SC_CREATED);
    }
  }

  private boolean isUrlValid(String[] urlPath, HttpServletResponse response) throws IOException {

    if (urlPath == null || urlPath.length <= 2) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().write("URL Path is null, missing parameters");
      return false;
    }

    if(!urlPath[1].equalsIgnoreCase("swipe")) {
      response.getWriter().write("First URL String is not swipe");
      response.getWriter().write(urlPath[1]);
      return false;
    }
    if(!urlPath[2].equalsIgnoreCase("left") &&
        !urlPath[2].equalsIgnoreCase("right")) {
      response.getWriter().write("Second URL String is not left or right");
      return false;
    }
//    response.getWriter().write("Inside the isUrl check\n");
    return true;
  }

  protected void processRequest(HttpServletRequest request, HttpServletResponse response, String leftOrRight)
      throws IOException {

    response.setContentType("application/json");
    Gson gson = new Gson();
    ObjectMapper objectMapper = new ObjectMapper();
    StringBuilder sb = new StringBuilder();
    BufferedReader bufferedReader = request.getReader();
    while (bufferedReader.ready()) {
      sb.append(bufferedReader.readLine());
    }

    try{
      SwiperDetails swiperDetails = objectMapper.readValue(sb.toString(), SwiperDetails.class);
      SwiperBody swiperBody = new SwiperBody(swiperDetails, leftOrRight);
      response.getWriter().print("Inside processRequest\n");
      response.getWriter().print(gson.toJson(swiperBody));
      response.setStatus(HttpServletResponse.SC_CREATED);
      Channel cur;
      try {
        cur = pool.borrowObject();
        cur.exchangeDeclare(EXCHANGE, BuiltinExchangeType.FANOUT);
//        cur.queueDeclare(QUEUE, false, false, false, null);
        String send = gson.toJson(swiperBody);
        cur.basicPublish(EXCHANGE, "", null, send.getBytes());
//        cur.basicPublish("", QUEUE, null, send.getBytes());
        pool.returnObject(cur);
      } catch (Exception e) {
        e.printStackTrace();
        response.getWriter().write("MQ meets error\n");
      }
    } catch (Exception e){
      response.getWriter().write("Handle json file error\n");
    }

  }

}
