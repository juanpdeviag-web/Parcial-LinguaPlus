package co.edu.uniquindio.linguaplus.app;

import co.edu.uniquindio.linguaplus.modelo.*;
import co.edu.uniquindio.linguaplus.modelo.comprobante.GeneradorComprobante;
import co.edu.uniquindio.linguaplus.modelo.comprobante.GeneradorExcel;
import co.edu.uniquindio.linguaplus.modelo.comprobante.GeneradorPdf;
import co.edu.uniquindio.linguaplus.modelo.modalidad.FabricaPresencial;
import co.edu.uniquindio.linguaplus.modelo.modalidad.FabricaVirtual;
import co.edu.uniquindio.linguaplus.modelo.modalidad.ServicioModalidad;
import co.edu.uniquindio.linguaplus.servicio.ServicioConsultas;

import java.time.LocalDate;
import java.util.List;

public class MainConsola {
    public static void main(String[] args) {
        Academia academia = new Academia("LinguaPlus", "NIT-DEMO", "Direccion demo", "6060000000", "demo@linguaplus.edu", "https://demo.local");
        Estudiante ana = new Estudiante("Ana Gomez", "1001", "28", "ana@demo.com", 20, LocalDate.of(2026,9,1));
        Estudiante luis = new Estudiante("Luis Ramirez", "1002", "12345", "luis@demo.com", 22, LocalDate.of(2026,9,2));
        academia.registrarEstudiante(ana); academia.registrarEstudiante(luis);

        ProgramaFormacion ingles = new ProgramaBasico("P01", "Ingles A1", "Ingles", "Programa base", 4, 300000,
                EstadoPrograma.ACTIVO, List.of("Plataforma virtual"));
        ProgramaFormacion personalizado = new ProgramaPersonalizado("P02", "Ingles personalizado", "Ingles", "Plan individual", 3, 450000,
                EstadoPrograma.ACTIVO, List.of("Tutor"), 8, "B1", "Preparacion entrevista");
        academia.registrarPrograma(ingles); academia.registrarPrograma(personalizado);

        Docente tutor = new Docente("D01", "Maria Lopez", "Ingles", "3000000000", 50000);
        academia.registrarDocente(tutor);
        ServicioAdicional simulacro = new ServicioAdicional("S01", "Simulacro internacional", "Simulacro", 120000, true);
        academia.registrarServicio(simulacro);

        System.out.println("--- Builder + Singleton ---");
        Matricula m1 = new Matricula.Builder().conEstudiante(ana).conPrograma(ingles).conFechaInicio(LocalDate.of(2026,9,10)).conDescuento(10).build();
        Matricula m2 = new Matricula.Builder().conEstudiante(luis).conPrograma(personalizado).conFechaInicio(LocalDate.of(2026,9,11))
                .conDocenteTutor(tutor).agregarServicio(simulacro).conObservaciones("Preparacion intensiva").build();
        academia.registrarMatricula(m1); academia.registrarMatricula(m2);
        System.out.println(m1); System.out.println(m2);
        try {
            new Matricula.Builder().conEstudiante(ana).conFechaInicio(LocalDate.now()).build();
        } catch (IllegalStateException e) { System.out.println("Matricula invalida rechazada -> " + e.getMessage()); }
        try {
            new Matricula.Builder().conEstudiante(ana).conPrograma(ingles).conFechaInicio(LocalDate.now()).conDescuento(35).build();
        } catch (IllegalStateException e) { System.out.println("Descuento invalido rechazado -> " + e.getMessage()); }

        System.out.println("\n--- Prototype ---");
        OfertaPeriodo plantilla = new OfertaPeriodo(LocalDate.of(2026,1,1));
        plantilla.agregarPrograma(new ProgramaOferta(ingles, "L-M 8:00", 10, "Presencial"));
        OfertaPeriodo periodo1 = plantilla.clone(); periodo1.setFecha(LocalDate.of(2026,9,1));
        OfertaPeriodo periodo2 = plantilla.clone(); periodo2.setFecha(LocalDate.of(2027,2,1));
        periodo1.getPrograma(0).ocuparCupo(); periodo1.getPrograma(0).ocuparCupo();
        System.out.println("Cupos periodo 1 -> " + periodo1.getPrograma(0).getCuposDisponibles());
        System.out.println("Cupos periodo 2 -> " + periodo2.getPrograma(0).getCuposDisponibles());
        System.out.println("Objetos oferta independientes -> " + (periodo1.getPrograma(0) != periodo2.getPrograma(0)));

        System.out.println("\n--- Factory Method ---");
        GeneradorComprobante pdf = new GeneradorPdf();
        GeneradorComprobante excel = new GeneradorExcel();
        System.out.println(pdf.emitir(m1));
        System.out.println(excel.emitir(m1));

        System.out.println("\n--- Abstract Factory ---");
        ServicioModalidad entrega = new ServicioModalidad();
        System.out.println("Presencial -> " + entrega.preparar(new FabricaPresencial()));
        System.out.println("Virtual -> " + entrega.preparar(new FabricaVirtual()));

        System.out.println("\n--- Consultas ---");
        ServicioConsultas consultas = new ServicioConsultas(academia);
        System.out.println("Telefono 28 encontrado -> " + consultas.buscarPorTelefono("28").orElse(null));
        System.out.println("Telefono 28 es perfecto -> " + consultas.telefonoEsNumeroPerfecto("28"));
        System.out.println("Ingresos septiembre -> $" + String.format("%.2f", consultas.calcularIngresos(LocalDate.of(2026,9,1), LocalDate.of(2026,9,30))));
    }
}
