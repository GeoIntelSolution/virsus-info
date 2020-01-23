package com.example.virus;

import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.validation.Valid;

@SpringBootApplication
public class VirusApplication  implements ApplicationRunner{

	@Value("${virus.json.dist}")
	private String jsonPath;

	public static void main(String[] args) {
		SpringApplication.run(VirusApplication.class, args);
	}

	@Bean(name="mongoTemplate")
	public MongoTemplate mongoTemplate(){
		MongoClient mongoClient = new MongoClient("localhost", 27017);
		SimpleMongoDbFactory dbFactory = new SimpleMongoDbFactory(mongoClient, "test");
		DefaultDbRefResolver defaultDbRefResolver = new DefaultDbRefResolver(dbFactory);
		MappingMongoConverter converter = new MappingMongoConverter(defaultDbRefResolver, new MongoMappingContext());
		converter.setTypeMapper(new DefaultMongoTypeMapper(null));
		MongoTemplate mongoTemplate = new MongoTemplate(dbFactory, converter);
		return mongoTemplate;
	}


	@Override
	public void run(ApplicationArguments args) throws Exception {
		System.out.println(String.format("Server is listening on %s", jsonPath));

		AppConfig.jsonPath=jsonPath;


	}

	private CorsConfiguration buildConfig() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.addAllowedOrigin("*");
		corsConfiguration.addAllowedHeader("*");
		corsConfiguration.addAllowedMethod("*");
		return corsConfiguration;
	}

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", buildConfig()); // 4
		return new CorsFilter(source);
	}


}
