package edu.unm.missingisirs.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
 
@Configuration
@ComponentScan(basePackages = { "edu.unm.missingisirs.*" })
@PropertySource("classpath:project.properties")
public class AppConfig {
	
	@Value("${uploadServerPath}")
	private String uploadServerPath;
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}

}
