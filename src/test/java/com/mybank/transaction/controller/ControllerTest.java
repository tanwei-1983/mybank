package com.mybank.transaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybank.transaction.domain.*;
import com.mybank.transaction.exception.DuplicateTransactionException;
import com.mybank.transaction.exception.GlobalExceptionHandler;
import com.mybank.transaction.exception.TransactionNotFoundException;
import com.mybank.transaction.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TransactionController 单元测试
 */
@ExtendWith(MockitoExtension.class)
class ControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreateTransaction_Success() throws Exception {
        // 准备测试数据
        TransactionRequest request = TransactionRequest.builder()
                .accountNumber("1234567890123456")
                .transactionType("DEPOSIT")
                .amount(new BigDecimal("1000.00"))
                .currency("CNY")
                .description("test DEPOSIT")
                .category("SALARY")
                .status("PENDING")
                .build();

        Transaction response = Transaction.builder()
                .id(1L)
                .accountNumber("1234567890123456")
                .transactionType("DEPOSIT")
                .amount(new BigDecimal("1000.00"))
                .currency("CNY")
                .description("test DEPOSIT")
                .category("SALARY")
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(transactionService.createTransaction(any(TransactionRequest.class))).thenReturn(response);

        // 执行测试
        mockMvc.perform(post("/v1/mybank/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("create transaction success"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.accountNumber").value("1234567890123456"))
                .andExpect(jsonPath("$.data.transactionType").value("DEPOSIT"))
                .andExpect(jsonPath("$.data.amount").value(1000.00));

