package uk.ac.le.qx16.pp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import uk.ac.le.qx16.pp.entities.TrackingRecord;

public interface TrackingRecordRepository extends JpaRepository<TrackingRecord, Integer>{
	
	@Query("select t from TrackingRecord t where t.user.id=:id")
	public List<TrackingRecord> findByUserId(@Param("id") Integer userId);
	
	public TrackingRecord findByPath(String path);
}
