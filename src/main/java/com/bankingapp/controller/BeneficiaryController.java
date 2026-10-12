package com.bankingapp.controller;

import com.bankingapp.dto.BeneficiaryLookupResponse;
import com.bankingapp.dto.BeneficiaryResponse;
import com.bankingapp.dto.SaveBeneficiaryRequest;
import com.bankingapp.service.BeneficiaryService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/beneficiaries")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    public BeneficiaryController(BeneficiaryService beneficiaryService) {
        this.beneficiaryService = beneficiaryService;
    }

    @GetMapping("/lookup")
    public BeneficiaryLookupResponse lookup(
            @RequestParam String accountNumber,
            Authentication authentication) {
        return beneficiaryService.lookupAccount(
                accountNumber, authentication.getName());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BeneficiaryResponse save(
            @RequestBody SaveBeneficiaryRequest request,
            Authentication authentication) {
        return beneficiaryService.save(request, authentication.getName());
    }

    @GetMapping
    public List<BeneficiaryResponse> getMine(
            Authentication authentication) {
        return beneficiaryService.getMine(authentication.getName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id,
            Authentication authentication) {
        beneficiaryService.delete(id, authentication.getName());
    }
}
