package com.mendel.transactions;

import com.mendel.transactions.model.TransactionDTO;
import java.util.List;

/**
 * Service interface for managing transactions. Provides operations to create/update transactions,
 * retrieve transactions by type, and calculate transaction totals.
 */
public interface TransactionBusinessService {

  /**
   * Creates a new transaction or updates an existing one.
   *
   * @param transaction the transaction data to create or update
   * @return the created or updated transaction
   */
  TransactionDTO createOrUpdateTransaction(final TransactionDTO transaction);

  /**
   * Retrieves all transactions of a specific type.
   *
   * @param type the transaction type to filter by
   * @return a list of transaction ids matching the specified type
   */
  List<Long> getTransactionsByType(final String type);

  /**
   * Calculates the total amount for a transaction including all its child transactions.
   *
   * @param transactionId the ID of the transaction to calculate the total for
   * @return the total amount including all child transactions
   */
  Double getTransactionsTotal(final Long transactionId);
}
