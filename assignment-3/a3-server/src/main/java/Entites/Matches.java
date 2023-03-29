package Entites;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Matches {

  private List<String> matchList;

  public Matches(List<String> matchList){
    this.matchList = matchList;
  }

}
