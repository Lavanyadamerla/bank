package com.bank.controller;

import com.bank.entity.Customer;
import com.bank.entity.Transaction;
import com.bank.exception.NoTransactionFoundException;
import com.bank.exception.TransactionFailedException;
import com.bank.repository.TransactionRepository;
import com.bank.service.BankService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class BankControllerTest {

	@Mock
	private BankService bankService;

	@Mock
	private TransactionRepository transactionRepository;

	@InjectMocks
	private BankController bankController;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testRegisterCustomer() {
		Customer customer = new Customer();
		customer.setFirstname("kota");
		customer.setLastname("bhavani");
		customer.setAge(24);
		customer.setEmailid("bhavani.kota@gmail.com");
		customer.setPhonenumber(98989898L);
		customer.setCustomerid(1);

		when(bankService.registerCustomer(customer)).thenReturn("Customer Registered");

		String response = bankController.registerCustomer(customer);
		assertEquals("Customer Registered", response);
		verify(bankService, times(1)).registerCustomer(customer);
	}

	@Test
	public void testTransferFund() {
		long fromAcc = 1001L;
		long toAcc = 1002L;
		double amount = 500.0;

		when(bankService.fundTransfer(fromAcc, toAcc, amount)).thenReturn("Transfer Successful");

		String response = bankController.transferFund(fromAcc, toAcc, amount);
		assertEquals("Transfer Successful", response);
		verify(bankService, times(1)).fundTransfer(fromAcc, toAcc, amount);
	}

	@Test
	public void testGetStatementWithData() {
		long accNumber = 1001L;
		int month = 7;
		int year = 2025;
		String type = "DEBIT";

		List<Transaction> transactions = Arrays.asList(new Transaction(), new Transaction());

		when(bankService.getStatement(accNumber, month, year, type)).thenReturn(transactions);

		ResponseEntity<List<Transaction>> response = bankController.getStatement(accNumber, month, year, type);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(2, response.getBody().size());
	}

	@Test
	public void testGetStatementNoData() {
		long accNumber = 1001L;
		int month = 7;
		int year = 2025;
		String type = "CREDIT";

		when(bankService.getStatement(accNumber, month, year, type))
				.thenThrow(new NoTransactionFoundException("No transactions found"));

		assertThrows(NoTransactionFoundException.class, () -> bankController.getStatement(accNumber, month, year, type));


	}

}