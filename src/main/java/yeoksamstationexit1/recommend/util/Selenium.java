package yeoksamstationexit1.recommend.util;

import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yeoksamstationexit1.recommend.dto.PlaceDTO;
import yeoksamstationexit1.recommend.entity.Place;
import yeoksamstationexit1.recommend.entity.PlaceTime;
import yeoksamstationexit1.recommend.entity.Station;
import yeoksamstationexit1.recommend.repository.PlaceRepository;
import yeoksamstationexit1.recommend.repository.PlaceTimeRepository;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class Selenium {

    private final PlaceRepository placeRepository;
    private final PlaceTimeRepository placeTimeRepository;

    @Autowired
    public Selenium(PlaceRepository placeRepository, PlaceTimeRepository placeTimeRepository) {
        this.placeRepository = placeRepository;
        this.placeTimeRepository = placeTimeRepository;
    }

    /**
     * 장소들의 상세 정보 링크를 이용하여 상세 정보 저장
     */
    public void createPlaceDetail(Station station, String category) throws InterruptedException {
        String keyword = null;
        if(category.equals("restaurant")) {
            keyword = "식당";
        } else if(category.equals("study")) {
            keyword = "스터디카페";
        } else if(category.equals("activity")) {
            keyword = "운동시설";
        } else if(category.equals("culture")) {
            keyword = "문화시설";
        }
        
        List<String> links = getPlaceDetailByCrawling(station.getName(), keyword); // 검색해서 나온 링크

        Path path = Paths.get("src/main/resources/chromedriver.exe"); // 크롬 드라이버 경로
        System.setProperty("webdriver.chrome.driver", path.toString()); // WebDriver 경로 설정

        ChromeOptions options = new ChromeOptions(); // 크롬 드라이버 옵션 설정
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-popup-blocking");   // 팝업 안띄움
        options.addArguments("--disable-gpu");  // gpu 비활성화

        for (String link : links) {
            WebDriver driverinfo = new ChromeDriver(options);
            WebDriverWait wait = new WebDriverWait(driverinfo, Duration.ofSeconds(10));    // 드라이버가 실행된 후 10초 기다림

            driverinfo.get(link);
            driverinfo.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS); // 페이지 전체가 로딩될때까지 기다림

            try {
                wait.until(
                        ExpectedConditions.presenceOfElementLocated(By.cssSelector("#_title > span.Fc1rA"))
                ); // 상호명이 뜰 때까지 최대 20초 대기

                String name = placeName(driverinfo); // 상호명
                System.out.println(name);
//            String placeCategory = placeCategory(driverinfo); // 카테고리

                wait.until(
                        ExpectedConditions.presenceOfElementLocated(By.cssSelector("#app-root > div > div > div > div > div > div > div > div > div.O8qbU.tQY7D > div > a > span.LDgIH"))
                );
                String address = placeAddress(driverinfo); // 주소

                Object[] review = placeReview(driverinfo); // 별점, 방문자 리뷰 수
                float grade = (float) review[0]; // 별점
                int reviewCnt = (int) review[1]; // 방문자 리뷰 수

                wait.until(
                        ExpectedConditions.presenceOfElementLocated(By.cssSelector("#app-root > div > div > div > div.CB8aP > div"))
                ); // 이미지 뜰 때까지 대기
                String imgUrl = placeImgUrl(driverinfo); // 이미지 링크

                Object[] timeAndDetail = placeTimeAndDetail(driverinfo); // 영업 시간, 상세 정보
                String detail = (String) timeAndDetail[0]; // 상세 정보
                System.out.println(detail);

                Optional<Place> optionalPlace = placeRepository.findByNameAndStationId(name, station.getStationId()); // 상호명, 역과 일치하는 장소
                if (optionalPlace.isEmpty()) { // 장소가 없으면 장소 생성 및 저장
                    Place newPlace = Place.builder()
                            .name(name)
                            .grade(grade)
                            .address(address)
                            .detail(detail)
                            .imgUrl(imgUrl)
                            .category(category)
                            .reviewCount(reviewCnt)
                            .station(station)
                            .build(); // Place 객체 생성
                    placeRepository.save(newPlace); // 저장

                    for (int i = 1; i < timeAndDetail.length; i++) {
                        PlaceTime newPlaceTime = PlaceTime.builder()
                                .place(newPlace)
                                .day((byte) i)
                                .time((Long) timeAndDetail[i])
                                .build(); // PlaceTime 객체 생성
                        placeTimeRepository.save(newPlaceTime); // 저장
                    }
                } else { // 장소가 있으면 업데이트
                    Place newPlace = optionalPlace
                            .map(entity -> entity.update(grade, address, detail, imgUrl, reviewCnt, category))
                            .orElse(Place.builder().build());
                    placeRepository.save(newPlace);
                    for (int i = 1; i < timeAndDetail.length; i++) {
                        Optional<PlaceTime> optionalPlaceTime = placeTimeRepository.findByPlaceNumAndDay(newPlace.getPlaceNum(), (byte) i);

                        final int finalI = i;
                        PlaceTime newPlaceTime = optionalPlaceTime
                                .map(entity -> entity.update(newPlace, (byte) finalI, (Long) timeAndDetail[finalI]))
                                .orElse(PlaceTime.builder().build());
                        placeTimeRepository.save(newPlaceTime);
                    }
                }

                driverinfo.quit(); // 종료
            } catch (Exception e) {
                driverinfo.quit();
                continue;
            }
        }
    }

    /**
     * 상호명
     */
    public String placeName(WebDriver driver) {
        return driver.findElement(By.cssSelector("#_title > span.Fc1rA")).getText();
    }

    /**
     * 카테고리
     */
