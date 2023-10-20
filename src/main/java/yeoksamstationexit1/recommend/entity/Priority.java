package yeoksamstationexit1.recommend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private int food;

    private String activity;

    private int culture;
}
