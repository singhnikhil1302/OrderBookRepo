package com.orderbook.springbootrestapiapp.orderbook.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.orderbook.springbootrestapiapp.common.OrderStatus;
import com.orderbook.springbootrestapiapp.common.OrderType;
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
	public OrderBook createOrderBook(Long instrumentId) throws OrderBookException {

		OrderBook orderBook = orderBookRepository.getByInstId(instrumentId);
		if (orderBook == null) {
			orderBook = new OrderBook();
			orderBook.setInstId(instrumentId);
			orderBook.setOrderBookstatus(Status.OPEN);
			orderBookRepository.save(orderBook);
			return orderBook;
		} else {
			throw new OrderBookException("OrderBook already exists with Instrument ID  : " + instrumentId);
		}

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

		OrderBook savedOrderBook = orderBookRepository.getByOrderBookId(orderBookId);

		if (!savedOrderBook.getOrderBookstatus().toString().equals(Status.OPEN.toString()))
			throw new OrderBookException(
					"OrderBook must be in Open state for Orders to be added" + savedOrderBook.getOrderBookstatus());
		OrderDetails orders;

		if (null == orderDetails.getOrderPrice() || orderDetails.getOrderPrice().compareTo(new BigDecimal(0.00)) == 0)
			orders = OrderDetails.builder().orderId(orderDetails.getOrderId())
					.orderQuantity(orderDetails.getOrderQuantity()).orderDate(orderDetails.getOrderDate())
					.orderPrice(orderDetails.getOrderPrice()).orderStatus(OrderStatus.VALID).orderBook(savedOrderBook)
					.orderType(OrderType.MARKET).build();
		else
			orders = OrderDetails.builder().orderId(orderDetails.getOrderId())
					.orderQuantity(orderDetails.getOrderQuantity()).orderDate(orderDetails.getOrderDate())
					.orderPrice(orderDetails.getOrderPrice()).orderStatus(OrderStatus.VALID).orderBook(savedOrderBook)
					.orderType(OrderType.LIMIT).build();

		orderRepository.save(orders);

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

		OrderBook orderBookToBeClosed = orderBookRepository.getByOrderBookId(orderBookId);
		if (orderBookToBeClosed.getOrderBookstatus().equals(Status.OPEN)) {
			// Only if the OrderBook is in Open Status that it can be closed
			orderBookToBeClosed.setOrderBookstatus(Status.CLOSED);
			orderBookRepository.save(orderBookToBeClosed);
			return orderBookToBeClosed.getOrderBookstatus().toString();
		} else {
			throw new OrderBookException(
					"OrderBookstatus is not Open for it to be Closed :  " + orderBookToBeClosed.getOrderBookstatus());
		}

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
		List<Execution> execList = executionRepository.getAllExecutionsById(orderBookId);

		if (null == execList || execList.isEmpty()) {
			if (savedOrderBook.getOrderBookstatus().toString().equalsIgnoreCase(Status.CLOSED.toString()))
				persistExecution(execution, savedOrderBook);

		} else {
			if (savedOrderBook.getOrderBookstatus().equals(Status.EXECUTED))
				return savedOrderBook;
			else {
				for (Execution exec : execList) {
					if (exec.getExecutionPrice().compareTo(execution.getExecutionPrice()) != 0)
						throw new OrderBookException("Execution Price should be same across all executions :  "
								+ execution.getExecutionPrice() + " != " + exec.getExecutionPrice());
				}
				persistExecution(execution, savedOrderBook);
			}

		}
		return savedOrderBook;

	}

	private void persistExecution(Execution execution, OrderBook orderBook) {

		execution.setOrderBook(orderBook);
		executionRepository.save(execution);
		updateOrdersAfterExecution(execution.getExecutionId());

	}

	private void updateOrdersAfterExecution(long execId) {

		Execution exec = executionRepository.getByExecutionId(execId);
		BigDecimal executionPrice = exec.getExecutionPrice();
		long executionQuantity = exec.getExecutionQuantity();
		long accOrderQuantity = 0;
		OrderBook savedOrderBook = orderBookRepository.getByOrderBookId(exec.getOrderBook().getOrderBookId());
		List<OrderDetails> orders = orderRepository.getAllOrdersById(exec.getOrderBook().getOrderBookId());
		List<OrderDetails> validOrders = new ArrayList<OrderDetails>();
		// Invalidate Orders for which the Order Price is Less than the Execution Price
		// when the Execution is applied

		for (OrderDetails orderDetls : orders) {

			if (orderDetls.getOrderPrice().compareTo(new BigDecimal(0.00)) == 0) {
				orderRepository.updateOrderExecQuantityById(0, orderDetls.getOrderId());
				continue;
			} else if (orderDetls.getOrderPrice().compareTo(executionPrice) == -1) {
				OrderDetails.builder().orderStatus(OrderStatus.INVALID).build();
				orderRepository.updateOrdersById(orderDetls.getOrderId());
				orderRepository.updateOrderExecQuantityById(0, orderDetls.getOrderId());
			} else {
				validOrders.add(orderDetls);
				accOrderQuantity += orderDetls.getOrderQuantity();
			}

		}

		computeLinearDistribution(validOrders, executionQuantity, accOrderQuantity);
		computeValidDemand(savedOrderBook.getOrderBookId(), exec);
		logger.info(orders.size());

	}

	/**
	 * Method for computing whether the OrderBook needs to be updated to Executed
	 * 
	 * @param orderBookId
	 * @param execution
	 */
	private void computeValidDemand(long orderBookId, Execution execution) {

		long executionQuantity = execution.getExecutionQuantity();
		long accOrderQuantity = 0;
		List<OrderDetails> orderDetails = orderRepository.getAllOrdersById(orderBookId);
		OrderBook orderBookToBeExecuted = orderBookRepository.getByOrderBookId(orderBookId);

		for (OrderDetails orders : orderDetails) {
			if (orders.getOrderPrice().compareTo(new BigDecimal(0.00)) == 0)
				continue;
			else {
				if (orders.getOrderStatus().equals(OrderStatus.VALID))
					accOrderQuantity += orders.getOrderQuantity();
			}
		}
		/*
		 * Only if the accumulatedOrder Quantity == Execution Quantity that a Demand
		 * becomes Valid and the OrderBook can be executed
		 */
		if (accOrderQuantity == executionQuantity) {
			orderBookToBeExecuted.setOrderBookstatus(Status.EXECUTED);
			orderBookRepository.save(orderBookToBeExecuted);
		}
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
		long sumOfApproxExecQuant = 0;

		if (accOrderQuantity == 0)
			throw new OrderBookException(
					"OrderBook must contain atleast one Valid Limit Order for Linear Distribution: accOrderQuantity =>"
							+ accOrderQuantity);

		BigDecimal simpleRatio = BigDecimal.valueOf(executionQuantity).divide(BigDecimal.valueOf(accOrderQuantity), 2,
				RoundingMode.HALF_UP);
		for (OrderDetails orderDtls : orders) {
			computedExecQuant = BigDecimal.valueOf(orderDtls.getOrderQuantity()).multiply(simpleRatio).longValue();
			logger.info("Computed value = " + computedExecQuant);
			sumOfApproxExecQuant += computedExecQuant;
			if (null == orderDtls.getOrderExecQuantity()) {
				OrderDetails.builder().orderExecQuantity(computedExecQuant).build();
			} else {
				computedExecQuant += orderDtls.getOrderExecQuantity();
			}
			orderRepository.updateOrderExecQuantityById(computedExecQuant, orderDtls.getOrderId());
			computedExecQuant = 0;
		}
		long orderBookId = orders.get(0).getOrderBook().getOrderBookId();
		long diff = Math.abs(sumOfApproxExecQuant - executionQuantity);
		// Fetching the Limit Orders which are Valid
		List<OrderDetails> validLimitOrders = orderRepository.getValidLimitOrders(orderBookId);
		validLimitOrders.sort((a, b) -> b.getOrderExecQuantity().compareTo(a.getOrderExecQuantity()));

		/*
		 * Handling the left over execution Quantity and allocating the remaining
		 * Execution Quantity to the Orders in the descending Order of their Quantity
		 */
		for (OrderDetails order : validLimitOrders) {

			if (diff > 0) {
				diff--;
				orderRepository.updateOrderExecQuantityById(order.getOrderExecQuantity() + 1, order.getOrderId());
			} else
				break;
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
		if (null == orders || orders.isEmpty()) {
			throw new OrderBookException(
					"To fetch Order Details atleast one order must be added to the Book:  => orderList = " + orders);
		} else {
			orders.forEach((orderDetails) -> {
				if (orderDetails.getOrderId() == orderId) {
					orderStats.setOrderStatus(orderDetails.getOrderStatus());
					orderStats.setOrderPrice(orderDetails.getOrderPrice());
					orderStats.setComputedExecQuantity(orderDetails.getOrderExecQuantity());
					orderStats.setExecutionPrice(execution.getExecutionPrice());
				}
			});
			return orderStats;
		}
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

		if (null == orders || orders.isEmpty())
			new OrderBookException(
					"To get OrderBook Statistics Order Details List must be non-empty  => orderList = " + orders);
		if (null == executions || executions.isEmpty())
			new OrderBookException(
					"To compute valid/Invalid Orders Execution List must be non-empty => executions =  " + orders);
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
		orders.sort((a, b) -> b.getOrderDate().compareTo(a.getOrderDate()));
		orderBookStats.setEarliestOrder(orders.get(orders.size() - 1));
		orderBookStats.setLatestOrder(orders.get(0));

		// Computing the number of Orders in the OrderBook
		orderBookStats.setAmtOfOrders(orders.size());

		// Sort on Quantity Field to get the Biggest Order and Smallest Order
		orders.sort((a, b) -> b.getOrderQuantity().compareTo(a.getOrderQuantity()));
		orderBookStats.setBiggestOrder(orders.get(0));
		orderBookStats.setSmallestOrder(orders.get(orders.size() - 1));

		// Computing number of Valid Orders and Invalid Orders
		for (OrderDetails orderDetls : orders) {

			if (orderDetls.getOrderStatus().toString().equalsIgnoreCase(OrderStatus.VALID.toString())) {
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
		HashMap<BigDecimal, Long> limitSpreadForValidOrders = new HashMap<BigDecimal, Long>();
		HashMap<BigDecimal, Long> limitSpreadForInValidOrders = new HashMap<BigDecimal, Long>();
		long quantity = 0;
		for (OrderDetails dtls : orders) {
			if (!limitSpread.containsKey(dtls.getOrderPrice())) {
				limitSpread.put(dtls.getOrderPrice(), dtls.getOrderQuantity());
				if (dtls.getOrderStatus().equals(OrderStatus.VALID))
					limitSpreadForValidOrders.put(dtls.getOrderPrice(), dtls.getOrderQuantity());
				else
					limitSpreadForInValidOrders.put(dtls.getOrderPrice(), dtls.getOrderQuantity());
			} else {
				quantity = limitSpread.get(dtls.getOrderPrice()) + dtls.getOrderQuantity();
				limitSpread.put(dtls.getOrderPrice(), quantity);
				if (dtls.getOrderStatus().equals(OrderStatus.VALID))
					limitSpreadForValidOrders.put(dtls.getOrderPrice(), dtls.getOrderQuantity());
				else
					limitSpreadForInValidOrders.put(dtls.getOrderPrice(), dtls.getOrderQuantity());
			}
		}
		quantity = 0;
		orderBookStats.setLimitSpread(limitSpread);
		orderBookStats.setLimitSpreadForValidOrders(limitSpreadForValidOrders);
		orderBookStats.setLimitSpreadForInValidOrders(limitSpreadForInValidOrders);
	}

}
