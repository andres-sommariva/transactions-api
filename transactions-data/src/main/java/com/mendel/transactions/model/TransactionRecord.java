package com.mendel.transactions.model;

import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class TransactionRecord {

  private Long id;
  private Double amount;
  private String type;
  private Long parentTransactionId;
  private Optional<List<TransactionRecord>> descendants = Optional.empty();
}
