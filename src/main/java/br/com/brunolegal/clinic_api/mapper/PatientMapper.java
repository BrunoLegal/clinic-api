package br.com.brunolegal.clinic_api.mapper;

import br.com.brunolegal.clinic_api.domain.Patient;
import br.com.brunolegal.clinic_api.dto.PatientDetailsDTO;
import br.com.brunolegal.clinic_api.dto.PatientRegistrationDTO;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    public Patient toEntity(PatientRegistrationDTO dto){
        Patient patient = new Patient();
        patient.setName(dto.name());
        patient.setEmail(dto.email());
        patient.setPhone(dto.phone());

        return patient;
    }

    public PatientDetailsDTO toDetailsDto(Patient patient){
        return new PatientDetailsDTO(patient.getId(), patient.getName(), patient.getEmail(), patient.getPhone());
    }
}
