package common.commondemo.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class MessageMapDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	private String keyName;
	private String expression;
	private String thValue;
	private String enUsValue;
	private String code;
	private String description;
	private String thTemplate;
	private String enUsTemplate;

}
