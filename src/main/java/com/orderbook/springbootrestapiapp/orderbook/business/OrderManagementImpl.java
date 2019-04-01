package com.orderbook.springbootrestapiapp.orderbook.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.orderbook.springbootrestapiapp.common.OrderStatus;
import com.orderbook.springbootrestapiapp.common.Status;
import com.orderbook.springbootrestapiapp.data.OrderBookExecutionRepository;
import com.orderbook.springbootrestapiapp.data.OrderBookRepository;
import com.orderbook.springbootrestapiapp.data.OrderRepository;
import com.orderbook.springbootrestapiapp.vo.Execution;
import com.orderbook.springbootrestapiapp.vo.OrderBook;
import com.orderbook.springbootrestapiapp.vo.OrderBookStatistics;
import com.orderbook.springbootrestapiapp.vo.OrderDetails;
import com.orderbook.springbootrestapiapp.vo.OrderStatistics;

/**
 * The purpose of the class is to process the requests for the OrderBooks
 * 
 * @author nikhil
 *
 */

@Service
public class OrderManagementImpl implements OrderManagement {

	private static final Logger logger = LogManager.getLogger(OrderManagementImpl.class);

	@Autowired
	private OrderBookRepository orderBookRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderBookExecutionRepository executionRepository;

	public void setExecutionRepository(OrderBookExecutionRepository executionRepository) {
		this.executionRepository = executionRepository;
	}

