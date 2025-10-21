package br.com.brunolegal.clinic_api.repository;

import br.com.brunolegal.clinic_api.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    //creates a query SELECT * FROM patients WHERE email = ?1
    boolean existsByEmail(String email);
}
