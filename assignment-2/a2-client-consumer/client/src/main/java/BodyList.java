import event.SwipeEvent;
import java.util.concurrent.BlockingQueue;

public class BodyList{

  private BlockingQueue<SwipeEvent> eventList;
  public int totalNumberNeedToSend;

  public BodyList(BlockingQueue<SwipeEvent> eventList, int totalNumberNeedToSend) {
    this.eventList = eventList;
    this.totalNumberNeedToSend = totalNumberNeedToSend;
  }

  public void generateList() {
    for(int i = 0; i < totalNumberNeedToSend; i++){
      GenerateSingleBody generateSingleBody = new GenerateSingleBody();
      SwipeEvent swipeEvent = generateSingleBody.getBody();
      eventList.offer(swipeEvent);
    }

  }



}
