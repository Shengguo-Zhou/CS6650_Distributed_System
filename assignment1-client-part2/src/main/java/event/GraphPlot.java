package event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

@Getter
public class GraphPlot{
  private List<Integer> list;

  public GraphPlot(ResultList resultList) {
    this.list = getRequestsPerSecond(resultList);
  }

  private List<Integer> getRequestsPerSecond(ResultList resultList){
    Collections.sort(resultList.getResultList(), (a, b) ->
        (int) (a.getStartTime() - b.getStartTime()));
    List<Integer> numberList = new ArrayList<>();
    long startTime = resultList.getResultList().get(0).getStartTime();
    int curCount = 0;
    for(Result result : resultList.getResultList()){
      if(result.getStartTime() - startTime < 1000) curCount++;
      else{
        numberList.add(curCount);
        startTime = result.getStartTime();
        curCount = 0;
      }
    }
    numberList.add(curCount);
    return numberList;
  }

}









