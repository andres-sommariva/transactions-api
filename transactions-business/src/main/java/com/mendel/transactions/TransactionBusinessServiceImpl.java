package com.mendel.transactions;

import com.mendel.transactions.model.TransactionDTO;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TransactionBusinessServiceImpl implements TransactionBusinessService {
  @Override
  public TransactionDTO createOrUpdateTransaction(TransactionDTO transaction) {
    return null;
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
