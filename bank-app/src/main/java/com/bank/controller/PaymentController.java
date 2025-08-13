package com.bank.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bank.entity.Account;
import com.bank.entity.DebitRequest;
import com.bank.service.BankService;
import com.bank.service.PaymentService;


@RestController
@RequestMapping("/api/bank")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    BankService bankService;
    

    @GetMapping("/account/{userId}")
    public Account getAccount(@PathVariable Long userId) {
        return paymentService.getAccount(userId);
    }


    @PostMapping("/debit")
    public String debit(@RequestBody DebitRequest debitRequest) {
        return paymentService.debitAccount(debitRequest.getUserId(), debitRequest.getAmount());
    }
}
