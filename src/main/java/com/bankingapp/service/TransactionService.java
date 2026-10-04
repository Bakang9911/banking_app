package com.bankingapp.service;

import com.bankingapp.entity.Account;
import com.bankingapp.entity.Transaction;
import com.bankingapp.entity.TransactionType;
import com.bankingapp.repository.AccountRepository;
import com.bankingapp.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(
            AccountRepository accountRepository,
            TransactionRepository transactionRepository) {

        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    // =========================================================
    // CUSTOMER - DEPOSIT
    // =========================================================

    @Transactional
    public Transaction deposit(
            Long accountId,
            BigDecimal amount,
            String username) {

        Account account = getAccountForUser(accountId, username);

        validateAmount(amount);

        account.setBalance(
                account.getBalance().add(amount)
        );

        accountRepository.save(account);

        return createTransaction(
                account,
                TransactionType.DEPOSIT,
                amount,
                null
        );
    }

    // =========================================================
    // ADMIN - DEPOSIT
    // =========================================================

    @Transactional
    public Transaction adminDeposit(
            Long accountId,
            BigDecimal amount) {

        validateAmount(amount);

        Account account = getAccount(accountId);

        account.setBalance(
                account.getBalance().add(amount)
        );

        accountRepository.save(account);

        return createTransaction(
                account,
                TransactionType.DEPOSIT,
                amount,
                null
        );
    }

    // =========================================================
    // CUSTOMER - WITHDRAW
    // =========================================================

    @Transactional
    public Transaction withdraw(
            Long accountId,
            BigDecimal amount,
            String username) {

        Account account =
                getAccountForUser(accountId, username);

        return performWithdrawal(account, amount);
    }

    // =========================================================
    // ADMIN - WITHDRAW
    // =========================================================

    @Transactional
    public Transaction adminWithdraw(
            Long accountId,
            BigDecimal amount) {

        Account account = getAccount(accountId);

        return performWithdrawal(account, amount);
    }

    // =========================================================
    // CUSTOMER - TRANSFER
    // =========================================================

    @Transactional
    public void transfer(
            Long fromAccountId,
            Long toAccountId,
            BigDecimal amount,
            String username) {

        Account fromAccount =
                getAccountForUser(fromAccountId, username);

        performTransfer(
                fromAccount,
                toAccountId,
                amount
        );
    }

    // =========================================================
    // ADMIN - TRANSFER
    // =========================================================

    @Transactional
    public void adminTransfer(
            Long fromAccountId,
            Long toAccountId,
            BigDecimal amount) {

        Account fromAccount =
                getAccount(fromAccountId);

        performTransfer(
                fromAccount,
                toAccountId,
                amount
        );
    }

    // =========================================================
    // CUSTOMER - VIEW OWN TRANSACTIONS
    // =========================================================

    public List<Transaction> getTransactions(
            Long accountId,
            String username) {

        getAccountForUser(accountId, username);

        return transactionRepository
                .findByAccountId(accountId);
    }

    // =========================================================
    // ADMIN - VIEW ONE ACCOUNT'S TRANSACTIONS
    // =========================================================

    public List<Transaction> adminGetTransactions(
            Long accountId) {

        getAccount(accountId);

        return transactionRepository
                .findByAccountId(accountId);
    }

    // =========================================================
    // ADMIN - VIEW ALL TRANSACTIONS
    // =========================================================

    public List<Transaction> adminGetAllTransactions() {

        return transactionRepository.findAll();
    }

    // =========================================================
    // INTERNAL - WITHDRAWAL
    // =========================================================

    private Transaction performWithdrawal(
            Account account,
            BigDecimal amount) {

        validateAmount(amount);

        if (account.getBalance()
                .compareTo(amount) < 0) {

            throw new IllegalArgumentException(
                    "Insufficient funds"
            );
        }

        account.setBalance(
                account.getBalance().subtract(amount)
        );

        accountRepository.save(account);

        return createTransaction(
                account,
                TransactionType.WITHDRAWAL,
                amount,
                null
        );
    }

    // =========================================================
    // INTERNAL - TRANSFER
    // =========================================================

    private void performTransfer(
            Account fromAccount,
            Long toAccountId,
            BigDecimal amount) {

        validateAmount(amount);

        if (fromAccount.getId().equals(toAccountId)) {

            throw new IllegalArgumentException(
                    "Cannot transfer to the same account"
            );
        }

        Account toAccount =
                getAccount(toAccountId);

        if (fromAccount.getBalance()
                .compareTo(amount) < 0) {

            throw new IllegalArgumentException(
                    "Insufficient funds"
            );
        }

        // Remove money from sender
        fromAccount.setBalance(
                fromAccount.getBalance()
                        .subtract(amount)
        );

        // Add money to recipient
        toAccount.setBalance(
                toAccount.getBalance()
                        .add(amount)
        );

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        LocalDateTime timestamp =
                LocalDateTime.now();

        // Sender transaction
        Transaction outgoingTransaction =
                new Transaction();

        outgoingTransaction.setAccount(fromAccount);
        outgoingTransaction.setRelatedAccount(toAccount);
        outgoingTransaction.setType(
                TransactionType.TRANSFER_OUT
        );
        outgoingTransaction.setAmount(amount);
        outgoingTransaction.setTimestamp(timestamp);

        // Recipient transaction
        Transaction incomingTransaction =
                new Transaction();

        incomingTransaction.setAccount(toAccount);
        incomingTransaction.setRelatedAccount(fromAccount);
        incomingTransaction.setType(
                TransactionType.TRANSFER_IN
        );
        incomingTransaction.setAmount(amount);
        incomingTransaction.setTimestamp(timestamp);

        transactionRepository.save(
                outgoingTransaction
        );

        transactionRepository.save(
                incomingTransaction
        );
    }

    // =========================================================
    // INTERNAL - CREATE TRANSACTION
    // =========================================================

    private Transaction createTransaction(
            Account account,
            TransactionType type,
            BigDecimal amount,
            Account relatedAccount) {

        Transaction transaction =
                new Transaction();

        transaction.setAccount(account);
        transaction.setRelatedAccount(
                relatedAccount
        );
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setTimestamp(
                LocalDateTime.now()
        );

        return transactionRepository.save(
                transaction
        );
    }

    // =========================================================
    // FIND ACCOUNT
    // =========================================================

    private Account getAccount(Long accountId) {

        return accountRepository
                .findById(accountId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Account not found"
                        ));
    }

    // =========================================================
    // FIND CUSTOMER'S ACCOUNT
    // =========================================================

    private Account getAccountForUser(
            Long accountId,
            String username) {

        Account account =
                getAccount(accountId);

        if (account.getUser() == null ||
                !account.getUser()
                        .getUsername()
                        .equals(username)) {

            throw new IllegalArgumentException(
                    "You do not own this account"
            );
        }

        return account;
    }

    // =========================================================
    // VALIDATE AMOUNT
    // =========================================================

    private void validateAmount(
            BigDecimal amount) {

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Amount must be greater than zero"
            );
        }
    }
}