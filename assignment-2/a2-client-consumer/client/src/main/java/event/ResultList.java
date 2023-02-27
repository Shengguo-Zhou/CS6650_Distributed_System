package event;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultList {
  private List<Result> resultList = new ArrayList<>();

  public ResultList() {}

  public ResultList(List<Result> resultList) {
    this.resultList = resultList;
  }

  public synchronized void addResult(List<Result> result){
    if(result == null || result.size() == 0) return;
    this.resultList.addAll(result);
  }
}
