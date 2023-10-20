package yeoksamstationexit1.recommend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;

import static org.hibernate.annotations.CascadeType.DELETE;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "Study")
public class Study {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private int id;

    @JsonProperty("place_num")
    @OneToOne
    @JoinColumn(name = "place_num")
    @NotNull
    @Cascade(DELETE)
    private Place place;

    @NotNull
    @Column(columnDefinition = "TINYINT(1)")
    private boolean quite;
}
