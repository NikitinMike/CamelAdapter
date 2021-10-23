package camel.msg.adapter.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MsgA implements Serializable {

    @NotBlank
    private String msg;
    @NotBlank
    @Pattern(regexp = "ru", message = "Only 'ru'")
    private String lng; // enum: [ru, en, es]
    private Coordinates coordinates;

}
