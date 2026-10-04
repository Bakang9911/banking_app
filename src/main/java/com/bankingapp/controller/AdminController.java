package com.bankingapp.controller;

import com.bankingapp.dto.AccountResponse;
import com.bankingapp.dto.DepositRequestResponse;
import com.bankingapp.dto.TransactionResponse;
import com.bankingapp.dto.UserResponse;
import com.bankingapp.entity.Account;
import com.bankingapp.entity.DepositRequest;
import com.bankingapp.entity.Transaction;
import com.bankingapp.entity.User;
import com.bankingapp.repository.AccountRepository;
import com.bankingapp.repository.UserRepository;
import com.bankingapp.service.DepositRequestService;
import com.bankingapp.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final TransactionService transactionService;
    private final DepositRequestService depositRequestService;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public AdminController(
            TransactionService transactionService,
            DepositRequestService depositRequestService,
            UserRepository userRepository,
            AccountRepository accountRepository) {

        this.transactionService = transactionService;
        this.depositRequestService = depositRequestService;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    // =========================================================
    // ADMIN - CUSTOMERS
    // =========================================================

    @GetMapping("/customers")
    public List<UserResponse> getCustomers() {

        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getRole()
                ))
                .toList();
    }

    // =========================================================
    // ADMIN - ALL ACCOUNTS
    // =========================================================

    @GetMapping("/accounts")
    public List<AccountResponse> getAccounts() {

        return accountRepository.findAll()
                .stream()
                .map(AdminController::toAccountResponse)
                .toList();
    }

    // =========================================================
    // ADMIN - PENDING DEPOSIT REQUESTS
    // =========================================================

    @GetMapping("/deposit-requests")
    public List<DepositRequestResponse> getPendingDepositRequests() {

        return depositRequestService
                .getPendingRequests()
                .stream()
                .map(AdminController::toDepositRequestResponse)
                .toList();
    }

    // =========================================================
    // ADMIN - ALL DEPOSIT REQUESTS
    // =========================================================

    @GetMapping("/deposit-requests/all")
    public List<DepositRequestResponse> getAllDepositRequests() {

        return depositRequestService
                .getAllRequests()
                .stream()
                .map(AdminController::toDepositRequestResponse)
                .toList();
    }

    // =========================================================
    // ADMIN - APPROVE DEPOSIT REQUEST
    // =========================================================

    @PostMapping("/deposit-requests/{requestId}/approve")
    public DepositRequestResponse approveDepositRequest(
            @PathVariable Long requestId) {

        DepositRequest request =
                depositRequestService.approveRequest(requestId);

        return toDepositRequestResponse(request);
    }

    // =========================================================
    // ADMIN - REJECT DEPOSIT REQUEST
    // =========================================================

    @PostMapping("/deposit-requests/{requestId}/reject")
    public DepositRequestResponse rejectDepositRequest(
            @PathVariable Long requestId) {

        DepositRequest request =
                depositRequestService.rejectRequest(requestId);

        return toDepositRequestResponse(request);
    }

    // =========================================================
    // ADMIN - DIRECT DEPOSIT
    // =========================================================

    @PostMapping("/transactions/deposit")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse deposit(
            @RequestParam Long accountId,
            @RequestParam BigDecimal amount) {

        Transaction transaction =
                transactionService.adminDeposit(
                        accountId,
                        amount
                );

        return toTransactionResponse(transaction);
    }

    // =========================================================
    // ADMIN - WITHDRAW
    // =========================================================

    @PostMapping("/transactions/withdraw")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse withdraw(
            @RequestParam Long accountId,
            @RequestParam BigDecimal amount) {

        Transaction transaction =
                transactionService.adminWithdraw(
                        accountId,
                        amount
                );

        return toTransactionResponse(transaction);
    }

    // =========================================================
    // ADMIN - TRANSFER
    // =========================================================

    @PostMapping("/transactions/transfer")
    @ResponseStatus(HttpStatus.CREATED)
    public String transfer(
            @RequestParam Long fromAccountId,
            @RequestParam Long toAccountId,
            @RequestParam BigDecimal amount) {

        transactionService.adminTransfer(
                fromAccountId,
                toAccountId,
                amount
        );

        return "Transfer completed successfully";
    }

    // =========================================================
    // ADMIN - ALL TRANSACTIONS
    // =========================================================

    @GetMapping("/transactions")
    public List<TransactionResponse> getAllTransactions() {

        return transactionService
                .adminGetAllTransactions()
                .stream()
                .map(AdminController::toTransactionResponse)
                .toList();
    }

    // =========================================================
    // ADMIN - ACCOUNT TRANSACTIONS
    // =========================================================

    @GetMapping("/transactions/{accountId}")
    public List<TransactionResponse> getAccountTransactions(
            @PathVariable Long accountId) {

        return transactionService
                .adminGetTransactions(accountId)
                .stream()
                .map(AdminController::toTransactionResponse)
                .toList();
    }

    // =========================================================
    // ACCOUNT RESPONSE
    // =========================================================

    private static AccountResponse toAccountResponse(
            Account account) {

        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getCurrency(),
                account.getName(),
                account.getType()
        );
    }

    // =========================================================
    // DEPOSIT REQUEST RESPONSE
    // =========================================================

    private static DepositRequestResponse toDepositRequestResponse(
            DepositRequest request) {

        Account account = request.getAccount();

        return new DepositRequestResponse(
                request.getId(),
                account.getId(),
                account.getAccountNumber(),
                account.getName(),
                request.getAmount(),
                request.getStatus().name(),
                request.getRequestedAt(),
                request.getProcessedAt()
        );
    }

    // =========================================================
    // TRANSACTION RESPONSE
    // =========================================================

    private static TransactionResponse toTransactionResponse(
            Transaction transaction) {

        String relatedAccountNumber = null;
        String relatedAccountName = null;
        String direction;
        String description;

        if (transaction.getRelatedAccount() != null) {

            relatedAccountNumber =
                    transaction.getRelatedAccount()
                            .getAccountNumber();

            relatedAccountName =
                    transaction.getRelatedAccount()
                            .getName();
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
                description =
                        "Transfer received from "
                                + relatedAccountName;
                break;

            case TRANSFER_OUT:
                direction = "MONEY_OUT";
                description =
                        "Transfer sent to "
                                + relatedAccountName;
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
                relatedAccountName
        );
    }
}