	public void setOrderRepository(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	public void setOrderBookRepository(OrderBookRepository orderBookRepository) {
		this.orderBookRepository = orderBookRepository;
	}

	/**
	 * Method for creating the order Book for an instrument It takes OrderBook as
	 * input parameters Returns the OrderBook object with all the OrderBook details
	 * populated
	 * 
	 * @param orderBook
	 * @return OrderBook
	 */
	@Override
	public OrderBook createOrderBook(OrderBook orderBook) {
		// TODO Auto-generated method stub

		orderBook.setOrderBookstatus(Status.OPEN.toString());
		orderBookRepository.save(orderBook);
		return orderBook;
	}

	/**
	 * Method for adding the orders to the order Book for an instrument It takes
	 * OrderBook and OrderBook Id input parameter Returns the OrderBook object with
	 * all the OrderBook details populated
	 * 
	 * @param orderBook, id
	 * @return OrderBook
	 */
	@Override
	public synchronized OrderDetails addOrders(OrderDetails orderDetails, long orderBookId) {
		// TODO Auto-generated method stub
		OrderBook savedOrderBook = orderBookRepository.getByOrderBookId(orderBookId);

		OrderDetails orders = OrderDetails.builder().orderId(orderDetails.getOrderId())
				.orderQuantity(orderDetails.getOrderQuantity()).orderDate(orderDetails.getOrderDate())
				.orderPrice(orderDetails.getOrderPrice()).orderStatus(OrderStatus.VALID.toString())
				.orderBook(savedOrderBook).build();

		if (savedOrderBook.getOrderBookstatus().equalsIgnoreCase(Status.OPEN.toString())) {
			orderRepository.save(orders);
		}

		return orders;
	}

	/**
	 * Method for closing the orderBook It takes OrderBook and Instrument ID as
	 * input parameters Returns the String message once the Order Book is closed
	 * 
	 * @param orderBook
	 * @param instId
	 * @return String
	 */
	@Override
	public String closeOrderBook(long orderBookId) {
		// TODO Auto-generated method stub
		OrderBook orderBookToBeClosed = orderBookRepository.getByOrderBookId(orderBookId);
		orderBookToBeClosed.setOrderBookstatus(Status.CLOSED.toString());
		orderBookRepository.save(orderBookToBeClosed);
		return orderBookToBeClosed.getOrderBookstatus().toString();
	}

	/**
	 * Method for adding executions to the orderBook It takes OrderBook and
	 * Instrument ID as input parameters Returns the String message once the
	 * executions are successfully added to the OrderBook
	 * 
	 * @param orderBook
	 * @param instId
	 * @return String
	 */
	@Override

	public OrderBook addExecutions(Execution execution, long orderBookId) {

		OrderBook savedOrderBook = orderBookRepository.getByOrderBookId(orderBookId);
		execution.setOrderBook(savedOrderBook);
		if (savedOrderBook.getOrderBookstatus().equalsIgnoreCase(Status.CLOSED.toString())) {
			executionRepository.save(execution);
			updateOrdersAfterExecution(execution.getExecutionId());
		}

		return savedOrderBook;
	}

	private void updateOrdersAfterExecution(long execId) {

		Execution exec = executionRepository.getByExecutionId(execId);
		BigDecimal executionPrice = exec.getExecutionPrice();
		long executionQuantity = exec.getExecutionQuantity();
		long[] accOrderQuantity = { 0 };
		OrderBook savedOrderBook = orderBookRepository.getByOrderBookId(exec.getOrderBook().getOrderBookId());
		List<OrderDetails> orders = orderRepository.getAllOrdersById(exec.getOrderBook().getOrderBookId());
		List<OrderDetails> validOrders = new ArrayList<OrderDetails>();
		// Invalidate Orders for which the Order Price is Less than the Execution Price
		// when the Execution is applied
		orders.forEach((orderDetails) -> {
			if (!(orderDetails.getOrderPrice().compareTo(new BigDecimal(0.00)) == 0)) {
				if (!(orderDetails.getOrderPrice().compareTo(executionPrice) == 1)) {
					OrderDetails.builder().orderStatus(OrderStatus.INVALID.toString());
					orderRepository.updateOrdersById(orderDetails.getOrderId());
				} else {
					accOrderQuantity[0] += orderDetails.getOrderQuantity();
					validOrders.add(orderDetails);
				}
			}
		});

		if (accOrderQuantity[0] > executionQuantity)
			computeLinearDistribution(validOrders, executionQuantity, accOrderQuantity[0]);
		else if (accOrderQuantity[0] == executionQuantity) {
			savedOrderBook.setOrderBookstatus(Status.EXECUTED.toString());
			orderBookRepository.save(savedOrderBook);
		}
		logger.info(orders.size());

	}

	/**
	 * Method to calculate and lineraly distribute the execution quantity among the
	 * valid Orders
	 * 
	 * @param orders
	 * @param executionQuantity
	 * @param accOrderQuantity
	 */
	private void computeLinearDistribution(List<OrderDetails> orders, long executionQuantity, Long accOrderQuantity) {

		long computedExecQuant = 0;
		BigDecimal simpleRatio = BigDecimal.valueOf(executionQuantity).divide(BigDecimal.valueOf(accOrderQuantity), 2,
				RoundingMode.HALF_UP);
		for (OrderDetails orderDtls : orders) {
			computedExecQuant = BigDecimal.valueOf(orderDtls.getOrderQuantity()).multiply(simpleRatio).longValue();
			OrderDetails.builder().orderExecQuantity(computedExecQuant);
			orderRepository.save(orderDtls);
			computedExecQuant = 0;

		}
	}

	/**
	 * Method for fetching the order details It takes OrderBook and Order ID as
	 * input parameters Returns the Order details with respect to the given Order Id
	 * 
	 * @param orderBook
	 * @param orderId
	 * @return OrderDetails
	 */

	@Override
	public OrderStatistics getOrderDetails(long orderBookId, long orderId) {

		OrderStatistics orderStats = new OrderStatistics();
		Execution execution = executionRepository.getByExecutionId(orderBookId);
		List<OrderDetails> orders = orderRepository.getAllOrdersById(orderBookId);

		orders.forEach((orderDetails) -> {
			if (orderDetails.getOrderId() == orderId) {
				orderStats.setOrderStatus(orderDetails.getOrderStatus());
				orderStats.setOrderPrice(orderDetails.getOrderPrice());
				orderStats.setComputedExecQuantity(orderDetails.getOrderQuantity());
				orderStats.setExecutionPrice(execution.getExecutionPrice());
			}
		});

		return orderStats;
	}

	/**
	 * Method for fetching the orderbook details The method computes the following
	 * details: accumulated Execution Quantity, Limit Spread / Demand, Latest and
	 * Earliest Orders, Biggest and Smallest Orders,accumulated Order Quantity,no Of
	 * Valid Orders, no Of Invalid Orders
	 * 
	 * @param orderBookId
	 * @return OrderBookStatistics
	 * 
	 */
	@Override
	public OrderBookStatistics getStatistics(long orderBookId) {

		OrderBookStatistics orderBookStats = new OrderBookStatistics();
		List<OrderDetails> orders = orderRepository.getAllOrdersById(orderBookId);
		List<Execution> executions = executionRepository.getAllExecutionsById(orderBookId);
		int numOfValidOrders = 0;
		int numOfInvalidOrders = 0;
		long accumulatedOrderQuant = 0;
		long accExecQuant = 0;

		// Compute accumulated Execution Quantity
		for (Execution exec : executions)
			accExecQuant += exec.getExecutionQuantity();
		orderBookStats.setAccumulatedExecutionQuantity(accExecQuant);

		// compute Limit Price Details
		computeLimitPriceSpread(orderBookStats, orders);

		// Sort on Date field for computing Earliest and Latest Order Entry
		Collections.sort(orders, new Comparator<OrderDetails>() {
			@Override
			public int compare(OrderDetails o1, OrderDetails o2) {
				if (o1.getOrderDate() == null || o2.getOrderDate() == null)
					return 0;
				return o1.getOrderDate().compareTo(o2.getOrderDate());
			}
		});
		orderBookStats.setEarliestOrder(orders.get(0));
		orderBookStats.setLatestOrder(orders.get(orders.size() - 1));

		// Computing the number of Orders in the OrderBook
		orderBookStats.setAmtOfOrders(orders.size());

		// Sort on Quantity Field to get the Biggest Order and Smallest Order
		Collections.sort(orders, new Comparator<OrderDetails>() {
			@Override
			public int compare(OrderDetails o1, OrderDetails o2) {
				if (o1.getOrderQuantity() == null || o2.getOrderQuantity() == null)
					return 0;
				return o1.getOrderQuantity().compareTo(o2.getOrderQuantity());
			}
		});
		orderBookStats.setBiggestOrder(orders.get(orders.size() - 1));
		orderBookStats.setSmallestOrder(orders.get(0));

		// Computing number of Valid Orders and Invalid Orders
		for (OrderDetails orderDetls : orders) {

			if (orderDetls.getOrderStatus().equalsIgnoreCase(OrderStatus.VALID.toString())) {
				numOfValidOrders++;
				accumulatedOrderQuant += orderDetls.getOrderQuantity();
			} else {
				numOfInvalidOrders++;
			}
		}
		orderBookStats.setDemand(accumulatedOrderQuant);
		orderBookStats.setNoOfInValidOrders(numOfValidOrders);
		orderBookStats.setNoOfInValidOrders(numOfInvalidOrders);

		return orderBookStats;

	}

	/**
	 * Method to calculate the LimitPrice Spread per Demand
	 * 
	 * @param orderBookStats
	 * @param orders
	 */
	private void computeLimitPriceSpread(OrderBookStatistics orderBookStats, List<OrderDetails> orders) {

		HashMap<BigDecimal, Long> limitSpread = new HashMap<BigDecimal, Long>();
		long quantity = 0;
		for (OrderDetails dtls : orders) {
			if (!limitSpread.containsKey(dtls.getOrderPrice()))
				limitSpread.put(dtls.getOrderPrice(), dtls.getOrderQuantity());
			else {
				quantity = limitSpread.get(dtls.getOrderPrice()) + dtls.getOrderQuantity();
				limitSpread.put(dtls.getOrderPrice(), quantity);
			}
		}

		orderBookStats.setLimitSpread(limitSpread);
	}

}
