package com.bank.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.entity.Account;
import com.bank.exception.AccountNotFoundException;
import com.bank.exception.InsufficientBalanceException;
import com.bank.exception.InvalidAmountException;
import com.bank.repository.AccountRepository;
import com.bank.repository.CustomerRepository;

@Service
public class PaymentService {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    AccountRepository accountRepository;

    public Account getAccount(Long userId) {
        Account account = accountRepository.findByCustomerId(userId);
        if (account == null) {
            throw new AccountNotFoundException("Account not found for userId: " + userId);
        }
        return account;
    }

    @Transactional
    public String debitAccount(Long userId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero.");
        }

        Account account = accountRepository.findByCustomerId(userId);
        if (account == null) {
            throw new AccountNotFoundException("Account not found for userId: " + userId);
        }

        double currentBalance = account.getBalance();
        double debitAmount = amount.doubleValue();

        if (currentBalance < debitAmount) {
            throw new InsufficientBalanceException("Insufficient funds for userId: " + userId);
        }

        account.setBalance(currentBalance - debitAmount);
        accountRepository.save(account);

        return "Successfully debited";
    }
}
