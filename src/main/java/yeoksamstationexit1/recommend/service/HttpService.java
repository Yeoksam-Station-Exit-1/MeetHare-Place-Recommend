package yeoksamstationexit1.recommend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class HttpService {
    private final WebClient webClient;

    @Value("${SystemUrl}")
    private String BaseUrl;

    public HttpService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(BaseUrl).build();
    }

    public Mono<String> fetchPostApi(String uri, String requestBody) {
        return webClient.post()
                .uri(uri)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> fetchGetApi(String uri) {
        return webClient.post()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class);
    }
}
