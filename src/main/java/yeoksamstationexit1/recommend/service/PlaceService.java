package yeoksamstationexit1.recommend.service;

<<<<<<< HEAD
import org.springframework.beans.factory.annotation.Autowired;
=======
import lombok.RequiredArgsConstructor;
>>>>>>> 60ff7e504617376f0479edff4c715688c2cec16f
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yeoksamstationexit1.recommend.dto.ComplexDTO;
import yeoksamstationexit1.recommend.dto.PlaceDTO;
import yeoksamstationexit1.recommend.dto.PlaceDetailDTO;
import yeoksamstationexit1.recommend.dto.RecommendRequestDTO;
import yeoksamstationexit1.recommend.entity.Place;
import yeoksamstationexit1.recommend.entity.PlaceTime;
import yeoksamstationexit1.recommend.entity.Priority;
import yeoksamstationexit1.recommend.repository.PlaceRepository;
import yeoksamstationexit1.recommend.repository.PlaceTimeRepository;
import yeoksamstationexit1.recommend.repository.PriorityRepository;
import yeoksamstationexit1.recommend.util.DataNotFoundException;

import java.util.*;

@Transactional
@RequiredArgsConstructor
@Service
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final PlaceTimeRepository placeTimeRepository;
    private final PriorityRepository priorityRepository;

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
        List<ComplexDTO> complexDTOList = placeRepository.findComplexDTOByStationAndDayAndCategory(recommendRequestDTO.getStationId(), (byte) recommendRequestDTO.getDate().getDayOfWeek().getValue(), recommendRequestDTO.getCategory());
        List<Priority> priorityList = priorityRepository.findAllById(recommendRequestDTO.getUserList());
        List<PlaceDTO> finalList = new ArrayList<>();
        List<Integer> idxList = new ArrayList<>();
        String preference;
        boolean quite;
        for (int i = 0; i < complexDTOList.size(); ++i) {
            ComplexDTO complexDTO = complexDTOList.get(i);
            loop:
            for (Priority priority : priorityList) {
                switch (recommendRequestDTO.getCategory()) {
                    case "restaurant":
                        preference = priority.getFood();
                        if (complexDTO.getDetail().contains(preference)) {
                            idxList.add(i);
                            finalList.add(new PlaceDTO(complexDTO));
                        }
                        break loop;
                    case "culture":
                        preference = priority.getCulture();
                        if (complexDTO.getDetail().contains(preference)) {
                            idxList.add(i);
                            finalList.add(new PlaceDTO(complexDTO));
                        }
                        break loop;
                    case "activity":
                        preference = priority.getActivity();
                        if (complexDTO.getDetail().contains(preference)) {
                            idxList.add(i);
                            finalList.add(new PlaceDTO(complexDTO));
                        }
                        break loop;
                    case "study":
                        quite = priority.isQuite();
                        if (quite)
                            if (complexDTO.getDetail().contains("조용")) {
                                complexDTOList.remove(complexDTO);
                                finalList.add(new PlaceDTO(complexDTO));
                            }
                        break loop;
                }
            }
        }

        for (int i = 0; i < complexDTOList.size(); ++i)
            if (!idxList.contains(i))
                finalList.add(new PlaceDTO(complexDTOList.get(i)));

        return finalList;
    }
}
