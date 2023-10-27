package yeoksamstationexit1.recommend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import yeoksamstationexit1.recommend.entity.Priority;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PriorityDTO {

    @JsonProperty("user_id")
    private int userID;

    private boolean quite;

    private int food;

    private String activity;

    private int culture;

    public Priority toPriority() {
        return new Priority(this.quite, this.food, this.activity, this.culture);
    }
}
