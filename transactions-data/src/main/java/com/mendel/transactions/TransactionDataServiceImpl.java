package com.mendel.transactions;

import com.mendel.transactions.model.TransactionEntity;
import com.mendel.transactions.model.TransactionRecord;
import java.time.Instant;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class TransactionDataServiceImpl implements TransactionDataService {

  private final Map<Long, TransactionEntity> data = new ConcurrentHashMap<>();

  @Override
  public TransactionRecord create(final TransactionRecord transaction) {
    Instant now = Instant.now();
    TransactionEntity newTransaction = TransactionEntity.builder()
        .id(transaction.getId())
        .amount(transaction.getAmount())
        .type(transaction.getType())
        .parentId(transaction.getParentTransactionId())
        .createdAt(now)
        .updatedAt(now)
        .build();

    TransactionEntity result = this.data.putIfAbsent(newTransaction.getId(), newTransaction);

    if (result != null) {
      throw new ConcurrentModificationException();
    }

    return transaction;
  }

  @Override
  public TransactionRecord read(Long transactionId, boolean loadChildren) {
    TransactionEntity currentTransaction = this.data.get(transactionId);

    if (currentTransaction == null) {
      throw new NoSuchElementException();
    }

    if (loadChildren) {
      // TODO: load children transactions
    }

    return TransactionRecord.builder()
        .id(currentTransaction.getId())
        .amount(currentTransaction.getAmount())
        .type(currentTransaction.getType())
        .parentTransactionId(currentTransaction.getParentId())
        .build();
  }

  @Override
  public TransactionRecord update(final TransactionRecord transaction) {
    TransactionEntity currentTransaction = this.data.get(transaction.getId());

    if (currentTransaction == null) {
      throw new NoSuchElementException();
    }

    Instant now = Instant.now();
    TransactionEntity updatedTransaction = TransactionEntity.builder()
        .id(transaction.getId())
        .amount(transaction.getAmount())
        .type(transaction.getType())
        .parentId(transaction.getParentTransactionId())
        .createdAt(currentTransaction.getCreatedAt())
        .updatedAt(now)
        .build();

    boolean updated = this.data.replace(updatedTransaction.getId(), currentTransaction, updatedTransaction);

    if (!updated) {
      throw new ConcurrentModificationException();
    }

    return transaction;
  }

  @Override
  public boolean exists(final Long transactionId) {
    return this.data.get(transactionId) != null;
  }

  @Override
  public List<TransactionRecord> getTransactionsByType(final String type) {
    return List.of();
  }
}
