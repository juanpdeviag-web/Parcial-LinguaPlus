package co.edu.uniquindio.linguaplus.modelo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.time.LocalDate;

public class ProgramaOferta implements Cloneable {
    private final ProgramaFormacion programa;
    private final String horario;
    private final IntegerProperty cuposDisponibles;
    private String modalidad; // "Presencial" o "Virtual"
    // NUEVOS ATRIBUTOS DE VIGENCIA AUTÓNOMA
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    public ProgramaOferta(ProgramaFormacion programa, String horario, int cuposDisponibles, String modalidad) {
        if (programa == null) throw new IllegalArgumentException("Programa obligatorio");
        if (cuposDisponibles < 0) throw new IllegalArgumentException("Cupos invalidos");
        this.programa = programa; this.horario = horario;
        this.cuposDisponibles = new SimpleIntegerProperty(cuposDisponibles);
        this.modalidad = modalidad;
    }

    public void ocuparCupo() {
        if (cuposDisponibles.get() == 0) throw new IllegalStateException("No hay cupos disponibles");
        cuposDisponibles.set(cuposDisponibles.get() - 1);
    }

    @Override
    public ProgramaOferta clone() {
        ProgramaOferta copia = new ProgramaOferta(programa, horario, cuposDisponibles.get(), modalidad);
        copia.setFechasVigencia(this.fechaInicio, this.fechaFin);
        return copia;
    }

    // Setters de soporte para inyectar el contexto temporal desde el contenedor OfertaPeriodo
    public void setFechasVigencia(LocalDate inicio, LocalDate fin) {
        this.fechaInicio = inicio;
        this.fechaFin = fin;
    }

    public ProgramaFormacion getPrograma() { return programa; }
    public String getHorario() { return horario; }
    public int getCuposDisponibles() { return cuposDisponibles.get(); }
    public IntegerProperty cuposDisponiblesProperty() { return cuposDisponibles; }
    public String getModalidad() { return modalidad; }
    public void setModalidad(String modalidad) { this.modalidad = modalidad; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    
    public String getBeneficiosModalidad() {
        if ("Presencial".equalsIgnoreCase(modalidad)) {
            return "Material impreso + Carné físico";
        } else if ("Virtual".equalsIgnoreCase(modalidad)) {
            return "Licencia de plataforma + Carné digital";
        }
        return "-";
    }
}
