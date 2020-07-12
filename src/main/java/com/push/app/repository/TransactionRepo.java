package com.push.app.repository;

import com.push.app.model.TrStatus;
import com.push.app.model.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByTrNo(String trNo);
    List<Transaction> findByUserIdAndTrStatus(String userId, TrStatus trStatus);
    Transaction findByTrNo(String trNo);
    Transaction findByTrNoAndTrStatus(String trNo, TrStatus trStatus);
}
