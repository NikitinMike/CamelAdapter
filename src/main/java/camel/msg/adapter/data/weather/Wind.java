package camel.msg.adapter.data.weather;

import lombok.Data;

@Data
public class Wind {
    float speed;
    float deg;
    float gust;
}
