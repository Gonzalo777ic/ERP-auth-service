package com.valeerp.hr;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByLegalEntityId(Long legalEntityId);

    Optional<Employee> findByLegalEntityIdAndDocumentTypeAndDocumentNumber(
            Long legalEntityId,
            IdentityDocumentType documentType,
            String documentNumber
    );

    Optional<Employee> findByLegalEntityIdAndCuspp(Long legalEntityId, String cuspp);

    boolean existsByUserId(Long userId);
}
