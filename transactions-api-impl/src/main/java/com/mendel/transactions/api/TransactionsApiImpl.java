package com.mendel.transactions.api;

import com.mendel.transactions.TransactionBusinessService;
import com.mendel.transactions.model.Transaction;
import com.mendel.transactions.model.TransactionDTO;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class TransactionsApiImpl implements TransactionsApi {

  @Autowired private TransactionBusinessService transactionBusinessService;

  @Override
  public ResponseEntity<?> createOrUpdateTransaction(
      final Transaction newTransaction, final Long id) {

    TransactionDTO transactionDTO =
        TransactionDTO.builder()
            .id(id)
            .amount(newTransaction.getAmount())
            .type(newTransaction.getType().toLowerCase())
            .parentTransactionId(newTransaction.getParentTransactionId())
            .build();

    TransactionDTO resultDTO =
        this.transactionBusinessService.createOrUpdateTransaction(transactionDTO);

    if (resultDTO.getIsNew()) {
      Transaction result =
          Transaction.builder()
              .amount(resultDTO.getAmount())
              .type(resultDTO.getType())
              .parentTransactionId(resultDTO.getParentTransactionId())
              .build();
      URI location =
          ServletUriComponentsBuilder.fromCurrentRequest()
              .path("/{id}")
              .buildAndExpand(resultDTO.getId())
              .toUri();
      return ResponseEntity.created(location).body(result);
    } else {
      return ResponseEntity.noContent().build();
    }
  }

  @Override
  public ResponseEntity<?> getTransactionsByType(String type) {
    return null;
  }

  @Override
  public ResponseEntity<?> getTransactionsTotal(Long id) {
    return null;
  }
}
