package yeoksamstationexit1.recommend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import yeoksamstationexit1.recommend.entity.Place;
import yeoksamstationexit1.recommend.entity.PlaceTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PlaceDetailDTO {

    @JsonProperty("place_num")
    private int placeNum;

    @JsonProperty("place_name")
    private String name;

    @JsonProperty("place_grade")
    private float grade;

    @JsonProperty("place_address")
    private String address;

    @JsonProperty("place_time")
    private Map<Integer, String> time;

    @JsonProperty("place_detail")
    private String detail;

    @JsonProperty("place_img_url")
    private String imgUrl;

    public PlaceDetailDTO(Place place, List<PlaceTime> placeTimeList) {
        this.placeNum = place.getPlaceNum();
        this.name = place.getName();
        this.grade = place.getGrade();
        this.address = place.getAddress();
        this.detail = place.getDetail();
        this.imgUrl = place.getImgUrl();
        this.time = generateTime(placeTimeList);
    }

    public Map<Integer, String> generateTime(List<PlaceTime> placeTimeList) {
        Map<Integer, String> timeList = new HashMap<>();
        StringBuilder sb;
        int startCnt, rangeCnt;
        for (PlaceTime placeTime : placeTimeList) {
            long binaryTime = placeTime.getTime();
            String binaryString = Long.toBinaryString(binaryTime);
            sb = new StringBuilder();
            startCnt = 0;
            rangeCnt = 0;
            for (int i = binaryString.length() - 1; i >= 0; --i) {
                if (binaryString.charAt(i) == '1') rangeCnt++;
                else {
                    if (rangeCnt == 0) startCnt++;
                    else {
                        String operatePeriod = generatePeriod(startCnt, rangeCnt);
                        if (sb.toString().isEmpty())
                            sb.append(operatePeriod);
                        else sb.append(", ").append(operatePeriod);
                        startCnt += rangeCnt;
                        startCnt++;
                        rangeCnt = 0;
                    }
                }
            }
            if (rangeCnt != 0) {
                String operatePeriod = generatePeriod(startCnt, rangeCnt);
                if (sb.toString().isEmpty())
                    sb.append(operatePeriod);
                else sb.append(", ").append(operatePeriod);
            }
            timeList.put((int) placeTime.getDay(), sb.toString());
        }
        return timeList;
    }

    public String generatePeriod(int startCnt, int rangeCnt) {
        return startCnt / 2 + ":" + (startCnt % 2 == 0 ? "00" : "30") + " ~ " + (startCnt + rangeCnt) / 2 + ":" + ((startCnt + rangeCnt) % 2 == 0 ? "00" : "30");
    }
}
