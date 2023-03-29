package Entites;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MatchStats {

  private Integer numLlikes = null;
  private Integer numDislikes = null;

  public MatchStats(Integer numLlikes, Integer numDislikes){
    this.numDislikes = numDislikes;
    this.numLlikes = numLlikes;
  }
}
