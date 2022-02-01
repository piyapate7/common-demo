package common.commondemo.model;

import java.io.Serializable;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
//@HeaderReqConstraint
//@BusinessRuleConstraint
public abstract class DtoReq implements Serializable {

	@JsonIgnore
	private static final long serialVersionUID = 1L;

	@JsonIgnore
	private Map<String, String> headers;

	@JsonIgnore
	private Boolean useAuditLog = true;
	
	@JsonIgnore
	private String statusCode;
	
	@JsonIgnore
	private String language;

	@NotNull(message = "timestamp is null")
	private Long timestamp;

	@NotBlank(message = "deviceUuid is blank")
	@NotNull(message = "deviceUuid is null")
	private String deviceUuid;

}