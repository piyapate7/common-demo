package common.commondemo.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

import lombok.Data;
import common.commondemo.config.FuncMapper;
import common.commondemo.config.YamlPropertiesBase;
import common.commondemo.model.MessageMapDetail;
import common.commondemo.repository.MessageMapDetailRepository;

@Component
@Data
public class InitOjectConfig {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private MessageMapDetailRepository messageMapDetailRepository;

	private Map<String, String> funcClassMapper = new HashMap<>();

	private Map<String, String> swaggerMockUrlMap = new HashMap<>();

	private Map<String, List<MessageMapDetail>> messageMapDetailList = new HashMap<>();

	@PostConstruct
	public void init() throws Exception {

		Map<String, YamlPropertiesBase> yamlPropertiesBase = applicationContext
				.getBeansOfType(YamlPropertiesBase.class);

		AbstractApplicationContext context = (AbstractApplicationContext) applicationContext;
		DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getBeanFactory();

		for (Map.Entry<String, YamlPropertiesBase> yml : yamlPropertiesBase.entrySet()) {

			YamlPropertiesBase ymlBase = yml.getValue();

			for (Map.Entry<String, String> entry : ymlBase.getServiceMapper().entrySet()) {
				beanFactory.registerAlias(entry.getKey(), entry.getValue());
			}

			for (Map.Entry<String, FuncMapper> entry : ymlBase.getFuncMapper().entrySet()) {
				funcClassMapper.put(entry.getKey(), yml.getKey());
				swaggerMockUrlMap.put(entry.getValue().getFuncName() + "." + entry.getValue().getServiceName(),
						entry.getKey());
			}
		}

//		List<MessageMapDetail> messageMapDetail = BeanMapUtils
//				.mapsToObjects(messageMapDetailRepository.getMessageMapDetailAll(), MessageMapDetail.class);
//
//		if (messageMapDetail != null && !messageMapDetail.isEmpty()) {
//			messageMapDetailList = messageMapDetail.stream()
//					.collect(Collectors.groupingBy(e -> e.getKeyName().toUpperCase()));
//		}

	}

}
