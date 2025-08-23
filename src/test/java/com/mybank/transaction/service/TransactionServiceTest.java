package com.mybank.transaction.service;

import com.mybank.transaction.domain.PageRequest;
import com.mybank.transaction.domain.PageResponse;
import com.mybank.transaction.domain.TransactionRequest;
import com.mybank.transaction.domain.Transaction;
import com.mybank.transaction.exception.DuplicateTransactionException;
import com.mybank.transaction.exception.TransactionNotFoundException;
import com.mybank.transaction.dao.TransactionDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 交易服务单元测试
 */
//@ExtendWith(MockitoExtension.class)
//@SpringBootTest
//@RunWith(SpringRunner.class)

@SpringBootTest
//@ActiveProfiles("test")
@Transactional
@Rollback
class TransactionServiceTest {
    @Autowired
    private TransactionDao transactionMapper;

    @Autowired
    private TransactionService transactionService;

    @Autowired private TransactionDao transactionDao;

    private TransactionRequest validRequest1, validRequest2;
//    private Transaction sampleTransaction;

    @BeforeEach
    void setUp() {
        validRequest1 = TransactionRequest.builder()
                .accountNumber("1111111111111")
                .transactionType("DEPOSIT")
                .amount(new BigDecimal("1000.00"))
                .currency("CNY")
                .description("test deposit")
                .category("SALARY")
                .build();
        /*  .id(id)
//                .transactionId(existingTransaction.getTransactionId())
                .accountNumber(request.getAccountNumber())
                .transactionType(request.getTransactionType())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .description(request.getDescription())
                .category(request.getCategory())
                .status(request.getStatus())
//                .createdAt(existingTransaction.getCreatedAt())
                .updatedAt(LocalDateTime.now())*/

        validRequest2 = TransactionRequest.builder()
                .accountNumber("22222222222")
                .transactionType("TRANSFER")
                .amount(new BigDecimal("2000.00"))
                .currency("CNY")
                .description("test food")
                .category("FOOD")
                .status("UNCOMPLETE")
                .build();

//        sampleTransaction = Transaction.builder()
//                .id(1L)
////                .transactionId("TXN1234567890ABCDEF")
//                .accountNumber("1234567890123456")
//                .transactionType("TRANSFER")
//                .amount(new BigDecimal("1000.00"))
//                .currency("CNY")
//                .description("测试存款")
//                .category("SALARY")
//                .status("COMPLETED")
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
    }

    @Test
    void createTransaction_success() {
        // Given
//        when(transactionMapper.existsByTransactionId(anyString())).thenReturn(false);
//        when(transactionMapper.insTran(any(Transaction.class))).thenReturn(1);
        // When

        /*validRequest = TransactionRequest.builder()
                .accountNumber("1234567890123456")
                .transactionType("DEPOSIT")
                .amount(new BigDecimal("1000.00"))
                .currency("CNY")
                .description("测试存款")
                .category("SALARY")
                .build();*/

        Long id = transactionService.createTransaction(validRequest1).getId();
//        System.out.println("id test is:"+id);
        assertNotNull(id);

        Transaction response=transactionMapper.selectById(id);
        // Then
        assertEquals(validRequest1.getAccountNumber(), response.getAccountNumber());
        assertEquals(validRequest1.getTransactionType(), response.getTransactionType());
        assertEquals(validRequest1.getAmount(), response.getAmount());
        assertEquals(validRequest1.getCurrency(), response.getCurrency());
        assertEquals(validRequest1.getDescription(), response.getDescription());
        assertEquals(validRequest1.getCategory(), response.getCategory());
//        assertTrue(response.getTransactionId().startsWith("TXN"));
//        verify(transactionMapper).existsByTransactionId(anyString());
//        verify(transactionMapper).insTran(any(Transaction.class));
    }

//    @Test
//    void createTransaction_failure() {
//        transactionService.createTransaction(validRequest1);
//        transactionService.createTransaction(validRequest1);
//        Assertions.assertThrows(DuplicateTransactionException.class, () -> {}, "DuplicateTransactionException error was expected");
//    }


//    @Test
//    void getTransactionById_Success() {
//        // Given
//        when(transactionMapper.selectById(1L)).thenReturn(sampleTransaction);
//
//        // When
//        TransactionResponse response = transactionService.getTransactionById(1L);
//
//        // Then
//        assertNotNull(response);
//        assertEquals(sampleTransaction.getId(), response.getId());
//        assertEquals(sampleTransaction.getTransactionId(), response.getTransactionId());
//
//        verify(transactionMapper).selectById(1L);
//    }

//    @Test
//    void getTransactionById_NotFound() {
//        // Given
//        when(transactionMapper.selectById(999L)).thenReturn(null);
//
//        // When & Then
//        assertThrows(TransactionNotFoundException.class, () -> {
//            transactionService.getTransactionById(999L);
//        });
//
//        verify(transactionMapper).selectById(999L);
//    }

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
    }

