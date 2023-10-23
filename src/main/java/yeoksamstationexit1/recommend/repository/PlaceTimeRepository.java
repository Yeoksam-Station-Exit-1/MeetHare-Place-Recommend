package yeoksamstationexit1.recommend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yeoksamstationexit1.recommend.entity.PlaceTime;

import java.util.List;

public interface PlaceTimeRepository extends JpaRepository<PlaceTime, Integer> {

    @Query("SELECT pt FROM PlaceTime pt WHERE pt.place.placeNum = :placeNum")
    List<PlaceTime> findAllByPlaceNum(@Param("placeNum") Integer placeNum);
}
