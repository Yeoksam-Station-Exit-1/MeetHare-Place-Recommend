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
@Table(name = "Place_Time")
public class PlaceTime {

    @Id
    @NotNull
    private int id;

    @JsonProperty("place_num")
    @OneToOne
    @JoinColumn(name = "place_num")
    @NotNull
    @Cascade(DELETE)
    private Place place;

    @NotNull
    private byte day;

    @NotNull
    private long time;

}