package com.mybank.transaction.service;

import com.mybank.transaction.dao.TransactionDao;
import com.mybank.transaction.domain.PageRequest;
import com.mybank.transaction.domain.TransactionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class ServiceStressTest {
    @Autowired
    private TransactionService transactionService;

    @Autowired private TransactionDao transactionDao;

    private TransactionRequest validRequest1;

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
        int parallelDeg = 500, nums=500000, totalNum=0;
        var executor = Executors.newFixedThreadPool(parallelDeg);
        AtomicInteger atomInt = new AtomicInteger(0);
        long t1 = System.currentTimeMillis();
        totalNum += nums;
        for (int i = 0; i < nums; ++i) {
            executor.submit(() -> {
                try {
                    transactionService.createTransaction(validRequest1);
                }catch(Exception e){
                    atomInt.incrementAndGet();
                }
            });
        }
        close(executor);
        double createTransReq = nums*1000.00/(System.currentTimeMillis()-t1);

        executor = Executors.newFixedThreadPool(parallelDeg);
        totalNum += nums;
        t1 = System.currentTimeMillis();
        for (int i = 0; i < nums; ++i) {
            executor.submit(() -> {
                try {
                    PageRequest pageRequest = new PageRequest(ThreadLocalRandom.current().nextInt(1, 11), ThreadLocalRandom.current().nextInt(1, 11)*10);
                    transactionService.getAllTransactions(pageRequest);
                } catch (Exception e) {
                    atomInt.incrementAndGet();
                }
            });
        }
        close(executor);
        double getAllTransactionsReqs = nums*1000.00/(System.currentTimeMillis()-t1);

        List<Long> idList=transactionDao.listId();

        executor = Executors.newFixedThreadPool(parallelDeg);
        totalNum += idList.size();
        t1 = System.currentTimeMillis();
        for (long nid : idList) {
            executor.submit(() -> {
                try {
                    transactionService.updateTransaction(nid, validRequest1);
                } catch (Exception e) {
                    atomInt.incrementAndGet();
                }
            });
        }
        close(executor);
        double updTransReqs = idList.size()*1000.00/(System.currentTimeMillis()-t1);

        executor = Executors.newFixedThreadPool(parallelDeg);
        totalNum += idList.size();
        t1 = System.currentTimeMillis();
        for (long nid : idList) {
            executor.submit(() -> {
                try {
                    transactionService.deleteTransaction(nid);
                } catch (Exception e) {
                    atomInt.incrementAndGet();
                }
            });
        }
        close(executor);
        double delReqs = idList.size()*1000.00/(System.currentTimeMillis()-t1);
        System.out.println("error transaction ratio: " + atomInt.intValue()*1.0/ totalNum);
        System.out.println("total api calls: " + totalNum);
        System.out.println("createTransaction req/s: " + createTransReq);
        System.out.println("getAllTransactions req/s: " + getAllTransactionsReqs);
        System.out.println("updateTransaction req/s: " + updTransReqs);
        System.out.println("deleteTransaction req/s: " + delReqs);
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
