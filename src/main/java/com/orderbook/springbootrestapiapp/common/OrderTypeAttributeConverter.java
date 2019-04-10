package com.orderbook.springbootrestapiapp.common;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class OrderTypeAttributeConverter implements AttributeConverter<OrderType, String> {

	@Override
	public String convertToDatabaseColumn(OrderType type) {
		
		switch (type) {
		  case LIMIT:
		   return "LIMIT";
		  case MARKET:
		   return "MARKET";
		  default:
		   throw new IllegalArgumentException("Unknown value: " + type);
		  }
	}

	@Override
	public OrderType convertToEntityAttribute(String dbData) {
		switch (dbData) {
		  case "LIMIT":
		   return OrderType.LIMIT;
		  case "MARKET":
		   return OrderType.MARKET;
		  default:
		   throw new IllegalArgumentException("Unknown value: " + dbData);
		  }
	}

}
