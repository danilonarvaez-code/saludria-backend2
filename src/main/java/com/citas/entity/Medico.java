package com.citas.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "medicos")
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del médico es obligatorio")
    @Size(min = 3, max = 100,
            message = "El nombre debe tener entre 3 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "La especialidad es obligatoria")
    @Size(max = 100,
            message = "La especialidad no puede superar 100 caracteres")
    @Column(nullable = false, length = 100)
    private String especialidad;

    @NotBlank(message = "La tarjeta profesional es obligatoria")
    @Size(max = 50,
            message = "La tarjeta profesional no puede superar 50 caracteres")
    @Column(name = "tarjeta_profesional",
            nullable = false,
            length = 50)
    private String tarjetaProfesional;

    @NotBlank(message = "El correo del médico es obligatorio")
    @Email(message = "El correo del médico no tiene un formato válido")
    @Column(nullable = false, length = 150)
    private String email;

    public Medico() {
    }

    public Medico(String nombre,
                   String especialidad,
                   String tarjetaProfesional,
                   String email) {

        this.nombre = nombre;
        this.especialidad = especialidad;
        this.tarjetaProfesional = tarjetaProfesional;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getTarjetaProfesional() {
        return tarjetaProfesional;
    }

    public void setTarjetaProfesional(String tarjetaProfesional) {
        this.tarjetaProfesional = tarjetaProfesional;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}