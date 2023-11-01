package yeoksamstationexit1.recommend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import yeoksamstationexit1.recommend.entity.Station;
import yeoksamstationexit1.recommend.repository.StationRepository;
import yeoksamstationexit1.recommend.util.Selenium;

import java.util.List;

@Service
public class SeleniumService {
    private final StationRepository stationRepository;
    private final Selenium selenium;

    @Autowired
    public SeleniumService(StationRepository stationRepository, Selenium selenium) {
        this.stationRepository = stationRepository;
        this.selenium = selenium;
    }

    @Scheduled(cron = "0 0 0 * * 1", zone = "Asia/Seoul") // 매주 월요일 0시 0분 0초
    public void createPlace() throws InterruptedException {
        List<Station> stations = stationRepository.findAll();
        System.out.println("Start!");
        System.out.println(stations.size());
        for(Station station : stations) {
            System.out.println(station.getName());
            selenium.createPlaceDetail(station, "식당"); // 카테고리 차후 수정
        }
        System.out.println("End!");
    }
}
