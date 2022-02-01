package common.commondemo.util;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import common.commondemo.config.FuncMapper;
import common.commondemo.config.YamlPropertiesBase;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Component
@SuppressWarnings("unchecked")
public class JavaBeanMethodUtil {

	private static ApplicationContext applicationContext;

	private static Map<String, YamlPropertiesBase> yamlPropertiesBase = null;

	private static InitOjectConfig initConfig;

	private static final Pattern FIELD_SEPARATOR = Pattern.compile("\\.");
	private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
	@SuppressWarnings("rawtypes")
	private static final ClassValue<Map<String, BiFunction>> CACHE = new ClassValue<Map<String, BiFunction>>() {
		@Override
		protected Map<String, BiFunction> computeValue(Class<?> type) {
			return new ConcurrentHashMap<>();
		}
	};

	@Autowired
	public JavaBeanMethodUtil(ApplicationContext ac, InitOjectConfig cfg) {
		applicationContext = ac;
		initConfig = cfg;

		yamlPropertiesBase = applicationContext.getBeansOfType(YamlPropertiesBase.class);
	}

	public static Tuple2<Object, FuncMapper> getMethodReqObject(String methodIndexName) {

		if (initConfig.getFuncClassMapper().containsKey(methodIndexName)) {

			String ymlObjectKeyName = initConfig.getFuncClassMapper().get(methodIndexName);
			YamlPropertiesBase yml = yamlPropertiesBase.get(ymlObjectKeyName);

			if (yml != null) {

				Map<String, FuncMapper> funcMap = yml.getFuncMapper();

				if (!MapUtils.isEmpty(funcMap)) {
					if (funcMap.containsKey(methodIndexName)) {
						FuncMapper funcMapper = funcMap.get(methodIndexName);

						Object req = applicationContext.getBean(funcMapper.getReqName());

						return Tuple.of(req, funcMapper);
					}
				}
			}
		}

		return null;

	}

	public static <T> T getMethodValue(String methodIndexName, Object reqValue) {

		if (initConfig.getFuncClassMapper().containsKey(methodIndexName)) {

			String ymlObjectKeyName = initConfig.getFuncClassMapper().get(methodIndexName);
			YamlPropertiesBase yml = yamlPropertiesBase.get(ymlObjectKeyName);

			if (yml != null) {

				Map<String, FuncMapper> funcMap = yml.getFuncMapper();

				if (!MapUtils.isEmpty(funcMap)) {
					if (funcMap.containsKey(methodIndexName)) {
						FuncMapper funcMapper = funcMap.get(methodIndexName);

						Object javaBean = applicationContext.getBean(funcMapper.getServiceName());
						Object req = applicationContext.getBean(funcMapper.getReqName());
						Object resp = applicationContext.getBean(funcMapper.getRespName());
						String funcName = funcMapper.getFuncName();

						req = reqValue;

						return getMethodValue(javaBean, funcName, req, resp);
					}
				}

			}

		}

		return null;

	}

	@SuppressWarnings("rawtypes")
	public static <T> T getMethodValue(Object javaBean, String methodName, Object req, Object resp) {

		BiFunction cacheFunction = getCachedFunction(javaBean.getClass(), methodName, req.getClass(), resp.getClass());

		Object obj = null;
		if (cacheFunction != null) {
			obj = cacheFunction.apply(javaBean, req);
			if (obj == null) {
				return null;
			}
		}

		return (T) obj;
	}

	@SuppressWarnings("rawtypes")
	private static BiFunction getCachedFunction(Class<?> javaBeanClass, String methodName, Class<?> req,
			Class<?> resp) {
		final BiFunction function = CACHE.get(javaBeanClass).get(methodName);
		if (function != null) {
			return function;
		}

		BiFunction cacheFunction = createAndCacheFunction(javaBeanClass, methodName, req, resp);
		if (cacheFunction == null) {
			return null;
		}

		return cacheFunction;
	}

	@SuppressWarnings("rawtypes")
	private static BiFunction createAndCacheFunction(Class<?> javaBeanClass, String path, Class<?> req, Class<?> resp) {
		BiFunction functionToBeCached = createFunctions(javaBeanClass, path, req, resp).stream().findFirst()
				.orElse(null);
		return cacheAndGetFunction(path, javaBeanClass, functionToBeCached);
	}

	@SuppressWarnings("rawtypes")
	private static BiFunction cacheAndGetFunction(String path, Class<?> javaBeanClass, BiFunction functionToBeCached) {
		BiFunction cachedFunction = CACHE.get(javaBeanClass).putIfAbsent(path, functionToBeCached);
		return cachedFunction != null ? cachedFunction : functionToBeCached;
	}

	@SuppressWarnings("rawtypes")
	private static List<BiFunction> createFunctions(Class<?> javaBeanClass, String path, Class<?> req, Class<?> resp) {
		List<BiFunction> functions = new ArrayList<>();
		Stream.of(FIELD_SEPARATOR.split(path)).reduce(javaBeanClass, (nestedJavaBeanClass, methodName) -> {
			Tuple2<? extends Class, BiFunction> getFunction = createFunction(methodName, nestedJavaBeanClass, req,
					resp);
			functions.add(getFunction._2);
			return getFunction._1;
		}, (previousClass, nextClass) -> nextClass);
		return functions;
	}

	@SuppressWarnings("rawtypes")
	private static Tuple2<? extends Class, BiFunction> createFunction(String methodName, Class<?> javaBeanClass,
			Class<?> req, Class<?> resp) {

		Stream<Method> streamOfMethod = Stream.of(javaBeanClass.getDeclaredMethods());

		return streamOfMethod.filter(method -> StringUtils.endsWithIgnoreCase(method.getName(), methodName))
				.map(method -> {
					return createTupleWithReturnTypeAndGetter(method, javaBeanClass, req, resp);
				}).findFirst().orElseThrow(IllegalStateException::new);
	}

	@SuppressWarnings("rawtypes")
	private static Tuple2<? extends Class, BiFunction> createTupleWithReturnTypeAndGetter(Method getterMethod,
			Class<?> javaBeanClass, Class<?> req, Class<?> resp) {
		try {
			return Tuple.of(getterMethod.getReturnType(),
					(BiFunction) createCallSite(getterMethod, javaBeanClass, req, resp).getTarget().invokeExact());
		} catch (Throwable e) {
			throw new IllegalArgumentException("Lambda creation failed for (" + getterMethod.getName() + ").", e);
		}
	}

	private static CallSite createCallSite(Method getterMethod, Class<?> javaBeanClass, Class<?> req, Class<?> resp)
			throws LambdaConversionException, NoSuchMethodException, IllegalAccessException {

		MethodType invokedType = MethodType.methodType(BiFunction.class);
		MethodType biFunc = MethodType.methodType(resp, req);
		MethodHandle target = LOOKUP.findVirtual(javaBeanClass, getterMethod.getName(), biFunc);
		MethodType func = target.type();

		return LambdaMetafactory.metafactory(LOOKUP, "apply", invokedType, func.generic(), target,
				MethodType.methodType(resp, javaBeanClass, req));
	}

}
