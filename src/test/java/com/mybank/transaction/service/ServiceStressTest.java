package com.mybank.transaction.service;

import com.mybank.transaction.dao.TransactionDao;
import com.mybank.transaction.domain.PageRequest;
import com.mybank.transaction.domain.PageResponse;
import com.mybank.transaction.domain.Transaction;
import com.mybank.transaction.domain.TransactionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Transactional
@Rollback
public class ServiceStressTest {
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
}
    @Test
    void stressTest() {
        int parallelDeg = 8;
        List<Long>idList = new ArrayList<>();
        var executor = Executors.newFixedThreadPool(parallelDeg);
        Vector<String>excepMsg = new Vector<>();
        long t1 = System.currentTimeMillis();
        for (int i = 0; i < 10000; ++i) {
            executor.submit(() -> {
                try {
                    idList.add(transactionService.createTransaction(validRequest1).getId());
                }catch(Exception e){
                    excepMsg.add(e.getMessage());
                }
            });
        }
        close(executor);
        double createTransReq = 10000*1000.00/(System.currentTimeMillis()-t1);

        executor = Executors.newFixedThreadPool(parallelDeg);
        t1 = System.currentTimeMillis();
        for (int i = 0; i < 10000; ++i) {
            executor.submit(() -> {
                try {
                    PageRequest pageRequest = new PageRequest(10, 10);
                    transactionService.getAllTransactions(pageRequest);
                } catch (Exception e) {
                    excepMsg.add(e.getMessage());
                }
            });
        }
        close(executor);
        double getAllTransactionsReqs = 10000*1000.00/(System.currentTimeMillis()-t1);

        executor = Executors.newFixedThreadPool(parallelDeg);
        t1 = System.currentTimeMillis();
        for (int i = 0; i < 10000; ++i) {
            int finalI = i;
            executor.submit(() -> {
                try {
                    transactionService.updateTransaction(idList.get(finalI), validRequest1);
                } catch (Exception e) {
                    excepMsg.add(e.getMessage());
                }
            });
        }
        close(executor);
        double updTransReqs = 10000*1000.00/(System.currentTimeMillis()-t1);


        executor = Executors.newFixedThreadPool(parallelDeg);
        t1 = System.currentTimeMillis();
        for (int i = 0; i < 10000; ++i) {
            int finalI = i;
            executor.submit(() -> {
                try {
                    transactionService.deleteTransaction(idList.get(finalI));
                } catch (Exception e) {
                    excepMsg.add(e.getMessage());
                }
            });
        }
        close(executor);
        if(excepMsg.size() > 0) throw new RuntimeException("CATCH EXCEPTION");
        double delReqs = 10000*1000.00/(System.currentTimeMillis()-t1);
        System.out.println("createTransaction req/s:" + createTransReq +", getAllTransactions req/s:"
                + getAllTransactionsReqs + ", updateTransaction req/s:" +updTransReqs + ", deleteTransaction req/s:" + delReqs);
    }

    private void close(ExecutorService executor){
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.MINUTES)) {
                executor.shutdown();
                throw new RuntimeException("线程池没有关闭");
            }
        } catch (InterruptedException e) {
            executor.shutdown();
        }
    }
}
