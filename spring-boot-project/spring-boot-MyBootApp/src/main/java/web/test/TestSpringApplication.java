package web.test;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;


/**
 * @author Wang danning
 * @since 2020-02-14 22:52
 **/
@SpringBootApplication
public class TestSpringApplication {

	public static void main(String[] args) {
		// SpringApplication.run(TestSpringApplication.class);
		SpringApplication springApplication = new SpringApplication(TestSpringApplication.class);
		ApplicationContext applicationContext = springApplication.run();
		MyAutoProperties bean = applicationContext.getBean(MyAutoProperties.class);
		System.out.println("name:" + bean.getName());
	}

	@Bean
	public HttpMessageConverters httpMessageConverters() {
		return new HttpMessageConverters(new FastJsonHttpMessageConverter());
	}

}
