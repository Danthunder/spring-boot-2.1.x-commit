package com.learn.springboot.test;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author Wang danning
 * @since 2020-03-23 23:11
 **/
public class MyApplicationContextInitializer implements ApplicationContextInitializer {
	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		System.out.println("MyApplicationContextInitializer...");
	}
}
