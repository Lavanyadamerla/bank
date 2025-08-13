package com.bank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import com.bank.entity.Account;
import com.bank.entity.Customer;
import com.bank.entity.Transaction;
import com.bank.exception.NoTransactionFoundException;
import com.bank.exception.RegistrationFailedException;
import com.bank.exception.StatementFetchException;
import com.bank.exception.TransactionFailedException;
import com.bank.repository.AccountRepository;
import com.bank.repository.CustomerRepository;
import com.bank.repository.TransactionRepository;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class BankServiceTest {

	@InjectMocks
	private BankService bankService;

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private TransactionRepository transactionRepository;

	@Test
	void testRegisterCustomerSuccess() {
		Customer customer = new Customer();
		customer.setCustomerid(1L);

		when(customerRepository.save(any())).thenReturn(customer);
		when(accountRepository.save(any())).thenReturn(new Account());

		String result = bankService.registerCustomer(customer);
		assertTrue(result.contains("Register succesfully"));
	}

	@Test
	void testRegisterCustomerFailure() {
		Customer customer = new Customer();
		when(customerRepository.save(any())).thenThrow(new RuntimeException("DB error"));

		assertThrows(RegistrationFailedException.class, () -> bankService.registerCustomer(customer));
	}

	@Test
	void testFundTransferSuccess() {
		Account from = new Account();
		from.setAccountNo(1L);
		from.setBalance(10000);
		from.setCustomerId(1L);

		Account to = new Account();
		to.setAccountNo(2L);
		to.setBalance(5000);
		to.setCustomerId(2L);

		when(accountRepository.findById(1L)).thenReturn(Optional.of(from));
		when(accountRepository.findById(2L)).thenReturn(Optional.of(to));

		String result = bankService.fundTransfer(1L, 2L, 1000);
		assertEquals("Transaction successful.", result);
	}

	@Test
	void testFundTransferInsufficientBalance() {
		Account from = new Account();
		from.setAccountNo(1L);
		from.setBalance(500);
		from.setCustomerId(1L);

		Account to = new Account();
		to.setAccountNo(2L);
		to.setBalance(5000);
		to.setCustomerId(2L);

		when(accountRepository.findById(1L)).thenReturn(Optional.of(from));
		when(accountRepository.findById(2L)).thenReturn(Optional.of(to));

		assertThrows(TransactionFailedException.class, () -> bankService.fundTransfer(1L, 2L, 1000));
	}

	@Test
	void testGetStatementSuccess() {
		Transaction tx = new Transaction();
		tx.setTransactionType("DEBIT");

		when(transactionRepository.findByAccountAndMonthYearAndType(anyLong(), anyString(), anyInt(), anyInt()))
				.thenReturn(List.of(tx));

		List<Transaction> result = bankService.getStatement(1L, 5, 2024, "DEBIT");
		assertEquals(1, result.size());
	}

	@Test
	void testGetStatementNoData() {
		when(transactionRepository.findByAccountAndMonthYearAndType(anyLong(), anyString(), anyInt(), anyInt()))
				.thenReturn(List.of());

		assertThrows(NoTransactionFoundException.class, () -> bankService.getStatement(1L, 5, 2024, "DEBIT"));
	}

	@Test
	void testGetStatementInvalidMonth() {
		assertThrows(StatementFetchException.class, () -> bankService.getStatement(1L, 13, 2024, "DEBIT"));
	}

	@Test
	void testGetStatementInvalidType() {
		assertThrows(StatementFetchException.class, () -> bankService.getStatement(1L, 5, 2024, "XYZ"));
	}
}
