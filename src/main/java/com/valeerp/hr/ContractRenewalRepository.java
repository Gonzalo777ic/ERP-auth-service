package com.valeerp.hr;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractRenewalRepository extends JpaRepository<ContractRenewal, Long> {

    List<ContractRenewal> findByContractIdOrderByStartsOnDesc(Long contractId);
}
