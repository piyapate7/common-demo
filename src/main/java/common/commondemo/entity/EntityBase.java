package common.commondemo.entity;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

import lombok.Data;

@MappedSuperclass
@Data
public abstract class EntityBase implements Serializable {
	private static final long serialVersionUID = 1L;

}
