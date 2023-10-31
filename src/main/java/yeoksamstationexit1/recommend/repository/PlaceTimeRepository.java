package yeoksamstationexit1.recommend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yeoksamstationexit1.recommend.entity.PlaceTime;

import java.util.List;
import java.util.Optional;

public interface PlaceTimeRepository extends JpaRepository<PlaceTime, Integer> {

    @Query("SELECT pt FROM PlaceTime pt WHERE pt.place.placeNum = :placeNum")
    List<PlaceTime> findAllByPlaceNum(@Param("placeNum") Integer placeNum);

    @Query("SELECT pt FROM PlaceTime pt WHERE pt.place.placeNum = :placeNum " +
            "AND pt.day = :day")
    Optional<PlaceTime> findByPlaceNumAndDay(@Param("placeNum") Integer placeNum, @Param("day") Integer day);
}
