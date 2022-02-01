package common.commondemo.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import common.commondemo.constant.ConstantConfig;
import common.commondemo.entity.TempEntity;

@Repository
public interface MessageMapDetailRepository extends JpaRepository<TempEntity, Long> {

	@Query(value = ConstantConfig.MSG_MAP_DETAIL_SQL, nativeQuery = true)
	List<Map<String, Object>> getMessageMapDetailAll();

}
