package com.gimnasio.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class EstadisticasService {

    private static final String[] MESES = {
            "Ene", "Feb", "Mar", "Abr", "May", "Jun",
            "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"
    };

    private static final String[] PLANES = {
            "Básico", "Estándar", "Premium", "Personalizado"
    };

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Long> contarRegistrosPorMes(int anio) {
        long[] conteos = new long[12];
        for (Cliente cliente : clienteRepository.findAll()) {
            if (cliente.getFechaInicio() == null) continue;
            if (cliente.getFechaInicio().getYear() != anio) continue;
            conteos[cliente.getFechaInicio().getMonthValue() - 1]++;
        }
        return java.util.Arrays.stream(conteos).boxed().toList();
    }

    public String[] getEtiquetasMeses() {
        return MESES;
    }

    public Map<String, Long> contarPorPlan(boolean soloActivos) {
        Map<String, Long> conteos = new LinkedHashMap<>();
        for (String plan : PLANES) {
            conteos.put(plan, 0L);
        }

        for (Cliente cliente : clienteRepository.findAll()) {
            if (soloActivos && !cliente.isActivo()) continue;
            String plan = cliente.getMembresia();
            if (plan == null || plan.isBlank()) continue;
            conteos.merge(plan, 1L, Long::sum);
        }

        conteos.entrySet().removeIf(e -> e.getValue() == 0);
        return conteos;
    }

    public int getAnioActual() {
        return Year.now().getValue();
    }

    public String registrosComoCsv(List<Long> registros) {
        return registros.stream()
                .map(String::valueOf)
                .reduce((a, b) -> a + "," + b)
                .orElse("");
    }
}
