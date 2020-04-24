package web.test;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

/**
 * @author Wang danning
 * @since 2020-02-15 23:56
 **/
@Component
public class MyListener implements GenericApplicationListener {

	// 表示是否某事件resolvableType感兴趣，感兴趣则返回true
	@Override
	public boolean supportsEventType(ResolvableType resolvableType) {
		// if(resolvableType.getType().equals(ApplicationStartingEvent.class)) {
		// return true;
		// }
		// else {
		// return false;
		// }
		return true;
	}

	@Override
	public boolean supportsSourceType(Class<?> sourceType) {
		return true;
	}

	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		System.out.println("-------MyListener onApplicationEvent-------");
	}

}
