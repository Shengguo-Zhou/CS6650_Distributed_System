package event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SwipeEvent {
  private String swiper;
  private String swipee;
  private String comment;
  private String leftOrRight;

  public SwipeEvent() {
  }

  public SwipeEvent(String swiper, String swipee, String comment, String leftOrRight) {
    this.swiper = swiper;
    this.swipee = swipee;
    this.comment = comment;
    this.leftOrRight = leftOrRight;
  }

}
