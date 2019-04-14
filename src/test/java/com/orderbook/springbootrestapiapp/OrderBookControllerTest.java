package com.orderbook.springbootrestapiapp;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.orderbook.springbootrestapiapp.common.OrderStatus;
import com.orderbook.springbootrestapiapp.common.Status;
import com.orderbook.springbootrestapiapp.controller.OrderBookController;
import com.orderbook.springbootrestapiapp.orderbook.business.OrderManagementImpl;
import com.orderbook.springbootrestapiapp.vo.Execution;
import com.orderbook.springbootrestapiapp.vo.OrderBook;
import com.orderbook.springbootrestapiapp.vo.OrderBookStatistics;
import com.orderbook.springbootrestapiapp.vo.OrderDetails;
import com.orderbook.springbootrestapiapp.vo.OrderStatistics;

//@WebMvcTest
@RunWith(SpringRunner.class)
@WebMvcTest(value = OrderBookController.class)
public class OrderBookControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OrderManagementImpl orderMgmt;

	@Test
	public void givenOrderBook_whenSave_thenGetOk() {

		OrderBook mockOrderBook = new OrderBook();
		mockOrderBook.setInstId(1L);
		mockOrderBook.setOrderBookId(1L);
		mockOrderBook.setOrderBookstatus(Status.CLOSED);

		String exampleOrderBookJson = "{\"instId\": 100000,\"orderBookId\":100000,\"orderBookstatus\":\"OPEN\"}";

		Mockito.when(orderMgmt.createOrderBook(Mockito.anyLong())).thenReturn(mockOrderBook);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/orderBook/100000")
				.accept(MediaType.APPLICATION_JSON).content(exampleOrderBookJson)
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result;
		try {
			result = mockMvc.perform(requestBuilder).andReturn();
			MockHttpServletResponse response = result.getResponse();
			assertEquals(HttpStatus.CREATED.value(), response.getStatus());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void givenIdAndOrderDetails_whenSave_thenGetOk() {

		OrderBook mockOrderBook = new OrderBook();
		mockOrderBook.setInstId(1L);
		mockOrderBook.setOrderBookId(1L);
		mockOrderBook.setOrderBookstatus(Status.OPEN);

		OrderDetails mockOrder = OrderDetails.builder().orderId(1L).orderQuantity(2L).orderExecQuantity(0L)
				.orderDate(LocalDate.now()).orderPrice(new BigDecimal(3000.0)).orderStatus(OrderStatus.VALID)
				.orderBook(mockOrderBook).build();
		String exampleOrderDetailJson = "{\"orderBook\": {\"instId\": 2100,\"orderBookId\": 0,\"orderBookstatus\": \"OPEN\"},\"orderDate\": \"2019-03-23\",\"orderId\": 0,\"orderPrice\": 1500,\"orderQuantity\": 800,\"orderStatus\": \"VALID\"}";

		Mockito.when(orderMgmt.addOrders(Mockito.any(OrderDetails.class), Mockito.anyLong())).thenReturn(mockOrder);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/orderBook/100000/order")
				.accept(MediaType.APPLICATION_JSON).content(exampleOrderDetailJson)
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result;
		try {
			result = mockMvc.perform(requestBuilder).andReturn();
			MockHttpServletResponse response = result.getResponse();
			assertEquals(HttpStatus.CREATED.value(), response.getStatus());
			System.out.println(result.getResponse());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void givenOrderBookId_updateStatuss_thenGetClosed() {

		OrderBook mockOrderBook = new OrderBook();
		mockOrderBook.setInstId(1L);
		mockOrderBook.setOrderBookId(1L);
		mockOrderBook.setOrderBookstatus(Status.CLOSED);

		Mockito.when(orderMgmt.closeOrderBook(Mockito.anyLong())).thenReturn(Status.CLOSED.toString());
		String exampleOrderBookJson = "{\"instId\": 100000,\"orderBookId\":100000,\"orderBookstatus\":\"OPEN\"}";

		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/orderBook/100000")
				.accept(MediaType.APPLICATION_JSON).content(exampleOrderBookJson)
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result;
		try {
			result = mockMvc.perform(requestBuilder).andReturn();
			MockHttpServletResponse response = result.getResponse();
			assertEquals(HttpStatus.OK.value(), response.getStatus());
			assertEquals(Status.CLOSED.toString(), response.getContentAsString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void givenOrderBookIdAndExecution_whenSave_thenGetOk() {

		OrderBook mockOrderBook = new OrderBook();
		mockOrderBook.setInstId(1L);
		mockOrderBook.setOrderBookId(1L);
		mockOrderBook.setOrderBookstatus(Status.CLOSED);

		Execution mockExecution = new Execution();
		mockExecution.setExecutionId(2L);
		mockExecution.setExecutionPrice(new BigDecimal(1000));
		mockExecution.setExecutionQuantity(500);

		Mockito.when(orderMgmt.addExecutions(Mockito.any(Execution.class), Mockito.anyLong()))
				.thenReturn(mockOrderBook);
		String exampleExecutionJson = "{\"executionId\": 0,\"executionPrice\": 1200, \"executionQuantity\": 500,\"orderBook\": {\"instId\": 2100,\"orderBookId\": 0,\"orderBookstatus\": \"CLOSED\"}}";

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/orderBook/100000/execution")
				.accept(MediaType.APPLICATION_JSON).content(exampleExecutionJson)
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result;
		try {
			result = mockMvc.perform(requestBuilder).andReturn();
			MockHttpServletResponse response = result.getResponse();
			assertEquals(HttpStatus.CREATED.value(), response.getStatus());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void givenOrderId_thenGetOrderDetails() throws Exception {

		OrderStatistics orderStats = new OrderStatistics();
		Mockito.when(orderMgmt.getOrderDetails(Mockito.anyLong(), Mockito.anyLong())).thenReturn(orderStats);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/orderBook/100000/order/100000")
				.accept(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		System.out.println(result.getResponse());
		String expected = "{\"computedExecQuantity\":0,\"orderId\":0,\"orderStatus\":null,\"orderPrice\":null,\"executionPrice\":null}";
		assertEquals(expected, result.getResponse().getContentAsString());
		JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), false);
	}

	@Test
	public void givenOrderBookId_thenGetOrderBookStats() throws Exception {

		String expected = "{\"amtOfOrders\":0,\"demand\":0,\"earliestOrder\":null,\"latestOrder\":null,\"limitSpread\":null,\"limitSpreadForValidOrders\":null,\"limitSpreadForInValidOrders\":null,\"orderStatus\":null,\"noOfValidOrders\":0,\"noOfInValidOrders\":0,\"noOfValidDemands\":0,\"noOfInValidDemands\":0,\"accumulatedExecutionQuantity\":0,\"biggestOrder\":null,\"smallestOrder\":null,\"computedExecQuantity\":0,\"orderId\":0,\"executionPrice\":0.0}";
		OrderBookStatistics orderStats = new OrderBookStatistics();
		Mockito.when(orderMgmt.getStatistics(Mockito.anyLong())).thenReturn(orderStats);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/orderBook/100000")
				.accept(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		assertEquals(expected, result.getResponse().getContentAsString());

	}
}