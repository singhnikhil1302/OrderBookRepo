package com.orderbook.springbootrestapiapp.vo;

import java.math.BigDecimal;

public class OrderStatistics {

	private long computedExecQuantity;

	private long orderId;
	
	private String orderStatus;
	
	private BigDecimal orderPrice;
	
	private BigDecimal executionPrice;

	public long getComputedExecQuantity() {
		return computedExecQuantity;
	}

	public void setComputedExecQuantity(long computedExecQuantity) {
		this.computedExecQuantity = computedExecQuantity;
	}

	public long getOrderId() {
		return orderId;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public BigDecimal getOrderPrice() {
		return orderPrice;
	}

	public void setOrderPrice(BigDecimal orderPrice) {
		this.orderPrice = orderPrice;
	}

	public BigDecimal getExecutionPrice() {
		return executionPrice;
	}

	public void setExecutionPrice(BigDecimal executionPrice) {
		this.executionPrice = executionPrice;
	}

}