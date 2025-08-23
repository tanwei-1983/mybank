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
    
    @NotBlank(message = "账户号码不能为空")
    @Pattern(regexp = "^[0-9]{16,19}$", message = "账户号码格式不正确")
    private String accountNumber;
    
    @NotBlank(message = "交易类型不能为空")
    @Pattern(regexp = "^(DEPOSIT|WITHDRAWAL|TRANSFER|PAYMENT|REFUND|FEE|INTEREST)$", 
             message = "交易类型必须是有效的类型")
    private String transactionType;
    
    @NotNull(message = "交易金额不能为空")
    @DecimalMin(value = "0.01", message = "交易金额必须大于0")
    @DecimalMax(value = "999999999.99", message = "交易金额不能超过999999999.99")
    private BigDecimal amount;
    
    @Pattern(regexp = "^[A-Z]{3}$", message = "货币类型必须是3位大写字母")
    private String currency = "CNY";
    
    @Size(max = 500, message = "交易描述不能超过500字符")
    private String description;
    
    @Pattern(regexp = "^[A-Z_]+$", message = "交易类别格式不正确")
    private String category;

    private String status;
}
