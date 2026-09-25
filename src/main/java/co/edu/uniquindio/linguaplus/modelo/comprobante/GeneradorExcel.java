package co.edu.uniquindio.linguaplus.modelo.comprobante;

import co.edu.uniquindio.linguaplus.modelo.Matricula;

public class GeneradorExcel implements GeneradorComprobante {
    @Override
    public String emitir(Matricula m) {
        StringBuilder sb = new StringBuilder();
        // Encabezados tabulares de Excel (separados por punto y coma para compatibilidad regional)
        sb.append("NUMERO_MATRICULA;DOCUMENTO;ESTUDIANTE;PROGRAMA;TIPO;VALOR_BASE;DESCUENTO_PORCENTAJE;TOTAL_PAGADO\n");
        sb.append(m.getNumeroMatricula()).append(";")
          .append(m.getEstudiante().getDocumento()).append(";")
          .append(m.getEstudiante().getNombreCompleto()).append(";")
          .append(m.getPrograma().getNombre()).append(";")
          .append(m.getPrograma().getTipo()).append(";")
          .append(String.format("%.2f", m.getPrograma().calcularValorBase())).append(";")
          .append(m.getDescuentoPorcentaje()).append(";")
          .append(String.format("%.2f", m.calcularValorTotal())).append("\n");
        return sb.toString();
    }
}
