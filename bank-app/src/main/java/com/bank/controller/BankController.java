package com.bank.controller;

import org.springframework.web.bind.annotation.RestController;

import com.bank.entity.Customer;
import com.bank.entity.Transaction;
import com.bank.repository.TransactionRepository;
import com.bank.service.BankService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class BankController {

	@Autowired
	BankService bankService;
	
	@Autowired
	TransactionRepository transactionRepository;

	@PostMapping("/customer/save")
	public String registerCustomer(@RequestBody Customer customer) {
		return bankService.registerCustomer(customer);
	}

	@PostMapping("/customer/amounttransfer")
	public String transferFund(@RequestParam long fromAcc, long toAcc, double amount) {
		return bankService.fundTransfer(fromAcc, toAcc, amount);
	}

	@PostMapping("/customer/statement")
	public ResponseEntity<List<Transaction>> getStatement(
	        @RequestParam long accNumber,
	        @RequestParam int month,
	        @RequestParam int year,
	        @RequestParam String transactionType) {

	    List<Transaction> transactions = bankService.getStatement(accNumber, month, year, transactionType);
	    return ResponseEntity.ok(transactions);
	}

	
	
	
	
	
	

}


