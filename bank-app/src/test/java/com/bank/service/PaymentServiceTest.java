package com.bank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bank.entity.Account;
import com.bank.exception.AccountNotFoundException;
import com.bank.exception.InsufficientBalanceException;
import com.bank.exception.InvalidAmountException;
import com.bank.repository.AccountRepository;
import com.bank.repository.CustomerRepository;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Test
    void testGetAccountSuccess() {
        Account account = new Account();
        account.setCustomerId(1L);
        when(accountRepository.findByCustomerId(1L)).thenReturn(account);

        Account result = paymentService.getAccount(1L);
        assertNotNull(result);
    }

    @Test
    void testGetAccountNotFound() {
        when(accountRepository.findByCustomerId(1L)).thenReturn(null);
        assertThrows(AccountNotFoundException.class, () -> paymentService.getAccount(1L));
    }

    @Test
    void testDebitAccountSuccess() {
        Account account = new Account();
        account.setCustomerId(1L);
        account.setBalance(1000.0);

        when(accountRepository.findByCustomerId(1L)).thenReturn(account);
        when(accountRepository.save(any())).thenReturn(account);

        String result = paymentService.debitAccount(1L, BigDecimal.valueOf(500));
        assertEquals("Successfully debited", result);
    }

    @Test
    void testDebitAccountInsufficientFunds() {
        Account account = new Account();
        account.setCustomerId(1L);
        account.setBalance(100.0);

        when(accountRepository.findByCustomerId(1L)).thenReturn(account);

        assertThrows(InsufficientBalanceException.class,
                () -> paymentService.debitAccount(1L, BigDecimal.valueOf(500)));
    }

    @Test
    void testDebitAccountInvalidAmount() {
        assertThrows(InvalidAmountException.class,
                () -> paymentService.debitAccount(1L, BigDecimal.valueOf(-100)));
    }

    @Test
    void testDebitAccountAccountNotFound() {
        when(accountRepository.findByCustomerId(1L)).thenReturn(null);
        assertThrows(AccountNotFoundException.class,
                () -> paymentService.debitAccount(1L, BigDecimal.valueOf(100)));
    }
}
