package com.orderbook.springbootrestapiapp.vo;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * POJO for capturing details of attributes at the OrderBook level
 * 
 * @author Nikhil
 *
 */
@Entity(name = "OrderBook")
@Table(name = "orderbook")
public class OrderBook implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7864723214782557377L;

	@Column(name = "ORDERBOOK_INSTRUMENTID")
	private int instId;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private long orderBookId;

	@Column(name = "ORDERBOOK_STATUS")
	private String orderBookstatus;

	public long getOrderBookId() {
		return orderBookId;
	}

	public void setOrderBookId(long orderBookId) {
		this.orderBookId = orderBookId;
	}

	public int getInstId() {
		return instId;
	}

	public void setInstId(int instId) {
		this.instId = instId;
	}

	public String getOrderBookstatus() {
		return orderBookstatus;
	}

	public void setOrderBookstatus(String orderBookstatus) {
		this.orderBookstatus = orderBookstatus;
	}

}
