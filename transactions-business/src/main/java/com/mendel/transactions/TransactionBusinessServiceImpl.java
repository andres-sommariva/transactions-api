package com.mendel.transactions;

import com.mendel.transactions.model.TransactionDTO;
import com.mendel.transactions.model.TransactionRecord;
import java.util.List;
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
  public List<Long> getTransactionsByType(String type) {
    List<TransactionRecord> transactions = this.transactionDataService.getTransactionsByType(type);
    return transactions.stream().map(TransactionRecord::getId).toList();
  }

  @Override
  public Double getTransactionsTotal(Long transactionId) {
    TransactionRecord transaction = this.transactionDataService.read(transactionId, true);

    Double descendantsTotalAmount =
        (transaction.getDescendants().isPresent()
            ? transaction.getDescendants().get().stream()
                .map(TransactionRecord::getAmount)
                .mapToDouble(Double::doubleValue)
                .sum()
            : Double.parseDouble("0.0"));

    return Double.sum(transaction.getAmount(), descendantsTotalAmount);
  }
}
