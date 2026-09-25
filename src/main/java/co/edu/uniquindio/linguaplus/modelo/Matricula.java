package co.edu.uniquindio.linguaplus.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Matricula {
    private final int numeroMatricula;
    private final Estudiante estudiante;
    private final ProgramaFormacion programa;
    private final LocalDate fechaInicio;
    private final Docente docenteTutor;
    private final List<ServicioAdicional> serviciosAdicionales;
    private final double descuentoPorcentaje;
    private final String observaciones;

    private Matricula(Builder b) {
        this.estudiante = b.estudiante;
        this.programa = b.programa;
        this.fechaInicio = b.fechaInicio;
        this.docenteTutor = b.docenteTutor;
        this.serviciosAdicionales = List.copyOf(b.serviciosAdicionales);
        this.descuentoPorcentaje = b.descuentoPorcentaje;
        this.observaciones = b.observaciones;
        this.numeroMatricula = ConsecutivoMatricula.getInstancia().siguiente();
    }

    public double calcularValorTotal() {
        double subtotal = programa.calcularValorBase();
        for (ServicioAdicional s : serviciosAdicionales) subtotal += s.getPrecio();
        return subtotal * (1.0 - descuentoPorcentaje / 100.0);
    }

    public int getNumeroMatricula() { return numeroMatricula; }
    public Estudiante getEstudiante() { return estudiante; }
    public ProgramaFormacion getPrograma() { return programa; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public Docente getDocenteTutor() { return docenteTutor; }
    public List<ServicioAdicional> getServiciosAdicionales() { return Collections.unmodifiableList(serviciosAdicionales); }
    public double getDescuentoPorcentaje() { return descuentoPorcentaje; }
    public String getObservaciones() { return observaciones; }

    @Override public String toString() {
        return "Matricula #" + numeroMatricula + " | " + estudiante.getNombreCompleto() +
               " | " + programa.getNombre() + " | $" + String.format("%.2f", calcularValorTotal());
    }

    public static class Builder {
        private Estudiante estudiante;
        private ProgramaFormacion programa;
        private LocalDate fechaInicio;
        private Docente docenteTutor;
        private final List<ServicioAdicional> serviciosAdicionales = new ArrayList<>();
        private double descuentoPorcentaje = 0;
        private String observaciones;

        public Builder conEstudiante(Estudiante estudiante) { this.estudiante = estudiante; return this; }
        public Builder conPrograma(ProgramaFormacion programa) { this.programa = programa; return this; }
        public Builder conFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; return this; }
        public Builder conDocenteTutor(Docente docenteTutor) { this.docenteTutor = docenteTutor; return this; }
        public Builder agregarServicio(ServicioAdicional servicio) { if (servicio != null) serviciosAdicionales.add(servicio); return this; }
        public Builder conDescuento(double porcentaje) { this.descuentoPorcentaje = porcentaje; return this; }
        public Builder conObservaciones(String observaciones) { this.observaciones = observaciones; return this; }

        public Matricula build() {
            if (estudiante == null) throw new IllegalStateException("La matricula requiere un estudiante");
            if (programa == null) throw new IllegalStateException("La matricula requiere un programa");
            if (fechaInicio == null) throw new IllegalStateException("La matricula requiere fecha de inicio");
            if (descuentoPorcentaje < 0 || descuentoPorcentaje > 30)
                throw new IllegalStateException("El descuento no puede superar el 30%");
            if (docenteTutor != null && !(programa instanceof ProgramaPersonalizado))
                throw new IllegalStateException("El tutor solo se asigna a programas personalizados");
            for (ServicioAdicional s : serviciosAdicionales) {
                if (!s.isDisponible()) throw new IllegalStateException("Servicio no disponible: " + s.getNombre());
            }
            return new Matricula(this);
        }
    }
}