//    @Test
//    void updateTransaction_NotFound() {
//        // Given
//        when(transactionMapper.selectById(999L)).thenReturn(null);
//
//        // When & Then
//        assertThrows(TransactionNotFoundException.class, () -> {
//            transactionService.updateTransaction(999L, validRequest);
//        });
//
//        verify(transactionMapper).selectById(999L);
//        verify(transactionMapper, never()).updTran(any(Transaction.class));
//    }

    @Test
    void deleteTransaction_success() {
        Long id = transactionService.createTransaction(validRequest1).getId();
        assertNotNull(id);

        transactionService.deleteTransaction(id);
        Transaction deleted = transactionDao.selectById(id);
        assertNull(deleted);
//        assertEquals("TRANSFER", updated.getTransactionType());
//        assertEquals(new BigDecimal("2000.00"), updated.getAmount());
//        assertEquals("test food", updated.getDescription());


//        transactionService.deleteTransaction(1L);
//        int delCnt = transactionDao.deleteById(1L);
//        if (delCnt == 0) throw new TransactionNotFoundException("交易不存在: " + 1);
//        Transaction response=transactionMapper.selectById(1L);
//        assertNull(response);
//        // Given
//        when(transactionMapper.selectById(1L)).thenReturn(sampleTransaction);
//        when(transactionMapper.deleteById(1L)).thenReturn(1);
//
//        // When
//        assertDoesNotThrow(() -> transactionService.deleteTransaction(1L));
//
//        // Then
//        verify(transactionMapper).selectById(1L);
//        verify(transactionMapper).deleteById(1L);
    }

    @Test
    void deleteTransaction_failure() {
        Long id = transactionService.createTransaction(validRequest1).getId();
        assertNotNull(id);
        transactionService.deleteTransaction(id);
        Assertions.assertThrows(TransactionNotFoundException.class, () -> transactionService.deleteTransaction(id), "TransactionNotFoundException error was expected");
    }

//    @Test
//    void deleteTransaction_NotFound() {
//        // Given
//        when(transactionMapper.selectById(999L)).thenReturn(null);
//
//        // When & Then
//        assertThrows(TransactionNotFoundException.class, () -> {
//            transactionService.deleteTransaction(999L);
//        });
//
//        verify(transactionMapper).selectById(999L);
//        verify(transactionMapper, never()).deleteById(anyLong());
//    }
    @Test
    void getAllTransactions() {
        for (int i = 0; i < 11; ++i) transactionService.createTransaction(validRequest1);
        // Given
        PageRequest pageRequest = new PageRequest(2, 10);
//        List<Transaction> transactions = Arrays.asList(sampleTransaction);
//        when(transactionMapper.selectByPage(0, 10, "createdAt", "DESC")).thenReturn(transactions);
//        when(transactionMapper.selectByPage(0, 10)).thenReturn(transactions);
//        when(transactionMapper.countTotal()).thenReturn(1L);

        // When
        PageResponse<Transaction> response = transactionService.getAllTransactions(pageRequest);
        var trans = response.getContent().get(0);

        /*.accountNumber("1111111111111")
                .transactionType("DEPOSIT")
                .amount(new BigDecimal("1000.00"))*/
        assertEquals(trans.getAccountNumber(), "1111111111111");
        assertEquals(trans.getTransactionType(), "DEPOSIT");
        assertEquals(trans.getAmount(), new BigDecimal("1000.00"));
        assertEquals(1, response.getContent().size());
        assertEquals(11, response.getTotalElements());
        assertEquals(2, response.getTotalPages());
        assertFalse(response.isHasNext());
        assertTrue(response.isHasPrevious());
//        verify(transactionMapper).selectByPage(0, 10, "createdAt", "DESC");
//        assertF(transactionMapper).selectByPage(0, 10);
//        verify(transactionMapper).countTotal();
    }

//    @Test
//    void getTransactionsByAccountNumber_Success() {
//        // Given
//        String accountNumber = "1234567890123456";
//        PageRequest pageRequest = new PageRequest(1, 10, "createdAt", "DESC");
//        List<Transaction> transactions = Arrays.asList(sampleTransaction);
//
//        when(transactionMapper.selectByAccountNumber(eq(accountNumber), eq(0), eq(10), anyString(), anyString()))
//                .thenReturn(transactions);
//        when(transactionMapper.countByAccountNumber(accountNumber)).thenReturn(1L);
//
//        // When
//        PageResponse<TransactionResponse> response = transactionService.getTransactionsByAccountNumber(accountNumber, pageRequest);
//
//        // Then
//        assertNotNull(response);
//        assertEquals(1, response.getContent().size());
//        assertEquals(1, response.getTotalElements());
//
//        verify(transactionMapper).selectByAccountNumber(eq(accountNumber), eq(0), eq(10), anyString(), anyString());
//        verify(transactionMapper).countByAccountNumber(accountNumber);
//    }
}
