package co.edu.uniquindio.linguaplus.modelo;

import java.time.LocalDate;
import java.util.Objects;

public class Estudiante {
    private final String nombreCompleto;
    private final String documento;
    private final String telefono;
    private final String correo;
    private final int edad;
    private final LocalDate fechaRegistro;

    public Estudiante(String nombreCompleto, String documento, String telefono,
                      String correo, int edad, LocalDate fechaRegistro) {
        if (nombreCompleto == null || nombreCompleto.isBlank()) throw new IllegalArgumentException("Nombre obligatorio");
        if (documento == null || documento.isBlank()) throw new IllegalArgumentException("Documento obligatorio");
        if (telefono == null || telefono.isBlank()) throw new IllegalArgumentException("Telefono obligatorio");
        if (edad <= 0) throw new IllegalArgumentException("Edad invalida");
        this.nombreCompleto = nombreCompleto;
        this.documento = documento;
        this.telefono = telefono;
        this.correo = correo;
        this.edad = edad;
        this.fechaRegistro = Objects.requireNonNull(fechaRegistro, "Fecha de registro obligatoria");
    }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getDocumento() { return documento; }
    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }
    public int getEdad() { return edad; }
    public LocalDate getFechaRegistro() { return fechaRegistro; }
    @Override public String toString() { return nombreCompleto + " (" + documento + ")"; }
}
