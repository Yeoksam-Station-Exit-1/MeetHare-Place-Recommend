package yeoksamstationexit1.recommend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yeoksamstationexit1.recommend.dto.PlaceDTO;
import yeoksamstationexit1.recommend.entity.Place;
import yeoksamstationexit1.recommend.repository.PlaceRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PlaceService {
    private final PlaceRepository placeRepository;

    @Autowired
    public PlaceService(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    public Map<String, List<PlaceDTO>> getSimplePlaceListByStationNum(Integer stationNum) {
        List<Place> placeList = placeRepository.findByStationId(stationNum);
        List<PlaceDTO> activityList = new ArrayList<>();
        List<PlaceDTO> cultureList = new ArrayList<>();
        List<PlaceDTO> restaurantList = new ArrayList<>();
        List<PlaceDTO> studyList = new ArrayList<>();
        List<PlaceDTO> etcList = new ArrayList<>();

        for (Place place : placeList) {
            switch (place.getCategory()) {
                case "activity":
                    activityList.add(new PlaceDTO(place));
                    break;
                case "culture":
                    cultureList.add(new PlaceDTO(place));
                    break;
                case "restaurant":
                    restaurantList.add(new PlaceDTO(place));
                    break;
                case "study":
                    studyList.add(new PlaceDTO(place));
                    break;
                default:
                    etcList.add(new PlaceDTO(place));
            }
        }

        Map<String, List<PlaceDTO>> returnMap = new HashMap<>();
        returnMap.put("activity", activityList);
        returnMap.put("culture", cultureList);
        returnMap.put("restaurant", restaurantList);
        returnMap.put("study", studyList);
        returnMap.put("etc", etcList);
        return returnMap;
    }
}
