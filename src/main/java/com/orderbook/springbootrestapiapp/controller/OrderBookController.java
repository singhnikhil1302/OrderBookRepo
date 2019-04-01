package com.orderbook.springbootrestapiapp.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.orderbook.springbootrestapiapp.common.Status;
import com.orderbook.springbootrestapiapp.orderbook.business.OrderManagement;
import com.orderbook.springbootrestapiapp.vo.Execution;
import com.orderbook.springbootrestapiapp.vo.OrderBook;
import com.orderbook.springbootrestapiapp.vo.OrderBookStatistics;
import com.orderbook.springbootrestapiapp.vo.OrderDetails;
import com.orderbook.springbootrestapiapp.vo.OrderStatistics;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

@RestController
public class OrderBookController {

	private static final Logger logger = LogManager.getLogger(OrderBookController.class);

	@Autowired
	private OrderManagement orderMgmt;

	@RequestMapping(method = RequestMethod.POST, value = "/orderBook")
	@ResponseBody
	@ApiOperation(value = "API to create an OrderBook")
	public ResponseEntity<String> createOrderBook(@RequestBody OrderBook orderBook) {
		orderMgmt.createOrderBook(orderBook);
		logger.info("OrderBook Created!!");
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@RequestMapping(method = RequestMethod.POST, value = "/orderBook/{orderBookId}/order")
	@ResponseBody
	@ApiOperation(value = "API to create an Order")
	public ResponseEntity<String> createOrder(@RequestBody OrderDetails orderDetails,
			@PathVariable("orderBookId") long orderBookId) {
		orderMgmt.addOrders(orderDetails, orderBookId);
		logger.info("Order Added!!");
		return ResponseEntity.status(HttpStatus.CREATED).build();

	}

	@ApiOperation(value = "API to close an OrderBook")
	@RequestMapping(method = RequestMethod.PUT, value = "/orderBook/{orderBookId}")
	@ResponseBody
	public String closeOrderBook(@PathVariable("orderBookId") long orderBookId) {
		String response = orderMgmt.closeOrderBook(orderBookId);
		logger.info(response);
		return response;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/orderBook/{orderBookId}/execution")
	@ResponseBody
	@ApiOperation(value = "API to add an Execution over an OrderBook")
	public ResponseEntity<String> addExecutions(@RequestBody Execution execution,
			@PathVariable("orderBookId") long orderBookId) {		
			orderMgmt.addExecutions(execution, orderBookId);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/orderBook/{orderBookId}/order/{orderId}")
	@ResponseBody
	@ApiOperation(value = "API to fetch OrderDetails")
	public ResponseEntity<OrderStatistics> getOrderDetails(@PathVariable("orderBookId") long orderBookId,
			@PathVariable("orderId") long orderId) {
		OrderStatistics orderStats = orderMgmt.getOrderDetails(orderBookId, orderId);
		return ResponseEntity.status(HttpStatus.OK).body(orderStats);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/orderBook/{orderBookId}")
	@ResponseBody
	@ApiOperation(value = "API to retrieve OrderBook Statistics")
	public ResponseEntity<OrderBookStatistics> getOrderBookStatistics(@PathVariable("orderBookId") long orderBookId) {
		OrderBookStatistics orderBookStats = orderMgmt.getStatistics(orderBookId);
		return ResponseEntity.status(HttpStatus.OK).body(orderBookStats);
	}

}