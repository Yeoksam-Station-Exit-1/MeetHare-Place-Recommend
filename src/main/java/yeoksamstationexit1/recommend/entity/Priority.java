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

    private byte food;

    private String activity;

    private byte culture;

    public Priority(boolean quite, int food, String activity, int culture) {
        this.activity = activity;
        this.culture = (byte) culture;
        this.food = (byte) food;
        this.quite = quite;
    }

    public void update(PriorityDTO priorityDTO) {
        this.activity = priorityDTO.getActivity();
        this.culture = (byte) priorityDTO.getCulture();
        this.food = (byte) priorityDTO.getFood();
        this.quite = priorityDTO.isQuite();
    }
}
