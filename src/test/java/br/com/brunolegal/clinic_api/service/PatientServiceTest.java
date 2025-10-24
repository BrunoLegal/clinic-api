package br.com.brunolegal.clinic_api.service;

import br.com.brunolegal.clinic_api.domain.Patient;
import br.com.brunolegal.clinic_api.dto.PatientDetailsDTO;
import br.com.brunolegal.clinic_api.dto.PatientRegistrationDTO;
import br.com.brunolegal.clinic_api.dto.PatientUpdateDTO;
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
import java.util.Optional;

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



    /*
    ------------------
    Happy Path Tests
    ------------------
     */
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
    public void listAll_WhenPatientsExist_ShouldReturnDtoList(){
        //Arrange
        Patient dummyPatient1 = new Patient(1L, "John Doe", "johndoe@test.com", "11999998888");
        Patient dummyPatient2 = new Patient(2L, "Jane Smith", "janesmith@test.com", "11988887777");
        List<Patient> dummyPatientList = List.of(dummyPatient1, dummyPatient2);

        when(patientRepository.findAllByActiveTrue()).thenReturn(dummyPatientList);

        //Act
        List<PatientDetailsDTO> result = patientService.listAll();

        //Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        verify(patientRepository).findAllByActiveTrue();


    }
    @Test
    public void getPatientById_WhenPatientExists_ShouldReturnPatientDetails() {
        //Arrange
        Long patientId = 1L;
        Patient dummyPatient = new Patient(patientId, "John Doe", "johndoe@test.com", "11999998888");
        PatientDetailsDTO dummyPatientDetailsDTO = new PatientDetailsDTO(patientId, "John Doe", "johndoe@test.com", "11999998888");
        when(patientRepository.findByIdAndActiveTrue(patientId)).thenReturn(java.util.Optional.of(dummyPatient));
        when(patientMapper.toDetailsDto(dummyPatient)).thenReturn(dummyPatientDetailsDTO);

        //Act
        PatientDetailsDTO result = patientService.getPatientById(patientId);

        //Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(dummyPatientDetailsDTO);
        verify(patientRepository).findByIdAndActiveTrue(patientId);

    }

    @Test
    public void updatePatient_WhenDataIsValid_ShouldReturnUpdatedPatientDetails(){
        //Arrange
        Long patientId = 1L;
        Patient oldPatient = new Patient(1L, "John Doe", "johndoe@test.com", "11999998888");
        PatientUpdateDTO updateDTO = new PatientUpdateDTO("John Updated", "johnupdated@test.com", "11977776666");
        PatientDetailsDTO updatedPatientDetailsDTO = new PatientDetailsDTO(1L, "John Updated", "johnupdated@test.com", "11977776666");

        when(patientRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(oldPatient));
        when(patientRepository.findByEmail(updateDTO.email())).thenReturn(Optional.empty());
        when(patientRepository.save(oldPatient)).thenReturn(oldPatient);
        when(patientMapper.toDetailsDto(oldPatient)).thenReturn(updatedPatientDetailsDTO);

        //Act
        PatientDetailsDTO result = patientService.updatePatient(patientId, updateDTO);

        //Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(updatedPatientDetailsDTO);

        verify(patientRepository).findByIdAndActiveTrue(patientId);
        verify(patientRepository).findByEmail(updateDTO.email());
        verify(patientRepository).save(oldPatient);
        verify(patientMapper).toDetailsDto(oldPatient);

    }

    @Test
    public void deletePatient_WhenPatientExists_ShouldSetActiveToFalse(){
        //Arrange
        Patient dummyPatient = new Patient(null, "John Doe", "johndoe@test.com", "11999998888", true);

        when(patientRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(dummyPatient));

        //Act
        patientService.deletePatient(1L);

        //Assert
        verify(patientRepository).findByIdAndActiveTrue(1L);
        verify(patientRepository).save(dummyPatient);
        assertThat(dummyPatient.getActive()).isFalse();

    }




    /*
    -------------------
     Sad Path Tests
    -------------------
    */


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
    public void getPatientById_WhenPatientDoesNotExist_ShouldThrowException() {
        //Arrange
        Long patientId = 99L;
        when(patientRepository.findByIdAndActiveTrue(patientId)).thenReturn(java.util.Optional.empty());

        //Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> patientService.getPatientById(patientId));

        verify(patientRepository).findByIdAndActiveTrue(patientId);
        verify(patientMapper, never()).toDetailsDto(any());
    }

    @Test
    public void updatePatient_WhenPatientDoesNotExist_ShouldThrowException() {
        //Arrange
        Long patientId = 99L;
        PatientUpdateDTO updateDTO = new PatientUpdateDTO("John Updated", "johnupdated@test.com", "11977776666");

        when(patientRepository.findByIdAndActiveTrue(patientId)).thenThrow(ResourceNotFoundException.class);

        //Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> patientService.updatePatient(patientId, updateDTO));

        verify(patientRepository).findByIdAndActiveTrue(patientId);
        verify(patientRepository, never()).findByEmail(any());
        verify(patientRepository, never()).save(any());
        verify(patientMapper, never()).toDetailsDto(any());
    }

    @Test
    public void updatePatient_WhenEmailAlreadyExists_ShouldThrowException(){
        //Arrange
        Long patientId = 1L;
        PatientUpdateDTO updateDTO = new PatientUpdateDTO("John Updated", "coolnewmail@test.com", "11977778888");
        Patient oldPatient = new Patient(1L, "John Doe", "johndoe@test.com", "11999998888");
        Patient existingPatient = new Patient(2L, "Jane Smith", "coolnewmail@test.com", "11988887777");

        when(patientRepository.findByIdAndActiveTrue(patientId)).thenReturn(Optional.of(oldPatient));
        when(patientRepository.findByEmail(updateDTO.email())).thenReturn(Optional.of(existingPatient));

        //Act & Assert
        assertThrows(DuplicateResourceException.class, () -> patientService.updatePatient(patientId, updateDTO));

        verify(patientRepository).findByIdAndActiveTrue(patientId);
        verify(patientRepository).findByEmail(updateDTO.email());
        verify(patientRepository, never()).save(any());
        verify(patientMapper, never()).toDetailsDto(any());

    }

    @Test
    public void deletePatient_WhenPatientDoesNotExist_ShouldThrowException(){
        //Arrange
        Long patientId = 99L;

        when(patientRepository.findByIdAndActiveTrue(patientId)).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> patientService.deletePatient(patientId));

        verify(patientRepository).findByIdAndActiveTrue(patientId);
        verify(patientRepository, never()).save(any());
    }


}
