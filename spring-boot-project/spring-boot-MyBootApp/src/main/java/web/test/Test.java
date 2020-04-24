package web.test;

import org.apache.catalina.LifecycleException;

/**
 * @author Wang danning
 * @since 2020-02-10 16:30
 **/
public class Test {

	public static void main(String[] args) {
		try {
			MySpringApplication.run();
		}
		catch (LifecycleException ex) {
			ex.printStackTrace();
		}
	}

}
