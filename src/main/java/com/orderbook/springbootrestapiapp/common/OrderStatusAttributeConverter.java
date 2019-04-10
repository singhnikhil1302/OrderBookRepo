package com.orderbook.springbootrestapiapp.common;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class OrderStatusAttributeConverter implements AttributeConverter<OrderStatus, String> {

	@Override
	public String convertToDatabaseColumn(OrderStatus status) {
		
		switch (status) {
		  case VALID:
		   return "VALID";
		  case INVALID:
		   return "INVALID";
		  default:
		   throw new IllegalArgumentException("Unknown value: " + status);
		  }
	}

	@Override
	public OrderStatus convertToEntityAttribute(String dbData) {
		switch (dbData) {
		  case "VALID":
		   return OrderStatus.VALID;
		  case "INVALID":
		   return OrderStatus.INVALID;
		  default:
		   throw new IllegalArgumentException("Unknown value: " + dbData);
		  }
	}

}
