package br.com.brunolegal.clinic_api.controller;

import br.com.brunolegal.clinic_api.dto.PatientDetailsDTO;
import br.com.brunolegal.clinic_api.dto.PatientRegistrationDTO;
import br.com.brunolegal.clinic_api.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientController {
    private final PatientService patientService;

    public PatientController (PatientService patientService){
        this.patientService = patientService;
    }
    @PostMapping
    public ResponseEntity<PatientDetailsDTO> register (
            @RequestBody @Valid PatientRegistrationDTO dto,
            UriComponentsBuilder uriBuilder
    ){
            PatientDetailsDTO savedDto = patientService.createPatient(dto);

            URI uri = uriBuilder
                    .path("/patients/{id}")
                    .buildAndExpand(savedDto.id())
                    .toUri();

            return ResponseEntity.created(uri).body(savedDto);

    }

    @GetMapping
    public ResponseEntity<List<PatientDetailsDTO>> listAll(){
        List<PatientDetailsDTO> patients = patientService.listAll();
        return ResponseEntity.ok(patients);
    }

}
