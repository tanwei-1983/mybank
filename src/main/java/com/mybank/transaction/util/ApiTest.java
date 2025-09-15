package com.mybank.transaction.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ApiTest {
    private static int PORT;
    static double createTransReq = 0.0;
    static AtomicInteger atomInt = new AtomicInteger(0);
    private static String BASEURL;
    static int parallelDeg = 100, nums = 100000, totalNum = 0;

    public static void main(String[] args) {
        PORT = Integer.parseInt(args[0]);
        BASEURL = "http://localhost:" + PORT + "/api/v1/mybank/transactions";
        testCreateTrans();
        testListAllTrans();
        testUpdTrans();
        testDelTrans();

        System.out.println("error transaction ratio: " + atomInt.intValue() * 1.0 / totalNum);
        System.out.println("total api calls: " + totalNum);
        System.out.println("createTransaction req/s: " + createTransReq);
        System.out.println("getAllTransactions req/s: " + getAllTransactionsReqs);
        System.out.println("updateTransaction req/s: " + updTransReqs);
        System.out.println("deleteTransaction req/s: " + delReqs);
    }



    static void testCreateTrans() {
        var executor = Executors.newFixedThreadPool(parallelDeg);
        totalNum += nums;
        long t1 = System.currentTimeMillis();
        for (int i = 0; i < nums; ++i) {
            executor.submit(() -> {
                try {
                    Map<String, Object> requestBody = new HashMap<>();
                    requestBody.put("accountNumber", "1234567890123456");
                    requestBody.put("transactionType", "DEPOSIT");
                    requestBody.put("amount", 1000.50);
                    requestBody.put("currency", "CNY");
                    requestBody.put("description", "工资收入");
                    requestBody.put("category", "SALARY");
                    requestBody.put("status", "COMPLETED");

                    // 发送POST请求
                    HttpResponse<String> response = HttpClientUtil.post(BASEURL, requestBody);

                    if (!HttpClientUtil.isSuccess(response)) {
//                        System.out.println("创建失败，交易ID: ");
                        atomInt.incrementAndGet();
                    }
                } catch (Exception e) {
                    atomInt.incrementAndGet();
                }
            });
        }
        close(executor);
        createTransReq = nums * 1000.00 / (System.currentTimeMillis() - t1);
    }

    static List<Long> getIdList() {
        try {
            HttpResponse<String> resp = HttpClientUtil.get(BASEURL + "/all");

            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> map = new Gson().fromJson(resp.body(), type);
            var slist = (List<String>) map.get("data");
            return slist.stream().map(Long::parseLong).toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    static double getAllTransactionsReqs = 0.0;

    static void testListAllTrans() {
        var executor = Executors.newFixedThreadPool(parallelDeg);
        totalNum += nums;
        long t1 = System.currentTimeMillis();
        for (int i = 0; i < nums; ++i) {
            executor.submit(() -> {
                try {
                    // 构建查询参数
                    Map<String, String> params = new HashMap<>();
                    params.put("page", String.valueOf(ThreadLocalRandom.current().nextInt(1, 11)));
                    params.put("size", String.valueOf(ThreadLocalRandom.current().nextInt(1, 11)*10));
                    // 发送GET请求
                    HttpResponse<String> response = HttpClientUtil.getWithParams(BASEURL, params);

                    // 解析响应
                    if (!HttpClientUtil.isSuccess(response)) {
//                        System.out.println("listTrans 失败 ");
                        atomInt.incrementAndGet();
                    }
                } catch (Exception e) {
                    atomInt.incrementAndGet();
                }
            });
        }
        close(executor);
        getAllTransactionsReqs = nums * 1000.00 / (System.currentTimeMillis() - t1);
    }

    static double updTransReqs = 0.0;

    static void testUpdTrans() {
        List<Long> idList = getIdList();
        var executor = Executors.newFixedThreadPool(parallelDeg);
        totalNum += idList.size();
        long t1 = System.currentTimeMillis();
        for (long nid : idList) {
            executor.submit(() -> {
                try {
                    // 构建更新请求体
                    Map<String, Object> requestBody = new HashMap<>();
                    requestBody.put("accountNumber", "1234567890123456");
                    requestBody.put("transactionType", "WITHDRAWAL");
                    requestBody.put("amount", 500.25);
                    requestBody.put("currency", "CNY");
                    requestBody.put("description", "更新后的交易描述");
                    requestBody.put("category", "SHOPPING");
                    requestBody.put("status", "COMPLETED");

                    // 发送PUT请求
                    String updateUrl = BASEURL + "/" + nid;
                    HttpResponse<String> response = HttpClientUtil.put(updateUrl, requestBody);
                    // 解析响应
                    if (!HttpClientUtil.isSuccess(response)) {
//                        System.out.println("testUpdTrans 失败 ");
                        atomInt.incrementAndGet();
                    }

                } catch (Exception e) {
                    atomInt.incrementAndGet();
                }
            });
        }
        close(executor);
        updTransReqs = nums * 1000.00 / (System.currentTimeMillis() - t1);
    }

    static double delReqs = 0.0;

    static void testDelTrans() {
        List<Long> idList = getIdList();
        var executor = Executors.newFixedThreadPool(parallelDeg);
        totalNum += idList.size();
        long t1 = System.currentTimeMillis();
        for (long nid : idList) {
            executor.submit(() -> {
                try {
                    String deleteUrl = BASEURL + "/" + nid;
                    HttpResponse<String> response = HttpClientUtil.delete(deleteUrl);
                    if (!HttpClientUtil.isSuccess(response)) {
                        atomInt.incrementAndGet();
                    }
                } catch (Exception e) {
                    atomInt.incrementAndGet();
                }
            });
        }
        close(executor);
        delReqs = nums * 1000.00 / (System.currentTimeMillis() - t1);
    }


    private static void close(ExecutorService executor) {
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
