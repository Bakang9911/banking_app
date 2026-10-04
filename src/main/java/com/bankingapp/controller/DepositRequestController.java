package com.bankingapp.controller;

import com.bankingapp.dto.DepositRequestResponse;
import com.bankingapp.entity.DepositRequest;
import com.bankingapp.service.DepositRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/deposit-requests")
public class DepositRequestController {

    private final DepositRequestService depositRequestService;

    public DepositRequestController(
            DepositRequestService depositRequestService) {

        this.depositRequestService =
                depositRequestService;
    }

    // =========================================================
    // CUSTOMER - CREATE REQUEST
    // =========================================================

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepositRequestResponse createRequest(
            @RequestParam BigDecimal amount,
            Authentication authentication) {

        DepositRequest request =
                depositRequestService.createRequest(
                        authentication.getName(),
                        amount
                );

        return toResponse(request);
    }

    // =========================================================
    // CUSTOMER - VIEW OWN REQUESTS
    // =========================================================

    @GetMapping
    public List<DepositRequestResponse> getMyRequests(
            Authentication authentication) {

        return depositRequestService
                .getRequestsForCustomer(
                        authentication.getName()
                )
                .stream()
                .map(DepositRequestController::toResponse)
                .toList();
    }

    // =========================================================
    // RESPONSE MAPPER
    // =========================================================

    private static DepositRequestResponse toResponse(
            DepositRequest request) {

        return new DepositRequestResponse(
                request.getId(),
                request.getAccount().getId(),
                request.getAccount().getAccountNumber(),
                request.getAccount().getName(),
                request.getAmount(),
                request.getStatus().name(),
                request.getRequestedAt(),
                request.getProcessedAt()
        );
    }
}