package co.edu.uniquindio.linguaplus.modelo.comprobante;
import co.edu.uniquindio.linguaplus.modelo.Matricula;

public class ComprobanteExcel implements ComprobantePago {
    @Override public String generar(Matricula m) {
        return "matricula\testudiante\tprograma\ttotal\n" + m.getNumeroMatricula() + "\t" +
               m.getEstudiante().getNombreCompleto() + "\t" + m.getPrograma().getNombre() + "\t" +
               String.format("%.2f", m.calcularValorTotal());
    }
}
