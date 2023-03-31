package Entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SwiperDetails {
  private String swiper = null;
  private String swipee = null;
  private String comment = null;
  private String leftOrRight = null;

  public SwiperDetails(String swiper, String swipee, String comment, String leftOrRight) {
    this.swiper = swiper;
    this.swipee = swipee;
    this.comment = comment;
    this.leftOrRight = leftOrRight;
  }
}