package co.edu.uniquindio.linguaplus.modelo;

public class Docente {
    private final String identificacion;
    private final String nombre;
    private final String idiomaEspecialidad;
    private final String telefono;
    private final double tarifaSesion;

    public Docente(String identificacion, String nombre, String idiomaEspecialidad,
                   String telefono, double tarifaSesion) {
        if (identificacion == null || identificacion.isBlank()) throw new IllegalArgumentException("Identificacion obligatoria");
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("Nombre obligatorio");
        if (idiomaEspecialidad == null || idiomaEspecialidad.isBlank()) throw new IllegalArgumentException("Idioma obligatorio");
        if (tarifaSesion < 0) throw new IllegalArgumentException("Tarifa invalida");
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.idiomaEspecialidad = idiomaEspecialidad;
        this.telefono = telefono;
        this.tarifaSesion = tarifaSesion;
    }
    public String getIdentificacion() { return identificacion; }
    public String getNombre() { return nombre; }
    public String getIdiomaEspecialidad() { return idiomaEspecialidad; }
    public String getTelefono() { return telefono; }
    public double getTarifaSesion() { return tarifaSesion; }
    @Override public String toString() { return nombre + " - " + idiomaEspecialidad; }
}
