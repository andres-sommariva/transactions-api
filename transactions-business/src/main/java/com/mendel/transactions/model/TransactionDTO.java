package com.mendel.transactions.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class TransactionDTO {

  private Long id;
  private Double amount;
  private String type;
  private Long parentTransactionId;
  private Boolean isNew;
}
