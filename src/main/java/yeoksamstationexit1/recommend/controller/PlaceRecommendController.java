package yeoksamstationexit1.recommend.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yeoksamstationexit1.recommend.dto.PlaceDTO;
import yeoksamstationexit1.recommend.dto.PlaceDetailDTO;
import yeoksamstationexit1.recommend.dto.PriorityDTO;
import yeoksamstationexit1.recommend.dto.RecommendRequestDTO;
import yeoksamstationexit1.recommend.service.PlaceService;
import yeoksamstationexit1.recommend.service.PriorityService;
import yeoksamstationexit1.recommend.util.ErrorHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@CrossOrigin("*")
@RestController
@Api(tags = "Place")
@RequestMapping("/place")
public class PlaceRecommendController {
    private final ErrorHandler errorHandler;
    private final PlaceService placeService;
    private final PriorityService priorityService;

    @ApiOperation(value = "비로그인 장소 추천", notes = "비로그인 상황에서 장소 추천 제공")
    @GetMapping("/simple/{stationNum}")
    public ResponseEntity<?> getSimplePlaceRecommend(@PathVariable Integer stationNum) {
        try {
            Map<String, List<PlaceDTO>> list = placeService.getSimplePlaceListByStationNum(stationNum);
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (Exception e) {
            return errorHandler.errorMessage(e);
        }
    }

    @ApiOperation(value = "로그인 장소 추천", notes = "로그인 상황에서 장소 추천 제공")
    @PostMapping("/complex")
    public ResponseEntity<?> getComplexPlaceRecommend(@RequestBody RecommendRequestDTO recommendRequestDTO) {
        try {
            List<PlaceDTO> list = placeService.getComplexPlaceRecommend(recommendRequestDTO);
            Map<String,List<PlaceDTO>> returnData = new HashMap<>();
            returnData.put(recommendRequestDTO.getCategory(),list);
            return new ResponseEntity<>(returnData, HttpStatus.OK);
        } catch (Exception e) {
            return errorHandler.errorMessage(e);
        }
    }

    @ApiOperation(value = "장소 세부 정보 조회", notes = "장소 세부 내용을 조회 시 화면 자료")
    @GetMapping("/detail/{placeNum}")
    public ResponseEntity<?> getPlaceDetail(@PathVariable Integer placeNum) {
        try {
            PlaceDetailDTO placeDetailDTO = placeService.getPlaceDetailByPlaceNum(placeNum);
            return new ResponseEntity<>(placeDetailDTO, HttpStatus.OK);
        } catch (Exception e) {
            return errorHandler.errorMessage(e);
        }
    }

    @ApiOperation(value = "선호도 조회", notes = "유저 아이디에 따른 선호 요소 조회")
    @GetMapping("/priority/{userId}")
    public ResponseEntity<?> getPriority(@PathVariable Long userId) {
        try {
            PriorityDTO priorityDTO = priorityService.getPriority(userId);
            return new ResponseEntity<>(priorityDTO,HttpStatus.OK);
        } catch (Exception e) {
            return errorHandler.errorMessage(e);
        }
    }
    @ApiOperation(value = "선호도 생성", notes = "회원가입 시 발생하는 선호도 저장")
    @PostMapping("/priority")
    public ResponseEntity<?> createPriority(@RequestBody PriorityDTO priorityDTO) {
        try {
            int result = priorityService.createPriority(priorityDTO);
            if (result == 1) return new ResponseEntity<>(HttpStatus.CREATED);
            else return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            return errorHandler.errorMessage(e);
        }
    }

    @ApiOperation(value = "선호도 갱신", notes = "회원정보 창에서 선호도 변경 후 저장")
    @PutMapping("/priority")
    public ResponseEntity<?> updatePriority(@RequestBody PriorityDTO priorityDTO) {
        try {
            priorityService.updatePriority(priorityDTO);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return errorHandler.errorMessage(e);
        }
    }

    @ApiOperation(value = "선호도 생성", notes = "회원 삭제 시 발생하는 선호도 삭제")
    @DeleteMapping("/priority")
    public ResponseEntity<?> deletePriority(@RequestBody Long userId) {
        try {
            priorityService.deletePriority(userId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return errorHandler.errorMessage(e);
        }
    }
}