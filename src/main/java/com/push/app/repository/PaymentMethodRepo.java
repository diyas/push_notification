package com.push.app.repository;

import com.push.app.model.domain.PaymentMethodView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMethodRepo extends JpaRepository<PaymentMethodView, Long> {
    PaymentMethodView findById(int id);
}
