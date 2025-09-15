package com.mybank.transaction.service;

import com.mybank.transaction.dao.TransactionDao;
import com.mybank.transaction.domain.PageRequest;
import com.mybank.transaction.domain.PageResponse;
import com.mybank.transaction.domain.Transaction;
import com.mybank.transaction.domain.TransactionRequest;
import com.mybank.transaction.exception.TransactionNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 交易服务单元测试
 */

@SpringBootTest
@Rollback
@Transactional
class TransactionServiceTest {
    @Autowired
    private TransactionDao transactionMapper;

    @Autowired
    private TransactionService transactionService;

    @Autowired private TransactionDao transactionDao;

    private TransactionRequest validRequest1, validRequest2;

    @BeforeEach
    void setUp() {
        validRequest1 = TransactionRequest.builder()
                .accountNumber("1111111111111111")
                .transactionType("DEPOSIT")
                .amount(new BigDecimal("1000.00"))
                .currency("CNY")
                .description("test deposit")
                .category("SALARY")
                .build();
        validRequest2 = TransactionRequest.builder()
                .accountNumber("2222222222222222")
                .transactionType("TRANSFER")
                .amount(new BigDecimal("2000.00"))
                .currency("CNY")
                .description("test food")
                .category("FOOD")
                .status("UNCOMPLETE")
                .build();
    }

    @Test
    void createTransaction_success() {
        Long id = transactionService.createTransaction(validRequest1).getId();
        assertNotNull(id);

        Transaction response=transactionMapper.selectById(id);
        // Then
        assertEquals(validRequest1.getAccountNumber(), response.getAccountNumber());
        assertEquals(validRequest1.getTransactionType(), response.getTransactionType());
        assertEquals(validRequest1.getAmount(), response.getAmount());
        assertEquals(validRequest1.getCurrency(), response.getCurrency());
        assertEquals(validRequest1.getDescription(), response.getDescription());
        assertEquals(validRequest1.getCategory(), response.getCategory());
    }

    @Test
    void updateTransaction_failure() {
        Long id = transactionService.createTransaction(validRequest1).getId();
        assertNotNull(id);
        transactionService.deleteTransaction(id);
        Assertions.assertThrows(TransactionNotFoundException.class, () ->transactionService.updateTransaction(id, validRequest2) , "TransactionNotFoundException error was expected");
    }

    @Test
    void updateTransaction_success(){
        Long id = transactionService.createTransaction(validRequest1).getId();
        assertNotNull(id);

        transactionService.updateTransaction(id, validRequest2);
        Transaction updated = transactionDao.selectById(id);
        assertEquals("TRANSFER", updated.getTransactionType());
        assertEquals(new BigDecimal("2000.00"), updated.getAmount());
        assertEquals("test food", updated.getDescription());
        assertEquals("FOOD", updated.getCategory());
    }

    @Test
    void deleteTransaction_success() {
        Long id = transactionService.createTransaction(validRequest1).getId();
        assertNotNull(id);

        transactionService.deleteTransaction(id);
        Transaction deleted = transactionDao.selectById(id);
        assertNull(deleted);

    }

    @Test
    void deleteTransaction_failure() {
        Long id = transactionService.createTransaction(validRequest1).getId();
        assertNotNull(id);
        transactionService.deleteTransaction(id);
        Assertions.assertThrows(TransactionNotFoundException.class, () -> transactionService.deleteTransaction(id), "TransactionNotFoundException error was expected");
    }

    @Test
    void getAllTransactions() {
        for (int i = 0; i < 11; ++i) transactionService.createTransaction(validRequest1);
        // Given
        PageRequest pageRequest = new PageRequest(2, 10);
        // When
        PageResponse<Transaction> response = transactionService.getAllTransactions(pageRequest);
        var trans = response.getContent().get(0);

        assertEquals(trans.getAccountNumber(), "1111111111111111");
        assertEquals(trans.getTransactionType(), "DEPOSIT");
        assertEquals(trans.getAmount(), new BigDecimal("1000.00"));
        assertEquals(1, response.getContent().size());
        assertEquals(11, response.getTotalElements());
        assertEquals(2, response.getTotalPages());
        assertFalse(response.isHasNext());
        assertTrue(response.isHasPrevious());
    }


}
