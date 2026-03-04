package com.mendel.transactions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mendel.transactions.model.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionsIT {

  @Autowired private MockMvc mockMvc;

  @Test
  void testCreateTransaction() throws Exception {
    // given
    long transactionId = System.currentTimeMillis();
    Transaction transactionDetails =
        Transaction.builder().amount(Double.parseDouble("9.99")).type("purchase").build();
    String transactionDetailsJson = toJson(transactionDetails);

    // when & then
    this.mockMvc
        .perform(
            put("/transactions/{id}", transactionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(transactionDetailsJson))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(transactionDetailsJson));
  }

  @Test
  void testCreateTransactionWithInvalidAmount() throws Exception {
    // given
    long transactionId = System.currentTimeMillis();
    Transaction transactionDetails = Transaction.builder().type("purchase").build();
    String transactionDetailsJson = toJson(transactionDetails);

    // when & then
    this.mockMvc
        .perform(
            put("/transactions/{id}", transactionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(transactionDetailsJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testCreateTransactionWithInvalidType() throws Exception {
    // given
    long transactionId = System.currentTimeMillis();
    Transaction transactionDetails =
        Transaction.builder().amount(Double.parseDouble("9.99")).build();
    String transactionDetailsJson = toJson(transactionDetails);

    // when & then
    this.mockMvc
        .perform(
            put("/transactions/{id}", transactionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(transactionDetailsJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testCreateTransactionWithInvalidTransactionId() throws Exception {
    // given
    Transaction transactionDetails =
        Transaction.builder().amount(Double.parseDouble("9.99")).type("purchase").build();
    String transactionDetailsJson = toJson(transactionDetails);

    // when & then
    this.mockMvc
        .perform(
            put("/transactions/{id}", "null")
                .contentType(MediaType.APPLICATION_JSON)
                .content(transactionDetailsJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testUpdateTransaction() throws Exception {
    // given
    long transactionId = System.currentTimeMillis();
    Transaction transactionDetails =
        Transaction.builder().amount(Double.parseDouble("9.99")).type("purchase").build();
    String transactionDetailsJson = toJson(transactionDetails);

    // create
    this.mockMvc
        .perform(
            put("/transactions/{id}", transactionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(transactionDetailsJson))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(transactionDetailsJson))
        .andReturn();

    // update
    this.mockMvc
        .perform(
            put("/transactions/{id}", transactionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(transactionDetailsJson))
        .andExpect(status().isNoContent())
        .andReturn();
  }

  private String toJson(Transaction transaction) {
    String jsonString = null;
    ObjectMapper mapper = new ObjectMapper();
    try {
      jsonString = mapper.writeValueAsString(transaction);
    } catch (JacksonException e) {
      e.printStackTrace();
    }
    return jsonString;
  }
}
