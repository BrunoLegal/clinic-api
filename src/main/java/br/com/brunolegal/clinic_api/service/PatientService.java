package br.com.brunolegal.clinic_api.service;

import br.com.brunolegal.clinic_api.domain.Patient;
import br.com.brunolegal.clinic_api.dto.PatientDetailsDTO;
import br.com.brunolegal.clinic_api.dto.PatientRegistrationDTO;
import br.com.brunolegal.clinic_api.mapper.PatientMapper;
import br.com.brunolegal.clinic_api.repository.PatientRepository;
import org.springframework.stereotype.Service;

@Service
public class PatientService {
    final PatientRepository patientRepository;
    final PatientMapper patientMapper;

    public PatientService(PatientRepository patientRepository, PatientMapper patientMapper){
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
    }

    public PatientDetailsDTO createPatient(PatientRegistrationDTO dto){
        if(patientRepository.existsByEmail(dto.email())){
            throw new IllegalArgumentException("Email already in use");
        }
        Patient patient = patientMapper.toEntity(dto);

        Patient savedPatient = patientRepository.save(patient);

        return patientMapper.toDetailsDto(savedPatient);

    }




}
