package com.mendel.transactions;

import com.mendel.transactions.model.TransactionDTO;
import java.util.List;

import com.mendel.transactions.model.TransactionRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionBusinessServiceImpl implements TransactionBusinessService {

  @Autowired private TransactionDataService transactionDataService;

  @Override
  public TransactionDTO createOrUpdateTransaction(TransactionDTO transaction) {
    TransactionRecord result;
    boolean isNew = true;

    TransactionRecord transactionRecord =
        TransactionRecord.builder()
            .id(transaction.getId())
            .amount(transaction.getAmount())
            .type(transaction.getType())
            .parentTransactionId(transaction.getParentTransactionId())
            .build();

    if (this.transactionDataService.exists(transactionRecord.getId())) {
      result = this.transactionDataService.update(transactionRecord);
      isNew = false;
    } else {
      result = this.transactionDataService.create(transactionRecord);
    }

    return TransactionDTO.builder()
        .id(result.getId())
        .amount(result.getAmount())
        .type(result.getType())
        .parentTransactionId(result.getParentTransactionId())
        .isNew(isNew)
        .build();
  }

  @Override
  public List<TransactionDTO> getTransactionsByType(String type) {
    return List.of();
  }

  @Override
  public Double getTransactionsTotal(Long transactionId) {
    return 0.0;
  }
}
