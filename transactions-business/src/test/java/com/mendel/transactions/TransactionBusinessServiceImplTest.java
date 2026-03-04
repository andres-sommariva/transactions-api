package com.mendel.transactions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.mendel.transactions.model.TransactionDTO;
import com.mendel.transactions.model.TransactionRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionBusinessServiceImplTest {

  @Mock private TransactionDataService transactionDataService;
  @InjectMocks private TransactionBusinessServiceImpl transactionBusinessService;

  @Test
  void testCreateOrUpdateTransactionWithNewTransactionIsSuccessful() {
    // given
    Long id = System.currentTimeMillis();
    TransactionDTO transactionDTO =
        TransactionDTO.builder().id(id).amount(Double.parseDouble("9.99")).type("purchase").build();
    TransactionRecord transactionRecord =
        TransactionRecord.builder()
            .id(id)
            .amount(Double.parseDouble("9.99"))
            .type("purchase")
            .build();
    doReturn(false).when(this.transactionDataService).exists(id);
    doReturn(transactionRecord).when(this.transactionDataService).create(any());

    // when
    TransactionDTO resultTransactionDTO =
        this.transactionBusinessService.createOrUpdateTransaction(transactionDTO);

    // then
    assertEquals(transactionDTO.getId(), resultTransactionDTO.getId());
    assertEquals(transactionDTO.getAmount(), resultTransactionDTO.getAmount());
    assertEquals(transactionDTO.getType(), resultTransactionDTO.getType());
    assertEquals(
        transactionDTO.getParentTransactionId(), resultTransactionDTO.getParentTransactionId());
    assertTrue(resultTransactionDTO.getIsNew());
    verify(this.transactionDataService).create(any());
    verify(this.transactionDataService, never()).update(any());
  }

  @Test
  void testCreateOrUpdateTransactionWithExistingTransactionIsSuccessful() {
    // given
    Long id = System.currentTimeMillis();
    TransactionDTO transactionDTO =
        TransactionDTO.builder().id(id).amount(Double.parseDouble("9.99")).type("purchase").build();
    TransactionRecord transactionRecord =
        TransactionRecord.builder()
            .id(id)
            .amount(Double.parseDouble("9.99"))
            .type("purchase")
            .build();
    doReturn(true).when(this.transactionDataService).exists(id);
    doReturn(transactionRecord).when(this.transactionDataService).update(any());

    // when
    TransactionDTO resultTransactionDTO =
        this.transactionBusinessService.createOrUpdateTransaction(transactionDTO);

    // then
    assertEquals(transactionDTO.getId(), resultTransactionDTO.getId());
    assertEquals(transactionDTO.getAmount(), resultTransactionDTO.getAmount());
    assertEquals(transactionDTO.getType(), resultTransactionDTO.getType());
    assertEquals(
        transactionDTO.getParentTransactionId(), resultTransactionDTO.getParentTransactionId());
    assertFalse(resultTransactionDTO.getIsNew());
    verify(this.transactionDataService, never()).create(any());
    verify(this.transactionDataService).update(any());
  }
}
