package common.commondemo.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import common.commondemo.constant.ConstantConfig;
import common.commondemo.exception.CommonException;
import common.commondemo.model.DtoResp;
import common.commondemo.model.TempResp;
import common.commondemo.service.ServiceFacade;

public abstract class ControllerFacade {

	@Autowired
	private ServiceFacade serviceFacade;

	private static final Logger LOG_EXCEPTION = LoggerFactory.getLogger("exception");

	@PostMapping("{funcId}")
	public ResponseEntity<DtoResp> receivedFunc(@PathVariable("funcId") String funcId,
			@RequestHeader Map<String, String> headers, @RequestBody String dataRequest) throws Exception {

		DtoResp dataResponse = serviceFacade.requestToFunc(funcId, headers, dataRequest);

		if (dataResponse.getHttpStatus() == null) {
			dataResponse.setHttpStatus(HttpStatus.OK);
		}

		return new ResponseEntity<DtoResp>(dataResponse, dataResponse.getHeaders(), dataResponse.getHttpStatus());
	}

	@ExceptionHandler(Exception.class)
	ResponseEntity<DtoResp> handleException(Throwable throwable) {

		String originalCause = null;
		String originalMessage = null;

		try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw);) {
			throwable.printStackTrace(pw);
			originalMessage = sw.toString();
		} catch (Throwable ignoreex) {
			originalMessage = throwable.getMessage();
		} finally {
			if (throwable.getCause() != null) {
				originalCause = throwable.getCause().getMessage();
			}
		}

		StringBuilder message = new StringBuilder();
		if (StringUtils.isNotBlank(originalMessage)) {
			message.append("throwingCause = ").append(originalCause).append(", throwingMassage = ")
					.append(originalMessage);
		}

		String errMsg = message.toString();

		DtoResp resp = new TempResp();
		resp.setStatus(false);
		resp.setStatusCode(ConstantConfig.STATUS_CODE_ERROR_DEFAULT);
		resp.setStatusMessage(ConstantConfig.STATUS_MSG_ERROR_DEFAULT);

		LOG_EXCEPTION.info("Exception: [ExceptionHandler] :: with result = {}\n\n{}", resp, errMsg);

		return new ResponseEntity<>(resp, HttpStatus.OK);
	}

	@ExceptionHandler(CommonException.class)
	ResponseEntity<DtoResp> handleCommonException(CommonException ex) {

		String errMsg = null;

		if (ex.getDtoResp() != null) {
			errMsg = ex.toString();
			LOG_EXCEPTION.info("CommonException: [ExceptionHandler] :: with result = {}\n\n{}", ex.getDtoResp(),
					errMsg);
			return new ResponseEntity<>(ex.getDtoResp(), HttpStatus.OK);
		}

		DtoResp resp = new TempResp();
		resp.setStatus(false);
		resp.setStatusCode(ConstantConfig.STATUS_CODE_ERROR_DEFAULT);
		resp.setStatusMessage(ConstantConfig.STATUS_MSG_ERROR_DEFAULT);

		errMsg = ex.toString();

		LOG_EXCEPTION.info("CommonException: [ExceptionHandler] :: with result = {}\n\n{}", resp, errMsg);

		return new ResponseEntity<>(resp, HttpStatus.OK);

	}
}
