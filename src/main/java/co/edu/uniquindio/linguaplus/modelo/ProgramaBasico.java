package co.edu.uniquindio.linguaplus.modelo;
import java.util.List;
public class ProgramaBasico extends ProgramaFormacion {
    public ProgramaBasico(String codigo, String nombre, String idioma, String descripcion,
                          int duracionMeses, double valorMensual, EstadoPrograma estado, List<String> beneficios) {
        super(codigo, nombre, idioma, descripcion, duracionMeses, valorMensual, estado, beneficios);
    }
    @Override public String getTipo() { return "Basico"; }
}
