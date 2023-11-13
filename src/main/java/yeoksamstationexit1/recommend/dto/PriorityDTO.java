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
    private long userID;

    private boolean quite;

    private String food;

    private String activity;

    private String culture;

    public Priority toPriority() {
        return new Priority(this.userID, this.quite, this.food, this.activity, this.culture);
    }

    public PriorityDTO(Priority priority) {
        this.userID = priority.getUserID();
        this.quite = priority.isQuite();
        this.food = priority.getFood();
        this.activity = priority.getActivity();
        this.culture = priority.getCulture();
    }
}
