package yeoksamstationexit1.recommend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "Place")
public class Place {

    @JsonProperty("place_num")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Column(name = "place_num")
    private int placeNum;

    @NotNull
    private String name;

    @NotNull
    private float grade;

    @NotNull
    private String address;

    @NotNull
    private String category;

    @JsonProperty("station_id")
    @NotNull
    @OneToOne
    @JoinColumn(name = "station_id")
    private Station station;
}
