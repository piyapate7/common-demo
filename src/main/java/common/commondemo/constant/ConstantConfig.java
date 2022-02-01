package common.commondemo.constant;

import org.springframework.stereotype.Component;

@Component
public class ConstantConfig {
	
	public static final String CONTROLLERFACADE_WITHIN_AOP = "within(th.co.stream.commons.controller.ControllerFacade+)";
	public static final String ISERVICEBASE_WITHIN_AOP = "within(th.co.stream.commons.service.IServiceBase+)";
	public static final String CACHEFORDEVICE_ANNOTATION_AOP = "@annotation(th.co.stream.commons.model.CacheForDevice)";
	public static final String ICBSSERVICEBASE_WITHIN_AOP = "within(th.co.stream.commons.service.cbs.ICbsServiceBase+)";
	
	public static final String VALUE_SWAGGER_MOCK_URL_PREFIX = "/swagger-mock";
	
	public static final String STATUS_CODE_SUCCESS = "00000";
	public static final String STATUS_MSG_SUCCESS = "Successful";
	
	public static final String STATUS_CODE_ERROR_DEFAULT = "GAE998";
	public static final String STATUS_MSG_ERROR_DEFAULT = "Internal Error";
	
	public static final String STATUS_CODE_ERROR_REQUIRED_FIELD = "GAB008";
	
	public static final String MSG_MAP_DETAIL_SQL = 
			"select me.key_name as keyName, " + 
			"me.expression as expression, " + 
			"me.th_value as thValue, " + 
			"me.en_us_value as enUsValue, " + 
			"mt.code as code, " + 
			"mt.description as description, " + 
			"mt.th_template as thTemplate, " + 
			"mt.en_us_template as enUsTemplate " + 
			"from message_code_expression me " + 
			"inner join message_code_template mt " +
			"on me.template_id = mt.id " +
			"where me.key_name is not null " +
			"order by me.key_name";
}
