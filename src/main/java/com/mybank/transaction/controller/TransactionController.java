package com.mybank.transaction.controller;

import com.mybank.transaction.domain.*;
import com.mybank.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

/**
 * Transaction Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/mybank/transactions")
@RequiredArgsConstructor
@Validated
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<ApiResponse<Transaction>> createTransaction(
            @Valid @RequestBody TransactionRequest request) {
        log.info("create transaction request: {}", request);
        
        Transaction response = transactionService.createTransaction(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("create transaction success", response));
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Transaction>> updateTransaction(
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody TransactionRequest request) {
        log.info("update transaction success: id={}, request={}", id, request);

        Transaction response = transactionService.updateTransaction(id, request);
        transactionService.updateTransaction(id, request);

        return ResponseEntity.ok(ApiResponse.success("update transaction success", response));
    }

    /**
     * 删除交易
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTransaction(
            @PathVariable @Min(1) Long id) {
        log.info("delete transaction request: id={}", id);
        
        transactionService.deleteTransaction(id);
        
        return ResponseEntity.ok(ApiResponse.success("delete transaction success", null));
    }

    /**
     * 分页查询所有交易
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<Transaction>>> getAllTransactions(
            @Valid PageRequest pageRequest) {
        log.info("Pagination query for all transaction requests: {}", pageRequest);
        
        PageResponse<Transaction> response = transactionService.getAllTransactions(pageRequest);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
