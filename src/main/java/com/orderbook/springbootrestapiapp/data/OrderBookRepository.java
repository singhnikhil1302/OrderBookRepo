package com.orderbook.springbootrestapiapp.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.orderbook.springbootrestapiapp.vo.OrderBook;
import com.orderbook.springbootrestapiapp.vo.OrderDetails;

public interface OrderBookRepository extends CrudRepository<OrderBook, Long> {

	public OrderBook getByOrderBookId(long id);
	
	public OrderBook save(OrderDetails orderDetails);

}
