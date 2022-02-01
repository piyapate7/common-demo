package common.commondemo.model;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class TempResp extends DtoResp implements Serializable {

	private static final long serialVersionUID = 1L;

}