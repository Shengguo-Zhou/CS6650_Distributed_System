package Entities;

import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LikeAndDislike {
  private AtomicInteger likeNumber;
  private AtomicInteger dislikeNumber;

  public LikeAndDislike() {
    this.likeNumber = new AtomicInteger(0);
    this.dislikeNumber = new AtomicInteger(0);
  }

  public LikeAndDislike(AtomicInteger likeNumber, AtomicInteger dislikeNumber) {
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
