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

    @Transactional
    public Transaction deposit(Long accountId, BigDecimal amount, String username) {
        Account account = getAccountForUser(accountId, username);
        validateAmount(amount);
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
        return createTransaction(account, TransactionType.DEPOSIT, amount, null, null);
    }

    @Transactional
    public Transaction adminDeposit(Long accountId, BigDecimal amount) {
        validateAmount(amount);
        Account account = getAccount(accountId);
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
        return createTransaction(account, TransactionType.DEPOSIT, amount, null, null);
    }

    @Transactional
    public Transaction withdraw(Long accountId, BigDecimal amount, String username) {
        Account account = getAccountForUser(accountId, username);
        return performWithdrawal(account, amount);
    }

    @Transactional
    public Transaction adminWithdraw(Long accountId, BigDecimal amount) {
        return performWithdrawal(getAccount(accountId), amount);
    }

    // Existing customer transfer method retained for compatibility.
    @Transactional
    public void transfer(
            Long fromAccountId,
            Long toAccountId,
            BigDecimal amount,
            String username) {
        Account fromAccount = getAccountForUser(fromAccountId, username);
        performTransfer(fromAccount, getAccount(toAccountId), amount, null);
    }

    // Customer transfer using the beneficiary's eight-digit account number.
    @Transactional
    public void transferToAccountNumber(
            Long fromAccountId,
            String beneficiaryAccountNumber,
            BigDecimal amount,
            String reference,
            String username) {

        Account fromAccount = getAccountForUser(fromAccountId, username);

        if (beneficiaryAccountNumber == null
                || !beneficiaryAccountNumber.matches("\\d{8}")) {
            throw new IllegalArgumentException(
                    "Beneficiary account number must contain exactly 8 digits.");
        }

        Account toAccount = accountRepository
                .findByAccountNumber(beneficiaryAccountNumber)
                .orElseThrow(() ->
                        new IllegalArgumentException("Beneficiary account not found."));

        performTransfer(fromAccount, toAccount, amount, normalizeReference(reference));
    }

    @Transactional
    public void adminTransfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        performTransfer(getAccount(fromAccountId), getAccount(toAccountId), amount, null);
    }

    public List<Transaction> getTransactions(Long accountId, String username) {
        getAccountForUser(accountId, username);
        return transactionRepository.findByAccountId(accountId);
    }

    public List<Transaction> adminGetTransactions(Long accountId) {
        getAccount(accountId);
        return transactionRepository.findByAccountId(accountId);
    }

    public List<Transaction> adminGetAllTransactions() {
        return transactionRepository.findAll();
    }

    private Transaction performWithdrawal(Account account, BigDecimal amount) {
        validateAmount(amount);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
        return createTransaction(account, TransactionType.WITHDRAWAL, amount, null, null);
    }

    private void performTransfer(
            Account fromAccount,
            Account toAccount,
            BigDecimal amount,
            String reference) {

        validateAmount(amount);

        if (fromAccount.getId().equals(toAccount.getId())) {
            throw new IllegalArgumentException("Cannot transfer to the same account.");
        }

        if (fromAccount.getCurrency() != null
                && toAccount.getCurrency() != null
                && !fromAccount.getCurrency().equals(toAccount.getCurrency())) {
            throw new IllegalArgumentException(
                    "Transfers between different currencies are not supported.");
        }

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        LocalDateTime timestamp = LocalDateTime.now();

        Transaction outgoing = new Transaction();
        outgoing.setAccount(fromAccount);
        outgoing.setRelatedAccount(toAccount);
        outgoing.setType(TransactionType.TRANSFER_OUT);
        outgoing.setAmount(amount);
        outgoing.setTimestamp(timestamp);
        outgoing.setReference(reference);

        Transaction incoming = new Transaction();
        incoming.setAccount(toAccount);
        incoming.setRelatedAccount(fromAccount);
        incoming.setType(TransactionType.TRANSFER_IN);
        incoming.setAmount(amount);
        incoming.setTimestamp(timestamp);
        incoming.setReference(reference);

        transactionRepository.save(outgoing);
        transactionRepository.save(incoming);
    }

    private Transaction createTransaction(
            Account account,
            TransactionType type,
            BigDecimal amount,
            Account relatedAccount,
            String reference) {

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setRelatedAccount(relatedAccount);
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setReference(reference);
        return transactionRepository.save(transaction);
    }

    private Account getAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    private Account getAccountForUser(Long accountId, String username) {
        Account account = getAccount(accountId);

        if (account.getUser() == null
                || !account.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("You do not own this account");
        }

        return account;
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if (amount.scale() > 2) {
            throw new IllegalArgumentException(
                    "Amount cannot contain more than two decimal places.");
        }
    }

    private String normalizeReference(String reference) {
        if (reference == null || reference.isBlank()) {
            return null;
        }

        String normalized = reference.trim();

        if (normalized.length() > 100) {
            throw new IllegalArgumentException(
                    "Payment reference cannot exceed 100 characters.");
        }

        return normalized;
    }
}