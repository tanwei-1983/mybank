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
 * 交易管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/mybank/transactions")
@RequiredArgsConstructor
@Validated
public class TransactionController {

    private final TransactionService transactionService;
    /**
     * 创建交易
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Transaction>> createTransaction(
            @Valid @RequestBody TransactionRequest request) {
        log.info("创建交易请求: {}", request);
        
        Transaction response = transactionService.createTransaction(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("交易创建成功", response));
//        return ResponseEntity.ok(ApiResponse.success("交易创建成功", null));
    }

    /**
     * 根据ID获取交易
     */
//    @GetMapping("/{id}")
//    public ResponseEntity<ApiResponse<TransactionResponse>> getTransactionById(
//            @PathVariable @Min(1) Long id) {
//        log.info("获取交易请求: id={}", id);
//
//        TransactionResponse response = transactionService.getTransactionById(id);
//
//        return ResponseEntity.ok(ApiResponse.success(response));
//    }
//
//    /**
//     * 根据交易ID获取交易
//     */
//    @GetMapping("/by-transaction-id/{transactionId}")
//    public ResponseEntity<ApiResponse<TransactionResponse>> getTransactionByTransactionId(
//            @PathVariable @NotBlank @Pattern(regexp = "^TXN[0-9]+[A-Z0-9]{8}$") String transactionId) {
//        log.info("根据交易ID获取交易请求: transactionId={}", transactionId);
//
//        TransactionResponse response = transactionService.getTransactionByTransactionId(transactionId);
//
//        return ResponseEntity.ok(ApiResponse.success(response));
//    }

    /**
     * 更新交易
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Transaction>> updateTransaction(
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody TransactionRequest request) {
        log.info("更新交易请求: id={}, request={}", id, request);

        Transaction response = transactionService.updateTransaction(id, request);
        transactionService.updateTransaction(id, request);

        return ResponseEntity.ok(ApiResponse.success("交易更新成功", response));
    }

    /**
     * 删除交易
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTransaction(
            @PathVariable @Min(1) Long id) {
        log.info("删除交易请求: id={}", id);
        
        transactionService.deleteTransaction(id);
        
        return ResponseEntity.ok(ApiResponse.success("交易删除成功", null));
    }

    /**
     * 分页查询所有交易
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<Transaction>>> getAllTransactions(
            @Valid PageRequest pageRequest) {
        log.info("分页查询所有交易请求: {}", pageRequest);
        
        PageResponse<Transaction> response = transactionService.getAllTransactions(pageRequest);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 根据账户号码分页查询交易
     */
//    @GetMapping("/account/{accountNumber}")
//    public ResponseEntity<ApiResponse<PageResponse<TransactionResponse>>> getTransactionsByAccountNumber(
//            @PathVariable @NotBlank @Pattern(regexp = "^[0-9]{16,19}$") String accountNumber,
//            @Valid PageRequest pageRequest) {
//        log.info("根据账户号码查询交易请求: accountNumber={}, pageRequest={}", accountNumber, pageRequest);
//
//        PageResponse<TransactionResponse> response = transactionService.getTransactionsByAccountNumber(accountNumber, pageRequest);
//
//        return ResponseEntity.ok(ApiResponse.success(response));
//    }
//
//    /**
//     * 根据交易类型分页查询交易
//     */
//    @GetMapping("/type/{transactionType}")
//    public ResponseEntity<ApiResponse<PageResponse<TransactionResponse>>> getTransactionsByType(
//            @PathVariable @NotBlank @Pattern(regexp = "^(DEPOSIT|WITHDRAWAL|TRANSFER|PAYMENT|REFUND|FEE|INTEREST)$") String transactionType,
//            @Valid PageRequest pageRequest) {
//        log.info("根据交易类型查询交易请求: transactionType={}, pageRequest={}", transactionType, pageRequest);
//
//        PageResponse<TransactionResponse> response = transactionService.getTransactionsByType(transactionType, pageRequest);
//
//        return ResponseEntity.ok(ApiResponse.success(response));
//    }
}
