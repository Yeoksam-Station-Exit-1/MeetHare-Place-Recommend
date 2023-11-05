package yeoksamstationexit1.recommend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yeoksamstationexit1.recommend.dto.PriorityDTO;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "Priority")
public class Priority {

    @JsonProperty("user_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    @NotNull
    private int userID;

    private boolean quite;

    private String food;

    private String activity;

    private String culture;

    public Priority(boolean quite, String food, String activity, String culture) {
        this.activity = activity;
        this.culture = culture;
        this.food = food;
        this.quite = quite;
    }

    public void update(PriorityDTO priorityDTO) {
        this.activity = priorityDTO.getActivity();
        this.culture = priorityDTO.getCulture();
        this.food = priorityDTO.getFood();
        this.quite = priorityDTO.isQuite();
    }
}
