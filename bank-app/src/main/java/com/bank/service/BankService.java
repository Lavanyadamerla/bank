package com.bank.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.entity.Account;
import com.bank.entity.Customer;
import com.bank.entity.Transaction;
import com.bank.exception.NoTransactionFoundException;
import com.bank.exception.RegistrationFailedException;
import com.bank.exception.StatementFetchException;
import com.bank.exception.TransactionFailedException;
import com.bank.repository.AccountRepository;
import com.bank.repository.BankRepository;
import com.bank.repository.CustomerRepository;
import com.bank.repository.TransactionRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class BankService {

	@Autowired
	BankRepository bankRepository;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	TransactionRepository transactionRepository;

	private static final SecureRandom random = new SecureRandom();

	@Transactional
	public String registerCustomer(Customer customer) {
		try {

			Optional<Customer> existCustomer = customerRepository.findByemailid(customer.getEmailid());
			System.out.println("Checking for email: " + customer.getEmailid());
			System.out.println("Customer exists: " + existCustomer.isPresent());
			if (existCustomer.isPresent()) {
				throw new RegistrationFailedException("Customer Already registered");
			}

			Customer savedCustomer = customerRepository.save(customer);
			long accoutNumber = randomAccountGenerator();

			Account acc = new Account();
			acc.setCustomerId(savedCustomer.getCustomerid());
			acc.setBalance(10000);
			acc.setAccountNo(accoutNumber);
			accountRepository.save(acc);

			return "Register succesfully and Account number : " + acc.getAccountNo() + "  - customer Id : "
					+ savedCustomer.getCustomerid();
		} catch (Exception e) {
			throw new RegistrationFailedException("Customer registration failed. Transaction rolled back.");
		}

	}

	private long randomAccountGenerator() {
		StringBuilder accountNumber = new StringBuilder();
		for (int i = 0; i < 16; i++) {
			int digit = random.nextInt(10);
			accountNumber.append(digit);
		}
		return Long.parseUnsignedLong(accountNumber.toString());

	}

	@Transactional
	public String fundTransfer(long fromAcc, long toAcc, double amount) {

		try {

			if (amount <= 0) {
				throw new TransactionFailedException("Amount should be greater than zero.");
			}

			Optional<Account> accountFrom = accountRepository.findById(fromAcc);
			if (!accountFrom.isPresent()) {
				new TransactionFailedException("From account is not valid.");
			}

			Optional<Account> accountTo = accountRepository.findById(toAcc);

			if (!accountFrom.isPresent()) {
				new TransactionFailedException("To account is not valid.");
			}

			double cuuretbalanceFromAcc = accountFrom.get().getBalance();

			double currentbalanceToAcc = accountTo.get().getBalance();

			if (cuuretbalanceFromAcc < amount) {
				throw new TransactionFailedException("Insufficient balance in account: " + fromAcc);
			}

			accountFrom.get().setBalance(cuuretbalanceFromAcc-amount);
			accountTo.get().setBalance(currentbalanceToAcc+amount);
			
			accountRepository.save(accountFrom.get());
			accountRepository.save(accountTo.get());
		
			
			Transaction debitTransaction = new Transaction();
			
			
			debitTransaction.setFromAcc(fromAcc);
			debitTransaction.setToAcc(toAcc);
			debitTransaction.setAmount(amount);
			debitTransaction.setTransactionTime(LocalDateTime.now());
			debitTransaction.setTransactionType("DEBIT");
			debitTransaction.setCustomerId(accountFrom.get().getCustomerId());

			Transaction creditTransaction = new Transaction();
			creditTransaction.setFromAcc(fromAcc);
			creditTransaction.setToAcc(toAcc);
			creditTransaction.setAmount(amount);
			creditTransaction.setTransactionTime(LocalDateTime.now());
			creditTransaction.setTransactionType("CREDIT");
			creditTransaction.setCustomerId(accountTo.get().getCustomerId());

			transactionRepository.save(debitTransaction);
			transactionRepository.save(creditTransaction);

			return "Transaction successful.";

		} catch (Exception e) {
			throw new TransactionFailedException("Transaction failed due to: " + e.getMessage());
		}

	}

	public List<Transaction> getStatement(long accNumber, int month, int year, String transactionType) {
		if (month < 1 || month > 12) {
			throw new StatementFetchException("Month must be between 1 and 12.");
		}

		if (year < 2000 || year > LocalDateTime.now().getYear()) {
			throw new StatementFetchException("Year is out of valid range.");
		}

		if (!transactionType.equalsIgnoreCase("DEBIT") && !transactionType.equalsIgnoreCase("CREDIT")) {
			throw new StatementFetchException("Transaction type must be DEBIT or CREDIT.");
		}

		List<Transaction> transactions = transactionRepository.findByAccountAndMonthYearAndType(accNumber,
				transactionType.toUpperCase(), month, year);

		if (transactions.isEmpty()) {
			throw new NoTransactionFoundException("No transactions found for the given criteria.");
		}

		return transactions;
	}

}
