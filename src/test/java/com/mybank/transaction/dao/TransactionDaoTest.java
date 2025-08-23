package com.mybank.transaction.dao;

import com.mybank.transaction.domain.Transaction;
import com.mybank.transaction.service.SnowflakeIdWorker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 交易DAO层测试
 */
@SpringBootTest
//@ActiveProfiles("test")
@Transactional
@Rollback
class TransactionDaoTest {

    @Autowired
    private TransactionDao transactionDao;

    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        testTransaction = Transaction.builder()
                .id(SnowflakeIdWorker.getInstance().genNextId())
                .accountNumber("1234567890123456")
                .transactionType("DEPOSIT")
                .amount(new BigDecimal("1000.00"))
                .currency("CNY")
                .description("测试存款")
                .category("SALARY")
                .status("COMPLETED")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testInsertTransaction() {
        // When
        int result = transactionDao.insTran(testTransaction);

        // Then
        assertEquals(1, result);
        assertNotNull(testTransaction.getId());
    }

    @Test
    void testSelectById() {
        // Given
        transactionDao.insTran(testTransaction);
        Long id = testTransaction.getId();

        // When
        Transaction found = transactionDao.selectById(id);

        // Then
        assertNotNull(found);
        assertEquals(testTransaction.getAccountNumber(), found.getAccountNumber());
        assertEquals(testTransaction.getTransactionType(), found.getTransactionType());
        assertEquals(testTransaction.getAmount(), found.getAmount());
    }

    @Test
    void testSelectById_NotFound() {
        // When
        Transaction found = transactionDao.selectById(999L);

        // Then
        assertNull(found);
    }

    @Test
    void testUpdateTransaction() {
        // Given
        transactionDao.insTran(testTransaction);
        Long id = testTransaction.getId();
        
        Transaction updateTransaction = Transaction.builder()
                .id(id)
                .accountNumber("1234567890123456")
                .transactionType("WITHDRAWAL")
                .amount(new BigDecimal("500.00"))
                .currency("CNY")
                .description("测试取款")
                .category("FOOD")
                .status("COMPLETED")
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        int result = transactionDao.updTran(updateTransaction);

        // Then
        assertEquals(1, result);
        
        Transaction updated = transactionDao.selectById(id);
        assertEquals("WITHDRAWAL", updated.getTransactionType());
        assertEquals(new BigDecimal("500.00"), updated.getAmount());
        assertEquals("测试取款", updated.getDescription());
    }

    @Test
    void testDeleteById() {
        // Given
        transactionDao.insTran(testTransaction);
        Long id = testTransaction.getId();
        // When
        int result = transactionDao.deleteById(id);
        // Then
        assertEquals(1, result);
        Transaction deleted = transactionDao.selectById(id);
        assertNull(deleted);
    }

//    @Test
//    void testSelectByPage() {
//        // Given
//        Transaction transaction1 = Transaction.builder()
//                .accountNumber("1111111111111111")
//                .transactionType("DEPOSIT")
//                .amount(new BigDecimal("1000.00"))
//                .currency("CNY")
//                .description("测试1")
//                .category("SALARY")
//                .status("COMPLETED")
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
//
//        Transaction transaction2 = Transaction.builder()
//                .accountNumber("2222222222222222")
//                .transactionType("WITHDRAWAL")
//                .amount(new BigDecimal("500.00"))
//                .currency("CNY")
//                .description("测试2")
//                .category("FOOD")
//                .status("COMPLETED")
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
//
//        transactionDao.insTran(transaction1);
//        transactionDao.insTran(transaction2);
//
//        // When
//        List<Transaction> transactions = transactionDao.selectByPage(0, 10);
//
//        // Then
//        assertNotNull(transactions);
//        assertTrue(transactions.size() >= 2);
//    }

//    @Test
//    void testCountTotal() {
//        // Given
//        transactionDao.insTran(testTransaction);
//
//        // When
//        long count = transactionDao.countTotal();
//
//        // Then
//        assertTrue(count > 0);
//    }
}
