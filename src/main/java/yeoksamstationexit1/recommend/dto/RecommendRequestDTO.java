package yeoksamstationexit1.recommend.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RecommendRequestDTO {

    @JsonProperty("station_id")
    private int stationId;

    @JsonProperty("user_list")
    private List<Integer> userList;

    private LocalDate date;

    @JsonProperty("final_time")
    private long finalTime;
}
