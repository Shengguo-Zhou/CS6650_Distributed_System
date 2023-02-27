package Entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SwiperDetails {
  private String swiper;
  private String swipee;
  private String comment;
  private String leftOrRight;

  public SwiperDetails() {}

  public SwiperDetails(String swiper, String swipee, String comment, String leftOrRight) {
    this.swiper = swiper;
    this.swipee = swipee;
    this.comment = comment;
    this.leftOrRight = leftOrRight;
  }
}