package event;

import java.util.Collections;
import java.util.List;

public class ResultProcess {
  private List<Result> resultList;

  public ResultProcess(List<Result> resultList) {
    this.resultList = resultList;
    Collections.sort(this.resultList, (a, b) ->
       (int) (a.getLatency() - b.getLatency())
    );
  }

  public double mean() {
    if(resultList.size() == 0) return 0.0;
    long sum = 0;
    for (Result result : this.resultList) sum += result.getLatency();
    return (double) sum / (double)(this.resultList.size());
  }

  public double median() {
    if(resultList.size() == 0) return 0.0;
    return resultList.get(resultList.size() / 2).getLatency();
  }

  public double get99p() {
    if(resultList.size() == 0) return 0.0;
    return resultList.get((int)Math.floor(resultList.size()*0.99)).getLatency();
  }

  public double min() {
    if(resultList.size() == 0) return 0.0;
    return resultList.get(0).getLatency();
  }

  public double max() {
    if(resultList.size() == 0) return 0.0;
    return resultList.get(resultList.size() - 1).getLatency();
  }


}
