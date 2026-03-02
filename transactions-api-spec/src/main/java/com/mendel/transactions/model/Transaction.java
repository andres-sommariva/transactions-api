package com.mendel.transactions.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Transaction {

  @JsonProperty("amount")
  private Double amount;

  @JsonProperty("type")
  private String type;

  @JsonProperty("parent_id")
  private Long parentTransactionId;
}
