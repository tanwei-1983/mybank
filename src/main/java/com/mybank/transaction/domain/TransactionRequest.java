package com.mybank.transaction.domain;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 交易请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    
    @NotBlank(message = "accountNumber can't be empty")
    @Pattern(regexp = "^[0-9]{16,19}$", message = "accountNumber format error")
    private String accountNumber;
    
    @NotBlank(message = "transaction type can't be empty")
    @Pattern(regexp = "^(DEPOSIT|WITHDRAWAL|TRANSFER|PAYMENT|REFUND|FEE|INTEREST)$", 
             message = "transaction type must be valid type")
    private String transactionType;
    
    @NotNull(message = "amount can't be empty")
    @DecimalMin(value = "0.01", message = "amount must be greater than 0")
    @DecimalMax(value = "999999999.99", message = "amount must less than 999999999.99")
    private BigDecimal amount;
    
    @Pattern(regexp = "^[A-Z]{3}$", message = "CNY must be 3 uppercase letter")
    private String currency = "CNY";
    
    @Size(max = 500, message = "description can't exceed 500 characters")
    private String description;
    
    @Pattern(regexp = "^[A-Z_]+$", message = "category format error")
    private String category;

    private String status;
}
