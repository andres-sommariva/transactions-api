package com.mendel.transactions.model;

import java.time.Instant;
import lombok.*;

@Data
@Builder
@EqualsAndHashCode
public class TransactionEntity {

  @Setter(AccessLevel.NONE)
  private Long id;

  private Double amount;
  private String type;
  private Long parentId;
  private Instant createdAt;
  private Instant updatedAt;
}
