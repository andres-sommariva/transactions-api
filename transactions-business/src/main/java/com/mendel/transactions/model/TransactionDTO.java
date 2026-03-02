package com.mendel.transactions.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TransactionDTO {

  private Long id;
  private Double amount;
  private String type;
  private Long parentTransactionId;
}
