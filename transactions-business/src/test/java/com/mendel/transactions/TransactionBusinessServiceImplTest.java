package com.mendel.transactions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.mendel.transactions.model.TransactionDTO;
import com.mendel.transactions.model.TransactionRecord;
import java.util.List;
import java.util.Optional;
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

  @Test
  void testGetTransactionsByTypeWithEmptyList() {
    // given
    String type = "purchase";
    doReturn(List.of()).when(this.transactionDataService).getTransactionsByType(type);

    // when
    List<Long> transactionIds = this.transactionBusinessService.getTransactionsByType(type);

    // then
    assertNotNull(transactionIds);
    assertTrue(transactionIds.isEmpty());
  }

  @Test
  void testGetTransactionsByTypeWithValues() {
    // given
    String type = "purchase";
    doReturn(
            List.of(
                TransactionRecord.builder().id(1L).build(),
                TransactionRecord.builder().id(2L).build()))
        .when(this.transactionDataService)
        .getTransactionsByType(type);

    // when
    List<Long> transactionIds = this.transactionBusinessService.getTransactionsByType(type);

    // then
    assertNotNull(transactionIds);
    assertFalse(transactionIds.isEmpty());
    assertEquals(2, transactionIds.size());
  }

  @Test
  void testGetTransactionsTotalWithoutDescendants() {
    // given
    doReturn(
            TransactionRecord.builder()
                .id(1L)
                .amount(Double.parseDouble("10.00"))
                .descendants(Optional.empty())
                .build())
        .when(this.transactionDataService)
        .read(1L, true);

    // when
    Double total = this.transactionBusinessService.getTransactionsTotal(1L);

    // then
    assertEquals(Double.parseDouble("10.00"), total);
  }

  @Test
  void testGetTransactionsTotalWithDescendants() {
    // given
    doReturn(
            TransactionRecord.builder()
                .id(1L)
                .amount(Double.parseDouble("10.00"))
                .descendants(
                    Optional.of(
                        List.of(
                            TransactionRecord.builder()
                                .id(2L)
                                .amount(Double.parseDouble("5.01"))
                                .parentTransactionId(1L)
                                .build(),
                            TransactionRecord.builder()
                                .id(3L)
                                .amount(Double.parseDouble("3.01"))
                                .parentTransactionId(1L)
                                .build())))
                .build())
        .when(this.transactionDataService)
        .read(1L, true);

    // when
    Double total = this.transactionBusinessService.getTransactionsTotal(1L);

    // then
    assertEquals(Double.parseDouble("18.02"), total);
  }
}
