package br.com.brunolegal.clinic_api.controller;

import br.com.brunolegal.clinic_api.domain.Patient;
import br.com.brunolegal.clinic_api.dto.PatientRegistrationDTO;
import br.com.brunolegal.clinic_api.dto.PatientUpdateDTO;
import br.com.brunolegal.clinic_api.repository.PatientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PatientRepository patientRepository;

    /*
    ------------------
    Happy Path Tests
    ------------------
     */

    @Test
    @Transactional
    public void register_WhenValidData_ShouldReturnCreated() throws Exception {
        //Arrange
        PatientRegistrationDTO dummyRegistrationDTO = new PatientRegistrationDTO("John Doe", "johndoe@test.com", "11999998888");

        //Act & Assert
        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dummyRegistrationDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("John Doe"))) // Verifique 'jsonPath' do 'org.springframework.test.web.servlet.result.MockMvcResultMatchers'
                .andExpect(jsonPath("$.email", is("johndoe@test.com")))
                .andExpect(jsonPath("$.phone", is("11999998888")));
    }

    @Test
    @Transactional
    public void listAllWhenPatientsExist_ShouldReturnOk() throws Exception {
        //Arrange
        Patient patient1 = new Patient(null, "Alice Smith", "alicesmith@test.com", "11988887777");
        Patient patient2 = new Patient(null, "Bob Johnson", "bobjohnson@test.com", "11977776666");
        Patient inactivePatient = new Patient(null, "Inactive Patient", "inactive@test.com", "11966665555", false);

        patientRepository.saveAll(List.of(patient1,patient2, inactivePatient));

        //Act & Assert
        mockMvc.perform(get("/patients")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Alice Smith")))
                .andExpect(jsonPath("$[1].name", is("Bob Johnson")));

    }

    @Test
    @Transactional
    public void getById_WhenPatientExists_ShouldReturnOk() throws Exception {
        //Arrange
        Patient dummyPatient = new Patient(null, "John Doe", "johndoe@test.com", "11999998888");
        Patient savedPatient = patientRepository.save(dummyPatient);

        //Act & Assert
        mockMvc.perform(get("/patients/{id}", savedPatient.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("johndoe@test.com")))
                .andExpect(jsonPath("$.phone", is("11999998888")));


    }

    @Test
    @Transactional
    public void update_WhenValidData_ShouldReturnOk() throws Exception {
        //Arrange
        Patient dummyPatient = new Patient(null, "John Doe", "johndoe@test.com", "11999998888");
        Patient savedPatient = patientRepository.save(dummyPatient);
        PatientUpdateDTO updateDTO = new PatientUpdateDTO("John Updated", "coolnewmail@test.com", "11911112222");

        //Act & Assert
        mockMvc.perform(put("/patients/{id}", savedPatient.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(updateDTO.name())))
                .andExpect(jsonPath("$.email", is(updateDTO.email())))
                .andExpect(jsonPath("$.phone", is(updateDTO.phone())));
    }

    @Test
    @Transactional
    public void delete_WhenPatientExists_ShouldReturnNoContent() throws Exception {
        //Arrange
        Patient dummyPatient = new Patient(null, "John Doe", "johndoe@test.com", "11999998888");
        Patient savedPatient = patientRepository.save(dummyPatient);

        //Act & Assert
        mockMvc.perform(delete("/patients/{id}", savedPatient.getId()))
                .andExpect(status().isNoContent());
    }



    /*
    ------------------
    Sad Path Tests
    ------------------
     */

    @Test
    @Transactional
    public void register_WhenDuplicateEmail_ShouldReturnConflict() throws Exception {
        //Arrange
        Patient existingPatient = new Patient(null, "John Doe", "johndoe@test.com", "11999998888");
        patientRepository.save(existingPatient);
        PatientRegistrationDTO duplicateRegistrationDTO = new PatientRegistrationDTO("John Doe", "johndoe@test.com", "11999998888");
        //Act & Assert
        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRegistrationDTO)))
                .andExpect(status().isConflict());

    }

    @Test
    @Transactional
    public void register_WhenInvalidData_ShouldReturnBadRequest() throws Exception {
        //Arrange
        PatientRegistrationDTO dummyRegistrationDTO = new PatientRegistrationDTO("", "invalid-email", "123");

        //Act & Assert
        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dummyRegistrationDTO)))
                .andExpect(status().isBadRequest());
    }


    @Test
    @Transactional
    public void getById_WhenPatientDoesNotExist_ShouldReturnNotFound() throws Exception{
        //Arrange empty
        //Act & Assert
        mockMvc.perform(get("/patients/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    @Transactional
    public void update_WhenPatientDoesNotExist_ShouldReturnNotFound() throws Exception {
        //Arrange
        Long nonExistentId = 99L;
        PatientUpdateDTO updateDTO = new PatientUpdateDTO("John Updated", "coolnewmail@test.com", "11911112222");

        //Act & Assert
        mockMvc.perform(put("/patients/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());

    }

    @Test
    @Transactional
    public void update_WhenDuplicateEmail_ShouldReturnConflict() throws Exception {
        //Arrange
        Patient patient1 = new Patient(null, "John Doe", "johndoe@test.com", "11999998888");
        Patient patient2 = new Patient(null, "Jane Smith", "coolnewmail@test.com", "11988887777");
        patientRepository.saveAll(List.of(patient1, patient2));
        PatientUpdateDTO updateDTO = new PatientUpdateDTO("John Updated", "coolnewmail@test.com", "11911112222");

        //Act & Assert
        mockMvc.perform(put("/patients/{id}", patient1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    @Transactional
    public void delete_WhenPatientDoesNotExist_ShouldReturnNotFound() throws Exception {
        //Act & Assert
        mockMvc.perform(delete("/patients/{id}", 99L)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void whenPatientIsInactive_ShouldReturnNotFound() throws Exception{
        //Arrange
        Patient inactivePatient = new Patient(null, "Inactive Patient", "inactive@test.com", "11966665555", false);
        Patient savedPatient = patientRepository.save(inactivePatient);
        //Act & Assert
        mockMvc.perform(get("/patients/{id}", savedPatient.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
