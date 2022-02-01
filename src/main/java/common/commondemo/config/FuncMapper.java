package common.commondemo.config;

import lombok.Data;

@Data
public class FuncMapper {

	private String serviceName = null;

	private String funcName = null;

	private String reqName = null;

	private String respName = null;

	private Boolean useAuditLog = true;

}
