package yeoksamstationexit1.recommend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RecommendRequestDTO {

    @JsonProperty("station_id")
    private int stationId;

    @JsonProperty("user_list")
    private List<Long> userList;

//    private LocalDate date;

    private String category;
}
