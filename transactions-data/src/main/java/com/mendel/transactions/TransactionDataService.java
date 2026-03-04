package com.mendel.transactions;

import com.mendel.transactions.model.TransactionRecord;
import java.util.List;

/**
 * Data service interface for transaction persistence operations. Provides CRUD operations and query
 * methods for transaction records.
 */
public interface TransactionDataService {

  /**
   * Creates a new transaction record in the data store.
   *
   * @param transaction the transaction record to create
   * @return the created transaction record with generated ID
   */
  TransactionRecord create(final TransactionRecord transaction);

  /**
   * Retrieves a transaction record by its ID.
   *
   * @param transactionId the ID of the transaction to retrieve
   * @param loadChildren whether to load child transactions recursively
   * @return the transaction record if found, null otherwise
   */
  TransactionRecord read(final Long transactionId, boolean loadChildren);

  /**
   * Updates an existing transaction record in the data store.
   *
   * @param transaction the transaction record to update
   * @return the updated transaction record
   */
  TransactionRecord update(final TransactionRecord transaction);

  /**
   * Checks if a transaction record exists by its ID.
   *
   * @param transactionId the ID of the transaction to check
   * @return true if the transaction exists, false otherwise
   */
  boolean exists(final Long transactionId);

  /**
   * Retrieves all transaction records of a specific type.
   *
   * @param type the transaction type to filter by
   * @return a list of transaction records matching the specified type
   */
  List<TransactionRecord> getTransactionsByType(final String type);
}
