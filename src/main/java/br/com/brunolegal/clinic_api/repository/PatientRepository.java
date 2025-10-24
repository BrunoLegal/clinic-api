package br.com.brunolegal.clinic_api.repository;

import br.com.brunolegal.clinic_api.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    //creates a query SELECT * FROM patients WHERE email = ?1
    boolean existsByEmail(String email);
    //creates a query SELECT * FROM patients WHERE email = ?1 that returns an Optional<Patient>
    Optional<Patient> findByEmail(String email);
    //creates a query SELECT * FROM patients WHERE active = true
    List<Patient> findAllByActiveTrue();
    //creates a query SELECT * FROM patients WHERE id = ?1 AND active = true
    Optional<Patient> findByIdAndActiveTrue(Long id);
}
