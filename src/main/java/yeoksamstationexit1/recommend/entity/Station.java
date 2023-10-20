package yeoksamstationexit1.recommend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "Station")
public class Station {

    @JsonProperty("station_id")
    @Id
    @NotNull
    @Column(name = "station_id")
    private int StationId;

    @NotNull
    private String name;

    @NotNull
    @JsonProperty("infra_count")
    @Column(name = "infra_count")
    private int infraCount;
}
