package com.bankingapp.service;

import com.bankingapp.dto.BeneficiaryLookupResponse;
import com.bankingapp.dto.BeneficiaryResponse;
import com.bankingapp.dto.SaveBeneficiaryRequest;
import com.bankingapp.entity.Account;
import com.bankingapp.entity.Beneficiary;
import com.bankingapp.entity.User;
import com.bankingapp.repository.AccountRepository;
import com.bankingapp.repository.BeneficiaryRepository;
import com.bankingapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BeneficiaryService {

    private final AccountRepository accountRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final UserRepository userRepository;

    public BeneficiaryService(
            AccountRepository accountRepository,
            BeneficiaryRepository beneficiaryRepository,
            UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.beneficiaryRepository = beneficiaryRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public BeneficiaryLookupResponse lookupAccount(
            String accountNumber, String username) {

        validateAccountNumber(accountNumber);

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() ->
                        new IllegalArgumentException("Account not found."));

        if (account.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException(
                    "You cannot add your own account as a beneficiary.");
        }

        return new BeneficiaryLookupResponse(
                account.getAccountNumber(),
                account.getUser().getName() + " "
                        + account.getUser().getSurname(),
                account.getType(),
                account.getCurrency()
        );
    }

    @Transactional
    public BeneficiaryResponse save(
            SaveBeneficiaryRequest request, String username) {

        if (request == null) {
            throw new IllegalArgumentException("Request is required.");
        }

        String accountNumber = request.getAccountNumber();
        String nickname = request.getNickname() == null
                ? "" : request.getNickname().trim();

        if (nickname.isBlank() || nickname.length() > 40) {
            throw new IllegalArgumentException(
                    "Nickname must contain between 1 and 40 characters.");
        }

        validateAccountNumber(accountNumber);

        User owner = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("Customer not found."));

        Account target = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() ->
                        new IllegalArgumentException("Account not found."));

        if (target.getUser().getId().equals(owner.getId())) {
            throw new IllegalArgumentException(
                    "You cannot add your own account as a beneficiary.");
        }

        if (beneficiaryRepository.existsByOwnerIdAndBeneficiaryAccountId(
                owner.getId(), target.getId())) {
            throw new IllegalArgumentException(
                    "This account is already saved as a beneficiary.");
        }

        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setOwner(owner);
        beneficiary.setBeneficiaryAccount(target);
        beneficiary.setNickname(nickname);

        return toResponse(beneficiaryRepository.save(beneficiary));
    }

    @Transactional(readOnly = true)
    public List<BeneficiaryResponse> getMine(String username) {
        return beneficiaryRepository
                .findByOwnerUsernameOrderByNicknameAsc(username)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void delete(Long beneficiaryId, String username) {
        Beneficiary beneficiary = beneficiaryRepository.findById(beneficiaryId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Beneficiary not found."));

        if (!beneficiary.getOwner().getUsername().equals(username)) {
            throw new IllegalArgumentException("Beneficiary not found.");
        }

        beneficiaryRepository.delete(beneficiary);
    }

    private BeneficiaryResponse toResponse(Beneficiary beneficiary) {
        Account account = beneficiary.getBeneficiaryAccount();

        return new BeneficiaryResponse(
                beneficiary.getId(),
                beneficiary.getNickname(),
                account.getAccountNumber(),
                account.getUser().getName() + " "
                        + account.getUser().getSurname(),
                account.getType(),
                account.getCurrency(),
                beneficiary.getCreatedAt()
        );
    }

    private void validateAccountNumber(String accountNumber) {
        if (accountNumber == null
                || !accountNumber.matches("\\d{8}")) {
            throw new IllegalArgumentException(
                    "Account number must contain exactly 8 digits.");
        }
    }
}
