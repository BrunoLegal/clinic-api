package br.com.brunolegal.clinic_api.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PatientRegistrationDTO(

        @NotBlank(message = "Name can't be empty")
        String name,

        @NotBlank(message = "Email can't be empty")
        @Email(message = "Email is invalid")
        String email,

        @NotBlank(message = "O telefone não pode estar em branco.")
        @Pattern(regexp = "\\d{10,11}", message = "The phone number must be between 10 and 11.")
        String phone


) {


}
