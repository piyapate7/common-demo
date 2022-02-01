package common.commondemo.config;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public abstract class YamlPropertiesBase {

	private Map<String, String> serviceMapper = new HashMap<>();

	private Map<String, FuncMapper> funcMapper = new HashMap<>();

}