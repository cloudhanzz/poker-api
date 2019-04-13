package jiayun.han.game.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.HashSet;

/**
 * 
 * @author Jiayun Han
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
	
	@Bean
	public Docket api() {
		
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(getApiInfo())
				.select()
				.apis(RequestHandlerSelectors.basePackage("jiayun.han.game.controller"))
				.paths(PathSelectors.any())
				.build()
				.protocols(new HashSet<>(Arrays.asList("HTTP", "HTTPS")));
	}

	private ApiInfo getApiInfo() {
		
		return new ApiInfoBuilder()
		           .title("Poker Game API")
		           .description("Providing APIs for Basic Poker Gaming")
		           .version("1.0")
		           .license("Apache 2.0")
		           .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
		           .contact(new Contact("Jiayun Han, Architect/Tech Lead at Askida", 
		   				                "https://www.linkedin.com/in/homecloud-han/", 
		   				                "jiayunhan@gmail.com"))
		           .build();
	}

}
