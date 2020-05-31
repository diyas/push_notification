package com.push.app.repository;

import com.push.app.model.TrStatus;
import com.push.app.model.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionHistoryRepo extends JpaRepository<Transaction, Long> {
    Transaction findByTrNo(String trNo);

    Transaction findByTrNoAndTrStatus(String trNo, TrStatus trStatus);
}
