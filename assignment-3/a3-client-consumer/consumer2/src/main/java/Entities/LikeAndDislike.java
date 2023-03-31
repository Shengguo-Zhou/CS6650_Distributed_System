package Entities;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LikeAndDislike {
  private AtomicInteger likeNumber = new AtomicInteger(0);
  private AtomicInteger dislikeNumber = new AtomicInteger(0);

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