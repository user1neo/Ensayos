package com.gimnasio.admin;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class ClienteControllerDuplicateTest {

    @Test
    void guardarClienteConCedulaDuplicadaDebeMantenerDatosDelFormulario() {
        ClienteController controller = new ClienteController();
        ClienteRepository clienteRepository = Mockito.mock(ClienteRepository.class);
        EstadisticasService estadisticasService = Mockito.mock(EstadisticasService.class);

        ReflectionTestUtils.setField(controller, "clienteRepository", clienteRepository);
        ReflectionTestUtils.setField(controller, "estadisticasService", estadisticasService);

        Cliente cliente = new Cliente();
        cliente.setId(1234567890L);
        cliente.setNombre("Ana Gómez");
        cliente.setEmail("ana@test.com");
        cliente.setTelefono("3124567890");
        cliente.setEdad(25);
        cliente.setMembresia("Básico");
        cliente.setFechaInicio(java.time.LocalDate.of(2024, 1, 1));
        cliente.setFechaVence(java.time.LocalDate.of(2024, 2, 1));

        BindingResult bindingResult = new BeanPropertyBindingResult(cliente, "clienteForm");
        ExtendedModelMap model = new ExtendedModelMap();

        when(clienteRepository.existsById(1234567890L)).thenReturn(true);
        when(clienteRepository.findAll()).thenReturn(List.of());

        String view = controller.guardarCliente(
                cliente,
                bindingResult,
                false,
                (org.springframework.web.servlet.mvc.support.RedirectAttributes) new RedirectAttributesModelMap(),
                model);

        assertThat(view).isEqualTo("index");
        assertThat(model.get("error")).isEqualTo("La cédula ingresada ya está registrada.");
        assertThat(model.get("clienteForm")).isSameAs(cliente);
        assertThat(((Cliente) model.get("clienteForm")).getNombre()).isEqualTo("Ana Gómez");
    }
}
