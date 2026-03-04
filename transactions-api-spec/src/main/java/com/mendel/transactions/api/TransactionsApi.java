package com.mendel.transactions.api;

import com.mendel.transactions.model.Transaction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
public interface TransactionsApi {

  @PutMapping("/{id}")
  ResponseEntity<?> createOrUpdateTransaction(
      @RequestBody final Transaction newTransaction, @PathVariable final Long id);

  @GetMapping("/types/{type}")
  ResponseEntity<?> getTransactionsByType(@PathVariable final String type);

  @GetMapping("/sum/{id}")
  ResponseEntity<?> getTransactionsTotal(@PathVariable final Long id);
}
