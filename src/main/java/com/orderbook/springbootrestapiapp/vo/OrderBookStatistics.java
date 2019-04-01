package com.orderbook.springbootrestapiapp.vo;

import java.math.BigDecimal;
import java.util.HashMap;

public class OrderBookStatistics {

	private int amtOfOrders;

	private long demand;
	
	private OrderDetails earliestOrder;
	
	private OrderDetails latestOrder;
	
	private HashMap<BigDecimal, Long> limitSpread;
	
	private HashMap<Long, String> orderStatus;
	
	private int noOfValidOrders;
	
	private int noOfInValidOrders;
	
	private int noOfValidDemands;
	
	private int noOfInValidDemands;
	
	private long accumulatedExecutionQuantity;
	
	private OrderDetails biggestOrder;
	
	private OrderDetails smallestOrder;
	
	private long computedExecQuantity;
	
	private long orderId;
	
	public long getOrderId() {
		return orderId;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	public long getComputedExecQuantity() {
		return computedExecQuantity;
	}

	public void setComputedExecQuantity(long computedExecQuantity) {
		this.computedExecQuantity = computedExecQuantity;
	}

	public HashMap<Long, String> getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(HashMap<Long, String> orderStatus) {
		this.orderStatus = orderStatus;
	}

	public double getExecutionPrice() {
		return executionPrice;
	}

	public void setExecutionPrice(double executionPrice) {
		this.executionPrice = executionPrice;
	}

	private double executionPrice;

	public int getAmtOfOrders() {
		return amtOfOrders;
	}

	public void setAmtOfOrders(int amtOfOrders) {
		this.amtOfOrders = amtOfOrders;
	}

	public long getDemand() {
		return demand;
	}

	public void setDemand(long demand) {
		this.demand = demand;
	}

	public OrderDetails getEarliestOrder() {
		return earliestOrder;
	}

	public void setEarliestOrder(OrderDetails earliestOrder) {
		this.earliestOrder = earliestOrder;
	}

	public OrderDetails getLatestOrder() {
		return latestOrder;
	}

	public void setLatestOrder(OrderDetails latestOrder) {
		this.latestOrder = latestOrder;
	}

	public HashMap<BigDecimal, Long> getLimitSpread() {
		return limitSpread;
	}

	public void setLimitSpread(HashMap<BigDecimal, Long> limitSpread) {
		this.limitSpread = limitSpread;
	}

	public int getNoOfValidOrders() {
		return noOfValidOrders;
	}

	public void setNoOfValidOrders(int noOfValidOrders) {
		this.noOfValidOrders = noOfValidOrders;
	}

	public int getNoOfInValidOrders() {
		return noOfInValidOrders;
	}

	public void setNoOfInValidOrders(int noOfInValidOrders) {
		this.noOfInValidOrders = noOfInValidOrders;
	}

	public int getNoOfValidDemands() {
		return noOfValidDemands;
	}

	public void setNoOfValidDemands(int noOfValidDemands) {
		this.noOfValidDemands = noOfValidDemands;
	}

	public int getNoOfInValidDemands() {
		return noOfInValidDemands;
	}

	public void setNoOfInValidDemands(int noOfInValidDemands) {
		this.noOfInValidDemands = noOfInValidDemands;
	}

	public long getAccumulatedExecutionQuantity() {
		return accumulatedExecutionQuantity;
	}

	public void setAccumulatedExecutionQuantity(long totalExecutionQuantity) {
		this.accumulatedExecutionQuantity = totalExecutionQuantity;
	}

	public OrderDetails getBiggestOrder() {
		return biggestOrder;
	}

	public void setBiggestOrder(OrderDetails biggestOrder) {
		this.biggestOrder = biggestOrder;
	}

	public OrderDetails getSmallestOrder() {
		return smallestOrder;
	}

	public void setSmallestOrder(OrderDetails smallestOrder) {
		this.smallestOrder = smallestOrder;
	}
	
}