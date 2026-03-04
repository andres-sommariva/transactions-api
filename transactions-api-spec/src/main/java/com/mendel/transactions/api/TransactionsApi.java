package com.mendel.transactions.api;

import com.mendel.transactions.model.Transaction;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
      @Valid @RequestBody final Transaction newTransaction, @NotNull @PathVariable final Long id);

  @GetMapping("/types/{type}")
  ResponseEntity<?> getTransactionsByType(@PathVariable final String type);

  @GetMapping("/sum/{id}")
  ResponseEntity<?> getTransactionsTotal(@PathVariable final Long id);
}
