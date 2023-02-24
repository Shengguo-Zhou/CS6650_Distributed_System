package event;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class OutputCSV {

  public OutputCSV() {}

  public void outPutCSVToPath(List<Result> resultList, String path) throws IOException {
    File csv = new File(path + "a1_result.csv");
    if(csv.exists()) csv.delete();
    FileWriter writer = new FileWriter(csv);
    writer.write("StartTime(mod 100000),Type,Latency,ResponseCode\n");
    for(Result result: resultList) writer.write(result.toString() + "\n");
    writer.close();
  }
  public void outPutCSVToPath2(List<Integer> list, String path) throws IOException {
    File csv = new File(path + "a1_time_result.csv");
    if(csv.exists()) csv.delete();
    FileWriter writer = new FileWriter(csv);
    writer.write("time\n");
    for(int result: list) writer.write(result + "\n");
    writer.close();
  }

}
