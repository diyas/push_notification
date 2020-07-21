package com.push.app.repository;

import com.push.app.model.TrStatusEnum;
import com.push.app.model.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByTrNo(String trNo);
    List<Transaction> findByUserIdAndTrStatus(String userId, TrStatusEnum trStatus);
    Transaction findByTrNo(String trNo);
    Transaction findByTrNoAndTrStatus(String trNo, TrStatusEnum trStatus);
}
