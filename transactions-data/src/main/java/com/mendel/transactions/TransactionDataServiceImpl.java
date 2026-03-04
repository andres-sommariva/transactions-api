package com.mendel.transactions;

import com.mendel.transactions.model.TransactionEntity;
import com.mendel.transactions.model.TransactionRecord;
import java.time.Instant;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class TransactionDataServiceImpl implements TransactionDataService {

  private final Map<Long, TransactionEntity> transactions = new ConcurrentHashMap<>();
  private final Map<String, List<Long>> transactionIdsByType = new ConcurrentHashMap<>();

  @Override
  public TransactionRecord create(final TransactionRecord transaction) {
    Instant now = Instant.now();
    TransactionEntity newTransaction =
        TransactionEntity.builder()
            .id(transaction.getId())
            .amount(transaction.getAmount())
            .type(transaction.getType())
            .parentId(transaction.getParentTransactionId())
            .createdAt(now)
            .updatedAt(now)
            .build();

    TransactionEntity result =
        this.transactions.putIfAbsent(newTransaction.getId(), newTransaction);

    if (result != null) {
      throw new ConcurrentModificationException();
    }

    addTransactionByType(transaction.getId(), transaction.getType());

    return transaction;
  }

  @Override
  public TransactionRecord read(Long transactionId, boolean loadChildren) {
    TransactionEntity currentTransaction = this.transactions.get(transactionId);

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
    TransactionEntity currentTransaction = this.transactions.get(transaction.getId());

    if (currentTransaction == null) {
      throw new NoSuchElementException();
    }

    Instant now = Instant.now();
    TransactionEntity updatedTransaction =
        TransactionEntity.builder()
            .id(transaction.getId())
            .amount(transaction.getAmount())
            .type(transaction.getType())
            .parentId(transaction.getParentTransactionId())
            .createdAt(currentTransaction.getCreatedAt())
            .updatedAt(now)
            .build();

    boolean updated =
        this.transactions.replace(
            updatedTransaction.getId(), currentTransaction, updatedTransaction);

    if (!updated) {
      throw new ConcurrentModificationException();
    }

    if (!currentTransaction.getType().equals(updatedTransaction.getType())) {
      removeTransactionByType(transaction.getId(), currentTransaction.getType());
      addTransactionByType(transaction.getId(), updatedTransaction.getType());
    }

    return transaction;
  }

  @Override
  public boolean exists(final Long transactionId) {
    return this.transactions.get(transactionId) != null;
  }

  @Override
  public List<TransactionRecord> getTransactionsByType(final String type) {
    List<Long> transactionIds = this.transactionIdsByType.get(type);

    if (transactionIds == null) {
      return List.of();
    }

    return transactionIds.stream()
        .map(id -> this.transactions.get(id))
        .map(
            transactionEntity ->
                TransactionRecord.builder()
                    .id(transactionEntity.getId())
                    .amount(transactionEntity.getAmount())
                    .type(transactionEntity.getType())
                    .parentTransactionId(transactionEntity.getParentId())
                    .build())
        .toList();
  }

  /**
   * Adds a transaction ID to the type-based index. Creates the type entry if it doesn't exist.
   *
   * @param id the transaction ID to add
   * @param type the transaction type to categorize under
   */
  private void addTransactionByType(Long id, String type) {
    this.transactionIdsByType.computeIfAbsent(type, s -> new ArrayList<>());
    this.transactionIdsByType.computeIfPresent(
        type,
        (s, transactionIds) -> {
          transactionIds.add(id);
          return transactionIds;
        });
  }

  /**
   * Removes a transaction ID from the type-based index.
   *
   * @param id the transaction ID to remove
   * @param type the transaction type to remove from
   */
  private void removeTransactionByType(Long id, String type) {
    this.transactionIdsByType.computeIfPresent(
        type,
        (s, transactionIds) -> {
          transactionIds.remove(id);
          return transactionIds;
        });
    if (this.transactionIdsByType.containsKey(type)
        && this.transactionIdsByType.get(type).isEmpty()) {
      this.transactionIdsByType.remove(type);
    }
  }
}
