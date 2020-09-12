package camel.msg.adapter.data;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MsgB implements Serializable {

  private String txt;
  private String createdDt; // Дата формирования сообщения format: rfc3339
  private Integer currentTemp; // Температура по Цельсию

}
