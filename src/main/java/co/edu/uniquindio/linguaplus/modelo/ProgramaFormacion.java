package co.edu.uniquindio.linguaplus.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ProgramaFormacion {
    private final String codigo;
    private final String nombre;
    private final String idioma;
    private final String descripcion;
    private final int duracionMeses;
    private final double valorMensual;
    private EstadoPrograma estado;
    private final List<String> beneficios;

    protected ProgramaFormacion(String codigo, String nombre, String idioma, String descripcion,
                                int duracionMeses, double valorMensual, EstadoPrograma estado,
                                List<String> beneficios) {
        if (codigo == null || codigo.isBlank()) throw new IllegalArgumentException("Codigo obligatorio");
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("Nombre obligatorio");
        if (duracionMeses <= 0) throw new IllegalArgumentException("Duracion invalida");
        if (valorMensual < 0) throw new IllegalArgumentException("Valor mensual invalido");
        this.codigo = codigo; this.nombre = nombre; this.idioma = idioma; this.descripcion = descripcion;
        this.duracionMeses = duracionMeses; this.valorMensual = valorMensual; this.estado = estado;
        this.beneficios = new ArrayList<>(beneficios == null ? List.of() : beneficios);
    }
    public double calcularValorBase() { return valorMensual * duracionMeses; }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getIdioma() { return idioma; }
    public String getDescripcion() { return descripcion; }
    public int getDuracionMeses() { return duracionMeses; }
    public double getValorMensual() { return valorMensual; }
    public EstadoPrograma getEstado() { return estado; }
    public void setEstado(EstadoPrograma estado) { this.estado = estado; }
    public List<String> getBeneficios() { return Collections.unmodifiableList(beneficios); }
    public abstract String getTipo();
    @Override public String toString() { return codigo + " - " + nombre + " (" + getTipo() + ")"; }
}
