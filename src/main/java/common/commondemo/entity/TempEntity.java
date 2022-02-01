package common.commondemo.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class TempEntity extends EntityBase {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	private int id;

}
