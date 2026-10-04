package com.bankingapp.repository;

import com.bankingapp.entity.DepositRequest;
import com.bankingapp.entity.DepositRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepositRequestRepository extends JpaRepository<DepositRequest, Long> {

    List<DepositRequest> findByAccountUserUsername(String username);

    List<DepositRequest> findByStatus(DepositRequestStatus status);
}