package com.valeerp.hr;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractShiftRepository extends JpaRepository<ContractShift, Long> {

    List<ContractShift> findByContractIdOrderByStartsOnDesc(Long contractId);
}
