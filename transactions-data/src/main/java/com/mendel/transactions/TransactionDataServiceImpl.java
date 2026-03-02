package com.mendel.transactions;

import com.mendel.transactions.model.TransactionRecord;
import java.util.List;

public class TransactionDataServiceImpl implements TransactionDataService {
  @Override
  public TransactionRecord create(TransactionRecord transaction) {
    return null;
  }

  @Override
  public TransactionRecord update(TransactionRecord transaction) {
    return null;
  }

  @Override
  public boolean exists(Long transactionId) {
    return false;
  }

  @Override
  public List<TransactionRecord> getTransactionsByType(String type) {
    return List.of();
  }
}
