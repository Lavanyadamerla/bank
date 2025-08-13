package com.bank.controller;

import com.bank.entity.Account;
import com.bank.entity.DebitRequest;
import com.bank.service.PaymentService;
import com.bank.service.BankService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class PaymentControllerTest {

    @InjectMocks
    private PaymentController paymentController;

    @Mock
    private PaymentService paymentService;

    @Mock
    private BankService bankService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testGetAccountSuccess() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();

        Account account = new Account();
        account.setCustomerId(1L);
        account.setBalance(1000.0);
        account.setAccountNo(1234567890123456L);

        when(paymentService.getAccount(1L)).thenReturn(account);

        mockMvc.perform(get("/api/bank/account/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1L))
                .andExpect(jsonPath("$.balance").value(1000.0))
                .andExpect(jsonPath("$.accountNo").value(1234567890123456L));
    }

    @Test
    void testDebitSuccess() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();

        DebitRequest request = new DebitRequest();
        request.setUserId(1L);
        request.setAmount(BigDecimal.valueOf(500));

        when(paymentService.debitAccount(1L, BigDecimal.valueOf(500))).thenReturn("Successfully debited");

        mockMvc.perform(post("/api/bank/debit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully debited"));
    }
}
