package camel.msg.adapter.data;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coordinates implements Serializable {

  @NotBlank
  double latitude; // Широта
  @NotBlank
  double longitude; // Долгота

  @Override
  public String toString(){
    return String.format("lat=%s&lon=%s", latitude, longitude);
  }

}
