package com.bank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BankExceptionHandler {

	@ExceptionHandler
	public ResponseEntity<String> handleAccountFoundException(AccountNotFoundException e) {
		return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);

	}

	@ExceptionHandler(InsufficientBalanceException.class)
	public ResponseEntity<String> handleInsufficientBalance(InsufficientBalanceException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(InvalidAmountException.class)
	public ResponseEntity<String> handleInvalidAmount(InvalidAmountException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(RegistrationFailedException.class)
	public ResponseEntity<String> handleRegistrationFailed(RegistrationFailedException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}


	@ExceptionHandler(NoTransactionFoundException.class)
	public ResponseEntity<String> handleNoTransactionFound(NoTransactionFoundException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.NO_CONTENT);
	}

	@ExceptionHandler(StatementFetchException.class)
	public ResponseEntity<String> handleStatementFetchException(StatementFetchException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleGeneralException(Exception ex) {
		return new ResponseEntity<>("An unexpected error occurred: " + ex.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
