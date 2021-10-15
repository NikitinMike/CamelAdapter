package camel.msg.adapter.data;

import java.io.Serializable;
import java.util.Date;

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

    public MsgB(String msg, Date date, Integer currentTemp) {
        txt = msg;
        this.currentTemp = currentTemp;
        createdDt = String.valueOf(date.getTime());
    }

    @Override
    public String toString() {
        return String.format("%s %s %d", txt, createdDt.substring(9), currentTemp);
    }

}
