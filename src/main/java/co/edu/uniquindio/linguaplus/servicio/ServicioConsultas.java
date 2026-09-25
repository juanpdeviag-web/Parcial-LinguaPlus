package co.edu.uniquindio.linguaplus.servicio;

import co.edu.uniquindio.linguaplus.modelo.Academia;
import co.edu.uniquindio.linguaplus.modelo.Estudiante;
import co.edu.uniquindio.linguaplus.modelo.Matricula;
import java.time.LocalDate;
import java.util.Optional;

public class ServicioConsultas {
    private final Academia academia;
    public ServicioConsultas(Academia academia) { this.academia = academia; }

    public Optional<Estudiante> buscarPorTelefono(String telefono) {
        return academia.buscarEstudiantePorTelefono(telefono);
    }

    public boolean telefonoEsNumeroPerfecto(String telefono) {
        String normalizado = telefono == null ? "" : telefono.replaceAll("\\D", "");
        if (normalizado.isEmpty()) return false;
        try { return esNumeroPerfecto(Long.parseLong(normalizado)); }
        catch (NumberFormatException ex) { return false; }
    }

    public boolean esNumeroPerfecto(long numero) {
        if (numero <= 1) return false;
        long suma = 1;
        for (long d = 2; d * d <= numero; d++) {
            if (numero % d == 0) {
                suma += d;
                long pareja = numero / d;
                if (pareja != d) suma += pareja;
                if (suma > numero) return false;
            }
        }
        return suma == numero;
    }

    public double calcularIngresos(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null || desde.isAfter(hasta))
            throw new IllegalArgumentException("Periodo de consulta invalido");
        double total = 0;
        for (Matricula m : academia.getMatriculas()) {
            LocalDate f = m.getFechaInicio();
            if (!f.isBefore(desde) && !f.isAfter(hasta)) total += m.calcularValorTotal();
        }
        return total;
    }
}
