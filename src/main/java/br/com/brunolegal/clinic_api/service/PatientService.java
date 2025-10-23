package br.com.brunolegal.clinic_api.service;

import br.com.brunolegal.clinic_api.domain.Patient;
import br.com.brunolegal.clinic_api.dto.PatientDetailsDTO;
import br.com.brunolegal.clinic_api.dto.PatientRegistrationDTO;
import br.com.brunolegal.clinic_api.dto.PatientUpdateDTO;
import br.com.brunolegal.clinic_api.exception.DuplicateResourceException;
import br.com.brunolegal.clinic_api.exception.ResourceNotFoundException;
import br.com.brunolegal.clinic_api.mapper.PatientMapper;
import br.com.brunolegal.clinic_api.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
            throw new DuplicateResourceException("Email already in use");
        }
        Patient patient = patientMapper.toEntity(dto);

        Patient savedPatient = patientRepository.save(patient);

        return patientMapper.toDetailsDto(savedPatient);

    }

    public PatientDetailsDTO getPatientById(Long id){
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
        return patientMapper.toDetailsDto(patient);
    }

    public List<PatientDetailsDTO> listAll(){
        List<Patient> patients = patientRepository.findAll();
        return patients.stream()
                .map(patientMapper::toDetailsDto)
                .collect(Collectors.toList());
    }

    public PatientDetailsDTO updatePatient(Long id, PatientUpdateDTO dto){
        Patient patient = patientRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
        Optional<Patient> patientByEmail = patientRepository.findByEmail(dto.email());
        if(patientByEmail.isPresent() && !patientByEmail.get().getId().equals(id)){
            throw new DuplicateResourceException("Email already in use");
        }

        patient.setName(dto.name());
        patient.setEmail(dto.email());
        patient.setPhone(dto.phone());

        Patient updatedPatient = patientRepository.save(patient);
        return patientMapper.toDetailsDto(updatedPatient);
    }






}
