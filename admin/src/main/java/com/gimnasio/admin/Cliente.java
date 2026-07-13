package com.gimnasio.admin;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
public class Cliente {

    @Id
    @NotNull(message = "La cédula no puede estar vacía")
    @Positive(message = "La cédula debe ser un número positivo")
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    private String nombre;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "Debe proporcionar un formato de correo válido")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(min = 10, max = 10, message = "El teléfono debe tener exactamente 10 dígitos")
    @Pattern(regexp = "\\d{10}", message = "El teléfono debe ser un número de 10 dígitos")
    private String telefono;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 12, message = "La edad mínima permitida es de 12 años")
    @Max(value = 100, message = "La edad máxima permitida es de 100 años")
    private Integer edad;

    @NotBlank(message = "Debe seleccionar un plan")
    private String membresia;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    private LocalDate fechaVence;

    private Long costoDia;

    public Cliente() {}

    // Método de validación personalizada para las fechas
    public boolean tieneFechasValidas() {
        if (this.fechaInicio == null || this.fechaVence == null) return false;
        return !this.fechaVence.isBefore(this.fechaInicio);
    }

    public boolean isActivo() {
        if (this.fechaVence == null) return false;
        return !this.fechaVence.isBefore(LocalDate.now());
    }

    // --- Mantén tus Getters y Setters intactos ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }
    public String getMembresia() { return membresia; }
    public void setMembresia(String membresia) { this.membresia = membresia; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaVence() { return fechaVence; }
    public void setFechaVence(LocalDate fechaVence) { this.fechaVence = fechaVence; }
    public Long getCostoDia() { return costoDia; }
public void setCostoDia(Long costoDia) { this.costoDia = costoDia; }
}