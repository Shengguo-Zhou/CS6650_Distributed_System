package event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Result {
  private long startTime;
  private long latency;
  private int responseCode;

  public Result() {
  }

  public Result(long startTime, long latency, int responseCode) {
    this.startTime = startTime;
    this.latency = latency;
    this.responseCode = responseCode;
  }

  public String toString() {
    return this.getStartTime() % 100_000 + ",POST," + this.getLatency() + "," + this.getResponseCode();
  }

}
