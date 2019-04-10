package com.orderbook.springbootrestapiapp.vo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.orderbook.springbootrestapiapp.common.Status;

import lombok.Getter;
import lombok.Setter;

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
	@Getter
	@Setter
	private Long instId;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	@Getter
	@Setter
	private Long orderBookId;

	@Column(name = "ORDERBOOK_STATUS")
	@Getter
	@Setter
	private Status orderBookstatus;

}
