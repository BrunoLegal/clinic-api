package br.com.brunolegal.clinic_api.controller;

import br.com.brunolegal.clinic_api.dto.PatientRegistrationDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class PatientControllerTest {
    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    private static void configureProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    static {
        postgreSQLContainer.start();
    }

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
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
    public void register_WhenInvalidData_ShouldReturnBadRequest() throws Exception {
        //Arrange
        PatientRegistrationDTO dummyRegistrationDTO = new PatientRegistrationDTO("", "invalid-email", "123");

        //Act & Assert
        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dummyRegistrationDTO)))
                .andExpect(status().isBadRequest());
    }

}
