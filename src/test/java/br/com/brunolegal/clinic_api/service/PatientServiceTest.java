package br.com.brunolegal.clinic_api.service;

import br.com.brunolegal.clinic_api.domain.Patient;
import br.com.brunolegal.clinic_api.dto.PatientDetailsDTO;
import br.com.brunolegal.clinic_api.dto.PatientRegistrationDTO;
import br.com.brunolegal.clinic_api.exception.DuplicateResourceException;
import br.com.brunolegal.clinic_api.exception.ResourceNotFoundException;
import br.com.brunolegal.clinic_api.mapper.PatientMapper;
import br.com.brunolegal.clinic_api.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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
        assertThrows(DuplicateResourceException.class, () -> patientService.createPatient(dummyRegistrationDTO));

        verify(patientRepository, never()).save(any());
        verify(patientMapper, never()).toEntity(any());
    }

    @Test
    public void listAll_WhenPatientsExist_ShouldReturnDtoList(){
        //Arrange
        Patient dummyPatient1 = new Patient(1L, "John Doe", "johndoe@test.com", "11999998888");
        Patient dummyPatient2 = new Patient(2L, "Jane Smith", "janesmith@test.com", "11988887777");
        List<Patient> dummyPatientList = List.of(dummyPatient1, dummyPatient2);

        when(patientRepository.findAll()).thenReturn(dummyPatientList);

        //Act
        List<PatientDetailsDTO> result = patientService.listAll();

        //Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        verify(patientRepository).findAll();


    }

    @Test
    public void getPatientById_WhenPatientExists_ShouldReturnPatientDetails() {
        //Arrange
        Long patientId = 1L;
        Patient dummyPatient = new Patient(patientId, "John Doe", "johndoe@test.com", "11999998888");
        PatientDetailsDTO dummyPatientDetailsDTO = new PatientDetailsDTO(patientId, "John Doe", "johndoe@test.com", "11999998888");
        when(patientRepository.findById(patientId)).thenReturn(java.util.Optional.of(dummyPatient));
        when(patientMapper.toDetailsDto(dummyPatient)).thenReturn(dummyPatientDetailsDTO);

        //Act
        PatientDetailsDTO result = patientService.getPatientById(patientId);

        //Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(dummyPatientDetailsDTO);
        verify(patientRepository).findById(patientId);

    }

    @Test
    public void getPatientById_WhenPatientDoesNotExist_ShouldThrowException() {
        //Arrange
        Long patientId = 99L;
        when(patientRepository.findById(patientId)).thenReturn(java.util.Optional.empty());

        //Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> patientService.getPatientById(patientId));

        verify(patientRepository).findById(patientId);
        verify(patientMapper, never()).toDetailsDto(any());
    }

}
