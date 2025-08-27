package com.mybank.transaction.service;

import com.mybank.transaction.domain.PageRequest;
import com.mybank.transaction.domain.PageResponse;
import com.mybank.transaction.domain.TransactionRequest;
import com.mybank.transaction.domain.Transaction;
import com.mybank.transaction.exception.DuplicateTransactionException;
import com.mybank.transaction.exception.TransactionNotFoundException;
import com.mybank.transaction.dao.TransactionDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implement Transaction Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionDao transactionDao;

    @Transactional
    @CacheEvict(value = "transactions", allEntries = true)
    public Transaction createTransaction(TransactionRequest request) {
        long id = SnowflakeIdWorker.getInstance().genNextId();
        Transaction transaction = Transaction.builder()
                .id(id)
                .tid(String.valueOf(id))
                .accountNumber(request.getAccountNumber())
                .transactionType(request.getTransactionType())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .description(request.getDescription())
                .category(request.getCategory())
                .status("COMPLETED")
                .createdAt(LocalDateTime.now())
                .build();
        try {
            transactionDao.insTran(transaction);
        } catch (Exception e) {
            throw new DuplicateTransactionException("duplicate transaction id");
        }
        
        return transaction;
    }

    @Transactional
    @CacheEvict(value = "transactions", allEntries = true)
    public Transaction updateTransaction(Long id, TransactionRequest request) {
        Transaction transaction = Transaction.builder()
                .id(id)
                .tid(String.valueOf(id))
                .accountNumber(request.getAccountNumber())
                .transactionType(request.getTransactionType())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .description(request.getDescription())
                .category(request.getCategory())
                .status(request.getStatus())
                .updatedAt(LocalDateTime.now())
                .build();

        int updCnt = transactionDao.updTran(transaction);
        if (updCnt == 0) throw new TransactionNotFoundException("transaction doesn't exist: " + id);        
        return transaction;
    }

    @Transactional
    @CacheEvict(value = "transactions", allEntries = true)
    public void deleteTransaction(Long id) {        
        int delCnt = transactionDao.deleteById(id);
        if (delCnt == 0) throw new TransactionNotFoundException("transaction doesn't exist: " + id);
    }

    @Cacheable(value = "transactions", key = "'all_' + #pageRequest.page + '_' + #pageRequest.size", sync = true)
    public PageResponse<Transaction> getAllTransactions(PageRequest pageRequest) {
        long totalElements = transactionDao.countTotal();
        if (totalElements == 0L) {
            return PageResponse.<Transaction>builder()
                    .content(new ArrayList<>())
                    .page(pageRequest.getPage())
                    .size(pageRequest.getSize())
                    .totalElements(0)
                    .totalPages(0)
                    .hasNext(false)
                    .hasPrevious(false)
                    .build();
        }
        int offset = (pageRequest.getPage() - 1) * pageRequest.getSize();
        List<Transaction> tlist = transactionDao.selectByPage(offset, pageRequest.getSize());

        int totalPages = (int) Math.ceil((double) totalElements / pageRequest.getSize());
        
        return PageResponse.<Transaction>builder()
                .content(tlist)
                .page(pageRequest.getPage())
                .size(pageRequest.getSize())
                .totalElements(totalElements)
                .totalPages(totalPages)
                .hasNext(pageRequest.getPage() < totalPages)
                .hasPrevious(pageRequest.getPage() > 1)
                .build();
    }
}
