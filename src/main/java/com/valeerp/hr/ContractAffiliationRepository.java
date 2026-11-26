package com.valeerp.hr;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractAffiliationRepository extends JpaRepository<ContractAffiliation, Long> {

    List<ContractAffiliation> findByContractIdOrderByIdAsc(Long contractId);
}
