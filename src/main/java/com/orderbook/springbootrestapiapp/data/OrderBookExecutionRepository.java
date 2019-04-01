package com.orderbook.springbootrestapiapp.data;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.orderbook.springbootrestapiapp.vo.Execution;

@Repository
public interface OrderBookExecutionRepository extends CrudRepository<Execution, Long> {

	public Execution getByExecutionId(long id);
	
	@Query(value = "select * from orderbook_execution where orderbook_id = ?", nativeQuery = true)
	public List<Execution> getAllExecutionsById(long orderBookId);

}
