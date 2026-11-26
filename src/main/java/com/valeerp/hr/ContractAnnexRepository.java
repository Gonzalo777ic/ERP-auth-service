package com.valeerp.hr;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractAnnexRepository extends JpaRepository<ContractAnnex, Long> {

    List<ContractAnnex> findByContractIdOrderByRecordedAtDesc(Long contractId);
}