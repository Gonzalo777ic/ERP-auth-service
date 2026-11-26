package com.valeerp.hr;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractWageRepository extends JpaRepository<ContractWage, Long> {

    List<ContractWage> findByContractIdOrderByValidFromDesc(Long contractId);
}
