package com.mybank.transaction;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 交易管理系统主应用程序
 */
@SpringBootApplication
@MapperScan("com.mybank.transaction.**.dao")
@EnableCaching
@EnableTransactionManagement
public class TransactionManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(TransactionManagementApplication.class, args);
    }
}
