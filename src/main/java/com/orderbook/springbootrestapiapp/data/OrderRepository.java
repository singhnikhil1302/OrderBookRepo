package com.orderbook.springbootrestapiapp.data;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.orderbook.springbootrestapiapp.vo.OrderDetails;

@Repository
public interface OrderRepository extends CrudRepository<OrderDetails, Long> {

	@Query(value = "select * from order_details where orderbook_id = ?", nativeQuery = true)
	public List<OrderDetails> getAllOrdersById(long orderBookId);

	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value = "update order_details set order_status='INVALID' where order_id = ?", nativeQuery = true)
	public void updateOrdersById(long orderId);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value = "update order_details set orderexec_quantity=? where order_id = ?", nativeQuery = true)
	public void updateOrderExecQuantityById(long execQuantity, long orderId);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value = "select * from order_details where orderbook_id = ? AND order_status='VALID' AND order_type = 'LIMIT'", nativeQuery = true)
	public List<OrderDetails> getValidLimitOrders(long orderBookId);
}
