package yeoksamstationexit1.recommend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import yeoksamstationexit1.recommend.entity.Place;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Integer> {

    @Query("SELECT p FROM Place p WHERE p.station.StationId = ?1 ORDER BY p.grade DESC")
    List<Place> findByStationId(Integer stationNum);
}
