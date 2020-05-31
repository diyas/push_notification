package com.push.app.repository;

import com.push.app.model.domain.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMethodRepo extends JpaRepository<PaymentMethod, Long> {
    PaymentMethod findById(int id);
}
