package yeoksamstationexit1.recommend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;

@Configuration
@EnableWebMvc
public class SwaggerConfig {
    @Bean
    public Docket api() {
        final ApiInfo apiInfo = new ApiInfoBuilder()
                .title("Turtle Logistics")
                .description("<h3>RestApi 문서 제공.</h3>")
                .contact(new Contact("Admin", "https://github.com/Yeoksam-Station-Exit-1", "john0513@naver.com")).license("MIT License")
                .version("V1.0.0").build();

        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(true)
                .securitySchemes(List.of(apiKey()))
                .apiInfo(apiInfo)
                .select()
                .apis(RequestHandlerSelectors.basePackage("yeoksamstationexit1.recommend.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiKey apiKey() {
        return new ApiKey("Authorization", "Authorization", "header");
    }


}
