package common.commondemo.model;

import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class DtoResp {

	@JsonIgnore
	private MultiValueMap<String, String> headers;

	@JsonIgnore
	private HttpStatus httpStatus;

	private Boolean status;

	private String statusCode;

	private String statusMessage;

}