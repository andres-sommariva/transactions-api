package com.mendel.transactions;

import static org.junit.jupiter.api.Assertions.*;

import com.mendel.transactions.model.TransactionRecord;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionDataServiceImplTest {

  @InjectMocks private TransactionDataServiceImpl transactionDataService;

  @Test
  void testCreateWithSuccess() {
    // given
    Long id = System.currentTimeMillis();
    TransactionRecord transaction = TransactionRecord.builder().id(id).build();

    // when
    TransactionRecord newTransaction = this.transactionDataService.create(transaction);

    // then
    assertEquals(transaction, newTransaction);
  }

  @Test
  void testCreateWithException() {
    // given
    Long id = System.currentTimeMillis();
    TransactionRecord transaction = TransactionRecord.builder().id(id).build();
    this.transactionDataService.create(transaction);

    // when & then
    assertThrows(
        ConcurrentModificationException.class,
        () -> this.transactionDataService.create(transaction));
  }

  @Test
  void testReadWithSuccess() {
    // given
    Long id = System.currentTimeMillis();
    TransactionRecord transaction =
        this.transactionDataService.create(
            TransactionRecord.builder()
                .id(id)
                .amount(Double.parseDouble("9.99"))
                .type("purchase")
                .build());

    // when
    TransactionRecord transactionRead = this.transactionDataService.read(id, false);

    // then
    assertEquals(transaction, transactionRead);
  }

  @Test
  void testReadWithException() {
    // given
    Long id = System.currentTimeMillis();

    // when & then
    assertThrows(NoSuchElementException.class, () -> this.transactionDataService.read(id, false));
  }

  @Test
  void testUpdateWithSuccess() {
    // given
    Long id = System.currentTimeMillis();
    TransactionRecord transaction = TransactionRecord.builder().id(id).build();
    this.transactionDataService.create(transaction);

    // when
    transaction =
        TransactionRecord.builder()
            .id(id)
            .type("purchase")
            .amount(Double.parseDouble("9.99"))
            .build();
    TransactionRecord updatedTransaction = this.transactionDataService.update(transaction);

    // then
    assertEquals(transaction, updatedTransaction);
  }

  @Test
  void testUpdateWithException_transactionDoesNotExists() {
    // given
    Long id = System.currentTimeMillis();
    TransactionRecord transaction = TransactionRecord.builder().id(id).build();

    // when & then
    assertThrows(
        NoSuchElementException.class, () -> this.transactionDataService.update(transaction));
  }

  @Test
  void testExistsWhenTrue() {
    // given
    Long id = System.currentTimeMillis();
    TransactionRecord transaction = TransactionRecord.builder().id(id).build();
    this.transactionDataService.create(transaction);

    // when
    boolean result = this.transactionDataService.exists(id);

    // then
    assertTrue(result);
  }

  @Test
  void testExistsWhenFalse() {
    // given
    Long id = System.currentTimeMillis();

    // when
    boolean result = this.transactionDataService.exists(id);

    // then
    assertFalse(result);
  }
}
