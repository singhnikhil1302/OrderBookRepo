package com.orderbook.springbootrestapiapp.data;

import org.springframework.data.repository.CrudRepository;

import com.orderbook.springbootrestapiapp.vo.OrderBook;
import com.orderbook.springbootrestapiapp.vo.OrderDetails;

public interface OrderBookRepository extends CrudRepository<OrderBook, Long> {

	public OrderBook getByOrderBookId(long id);
	
	public OrderBook getByInstId(long id);
	
	public OrderBook save(OrderDetails orderDetails);

}
