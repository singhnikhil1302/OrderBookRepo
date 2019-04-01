package com.orderbook.springbootrestapiapp.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.orderbook.springbootrestapiapp.common.JsonDateSerializer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * POJO for accepted Orders. This class is immutable because once the orders are
 * accepted the order cannot be added again
 * 
 * @author Nikhil
 *
 */
@Builder
@Entity(name = "OrderDetails")
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "order_details")
@ToString
public class OrderDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7648002306464567400L;

	@Id
	@Column(name = "ORDER_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter
	@Setter(value = AccessLevel.PRIVATE)
	Long orderId;

	@Getter
	@Setter(value = AccessLevel.PRIVATE)
	@Column(name = "ORDER_QUANTITY")
	Long orderQuantity;
	
	@Getter
	@Setter(value = AccessLevel.PRIVATE)
	@Column(name = "ORDEREXEC_QUANTITY")
	Long orderExecQuantity;

	@Getter
	@Setter(value = AccessLevel.PRIVATE)
	@JsonSerialize(using = JsonDateSerializer.class)
	@Column(name = "ORDER_DATE")
	LocalDate orderDate;

	@Getter
	@Setter(value = AccessLevel.PRIVATE)
	@Column(name = "ORDER_PRICE")
	BigDecimal orderPrice;
	
	@Getter
	@Setter(value = AccessLevel.PRIVATE)
	@Column(name = "ORDER_STATUS")
	String orderStatus;

	@Getter
	@Setter(value = AccessLevel.PRIVATE)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "ORDERBOOK_ID", nullable = false)
	OrderBook orderBook;

}