package com.mendel.transactions.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@EqualsAndHashCode
public class TransactionDTO {

  private Long id;
  private Double amount;
  private String type;
  private Long parentTransactionId;

  @Setter(AccessLevel.NONE)
  private Boolean isNew;
}
