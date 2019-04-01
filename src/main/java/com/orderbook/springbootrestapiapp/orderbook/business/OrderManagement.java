package com.orderbook.springbootrestapiapp.orderbook.business;

import com.orderbook.springbootrestapiapp.vo.Execution;
import com.orderbook.springbootrestapiapp.vo.OrderBook;
import com.orderbook.springbootrestapiapp.vo.OrderBookStatistics;
import com.orderbook.springbootrestapiapp.vo.OrderDetails;
import com.orderbook.springbootrestapiapp.vo.OrderStatistics;

public interface OrderManagement {

	public OrderBook addExecutions(Execution execution, long id);

	public OrderBook createOrderBook(OrderBook orderBook);

	public String closeOrderBook(long id);

	public OrderStatistics getOrderDetails(long id, long orderId);

	public OrderBookStatistics getStatistics(long id);

	public OrderDetails addOrders(OrderDetails orderDetails, long id);

}
