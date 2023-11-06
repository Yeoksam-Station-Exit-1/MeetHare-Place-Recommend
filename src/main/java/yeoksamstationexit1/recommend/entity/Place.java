package yeoksamstationexit1.recommend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import lombok.Builder;
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
    private String detail;

    @NotNull
    @Column(columnDefinition = "VARCHAR(600)")
    private String imgUrl;

    @NotNull
    private String category;

    @NotNull
    @JsonProperty("review_count")
    @Column(name = "review_count")
    private int reviewCount;

    @JsonProperty("station_id")
    @NotNull
    @OneToOne
    @JoinColumn(name = "station_id")
    private Station station;

    @Builder
    public Place(String name, float grade, String address, String detail, String imgUrl, String category, int reviewCount, Station station) {
        this.name = name;
        this.grade = grade;
        this.address = address;
        this.detail = detail;
        this.imgUrl = imgUrl;
        this.category = category;
        this.reviewCount = reviewCount;
        this.station = station;
    }

    public Place update(float grade, String address, String detail, String imgUrl, int reviewCount, String category) {
        this.grade = grade;
        this.address = address;
        this.detail = detail;
        this.imgUrl = imgUrl;
        this.category = category;
        this.reviewCount = reviewCount;
        return this;
    }
}
