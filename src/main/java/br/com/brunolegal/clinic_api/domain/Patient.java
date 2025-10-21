package br.com.brunolegal.clinic_api.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Table(name="patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank //must not be null
    private String name;

    @NotBlank
    @Email //must be a valid email address
    @Column(unique = true)
    private String email;

    @NotBlank
    @Pattern(regexp = "\\d{10,11}") //validates between 10 and 11 numbers for the phone
    private String phone;
}
