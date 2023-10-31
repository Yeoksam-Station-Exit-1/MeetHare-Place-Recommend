package yeoksamstationexit1.recommend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yeoksamstationexit1.recommend.dto.ComplexDTO;
import yeoksamstationexit1.recommend.entity.Place;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Integer> {

    @Query("SELECT p FROM Place p WHERE p.station.StationId = :stationNum ORDER BY p.grade DESC")
    List<Place> findByStationId(@Param("stationNum") Integer stationNum);

    @Query("SELECT p.placeNum, p.name, p.grade, p.address, pt.time FROM Place p " +
            "JOIN p.station s JOIN PlaceTime pt WHERE s.StationId = :stationId " +
            "AND pt.day = :day ORDER BY p.grade DESC")
    List<ComplexDTO> findPlaceAndTimeByStationAndDay(@Param("stationId") Integer stationId, @Param("day") Integer day);

    @Query("SELECT p FROM Place p WHERE p.station.StationId = :stationNum " +
            "AND p.name = :name")
    Optional<Place> findByNameAndStationId(@Param("name") String name, @Param("stationNum") Integer stationNum);
}
