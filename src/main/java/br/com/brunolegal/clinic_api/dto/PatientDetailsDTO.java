package br.com.brunolegal.clinic_api.dto;


public record PatientDetailsDTO(

        Long id,
        String name,
        String email,
        String phone

) {

}
