package yeoksamstationexit1.recommend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yeoksamstationexit1.recommend.dto.ComplexDTO;
import yeoksamstationexit1.recommend.entity.Place;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Integer> {

    @Query("SELECT p FROM Place p WHERE p.station.StationId = :stationNum ORDER BY p.grade DESC, p.reviewCount DESC")
    List<Place> findByStationId(@Param("stationNum") Integer stationNum);

    @Query("SELECT new yeoksamstationexit1.recommend.dto.ComplexDTO(p.placeNum, p.name, p.grade,p.category, p.address, p.detail)" +
            "FROM Place p " +
            "WHERE p.station.StationId = :stationId AND p.category = :category ORDER BY p.grade DESC, p.reviewCount DESC")
    List<ComplexDTO> findComplexDTOByStationAndCategory(@Param("stationId") Integer stationId, @Param("category") String category);

    @Query("SELECT p FROM Place p WHERE p.station.StationId = :stationNum " +
            "AND p.name = :name")
    Optional<Place> findByNameAndStationId(@Param("name") String name, @Param("stationNum") Integer stationNum);
}
