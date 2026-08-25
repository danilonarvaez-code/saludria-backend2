package com.citas.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "detalle_cita")
public class DetalleCita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cita_id", nullable = false)
    @JsonIgnoreProperties({
        "hibernateLazyInitializer",
        "handler"
    })
    private Cita cita;

    @Column(name = "motivo_consulta", length = 500)
    private String motivoConsulta;

    @Column(name = "observaciones", length = 1000)
    private String observaciones;

    @Column(name = "diagnostico", length = 500)
    private String diagnostico;

    @Column(name = "tratamiento", length = 1000)
    private String tratamiento;

    @Column(name = "estado_atencion", length = 50)
    private String estadoAtencion;

    public DetalleCita() {
    }

    public DetalleCita(
            Long id,
            Cita cita,
            String motivoConsulta,
            String observaciones,
            String diagnostico,
            String tratamiento,
            String estadoAtencion) {

        this.id = id;
        this.cita = cita;
        this.motivoConsulta = motivoConsulta;
        this.observaciones = observaciones;
        this.diagnostico = diagnostico;
        this.tratamiento = tratamiento;
        this.estadoAtencion = estadoAtencion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cita getCita() {
        return cita;
    }

    public void setCita(Cita cita) {
        this.cita = cita;
    }

    public String getMotivoConsulta() {
        return motivoConsulta;
    }

    public void setMotivoConsulta(String motivoConsulta) {
        this.motivoConsulta = motivoConsulta;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(String tratamiento) {
        this.tratamiento = tratamiento;
    }

    public String getEstadoAtencion() {
        return estadoAtencion;
    }

    public void setEstadoAtencion(String estadoAtencion) {
        this.estadoAtencion = estadoAtencion;
    }
}