package co.edu.uniquindio.linguaplus.modelo.comprobante;

import co.edu.uniquindio.linguaplus.modelo.Matricula;
import co.edu.uniquindio.linguaplus.modelo.ServicioAdicional;

public class GeneradorPdf implements GeneradorComprobante {
    @Override
    public String emitir(Matricula m) {
        StringBuilder sb = new StringBuilder();
        sb.append("==================================================\n");
        sb.append("             COMPROBANTE OFICIAL DE PAGO          \n");
        sb.append("                  LINGUAPLUS ACADEMY              \n");
        sb.append("==================================================\n");
        sb.append("Matrícula Nro: ").append(m.getNumeroMatricula()).append("\n");
        sb.append("Fecha de Inicio: ").append(m.getFechaInicio()).append("\n");
        sb.append("Estudiante: ").append(m.getEstudiante().getNombreCompleto()).append("\n");
        sb.append("Documento: ").append(m.getEstudiante().getDocumento()).append("\n");
        sb.append("--------------------------------------------------\n");
        sb.append("Programa: ").append(m.getPrograma().getNombre()).append(" (").append(m.getPrograma().getTipo()).append(")\n");
        sb.append("Valor Base: $").append(String.format("%.2f", m.getPrograma().calcularValorBase())).append("\n");
        
        if (!m.getServiciosAdicionales().isEmpty()) {
            sb.append("Servicios Adicionales:\n");
            for (ServicioAdicional s : m.getServiciosAdicionales()) {
                sb.append("  - ").append(s.getNombre()).append(": $").append(String.format("%.2f", s.getPrecio())).append("\n");
            }
        }
        
        sb.append("Descuento Aplicado: ").append(m.getDescuentoPorcentaje()).append("%\n");
        sb.append("--------------------------------------------------\n");
        sb.append("TOTAL PAGADO: $").append(String.format("%.2f", m.calcularValorTotal())).append("\n");
        sb.append("Observaciones: ").append(m.getObservaciones() != null ? m.getObservaciones() : "Ninguna").append("\n");
        sb.append("==================================================\n");
        return sb.toString();
    }
}
