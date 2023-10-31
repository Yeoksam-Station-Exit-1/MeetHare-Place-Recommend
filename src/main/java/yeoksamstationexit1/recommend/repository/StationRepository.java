package yeoksamstationexit1.recommend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yeoksamstationexit1.recommend.entity.Station;

public interface StationRepository extends JpaRepository<Station, Integer> {
}
