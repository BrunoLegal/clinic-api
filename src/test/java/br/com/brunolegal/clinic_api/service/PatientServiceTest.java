package br.com.brunolegal.clinic_api.service;

import br.com.brunolegal.clinic_api.domain.Patient;
import br.com.brunolegal.clinic_api.dto.PatientDetailsDTO;
import br.com.brunolegal.clinic_api.dto.PatientRegistrationDTO;
import br.com.brunolegal.clinic_api.mapper.PatientMapper;
import br.com.brunolegal.clinic_api.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private PatientMapper patientMapper;

    @InjectMocks
    private PatientService patientService;

    @Test
    public void createPatient_WhenEmailIsNew_ShouldReturnPatientDetails(){
        //Arrange
        PatientRegistrationDTO dummyRegistrationDTO = new PatientRegistrationDTO("John Doe", "johndoe@test.com", "11999998888");
        Patient dummyPatientNoId = new Patient(null, "John Doe", "johndoe@test.com", "11999998888");
        Patient dummyPatient = new Patient(1L, "John Doe", "johndoe@test.com", "11999998888");
        PatientDetailsDTO dummyPatientDetailsDTO = new PatientDetailsDTO(1L, "John Doe", "johndoe@test.com", "11999998888");

        when(patientRepository.existsByEmail(dummyRegistrationDTO.email())).thenReturn(false);
        when(patientMapper.toEntity(dummyRegistrationDTO)).thenReturn(dummyPatientNoId);
        when(patientRepository.save(dummyPatientNoId)).thenReturn(dummyPatient);
        when(patientMapper.toDetailsDto(dummyPatient)).thenReturn(dummyPatientDetailsDTO);

        //Act
        PatientDetailsDTO result = patientService.createPatient(dummyRegistrationDTO);

        //Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(dummyPatientDetailsDTO);
        verify(patientRepository).existsByEmail(dummyRegistrationDTO.email());
        verify(patientRepository).save(dummyPatientNoId);

    }
    @Test
    public void createPatient_WhenEmailAlreadyExists_ShouldThrowException() {
        //Arrange
        PatientRegistrationDTO dummyRegistrationDTO = new PatientRegistrationDTO("John Doe", "johndoe@test.com", "11999998888");

        when(patientRepository.existsByEmail(dummyRegistrationDTO.email())).thenReturn(true);

        //Act & Assert
        assertThrows(IllegalArgumentException.class, () -> patientService.createPatient(dummyRegistrationDTO));

        verify(patientRepository, never()).save(any());
        verify(patientMapper, never()).toEntity(any());
    }
}
