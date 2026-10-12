package com.bankingapp.repository;

import com.bankingapp.entity.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BeneficiaryRepository
        extends JpaRepository<Beneficiary, Long> {

    List<Beneficiary> findByOwnerUsernameOrderByNicknameAsc(String username);

    boolean existsByOwnerIdAndBeneficiaryAccountId(
            Long ownerId,
            Long beneficiaryAccountId
    );
}
