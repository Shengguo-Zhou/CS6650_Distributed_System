import event.SwipeEvent;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;

public class GenerateSingleBody {
  private static int SWIPER_NUMBER = 5000;
  private static int SWIPEE_NUMBER = 1000_000;

  public GenerateSingleBody() {}

  public SwipeEvent getBody(){
    SwipeEvent swipeEvent = new SwipeEvent();
    swipeEvent = generateBody(swipeEvent);

    Random rd = new Random();
    if(rd.nextBoolean()){
      swipeEvent.setLeftOrRight("left");
    }
    else{
      swipeEvent.setLeftOrRight("right");
    }
    return swipeEvent;
  }

  private SwipeEvent generateBody(SwipeEvent swipeEvent){
    int swiper = (int) (Math.random() * SWIPER_NUMBER + 1);
    int swipee = (int) (Math.random() * SWIPEE_NUMBER + 1);
    int length = 256;
    boolean useLetters = true;
    boolean useNumbers = false;
    String comment = RandomStringUtils.random(length, useLetters, useNumbers);
    swipeEvent.setSwiper(String.valueOf(swiper));
    swipeEvent.setSwipee(String.valueOf(swipee));
    swipeEvent.setComment(comment);
    return swipeEvent;
  }

}
