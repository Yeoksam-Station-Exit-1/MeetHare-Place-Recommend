package yeoksamstationexit1.recommend.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yeoksamstationexit1.recommend.dto.PlaceDTO;
import yeoksamstationexit1.recommend.service.PlaceService;
import yeoksamstationexit1.recommend.util.ErrorHandler;

import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@Api(tags = "Place")
@RequestMapping("/place")
public class PlaceRecommendController {
    private final ErrorHandler errorHandler;
    private final PlaceService placeService;

    @Autowired
    public PlaceRecommendController(ErrorHandler errorHandler, PlaceService placeService) {
        this.errorHandler = errorHandler;
        this.placeService = placeService;
    }


    @ApiOperation(value = "비로그인 장소 추천", notes = "비로그인 상황에서 장소 추천 제공")
    @GetMapping("/simple/{stationNum}")
    public ResponseEntity<?> getMachineStatus(@PathVariable Integer stationNum) {
        try {
            Map<String, List<PlaceDTO>> list = placeService.getSimplePlaceListByStationNum(stationNum);
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (Exception e) {
            return errorHandler.errorMessage(e);
        }
    }
}