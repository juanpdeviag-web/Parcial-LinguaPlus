package co.edu.uniquindio.linguaplus.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OfertaPeriodo implements Cloneable {
    private LocalDate fecha;
    private LocalDate fechaFin;
    private List<ProgramaOferta> programas = new ArrayList<>();
    public OfertaPeriodo(LocalDate fecha) { this.fecha = fecha; }
    public void agregarPrograma(ProgramaOferta oferta) { programas.add(oferta); }
    @Override public OfertaPeriodo clone() {
        OfertaPeriodo copia = new OfertaPeriodo(fecha);
        copia.setFechaFin(fechaFin);
        for (ProgramaOferta oferta : programas) copia.programas.add(oferta.clone());
        return copia;
    }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public List<ProgramaOferta> getProgramas() { return Collections.unmodifiableList(programas); }
    public ProgramaOferta getPrograma(int indice) { return programas.get(indice); }
    
    // Método para encontrar un ProgramaOferta por su programa de formación
    public ProgramaOferta encontrarProgramaOferta(ProgramaFormacion programa) {
        return programas.stream()
            .filter(po -> po.getPrograma().getCodigo().equals(programa.getCodigo()))
            .findFirst()
            .orElse(null);
    }

    @Override
    public String toString() {
        return String.format("%s - %s", fecha, fechaFin != null ? fechaFin : "Sin fecha fin");
    }

}
