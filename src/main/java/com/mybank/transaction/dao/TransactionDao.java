package com.mybank.transaction.dao;

import com.mybank.transaction.domain.Transaction;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 交易Mapper接口
 */
@Component
public interface TransactionDao {
    
    /**
     * 插入交易记录
     */
    int insTran(Transaction transaction);
    
    /**
     * 根据ID查询交易
     */
    Transaction selectById(Long id);
    
    /**
     * 根据交易ID查询交易
     */

    /**
     * 更新交易记录
     */
    int updTran(Transaction transaction);

    /**
     * 删除交易记录
     */
    int deleteById(Long id);
    
    /**
     * 分页查询交易列表
     */

    List<Transaction> selectByPage(int offset, int limit);

    /**
     * 统计总交易数
     */
    long countTotal();

    List<Long> listId();
}
