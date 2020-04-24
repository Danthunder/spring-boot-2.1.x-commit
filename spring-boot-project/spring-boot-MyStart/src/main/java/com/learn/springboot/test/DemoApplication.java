package com.learn.springboot.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;


/**
 * @author Wang danning
 * @since 2020-03-19 23:20
 **/
@SpringBootApplication
@Import(Red.class)
public class DemoApplication {

	public static void main(String[] args) {
//		SpringApplication application = new SpringApplication(DemoApplication.class);
//		application.addInitializers(new MyApplicationContextInitializer());
//		application.run(args);
		SpringApplication.run(DemoApplication.class);
	}
}
