package Entites;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SwiperDetails {
  private String swiper;
  private String swipee;
  private String comment;

  public SwiperDetails() {}

  public SwiperDetails(String swiper, String swipee, String comment) {
    this.swiper = swiper;
    this.swipee = swipee;
    this.comment = comment;
  }
}
