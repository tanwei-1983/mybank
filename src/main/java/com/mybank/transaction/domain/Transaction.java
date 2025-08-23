package com.mybank.transaction.domain;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Transaction {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 账户号码
     */
    private String accountNumber;
    
    /**
     * 交易类型
     */
    private String transactionType;
    
    /**
     * 交易金额
     */
    private BigDecimal amount;
    
    /**
     * 货币类型
     */
    private String currency;
    
    /**
     * 交易描述
     */
    private String description;
    
    /**
     * 交易类别
     */
    private String category;
    
    /**
     * 交易状态
     */
    private String status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
