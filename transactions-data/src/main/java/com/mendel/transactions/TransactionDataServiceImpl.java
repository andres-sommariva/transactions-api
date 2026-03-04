package com.mendel.transactions;

import com.mendel.transactions.model.TransactionEntity;
import com.mendel.transactions.model.TransactionRecord;
import java.time.Instant;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class TransactionDataServiceImpl implements TransactionDataService {

  private final Map<Long, TransactionEntity> transactions = new ConcurrentHashMap<>();
  private final Map<String, List<Long>> transactionIdsByType = new ConcurrentHashMap<>();
  private final Map<Long, List<Long>> transactionIdsByParent = new ConcurrentHashMap<>();

  @Override
  public TransactionRecord create(final TransactionRecord transaction) {

    validateTransaction(transaction, false);

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

    addToIndex(newTransaction);

    return transaction;
  }

  @Override
  public TransactionRecord read(Long transactionId, boolean loadDescendants) {
    TransactionEntity currentTransaction = this.transactions.get(transactionId);

    if (currentTransaction == null) {
      throw new NoSuchElementException("Transaction not found.");
    }

    List<TransactionRecord> descendantTransactions = null;
    if (loadDescendants) {
      List<Long> descendantTransactionIds =
          getDescendantTransactionIds(transactionId, transactionId);
      descendantTransactions =
          descendantTransactionIds.stream()
              .map(this.transactions::get)
              .map(this::mapToTransactionRecord)
              .toList();
    }

    return TransactionRecord.builder()
        .id(currentTransaction.getId())
        .amount(currentTransaction.getAmount())
        .type(currentTransaction.getType())
        .parentTransactionId(currentTransaction.getParentId())
        .descendants(Optional.ofNullable(descendantTransactions))
        .build();
  }

  @Override
  public TransactionRecord update(final TransactionRecord transaction) {
    TransactionEntity currentTransaction = this.transactions.get(transaction.getId());

    if (currentTransaction == null) {
      throw new NoSuchElementException();
    }

    validateTransaction(transaction, true);

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

    updateIndexes(currentTransaction, updatedTransaction);

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
        .map(this.transactions::get)
        .map(this::mapToTransactionRecord)
        .toList();
  }

  /**
   * Recursively retrieves all descendant transaction IDs for a given parent. Traverses the
   * parent-child hierarchy to collect all descendant IDs.
   *
   * @param rootId the root transaction ID to exclude from results
   * @param parentId the parent transaction ID to start traversal from
   * @return list of all descendant transaction IDs
   */
  private List<Long> getDescendantTransactionIds(Long rootId, Long parentId) {
    ArrayList<Long> result = new ArrayList<>();
    List<Long> childrenIds = this.transactionIdsByParent.get(parentId);

    if (childrenIds != null) {
      for (Long childrenId : childrenIds) {
        result.addAll(getDescendantTransactionIds(rootId, childrenId));
      }
    }
    if (!rootId.equals(parentId)) {
      result.add(parentId);
    }

    return result;
  }

  /**
   * Adds a new transaction to both type and parent-child indexes. Ensures the transaction is
   * discoverable by type and parent relationships.
   *
   * @param newTransaction the transaction entity to add to indexes
   */
  private void addToIndex(TransactionEntity newTransaction) {
    addTransactionByType(newTransaction.getId(), newTransaction.getType());
    addTransactionChildren(newTransaction.getId(), newTransaction.getParentId());
  }

  /**
   * Updates the type and parent-child indexes when a transaction is modified. Removes old index
   * entries and adds new ones based on changes.
   *
   * @param currentTransaction the original transaction entity before update
   * @param updatedTransaction the new transaction entity after update
   */
  private void updateIndexes(
      TransactionEntity currentTransaction, TransactionEntity updatedTransaction) {
    if (!currentTransaction.getType().equals(updatedTransaction.getType())) {
      removeTransactionByType(currentTransaction.getId(), currentTransaction.getType());
      addTransactionByType(updatedTransaction.getId(), updatedTransaction.getType());
    }
    if (!Objects.equals(currentTransaction.getParentId(), updatedTransaction.getParentId())) {
      removeTransactionChildren(currentTransaction.getId(), currentTransaction.getParentId());
      addTransactionChildren(updatedTransaction.getId(), updatedTransaction.getParentId());
    }
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

  /**
   * Adds a transaction ID to the parent-based children index. Creates the parent entry if it
   * doesn't exist.
   *
   * @param id the transaction ID to add as a child
   * @param parentId the parent transaction ID to categorize under
   */
  private void addTransactionChildren(Long id, Long parentId) {
    if (parentId == null) {
      return;
    }
    this.transactionIdsByParent.computeIfAbsent(parentId, s -> new ArrayList<>());
    this.transactionIdsByParent.computeIfPresent(
        parentId,
        (s, transactionIds) -> {
          transactionIds.add(id);
          return transactionIds;
        });
  }

  /**
   * Removes a transaction ID from the parent-based children index. Removes the parent entry if no
   * children remain.
   *
   * @param id the transaction ID to remove from children
   * @param parentId the parent transaction ID to remove from
   */
  private void removeTransactionChildren(Long id, Long parentId) {
    if (parentId == null) {
      return;
    }
    this.transactionIdsByParent.computeIfPresent(
        parentId,
        (s, transactionIds) -> {
          transactionIds.remove(id);
          return transactionIds;
        });
    if (this.transactionIdsByParent.containsKey(parentId)
        && this.transactionIdsByParent.get(parentId).isEmpty()) {
      this.transactionIdsByParent.remove(parentId);
    }
  }

  /**
   * Validates a transaction record for business rules. Checks if parent transaction exists and
   * optionally validates for cyclic dependencies.
   *
   * @param transaction the transaction to validate
   * @param checkCycle whether to perform cycle validation (for updates)
   * @throws NoSuchElementException if parent transaction is not found
   * @throws IllegalArgumentException if cyclic dependency is detected
   */
  private void validateTransaction(TransactionRecord transaction, boolean checkCycle) {
    if (transaction.getParentTransactionId() != null) {
      if (this.transactions.get(transaction.getParentTransactionId()) == null) {
        throw new NoSuchElementException("Parent transaction not found.");
      }
      if (checkCycle) {
        validateTransactionCycle(transaction);
      }
    }
  }

  /**
   * Validates that a transaction does not create a cyclic dependency. Traverses up the parent
   * hierarchy to ensure the transaction does not eventually reference itself as an ancestor.
   *
   * @param transaction the transaction to validate for cycles
   * @throws IllegalArgumentException if cyclic dependency is detected
   */
  private void validateTransactionCycle(TransactionRecord transaction) {
    boolean hasCycle = false;
    TransactionEntity ancestorTransaction =
        this.transactions.get(transaction.getParentTransactionId());
    while (ancestorTransaction.getParentId() != null) {
      if (transaction.getId().equals(ancestorTransaction.getParentId())) {
        hasCycle = true;
        break;
      }
      ancestorTransaction = this.transactions.get(ancestorTransaction.getParentId());
    }
    if (hasCycle) {
      throw new IllegalArgumentException(
          "Transaction can not be saved due to cyclic dependency for the specified 'parent_id'.");
    }
  }

  /**
   * Maps a TransactionEntity to a TransactionRecord. Converts between the internal entity model and
   * the external record model.
   *
   * @param transactionEntity the entity to map
   * @return the corresponding transaction record
   */
  private TransactionRecord mapToTransactionRecord(TransactionEntity transactionEntity) {
    return TransactionRecord.builder()
        .id(transactionEntity.getId())
        .amount(transactionEntity.getAmount())
        .type(transactionEntity.getType())
        .parentTransactionId(transactionEntity.getParentId())
        .build();
  }
}