//    public String placeCategory(WebDriver driver) {
//        String category = driver.findElement(By.cssSelector("#_title > span.DJJvD")).getText();
//        return category;
//    }

    /**
     * 주소
     */
    public String placeAddress(WebDriver driver) {
        return driver.findElement(By.cssSelector("#app-root > div > div > div > div > div > div > div > div > div.O8qbU.tQY7D > div > a > span.LDgIH")).getText();
    }

    /**
     * 별점, 방문자 리뷰 수
     */
    public Object[] placeReview(WebDriver driver) {
        float rating; // 별점
        int reviewCnt; // 방문자 리뷰 수
        try {
            WebElement reviewinfo = driver.findElement(By.cssSelector("#app-root > div > div > div > div.place_section.no_margin.OP4V8 > div.zD5Nm.undefined > div.dAsGb"));

            if(reviewinfo.getAttribute("innerHTML").isEmpty()) { // 리뷰 정보가 전혀 없는 경우
                rating = 0.0f;
                reviewCnt = 0;
            } else {
                // 리뷰 정보가 있는 경우
                WebElement firstElement = driver.findElement(By.cssSelector("#app-root > div > div > div > div.place_section.no_margin.OP4V8 > div.zD5Nm.undefined > div.dAsGb > span:nth-child(1)")); // 첫 번째 요소
                String firstElementAttribute = firstElement.getAttribute("class");

                if(firstElementAttribute.contains("LXIwF")) { // 별점이 있는 경우
                    rating = Float.parseFloat(firstElement.getText().replaceAll("[^0-9.]", "")); // 별점 float로 저장
                    WebElement visitCntElement = driver.findElement(By.cssSelector("#app-root > div > div > div > div.place_section.no_margin.OP4V8 > div.zD5Nm.undefined > div.dAsGb > span:nth-child(2)")); // 두 번째 요소
                    String visit = visitCntElement.getText();
                    String[] parts = visit.split(" ");

                    if(visit.contains("방문자리뷰")) { // 방문자리뷰인 경우
                        reviewCnt = Integer.parseInt(parts[1].replace(",", ""));
                    } else { // 방문자리뷰 아닌 경우
                        reviewCnt = 0;
                    }
                } else { // 별점이 없는 경우
                    rating = 0.0f;
                    String visit = firstElement.getText();
                    String[] parts = visit.split(" ");

                    if(visit.contains("방문자리뷰")) { // 방문자리뷰인 경우
                        reviewCnt = Integer.parseInt(parts[1].replace(",", ""));
                    } else { // 방문자리뷰 아닌 경우
                        reviewCnt = 0;
                    }
                }
            }
        } catch (NoSuchElementException e) {
            rating = 0.0f;
            reviewCnt = 0;
        }
        return new Object[] {rating, reviewCnt};
    }

    /**
     * 이미지 url
     */
    public String placeImgUrl(WebDriver driver) {
        String imgUrl = null;
        try {
            WebElement imageElement = driver.findElement(By.cssSelector("#app-root > div > div > div > div.CB8aP > div"));
            String imageClassAttribute = imageElement.getAttribute("class");

            if(imageClassAttribute.contains("uDR4i")) { // 업체 사진 등록된 경우
                WebElement imageLinkElement = driver.findElement(By.cssSelector("#_autoPlayable > div"));
                String imageStyle = imageLinkElement.getAttribute("style");
                String regex = "url\\((.*?)\\)";

                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(imageStyle);

                if(matcher.find()) {
                    imgUrl = matcher.group(1);
                    imgUrl = imgUrl.replace("\"", "");
                }
            } else { // 업체 사진 등록 안 된 경우
                imgUrl = "등록된 사진 없음";
            }
        } catch (NoSuchElementException e) {
            imgUrl = "등록된 사진 없음";
        }
        return imgUrl;
    }

    /**
     * 영업 시간, 상세 정보
     */
    public Object[] placeTimeAndDetail(WebDriver driver) throws InterruptedException {
        String detail = "정보 없음"; // 상세 정보
        Map<Integer, Long> times = new HashMap<>(); // 운영 시간
        String defaultTime = convertTimeToBits("12:00 - 18:00"); // 기본 시간

        List<WebElement> infos = driver.findElements(By.cssSelector("#app-root > div > div > div > div > div > div.place_section.no_margin > div > div > div.O8qbU")); // 정보 목록

        for(int num = 0; num < infos.size(); num++) {
            WebElement infoElement = infos.get(num); // 현재 정보 요소
            String infoClassAttribute = infoElement.getAttribute("class"); // 클래스 이름
            if(infoClassAttribute.contains("jKv4e")) continue; // 세스코멤버스 기호
            else if(infoClassAttribute.contains("NqeGY")) continue; // 플레이스 관리 권유 버튼

            try {
                WebElement titleElement = infoElement.findElement(By.cssSelector("strong > span.place_blind")); // 정보 종류
                if(titleElement.getText().equals("설명")) { // 장소에 대한 설명인 경우
                    WebElement descriptionElement = infoElement.findElement(By.cssSelector("span.zPfVt"));
                    if(descriptionElement.getAttribute("innerText").trim().isEmpty()) { // 설명이 없을 경우
                        detail = "정보 없음";
                    } else {
                        descriptionElement.click();
                        Thread.sleep(300);
                        detail = descriptionElement.getText();
                        if(detail.length() > 250) { // 만약 설명의 글자가 250자를 넘을 경우
                            detail = detail.substring(0, 250) + "...";
                        }
                    }
                } else if(titleElement.getText().contains("영업시간")) { // 영업 시간에 대한 설명인 경우
                    if(infoClassAttribute.contains("J1zN9")) { // 영업 시간이 등록되지 않은 경우 - 월 ~ 일 모두 동일한 시간 임의로 부여
                        for (int day = 1; day <= 7; day++) {
                            times.put(day, Long.parseLong(defaultTime, 2));
                        }
                        break;
                    } else if(infoClassAttribute.contains("pSavy")) { // 운영시간이 등록된 경우
                        WebElement timeinfoElement = infoElement.findElement(By.cssSelector("div.y6tNq"));
                        timeinfoElement.click(); // 클릭
                        Thread.sleep(300); // 대기

                        List<WebElement> timeElement = infoElement.findElements(By.cssSelector("div.vV_z_ > a > div.w9QyJ")); // 요일별 운영시간 정보

                        if(timeElement.get(1).findElement(By.cssSelector("div.y6tNq > span.A_cdD > span.i8cJw")).getText().contains("매일")) { // 운영시간이 "매일"로 등록된 경우
                            for(WebElement time : timeElement) {
                                if (time.getAttribute("class").contains("vI8SM") || time.getAttribute("class").contains("DzD3b")) continue; // 오늘 운영시간 정보인 경우
                                else if (time.getAttribute("class").contains("yN6TD")) continue; // 매장 휴무 정보인 경우
                                else {
                                    for (int day = 1; day <= 7; day++) {
                                        times.put(day, Long.parseLong(convertTimeToBits(time.findElement(By.cssSelector("div.y6tNq > span.A_cdD > div.H3ua4")).getText()), 2));
                                    }
                                    break;
                                }
                            }
                        } else {
                            for(WebElement time : timeElement) {
                                if (time.getAttribute("class").contains("vI8SM") || time.getAttribute("class").contains("DzD3b")) continue; // 오늘 운영시간 정보인 경우
                                else if (time.getAttribute("class").contains("yN6TD")) continue; // 매장 휴무 정보인 경우
                                else {
                                    String day = time.findElement(By.cssSelector("div.y6tNq > span.A_cdD > span.i8cJw")).getText();

                                    if(day.charAt(0) == '월') {
                                        times.put(1, Long.parseLong(convertTimeToBits(time.findElement(By.cssSelector("div.y6tNq > span.A_cdD > div.H3ua4")).getText()), 2));
                                    } else if(day.charAt(0) == '화') {
                                        times.put(2, Long.parseLong(convertTimeToBits(time.findElement(By.cssSelector("div.y6tNq > span.A_cdD > div.H3ua4")).getText()), 2));
                                    } else if(day.charAt(0) == '수') {
                                        times.put(3, Long.parseLong(convertTimeToBits(time.findElement(By.cssSelector("div.y6tNq > span.A_cdD > div.H3ua4")).getText()), 2));
                                    } else if(day.charAt(0) == '목') {
                                        times.put(4, Long.parseLong(convertTimeToBits(time.findElement(By.cssSelector("div.y6tNq > span.A_cdD > div.H3ua4")).getText()), 2));
                                    } else if(day.charAt(0) == '금') {
                                        times.put(5, Long.parseLong(convertTimeToBits(time.findElement(By.cssSelector("div.y6tNq > span.A_cdD > div.H3ua4")).getText()), 2));
                                    } else if(day.charAt(0) == '토') {
                                        times.put(6, Long.parseLong(convertTimeToBits(time.findElement(By.cssSelector("div.y6tNq > span.A_cdD > div.H3ua4")).getText()), 2));
                                    } else if(day.charAt(0) == '일') {
                                        times.put(7, Long.parseLong(convertTimeToBits(time.findElement(By.cssSelector("div.y6tNq > span.A_cdD > div.H3ua4")).getText()), 2));
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (NoSuchElementException e) {
                continue;
            }
        }

        if(times.size() == 0) {
            for (int day = 1; day <= 7; day++) {
                times.put(day, Long.parseLong(defaultTime, 2));
            }
        }

        Object[] result = new Object[8];
        result[0] = detail;
        for(int i = 1; i <= 7; i++) {
            result[i] = times.get(i);
        }
        return result;
    }

    /**
     * 장소에 대한 상세 정보 링크들 selenium crawling 통해 가져오기
     */
    public List<String> getPlaceDetailByCrawling(String station, String keyword) throws InterruptedException {
        String mapUrl = "https://map.naver.com/p/search/"; // 네이버 지도 검색 url
        List<String> links = new ArrayList<>(); // 링크
        String searchKeyword = station + " " + keyword;
        String encodedKeyword = URLEncoder.encode(searchKeyword, StandardCharsets.UTF_8); // 인코딩
        String searchUrl = mapUrl + encodedKeyword + "?c=15.00,0,0,0,dh"; // 탐색할 링크

        Path path = Paths.get("src/main/resources/chromedriver.exe"); // 크롬 드라이버 경로
        System.setProperty("webdriver.chrome.driver", path.toString()); // WebDriver 경로 설정

        ChromeOptions options = new ChromeOptions(); // 크롬 드라이버 옵션 설정
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-popup-blocking");   // 팝업 안띄움
        options.addArguments("--disable-gpu");  // gpu 비활성화

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(20)); // 드라이버가 실행된 후 20초 기다림
        driver.get(searchUrl);

        try {
            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS); // 페이지 전체가 로딩될때까지 기다림
            driver.switchTo().frame(driver.findElement(By.cssSelector("#searchIframe"))); // 검색 목록으로 frame 이동

            webDriverWait.until(
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.XUrfU"))
            );
            if(driver.findElement(By.cssSelector("div.XUrfU")).getText().contains("조건에 맞는 업체가 없습니다")) { // 검색 결과가 없을 때 - 아무 것도 하지 않음
            } else {
                webDriverWait.until(
                        ExpectedConditions.presenceOfElementLocated(By.cssSelector("#_pcmap_list_scroll_container > ul > li"))
                ); // 해당 요소 찾을 때 까지 최대 20초 대기

                // 스크롤 다운 (네이버 지도의 경우 한 페이지에 50개의 장소가 뜸 - 광고까지 54개)
                for(int j = 0; j <= 5; j++) {
                    List<WebElement> scrolls = driver.findElements(By.cssSelector("#_pcmap_list_scroll_container > ul > li")); // 장소 목록 저장
                    WebElement lastElement = scrolls.get(scrolls.size() - 1); // 장소 목록 중 마지막 장소 요소 저장
                    ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView();", lastElement); // 마지막 장소가 위에 올때까지 스크롤
                    Thread.sleep(500);
                }

                List<WebElement> contents = driver.findElements(By.cssSelector("#_pcmap_list_scroll_container > ul > li"));

                String nameElementSelector = null;
                if(keyword.equals("식당")) {
                    nameElementSelector = "#_pcmap_list_scroll_container > ul > li > div.CHC5F > a > div > div > span.place_bluelink.TYaxT";
                } else if(keyword.equals("스터디카페") || keyword.equals("운동시설") || keyword.equals("문화시설")) {
                    nameElementSelector = "#_pcmap_list_scroll_container > ul > li > div > div > a > div > div > span.place_bluelink";
                    String nameElementClass = driver.findElement(By.cssSelector(nameElementSelector)).getAttribute("class");
                    if(nameElementClass.contains("YwYLL")) {
                        nameElementSelector += ".YwYLL";
                    } else if(nameElementClass.contains("t3s7S")) {
                        nameElementSelector += ".t3s7S";
                    }
                }

                if(contents.size() > 0) {
                    for(WebElement content : contents) {
                        String classAttribute = content.getAttribute("class");
                        if(classAttribute.contains("cZnHG") || classAttribute.contains("hTu5x")) continue; // 해당 장소가 광고일 경우 넘어감


                        WebElement nameElement = content.findElement(By.cssSelector(nameElementSelector)); // 상호명
                        System.out.println(nameElement.getText());
                        nameElement.click(); // 장소 상호명 클릭
                        webDriverWait.until((ExpectedCondition<Boolean>) webDriver ->
                                ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
//                        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(5000)); // 대기
                        driver.switchTo().defaultContent(); // 상위 프레임으로 이동
                        webDriverWait.until(
                                ExpectedConditions.presenceOfElementLocated(By.cssSelector("#entryIframe"))
                        ); // #entryIframe가 로딩될 때까지 대기

                        String link = "";
                        try {
                            link = driver.findElement(By.cssSelector("#entryIframe")).getAttribute("src");
                        } catch (StaleElementReferenceException e) {
                            WebElement entryIframe = driver.findElement(By.cssSelector("#entryIframe"));
                            link = entryIframe.getAttribute("src");
                        }

                        links.add(link); // links에 link 추가

//                        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(5000)); // 대기
                        webDriverWait.until((ExpectedCondition<Boolean>) webDriver ->
                                ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
                        WebElement button = driver.findElement(By.cssSelector("#section_content > div > div.sc-1wsjitl.OWZjJ > button.sc-lc28fh.bFIegC")); // 상세 정보 iframe 닫기 버튼
                        button.click(); // 닫기 버튼 클릭
//                        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(5000)); // 대기
                        webDriverWait.until((ExpectedCondition<Boolean>) webDriver ->
                                ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
                        driver.switchTo().frame(driver.findElement(By.cssSelector("#searchIframe"))); // 검색 목록으로 frame 이동
//                        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(5000)); // 대기
                        webDriverWait.until((ExpectedCondition<Boolean>) webDriver ->
                                ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
                    }
                }
            }
        } catch (Exception e) {
        }

            // 페이지 이동
//            List<WebElement> pageNums = driver.findElements(By.cssSelector("#app-root > div > div.XUrfU > div.zRM9F > a.mBN2s")); // 페이지 번호 리스트
//            WebElement nextPageNum = null; // 다음 페이지 번호
//            for(WebElement pageNum : pageNums) {
//                String pageClassAttribute = pageNum.getAttribute("class");
//                int idx = 0; // pageNums 리스트에서 pageNum의 인덱스
//
//                if(pageClassAttribute.contains("qxokY")) { // 현재 페이지 찾았을 때
//                    idx = pageNums.indexOf(pageNum); // 현재 페이지의 리스트 인덱스
//                    if(idx == pageNums.size() - 1) { // 현재 페이지가 마지막 페이지일 경우
//                        break;
//                    } else { // 현재 페이지가 마지막 페이지가 아닐 경우
//                        idx++; // 인덱스 +1
//                        nextPageNum = pageNums.get(idx); // 다음 페이지 번호 지정
//                        nextPageNum.click(); // 클릭
//                        break;
//                    }
//                }
//            }
//            if (nextPageNum == null) break;
        driver.quit(); // 종료
        return links;
    }

    /**
     * 시간 범위를 2진수로 변환
     */
    public String convertTimeToBits(String timeRange) {
        StringBuilder result = new StringBuilder();

        timeRange = timeRange.replace(" - ", " ");
        timeRange = timeRange.replace("\n", " ");
        String[] times = timeRange.split(" ");
        int[] bitArray = new int[48];

        try {
            String startTime = times[0];
            String endTime = times[1];

            int startMinutes = convertToMinutes(startTime);
            int endMinutes = convertToMinutes(endTime);

            if(startMinutes == -1 || endMinutes == -1) { // 쉬는 날인 경우
            } else if(startMinutes < endMinutes) { // 시작시간보다 끝나는 시간이 늦을 경우 (ex. 10:00 - 19:00)
                for(int mm = startMinutes; mm < endMinutes; mm += 30) {
                    bitArray[47 - mm / 30] = 1;
                }
            } else if(startMinutes > endMinutes) { // 시작시간보다 끝나는 시간이 빠를 경우 (ex. 10:00 - 02:00)
                for(int mm = startMinutes; mm < 24 * 60; mm += 30) {
                    bitArray[47 - mm / 30] = 1;
                }
                for(int mm = 0; mm < endMinutes; mm += 30) {
                    bitArray[47 - mm / 30] = 1;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
        }


        for(int bit : bitArray) {
            result.append(bit);
        }

        return result.toString();
    }

    /**
     * 시간을 분으로 환산
     */
    public int convertToMinutes(String time) {
        String[] parts = time.split(":");
        try {
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            return hours * 60 + minutes;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
