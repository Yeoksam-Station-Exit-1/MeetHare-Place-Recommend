package yeoksamstationexit1.recommend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import yeoksamstationexit1.recommend.entity.Place;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PlaceDTO {

    @JsonProperty("place_num")
    private int placeNum;

    @JsonProperty("place_name")
    private String name;

    @JsonProperty("place_grade")
    private float grade;

    @JsonProperty("place_address")
    private String address;

    @JsonProperty("place_category")
    private String category;

    public PlaceDTO(Place place) {
        this.placeNum = place.getPlaceNum();
        this.name = place.getName();
        this.grade = place.getGrade();
        this.address = place.getAddress();
        this.category = place.getCategory();
    }
}
