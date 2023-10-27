package yeoksamstationexit1.recommend.service;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yeoksamstationexit1.recommend.dto.ComplexDTO;
import yeoksamstationexit1.recommend.dto.PlaceDTO;
import yeoksamstationexit1.recommend.dto.PlaceDetailDTO;
import yeoksamstationexit1.recommend.dto.RecommendRequestDTO;
import yeoksamstationexit1.recommend.entity.Place;
import yeoksamstationexit1.recommend.entity.PlaceTime;
import yeoksamstationexit1.recommend.repository.PlaceRepository;
import yeoksamstationexit1.recommend.repository.PlaceTimeRepository;
import yeoksamstationexit1.recommend.util.DataNotFoundException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final PlaceTimeRepository placeTimeRepository;
    private final HttpService httpService;

    @Autowired
    public PlaceService(PlaceRepository placeRepository, PlaceTimeRepository placeTimeRepository, HttpService httpService) {
        this.placeRepository = placeRepository;
        this.placeTimeRepository = placeTimeRepository;
        this.httpService = httpService;
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

    public PlaceDetailDTO getPlaceDetailByPlaceNum(Integer placeNum) throws DataNotFoundException {
        Optional<Place> optionalPlaceDTO = placeRepository.findById(placeNum);
        List<PlaceTime> placeTimeList = placeTimeRepository.findAllByPlaceNum(placeNum);
        if (optionalPlaceDTO.isPresent())
            return new PlaceDetailDTO(optionalPlaceDTO.get(), placeTimeList);
        else
            throw new DataNotFoundException("It's a place that doesn't exist.");
    }

    public List<PlaceDTO> getComplexPlaceRecommend(RecommendRequestDTO recommendRequestDTO) {
        List<ComplexDTO> complexDTOList = placeRepository.findPlaceAndTimeByStationAndDay(recommendRequestDTO.getStationId(), recommendRequestDTO.getDate().getDayOfWeek().getValue());

//        List<Priority> priorityList = priorityRepository.findAllById(recommendRequestDTO.getUserList());
        List<PlaceDTO> suitableList = new ArrayList<>();
        long finalTime = recommendRequestDTO.getFinalTime();
        for (ComplexDTO complexDTO : complexDTOList) {
            if ((finalTime & complexDTO.getTime()) > 0) {
                suitableList.add(new PlaceDTO(complexDTO));
            }
        }

        return suitableList;
    }
}
