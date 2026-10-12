package com.bankingapp.controller;

import com.bankingapp.dto.TransactionResponse;
import com.bankingapp.entity.Transaction;
import com.bankingapp.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse deposit(
            @RequestParam Long accountId,
            @RequestParam BigDecimal amount,
            Authentication authentication) {

        Transaction transaction = transactionService.deposit(
                accountId, amount, authentication.getName());

        return toResponse(transaction);
    }

    @PostMapping("/withdraw")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse withdraw(
            @RequestParam Long accountId,
            @RequestParam BigDecimal amount,
            Authentication authentication) {

        Transaction transaction = transactionService.withdraw(
                accountId, amount, authentication.getName());

        return toResponse(transaction);
    }

    // Existing transfer endpoint retained for compatibility.
    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.CREATED)
    public void transfer(
            @RequestParam Long fromAccountId,
            @RequestParam Long toAccountId,
            @RequestParam BigDecimal amount,
            Authentication authentication) {

        transactionService.transfer(
                fromAccountId,
                toAccountId,
                amount,
                authentication.getName());
    }

    // New transfer endpoint using the recipient's eight-digit account number.
    @PostMapping("/transfer-to-account-number")
    @ResponseStatus(HttpStatus.CREATED)
    public void transferToAccountNumber(
            @RequestParam Long fromAccountId,
            @RequestParam String beneficiaryAccountNumber,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String reference,
            Authentication authentication) {

        transactionService.transferToAccountNumber(
                fromAccountId,
                beneficiaryAccountNumber,
                amount,
                reference,
                authentication.getName());
    }

    @GetMapping
    public List<TransactionResponse> getTransactions(
            @RequestParam Long accountId,
            Authentication authentication) {

        return transactionService
                .getTransactions(accountId, authentication.getName())
                .stream()
                .map(TransactionController::toResponse)
                .toList();
    }

    private static TransactionResponse toResponse(Transaction transaction) {

        String relatedAccountNumber = null;
        String relatedAccountName = null;
        String direction;
        String description;

        if (transaction.getRelatedAccount() != null) {
            relatedAccountNumber =
                    transaction.getRelatedAccount().getAccountNumber();
            relatedAccountName =
                    transaction.getRelatedAccount().getName();
        }

        switch (transaction.getType()) {
            case DEPOSIT:
                direction = "MONEY_IN";
                description = "Cash deposit";
                break;

            case WITHDRAWAL:
                direction = "MONEY_OUT";
                description = "Cash withdrawal";
                break;

            case TRANSFER_IN:
                direction = "MONEY_IN";
                description = "Transfer received from " + relatedAccountName;
                break;

            case TRANSFER_OUT:
                direction = "MONEY_OUT";
                description = "Transfer sent to " + relatedAccountName;
                break;

            default:
                direction = "UNKNOWN";
                description = "Transaction";
        }

        return new TransactionResponse(
               transaction.getId(),
        transaction.getType().name(),
        direction,
        description,
        transaction.getAmount(),
        transaction.getTimestamp(),
        relatedAccountNumber,
        relatedAccountName,
        transaction.getReference()
        );
    }
}