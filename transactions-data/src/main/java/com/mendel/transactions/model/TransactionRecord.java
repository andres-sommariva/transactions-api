package com.mendel.transactions.model;

import java.time.Instant;
import lombok.Data;

@Data
public class TransactionRecord {

  private Long id;
  private Double amount;
  private String type;
  private Long parentId;
  private Instant createdAt;
  private Instant updatedAt;
}
