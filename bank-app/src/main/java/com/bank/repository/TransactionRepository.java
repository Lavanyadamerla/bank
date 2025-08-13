package com.bank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bank.entity.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	@Query("SELECT t FROM Transaction t " +
		       "WHERE (t.fromAcc = :accNumber OR t.toAcc = :accNumber) " +
		       "AND EXTRACT(MONTH FROM t.transactionTime) = :month " +
		       "AND EXTRACT(YEAR FROM t.transactionTime) = :year " +
		       "AND t.transactionType = :transactionType")
		List<Transaction> findByAccountAndMonthYearAndType(
		        @Param("accNumber") long accNumber,
		        @Param("transactionType") String transactionType,
		        @Param("month") int month,
		        @Param("year") int year);


}
