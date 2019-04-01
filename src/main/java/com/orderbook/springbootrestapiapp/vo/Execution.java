package com.orderbook.springbootrestapiapp.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "OrderBookExecution")
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Table(name = "orderbook_execution")
@ToString
public class Execution implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2395929079862836498L;

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter
	@Setter
	private long executionId;

	@Getter
	@Setter
	@Column(name = "EXECUTION_QUANTITY")
	private long executionQuantity;

	@Getter
	@Setter
	@Column(name = "EXECUTION_PRICE")
	private BigDecimal executionPrice;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "ORDERBOOK_ID", nullable = false)
	private OrderBook orderBook;

}
