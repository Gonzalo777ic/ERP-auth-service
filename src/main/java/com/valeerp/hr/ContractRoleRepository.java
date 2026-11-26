package com.valeerp.hr;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractRoleRepository extends JpaRepository<ContractRole, Long> {

    List<ContractRole> findByContractIdOrderByStartsOnDesc(Long contractId);
}
