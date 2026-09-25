package co.edu.uniquindio.linguaplus.modelo;
import java.util.List;
public class ProgramaPersonalizado extends ProgramaFormacion {
    private final int sesionesTutor;
    private final String nivelRequerido;
    private final String objetivosEstudiante;

    public ProgramaPersonalizado(String codigo, String nombre, String idioma, String descripcion,
                                 int duracionMeses, double valorMensual, EstadoPrograma estado, List<String> beneficios,
                                 int sesionesTutor, String nivelRequerido, String objetivosEstudiante) {
        super(codigo, nombre, idioma, descripcion, duracionMeses, valorMensual, estado, beneficios);
        if (sesionesTutor < 0) throw new IllegalArgumentException("Sesiones invalidas");
        if (nivelRequerido == null || nivelRequerido.isBlank()) throw new IllegalArgumentException("Nivel requerido obligatorio");
        if (objetivosEstudiante == null || objetivosEstudiante.isBlank()) throw new IllegalArgumentException("Objetivos obligatorios");
        this.sesionesTutor = sesionesTutor;
        this.nivelRequerido = nivelRequerido;
        this.objetivosEstudiante = objetivosEstudiante;
    }
    public int getSesionesTutor() { return sesionesTutor; }
    public String getNivelRequerido() { return nivelRequerido; }
    public String getObjetivosEstudiante() { return objetivosEstudiante; }
    @Override public String getTipo() { return "Personalizado"; }
}
