package web.test;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Wang danning
 * @since 2020-02-16 14:01
 **/
@Component
@ConfigurationProperties(prefix = "tiger")
public class MyAutoProperties {

	private int age;

	private String name;

	public int getAge() {
		return this.age;
	}

	public void setAge(int age) {
		System.out.println("-------------setAge:" + age);
		this.age = age;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		System.out.println("-------------setName:" + name);
		this.name = name;
	}

}
