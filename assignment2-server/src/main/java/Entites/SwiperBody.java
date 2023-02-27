package Entites;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SwiperBody {

  private String swiper;
  private String swipee;
  private String comment;
  private String leftOrRight;

  public SwiperBody(SwiperDetails swiperDetails, String leftOrRight) {
    this.swiper = swiperDetails.getSwiper();
    this.swipee = swiperDetails.getSwipee();
    this.comment = swiperDetails.getComment();
    this.leftOrRight = leftOrRight;
  }

  public SwiperBody(String swiper, String swipee, String comment, String leftOrRight) {
    this.swiper = swiper;
    this.swipee = swipee;
    this.comment = comment;
    this.leftOrRight = leftOrRight;
  }

}
