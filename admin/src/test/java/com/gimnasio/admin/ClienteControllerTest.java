package com.gimnasio.admin;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ClienteControllerTest {

    @Autowired
    private Validator validator;

    @Test
    void clientesDebeRenderizarLaVistaSinErrores() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Ana Gómez");
        cliente.setEmail("ana@test.com");
        cliente.setTelefono("3124567890");
        cliente.setEdad(25);
        cliente.setMembresia("Básico");
        cliente.setFechaInicio(java.time.LocalDate.of(2024, 1, 1));
        cliente.setFechaVence(java.time.LocalDate.of(2024, 2, 1));

        Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);
        assertThat(violations).isEmpty();
    }

    @Test
    void edadFueraDeRangoDebeGenerarErrorDeValidacion() {
        Cliente cliente = new Cliente();
        cliente.setId(1234567890L);
        cliente.setNombre("Ana Gómez");
        cliente.setEmail("ana@test.com");
        cliente.setTelefono("3124567890");
        cliente.setEdad(11);
        cliente.setMembresia("Básico");

        Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("La edad mínima permitida es de 12 años");
    }

    @Test
    void cedulaNoPositivaDebeGenerarErrorDeValidacion() {
        Cliente cliente = new Cliente();
        cliente.setId(0L);
        cliente.setNombre("Ana Gómez");
        cliente.setEmail("ana@test.com");
        cliente.setTelefono("3124567890");
        cliente.setEdad(25);
        cliente.setMembresia("Básico");
        cliente.setFechaInicio(java.time.LocalDate.of(2024, 1, 1));
        cliente.setFechaVence(java.time.LocalDate.of(2024, 2, 1));

        Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("La cédula debe ser un número entero positivo");
    }

    @Test
    void nombreConNumerosDebeGenerarErrorDeValidacion() {
        Cliente cliente = new Cliente();
        cliente.setId(1234567890L);
        cliente.setNombre("Ana123");
        cliente.setEmail("ana@test.com");
        cliente.setTelefono("3124567890");
        cliente.setEdad(25);
        cliente.setMembresia("Básico");
        cliente.setFechaInicio(java.time.LocalDate.of(2024, 1, 1));
        cliente.setFechaVence(java.time.LocalDate.of(2024, 2, 1));

        Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("El nombre solo puede tener letras y espacios, sin números ni caracteres especiales");
    }

    @Test
    void telefonoSinFormatoColombianoDebeGenerarErrorDeValidacion() {
        Cliente cliente = new Cliente();
        cliente.setId(1234567890L);
        cliente.setNombre("Ana Gómez");
        cliente.setEmail("ana@test.com");
        cliente.setTelefono("1234567890");
        cliente.setEdad(25);
        cliente.setMembresia("Básico");
        cliente.setFechaInicio(java.time.LocalDate.of(2024, 1, 1));
        cliente.setFechaVence(java.time.LocalDate.of(2024, 2, 1));

        Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("El teléfono debe tener 10 dígitos y empezar en 3");
    }
}
