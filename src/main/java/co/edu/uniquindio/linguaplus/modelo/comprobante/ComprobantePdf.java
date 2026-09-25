package co.edu.uniquindio.linguaplus.modelo.comprobante;
import co.edu.uniquindio.linguaplus.modelo.Matricula;

public class ComprobantePdf implements ComprobantePago {
    @Override public String generar(Matricula m) {
        return "[PDF] Matricula #" + m.getNumeroMatricula() + " | Estudiante: " + m.getEstudiante().getNombreCompleto() +
               " | Programa: " + m.getPrograma().getNombre() + " | Total: $" + String.format("%.2f", m.calcularValorTotal());
    }
}
