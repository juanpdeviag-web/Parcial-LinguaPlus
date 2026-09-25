package co.edu.uniquindio.linguaplus.controlador;

import co.edu.uniquindio.linguaplus.modelo.*;
import javafx.fxml.FXML;
import java.time.LocalDate;
import java.util.List;

public class MainController {

    @FXML private EstudiantesController pestaniaEstudiantesController;
    @FXML private ProgramasController pestaniaProgramasController; // Añadido soporte para el controlador de programas
    @FXML private DocentesController pestaniaDocentesController;
    @FXML private OfertasController pestaniaOfertasController;
    @FXML private MatriculasController pestaniaMatriculasController;
    @FXML private ConsultasController pestaniaConsultasController;

    private final Academia academia = new Academia("LinguaPlus", "NIT-DEMO", "Direccion", "6060000000", "contacto@linguaplus.edu", "www.linguaplus.demo"); //

    @FXML
    public void initialize() {
        cargarDatosDemostracion();
        inyectarContextos();
    }

    private void inyectarContextos() {
        if (pestaniaEstudiantesController != null) pestaniaEstudiantesController.setContexto(academia);
        if (pestaniaProgramasController != null) pestaniaProgramasController.setContexto(academia, this); // Conexión de programas con MainController
        if (pestaniaDocentesController != null) pestaniaDocentesController.setContexto(academia);
        if (pestaniaMatriculasController != null) pestaniaMatriculasController.setContexto(academia, this);
        if (pestaniaConsultasController != null) pestaniaConsultasController.setContexto(academia);

        // CORRECCIÓN: Se añade 'this' como segundo argumento para cumplir con la nueva firma
        if (pestaniaOfertasController != null) {
            pestaniaOfertasController.setContexto(academia, this);
        }
    }

    // Método encargado de refrescar la pestaña de matrículas y ofertas al registrar una matrícula
    public void notificarCambioOfertas() {
        if (pestaniaMatriculasController != null) {
            pestaniaMatriculasController.refrescar();
        }
        if (pestaniaOfertasController != null) {
            pestaniaOfertasController.refrescar();
        }
    }

    // Método encargado de refrescar la pestaña de ofertas al crear/modificar un programa
    public void notificarCambioProgramas() {
        if (pestaniaOfertasController != null) {
            pestaniaOfertasController.refrescar();
        }
    }

    private void cargarDatosDemostracion() {
        ProgramaFormacion p1 = new ProgramaBasico("P01", "Inglés A1", "Inglés", "Básico", 4, 300000, EstadoPrograma.ACTIVO, List.of("Club")); //
        ProgramaFormacion p2 = new ProgramaIntensivo("P02", "Francés intensivo", "Francés", "Intensivo", 3, 420000, EstadoPrograma.ACTIVO, List.of("Plataforma")); //
        ProgramaFormacion p3 = new ProgramaPersonalizado("P03", "Inglés personalizado", "Inglés", "Individual", 3, 500000, EstadoPrograma.ACTIVO, List.of("Tutor"), 8, "B1", "Objetivos"); //
        academia.registrarPrograma(p1); academia.registrarPrograma(p2); academia.registrarPrograma(p3); //

        academia.registrarDocente(new Docente("D01", "Laura Ruiz", "Inglés", "3001112233", 60000)); //
        academia.registrarDocente(new Docente("D02", "Carlos Gómez", "Francés", "3004445566", 75000)); //

        academia.registrarServicio(new ServicioAdicional("S01", "Examen nivelación", "Diagnóstico", 80000, true)); //
        academia.registrarEstudiante(new Estudiante("Juan Pérez", "1001", "3009998877", "juan@mail.com", 21, LocalDate.now())); //

        // Crear oferta de periodo del 1 al 30 de septiembre para los programas existentes
        OfertaPeriodo ofertaSeptiembre = new OfertaPeriodo(LocalDate.of(2026, 9, 1));
        ofertaSeptiembre.setFechaFin(LocalDate.of(2026, 9, 30));

        ProgramaOferta po1 = new ProgramaOferta(p1, "Mañana (08:00 - 12:00)", 20, "Presencial");
        po1.setFechasVigencia(ofertaSeptiembre.getFecha(), ofertaSeptiembre.getFechaFin());
        ofertaSeptiembre.agregarPrograma(po1);

        ProgramaOferta po2 = new ProgramaOferta(p2, "Tarde (14:00 - 18:00)", 15, "Virtual");
        po2.setFechasVigencia(ofertaSeptiembre.getFecha(), ofertaSeptiembre.getFechaFin());
        ofertaSeptiembre.agregarPrograma(po2);

        ProgramaOferta po3 = new ProgramaOferta(p3, "Noche (18:30 - 21:30)", 10, "Presencial");
        po3.setFechasVigencia(ofertaSeptiembre.getFecha(), ofertaSeptiembre.getFechaFin());
        ofertaSeptiembre.agregarPrograma(po3);

        academia.registrarOfertaPeriodo(ofertaSeptiembre);
        
        // Crear matrículas de demostración para probar la visualización de fechas
        Matricula matriculaDemo1 = new Matricula.Builder()
                .conEstudiante(academia.getEstudiantes().get(0))
                .conPrograma(p1)
                .conFechaInicio(ofertaSeptiembre.getFecha())
                .conDescuento(0)
                .build();
        academia.registrarMatricula(matriculaDemo1);
        
        // Ocupar cupo en el programa oferta correspondiente
        ProgramaOferta poMatriculado = ofertaSeptiembre.encontrarProgramaOferta(p1);
        if (poMatriculado != null) {
            poMatriculado.ocuparCupo();
        }
    }
}
