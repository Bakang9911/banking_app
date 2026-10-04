package com.bankingapp.service;

import com.bankingapp.entity.Account;
import com.bankingapp.entity.DepositRequest;
import com.bankingapp.entity.DepositRequestStatus;
import com.bankingapp.repository.AccountRepository;
import com.bankingapp.repository.DepositRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DepositRequestService {

    private final DepositRequestRepository depositRequestRepository;
    private final AccountRepository accountRepository;
    private final TransactionService transactionService;

    public DepositRequestService(
            DepositRequestRepository depositRequestRepository,
            AccountRepository accountRepository,
            TransactionService transactionService) {

        this.depositRequestRepository = depositRequestRepository;
        this.accountRepository = accountRepository;
        this.transactionService = transactionService;
    }

    // =========================================================
    // CUSTOMER - CREATE DEPOSIT REQUEST
    // =========================================================

    @Transactional
    public DepositRequest createRequest(
            String username,
            BigDecimal amount) {

        validateAmount(amount);

        Account account = accountRepository
                .findByUserUsername(username)
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Customer account not found"
                        ));

        DepositRequest request = new DepositRequest();

        request.setAccount(account);
        request.setAmount(amount);
        request.setStatus(DepositRequestStatus.PENDING);
        request.setRequestedAt(LocalDateTime.now());

        return depositRequestRepository.save(request);
    }

    // =========================================================
    // CUSTOMER - VIEW OWN REQUESTS
    // =========================================================

    public List<DepositRequest> getRequestsForCustomer(
            String username) {

        return depositRequestRepository
                .findByAccountUserUsername(username);
    }

    // =========================================================
    // ADMIN - VIEW ALL PENDING REQUESTS
    // =========================================================

    public List<DepositRequest> getPendingRequests() {

        return depositRequestRepository
                .findByStatus(DepositRequestStatus.PENDING);
    }

    // =========================================================
    // ADMIN - VIEW ALL REQUESTS
    // =========================================================

    public List<DepositRequest> getAllRequests() {

        return depositRequestRepository.findAll();
    }

    // =========================================================
    // ADMIN - APPROVE REQUEST
    // =========================================================

    @Transactional
    public DepositRequest approveRequest(Long requestId) {

        DepositRequest request = getRequest(requestId);

        if (request.getStatus() != DepositRequestStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Only pending requests can be approved"
            );
        }

        // Actually deposit the money
        transactionService.adminDeposit(
                request.getAccount().getId(),
                request.getAmount()
        );

        // Update request status
        request.setStatus(DepositRequestStatus.APPROVED);
        request.setProcessedAt(LocalDateTime.now());

        return depositRequestRepository.save(request);
    }

    // =========================================================
    // ADMIN - REJECT REQUEST
    // =========================================================

    @Transactional
    public DepositRequest rejectRequest(Long requestId) {

        DepositRequest request = getRequest(requestId);

        if (request.getStatus() != DepositRequestStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Only pending requests can be rejected"
            );
        }

        request.setStatus(DepositRequestStatus.REJECTED);
        request.setProcessedAt(LocalDateTime.now());

        return depositRequestRepository.save(request);
    }

    // =========================================================
    // FIND REQUEST
    // =========================================================

    private DepositRequest getRequest(Long requestId) {

        return depositRequestRepository.findById(requestId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Deposit request not found"
                        ));
    }

    // =========================================================
    // VALIDATE AMOUNT
    // =========================================================

    private void validateAmount(BigDecimal amount) {

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Deposit amount must be greater than zero"
            );
        }
    }
}