package Entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LikeAndDislike {
  private Integer likeNumber;
  private Integer dislikeNumber;

  public LikeAndDislike() {
  }

  public LikeAndDislike(Integer likeNumber, Integer dislikeNumber) {
    this.likeNumber = likeNumber;
    this.dislikeNumber = dislikeNumber;
  }

  @Override
  public String toString() {
    return "LikeAndDislike{" +
        "likeNumber=" + likeNumber +
        ", dislikeNumber=" + dislikeNumber +
        '}';
  }
}
