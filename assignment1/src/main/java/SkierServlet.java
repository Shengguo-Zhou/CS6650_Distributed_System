import Entites.SwiperDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import java.io.BufferedReader;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "SkierServlet", value = "/SkierServlet")
public class SkierServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.setContentType("text/html");
    response.setStatus(HttpServletResponse.SC_OK);
    response.getWriter().write("Assignment 1 doGet is working");
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.setContentType("application/json");
    String urlPath = request.getPathInfo();

    // url null check
    if (urlPath == null || urlPath.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().write("URL Path is null, missing parameters");
      return;
    }

    String[] urlParts = urlPath.split("/");

    if (!isUrlValid(urlParts, response)) {
//      response.getWriter().write("failed in valid check");
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } else {
      //json;
      processRequest(request, response);
      response.getWriter().write("Assignment 1 doPost is working");
      response.setStatus(HttpServletResponse.SC_CREATED);
    }
  }

  private boolean isUrlValid(String[] urlPath, HttpServletResponse response) throws IOException {
    // urlPath  = "/1/seasons/2019/day/1/skier/123"
    // urlParts = [, 1, seasons, 2019, day, 1, skier, 123]
//    if(urlPath.length < 6) {
//      response.getWriter().write("too short");
//      return false;
//    }

    if(!urlPath[1].equalsIgnoreCase("swipe")) {
      response.getWriter().write("First String is not swipe");
      response.getWriter().write(urlPath[1]);
      return false;
    }
    if(!urlPath[2].equalsIgnoreCase("left") &&
        !urlPath[2].equalsIgnoreCase("right")) {
      response.getWriter().write("Second string is not left or right");
      return false;
    }

    return true;
  }

  protected void processRequest(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.setContentType("application/json");
    Gson gson = new Gson();
    ObjectMapper objectMapper = new ObjectMapper();
    //try catch
    StringBuilder sb = new StringBuilder();
    BufferedReader bufferedReader = request.getReader();
    while (bufferedReader.ready()){
      sb.append(bufferedReader.readLine());
    }

    try{
      SwiperDetails swiperDetails = objectMapper.readValue(sb.toString(), SwiperDetails.class);
//      response.getOutputStream().print(gson.toJson(swiperDetails));
      response.getWriter().print(gson.toJson(swiperDetails));
      response.setStatus(HttpServletResponse.SC_CREATED);
    } catch (Exception e){
      response.getWriter().write("Handle json file error");
    }

  }


}

