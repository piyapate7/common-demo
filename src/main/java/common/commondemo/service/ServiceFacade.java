package common.commondemo.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.vavr.Tuple2;
import common.commondemo.config.FuncMapper;
import common.commondemo.model.DtoReq;
import common.commondemo.model.DtoResp;
import common.commondemo.util.JavaBeanMethodUtil;

@Service
public class ServiceFacade {

	public DtoResp requestToFunc(String funcName, Map<String, String> headers, String jsonReq) throws Exception {

		funcName = funcName.toLowerCase();

		Tuple2<Object, FuncMapper> methodReqObject = JavaBeanMethodUtil.getMethodReqObject(funcName);

		Object objReq = methodReqObject._1;
		FuncMapper funcMap = methodReqObject._2;

		if (objReq == null) {
			throw new Exception("url not found.");
		}

		ObjectMapper objectMapper = new ObjectMapper();
		DtoReq req = (DtoReq) objectMapper.readValue(jsonReq, objReq.getClass());
		objectMapper = null;

		req.setHeaders(headers);
		req.setUseAuditLog(funcMap.getUseAuditLog());

		DtoResp objResp = JavaBeanMethodUtil.getMethodValue(funcName, req);

		return objResp;

	}

}
