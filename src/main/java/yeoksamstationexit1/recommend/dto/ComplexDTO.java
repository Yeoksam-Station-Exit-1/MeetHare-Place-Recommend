package yeoksamstationexit1.recommend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ComplexDTO {

    @JsonProperty("place_num")
    private int placeNum;

    private String name;

    private float grade;

    private String address;

    private String detail;
}
