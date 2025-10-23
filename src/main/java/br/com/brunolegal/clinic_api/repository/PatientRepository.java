package br.com.brunolegal.clinic_api.repository;

import br.com.brunolegal.clinic_api.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    //creates a query SELECT * FROM patients WHERE email = ?1
    boolean existsByEmail(String email);
    //creates a query SELECT * FROM patients WHERE email = ?1 that returns an Optional<Patient>
    Optional<Patient> findByEmail(String email);
}