        verify(transactionService, times(1)).createTransaction(any(TransactionRequest.class));
    }

    @Test
    void testCreateTransaction_ValidationError() throws Exception {
        // 准备无效的测试数据
        TransactionRequest request = TransactionRequest.builder()
                .accountNumber("123") // 无效的账户号
                .transactionType("INVALID_TYPE") // 无效的交易类型
                .amount(new BigDecimal("-100")) // 无效的金额
                .build();

        // 执行测试
        mockMvc.perform(post("/v1/mybank/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateTransaction_DuplicateTransaction() throws Exception {
        // 准备测试数据
        TransactionRequest request = TransactionRequest.builder()
                .accountNumber("1234567890123456")
                .transactionType("DEPOSIT")
                .amount(new BigDecimal("1000.00"))
                .currency("CNY")
                .description("测试存款")
                .category("SALARY")
                .status("PENDING")
                .build();

        when(transactionService.createTransaction(any(TransactionRequest.class)))
                .thenThrow(new DuplicateTransactionException("transaction conflict"));

        // 执行测试
        mockMvc.perform(post("/v1/mybank/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void testUpdateTransaction_Success() throws Exception {
        // 准备测试数据
        Long transactionId = 1L;
        TransactionRequest request = TransactionRequest.builder()
                .accountNumber("1234567890123456")
                .transactionType("WITHDRAWAL")
                .amount(new BigDecimal("500.00"))
                .currency("CNY")
                .description("test WITHDRAWAL")
                .category("EXPENSE")
                .status("COMPLETED")
                .build();

        Transaction response = Transaction.builder()
                .id(transactionId)
                .accountNumber("1234567890123456")
                .transactionType("WITHDRAWAL")
                .amount(new BigDecimal("500.00"))
                .currency("CNY")
                .description("test WITHDRAWAL")
                .category("EXPENSE")
                .status("COMPLETED")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(transactionService.updateTransaction(eq(transactionId), any(TransactionRequest.class)))
                .thenReturn(response);

        // 执行测试
        mockMvc.perform(put("/v1/mybank/transactions/{id}", transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("update transaction success"))
                .andExpect(jsonPath("$.data.id").value(transactionId))
                .andExpect(jsonPath("$.data.transactionType").value("WITHDRAWAL"))
                .andExpect(jsonPath("$.data.amount").value(500.00));

        verify(transactionService, times(2)).updateTransaction(eq(transactionId), any(TransactionRequest.class));
    }

    @Test
    void testUpdateTransaction_NotFound() throws Exception {
        // 准备测试数据
        Long transactionId = 999L;
        TransactionRequest request = TransactionRequest.builder()
                .accountNumber("1234567890123456")
                .transactionType("WITHDRAWAL")
                .amount(new BigDecimal("500.00"))
                .currency("CNY")
                .description("测试取款")
                .category("EXPENSE")
                .status("COMPLETED")
                .build();

        when(transactionService.updateTransaction(eq(transactionId), any(TransactionRequest.class)))
                .thenThrow(new TransactionNotFoundException("交易不存在"));

        // 执行测试
        mockMvc.perform(put("/v1/mybank/transactions/{id}", transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteTransaction_Success() throws Exception {
        // 准备测试数据
        Long transactionId = 1L;
        doNothing().when(transactionService).deleteTransaction(transactionId);

        // 执行测试
        mockMvc.perform(delete("/v1/mybank/transactions/{id}", transactionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("delete transaction success"));

        verify(transactionService, times(1)).deleteTransaction(transactionId);
    }

    @Test
    void testDeleteTransaction_NotFound() throws Exception {
        // 准备测试数据
        Long transactionId = 999L;
        doThrow(new TransactionNotFoundException("交易不存在"))
                .when(transactionService).deleteTransaction(transactionId);

        mockMvc.perform(delete("/v1/mybank/transactions/{id}", transactionId))
                .andExpect(status().isNotFound());

    }

    @Test
    void testGetAllTransactions_Success() throws Exception {
        // 准备测试数据
        List<Transaction> transactions = Arrays.asList(
                Transaction.builder()
                        .id(1L)
                        .accountNumber("1234567890123456")
                        .transactionType("DEPOSIT")
                        .amount(new BigDecimal("1000.00"))
                        .currency("CNY")
                        .description("测试存款")
                        .category("SALARY")
                        .status("COMPLETED")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),
                Transaction.builder()
                        .id(2L)
                        .accountNumber("1234567890123457")
                        .transactionType("WITHDRAWAL")
                        .amount(new BigDecimal("500.00"))
                        .currency("CNY")
                        .description("测试取款")
                        .category("EXPENSE")
                        .status("COMPLETED")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );

        PageResponse<Transaction> pageResponse = PageResponse.<Transaction>builder()
                .content(transactions)
                .totalElements(2L)
                .totalPages(1)
                .page(1)
                .size(10)
                .build();

        when(transactionService.getAllTransactions(any(PageRequest.class))).thenReturn(pageResponse);

        // 执行测试
        mockMvc.perform(get("/v1/mybank/transactions")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.size").value(10));

        verify(transactionService, times(1)).getAllTransactions(any(PageRequest.class));
    }

    @Test
    void testGetAllTransactions_EmptyResult() throws Exception {
        // 准备测试数据
        PageResponse<Transaction> pageResponse = PageResponse.<Transaction>builder()
                .content(Arrays.asList())
                .totalElements(0L)
                .totalPages(0)
                .page(1)
                .size(10)
                .build();

        when(transactionService.getAllTransactions(any(PageRequest.class))).thenReturn(pageResponse);

        // 执行测试
        mockMvc.perform(get("/v1/mybank/transactions")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(0))
                .andExpect(jsonPath("$.data.totalElements").value(0));

        verify(transactionService, times(1)).getAllTransactions(any(PageRequest.class));
    }

    @Test
    void testGetAllTransactions_InvalidPageRequest() throws Exception {
        // 执行测试 - 无效的分页参数
        mockMvc.perform(get("/v1/mybank/transactions")
                        .param("page", "0") // 无效的页码
                        .param("size", "0")) // 无效的页面大小
                .andExpect(status().isBadRequest());
    }
}
