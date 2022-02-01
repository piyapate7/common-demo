package common.commondemo.exception;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import common.commondemo.model.DtoResp;

@Data
@EqualsAndHashCode(callSuper = false)
public class CommonException extends RuntimeException implements Serializable {

	private static final long serialVersionUID = 1905122041950251207L;

	private transient DtoResp dtoResp = null;

	private String originalCause;
	private String originalMessage;
	private Throwable throwable;

	public CommonException(String message) {
		super(message);
		this.originalMessage = message;
	}

	public CommonException(Throwable e) {
		super(e);
		this.throwable = e;
		getStackTrace(e);
	}

	public CommonException(Throwable e, DtoResp req) {
		super(e);
		this.dtoResp = req;
		getStackTrace(e);
	}

	private void getStackTrace(Throwable e) {
		try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw);) {
			e.printStackTrace(pw);
			this.originalMessage = sw.toString();
		} catch (Exception ignoreex) {
			this.originalMessage = e.getMessage();
		} finally {
			if (e.getCause() != null) {
				this.originalCause = e.getCause().getMessage();
			}
		}
	}

	public Throwable getThrowableOriginal() {
		Throwable e = null;
		if (null != this.throwable) {
			e = this.throwable;
		}
		return e;
	}

	@Override
	public String getLocalizedMessage() {
		return this.originalCause;
	}

	@Override
	public String getMessage() {
		StringBuilder message = new StringBuilder();
		if (StringUtils.isNotBlank(originalMessage)) {
			message.append("throwingCause = ").append(originalCause).append(", throwingMassage = ")
					.append(originalMessage);
		}
		return message.toString();
	}

	@Override
	public String toString() {
		return getMessage();
	}

}
