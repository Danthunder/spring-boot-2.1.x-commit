package web.controllor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Wang danning
 * @since: 2020-02-10 20:46
 **/
@Controller
public class IndexController {

	@RequestMapping("/indexFunc/{id}")
	@ResponseBody
	public Map<String, String> indexFunc(@PathVariable String id) {
		Map<String, String> map = new HashMap<>();
		System.out.println("IndexController indexFunc with one param");
		map.put("indexFunc2:id", id);
		return map;
	}

	@RequestMapping("/indexFunc/{id}/{id2}")
	@ResponseBody
	public Map<String, String> indexFunc(@PathVariable String id, @PathVariable String id2) {
		Map<String, String> map = new HashMap<>();
		System.out.println("IndexController indexFunc with 2 params");
		map.put("indexFunc:id", id);
		map.put("indexFunc:id2", id2);
		return map;
	}

}
