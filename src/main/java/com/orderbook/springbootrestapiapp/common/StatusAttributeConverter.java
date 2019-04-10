package com.orderbook.springbootrestapiapp.common;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class StatusAttributeConverter implements AttributeConverter<Status, String> {

	@Override
	public String convertToDatabaseColumn(Status status) {
		
		switch (status) {
		  case CLOSED:
		   return "CLOSED";
		  case OPEN:
		   return "OPEN";
		  case EXECUTED:
		   return "EXECUTED";
		  default:
		   throw new IllegalArgumentException("Unknown value: " + status);
		  }
	}

	@Override
	public Status convertToEntityAttribute(String dbData) {
		switch (dbData) {
		  case "CLOSED":
		   return Status.CLOSED;
		  case "OPEN":
		   return Status.OPEN;
		  case "EXECUTED":
		   return Status.EXECUTED;
		  default:
		   throw new IllegalArgumentException("Unknown value: " + dbData);
		  }
	}

}
