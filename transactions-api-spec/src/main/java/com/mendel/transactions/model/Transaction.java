package com.mendel.transactions.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

  @NotNull
  @JsonProperty("amount")
  private Double amount;

  @NotEmpty
  @Pattern(regexp = "^[a-zA-Z0-9]+$")
  @JsonProperty("type")
  private String type;

  @JsonProperty("parent_id")
  private Long parentTransactionId;
}
