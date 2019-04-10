package com.orderbook.springbootrestapiapp.orderbook.business;

public class OrderBookException extends RuntimeException {
	
	private static final long serialVersionUID = 959278392605118006L;

	public OrderBookException(String errorMessage) {
        super(errorMessage);
    }
	

}
