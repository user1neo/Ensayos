package com.gimnasio.admin;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @ModelAttribute("clienteForm")
    public Cliente clienteForm() {
        return new Cliente();
    }

    @GetMapping("/")
    public String verDashboard(Model model) {
        List<Cliente> clientes = clienteRepository.findAll();
        
        long totalClientes = clientes.size();
        long activos = clientes.stream().filter(Cliente::isActivo).count();
        long inactivos = totalClientes - activos;
        
        // 📌 CÁLCULO SEGURO: Multiplica días reales por el costo diario guardado en el cliente
        long ingresosEstimados = clientes.stream().mapToLong(c -> {
            if (c.getFechaInicio() == null || c.getFechaVence() == null) {
                return 0;
            }
            
            long diasDuracion = java.time.temporal.ChronoUnit.DAYS.between(c.getFechaInicio(), c.getFechaVence());
            if (diasDuracion <= 0) diasDuracion = 1;

            // Si por alguna razón el costo por día es nulo o cero, le asignamos el mínimo (Básico: 800)
            long precioPorDia = (c.getCostoDia() != null && c.getCostoDia() > 0) ? c.getCostoDia() : 800;

            return precioPorDia * diasDuracion;
        }).sum();

        model.addAttribute("totalClientes", totalClientes);
        model.addAttribute("activos", activos);
        model.addAttribute("inactivos", inactivos);
        model.addAttribute("ingresos", ingresosEstimados);
        model.addAttribute("vista", "dashboard");
        return "index";
    }
    
    @GetMapping("/calculadora")
    public String verCalculadora(Model model) {
        model.addAttribute("vista", "calculadora");
        return "index";
    }


    @GetMapping("/clientes")
    public String listarClientes(
            @RequestParam(value = "buscar", required = false) String buscar,
            @RequestParam(value = "editarId", required = false) Long editarId,
            Model model) {
        
        java.util.List<Cliente> listaClientes = new java.util.ArrayList<>();
        
        if (buscar != null && !buscar.trim().isEmpty()) {
            String query = buscar.trim();
            try {
                // 1. Intentamos convertir a número para buscar por Cédula (ID)
                Long cedula = Long.parseLong(query);
                clienteRepository.findById(cedula).ifPresent(listaClientes::add);
            } catch (NumberFormatException e) {
                // 2. Si contiene letras, buscamos por Nombre
                listaClientes = clienteRepository.findByNombreContainingIgnoreCase(query);
            }
            model.addAttribute("buscarQuery", buscar);
        } else {
            // 3. Si no hay búsqueda, listamos todo
            listaClientes = clienteRepository.findAll();
        }
        
        model.addAttribute("clientes", listaClientes);
        
        // Evita sobreescribir el 'clienteForm' si ya viene con errores de validación
        if (!model.containsAttribute("clienteForm")) {
            model.addAttribute("clienteForm", editarId != null ? clienteRepository.findById(editarId).orElse(new Cliente()) : new Cliente());
        }
        
        model.addAttribute("modoEdicion", editarId != null);
        model.addAttribute("vista", "clientes");
        return "index";
    }

    @PostMapping("/guardar")
    public String guardarCliente(
            @Valid @ModelAttribute("clienteForm") Cliente cliente,
            BindingResult result,
            @RequestParam("modoEdicion") boolean modoEdicion,
            RedirectAttributes redirectAttributes,
            org.springframework.ui.Model model) { // 📌 Agregamos el Model aquí

        // 1. Validaciones estructurales anotadas de Jakarta (Spring)
        if (result.hasErrors()) {
            String mensajeError = "Por favor corrige los campos marcados.";
            if (result.hasFieldErrors("edad")) {
                mensajeError = "La edad ingresada no es válida. Debe estar entre 12 y 100 años.";
            }

            model.addAttribute("clientes", clienteRepository.findAll());
            model.addAttribute("vista", "clientes");
            model.addAttribute("error", mensajeError);
            model.addAttribute("modoEdicion", modoEdicion);
            model.addAttribute("clienteForm", cliente);
            return "index";
        }

        // 📌 ASIGNACIÓN DE COSTO DIARIO SEGÚN EL PLAN SELECCIONADO
        if (cliente.getCostoDia() == null || cliente.getCostoDia() == 0) {
            long costoAsignado = switch (cliente.getMembresia()) {
                case "Estándar" -> 1200;
                case "Premium" -> 1800;
                case "Básico" -> 800;
                default -> 800;
            };
            cliente.setCostoDia(costoAsignado);
        }

        // 2. Validación de lógica de negocio (Fechas consistentes)
        if (!cliente.tieneFechasValidas()) {
            model.addAttribute("clientes", clienteRepository.findAll());
            model.addAttribute("vista", "clientes");
            model.addAttribute("error", "Error: La fecha de vencimiento no puede ser anterior a la de inicio.");
            return "index"; // ✏️ Retorno directo
        }

        // 3. Validación de Llave Primaria Duplicada
        if (!modoEdicion && clienteRepository.existsById(cliente.getId())) {
            model.addAttribute("clientes", clienteRepository.findAll());
            model.addAttribute("vista", "clientes");
            model.addAttribute("error", "La cédula ingresada ya está registrada.");
            return "index"; // ✏️ Retorno directo
        }

        // Si todo sale bien, AQUÍ SÍ se usa el redirect original
        clienteRepository.save(cliente);
        redirectAttributes.addFlashAttribute("exito", "Cliente procesado exitosamente.");
        return "redirect:/clientes";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCliente(@PathVariable("id") Long id) {
        clienteRepository.deleteById(id);
        return "redirect:/clientes";
    }
}