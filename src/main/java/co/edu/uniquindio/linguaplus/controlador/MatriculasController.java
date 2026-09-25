package co.edu.uniquindio.linguaplus.controlador;

import co.edu.uniquindio.linguaplus.modelo.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class MatriculasController {
    @FXML private ComboBox<Estudiante> cmbEstudiante;
    @FXML private ComboBox<ProgramaFormacion> cmbPrograma;
    @FXML private ComboBox<OfertaPeriodo> cmbPeriodoOferta; // Restringe la selección a períodos válidos
    @FXML private ComboBox<Docente> cmbTutor;
    @FXML private ListView<ServicioAdicional> lstServicios;
    @FXML private TextField txtDescuento, txtObservaciones;
    @FXML private Label lblMatricula;
    @FXML private TableView<Matricula> tblMatriculas;
    @FXML private TableColumn<Matricula, String> colMatNumero, colMatEstudiante, colMatPrograma, colMatFechaInicio, colMatFechaFin, colMatTotal, colMatServicios;

    private Academia academia;
    private MainController mainController;

    @FXML
    public void initialize() {
        cmbTutor.setDisable(true);
        
        // Configurar columnas de la tabla
        colMatNumero.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(String.valueOf(d.getValue().getNumeroMatricula())));
        colMatEstudiante.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getEstudiante().getNombreCompleto()));
        colMatPrograma.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getPrograma().getNombre()));
        colMatFechaInicio.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getFechaInicio().toString()));
        colMatFechaFin.setCellValueFactory(d -> {
            // La fecha fin se calcula sumando la duración del programa a la fecha inicio
            LocalDate fechaInicio = d.getValue().getFechaInicio();
            LocalDate fechaFin = fechaInicio.plusMonths(d.getValue().getPrograma().getDuracionMeses());
            return new javafx.beans.property.SimpleStringProperty(fechaFin.toString());
        });
        colMatTotal.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(String.format("$%.2f", d.getValue().calcularValorTotal())));
        colMatServicios.setCellValueFactory(d -> {
            List<String> servicios = d.getValue().getServiciosAdicionales().stream()
                .map(s -> s.getNombre())
                .toList();
            return new javafx.beans.property.SimpleStringProperty(servicios.isEmpty() ? "Ninguno" : String.join(", ", servicios));
        });
    }

    public void setContexto(Academia academia, MainController main) {
        this.academia = academia;
        this.mainController = main;
        refrescar();
        
        // Listener para filtrar programas cuando se selecciona un período
        cmbPeriodoOferta.setOnAction(e -> filtrarProgramasPorPeriodo());
    }

    @FXML
    private void onPeriodoSeleccionado() {
        filtrarProgramasPorPeriodo();
    }
    
    @FXML
    private void filtrarProgramasPorPeriodo() {
        OfertaPeriodo periodo = cmbPeriodoOferta.getValue();
        if (periodo != null) {
            // Filtrar programas que están activos en el período seleccionado
            var programasEnPeriodo = periodo.getProgramas().stream()
                .map(ProgramaOferta::getPrograma)
                .toList();
            cmbPrograma.setItems(FXCollections.observableArrayList(programasEnPeriodo));
        } else {
            // Si no hay período seleccionado, mostrar todos los programas
            cmbPrograma.setItems(FXCollections.observableArrayList(academia.getProgramas()));
        }
        // Limpiar selección de programa
        cmbPrograma.setValue(null);
    }

    @FXML
    private void registrarMatricula() {
        try {
            Estudiante est = cmbEstudiante.getValue();
            ProgramaFormacion prog = cmbPrograma.getValue();
            OfertaPeriodo periodo = cmbPeriodoOferta.getValue();

            if (est == null || prog == null || periodo == null) {
                throw new IllegalStateException("Estudiante, Programa y Período Activo son obligatorios.");
            }

            // Validar concurrencia estricta en el MISMO período
            boolean yaEnPeriodo = academia.getMatriculas().stream()
                    .anyMatch(m -> m.getEstudiante().getDocumento().equals(est.getDocumento())
                            && m.getPrograma().getCodigo().equals(prog.getCodigo())
                            && m.getFechaInicio().equals(periodo.getFecha()));

            if (yaEnPeriodo) {
                // Alerta nativa de advertencia dándole la opción de continuar o reajustar al nivel avanzado
                Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
                alerta.setTitle("Curso ya activo");
                alerta.setHeaderText("Aviso de progresión académica");
                alerta.setContentText("Este curso ya se encuentra activo en el período seleccionado. ¿Está de acuerdo en continuar con este registro o prefiere cancelar para elegir un nivel más avanzado?");

                Optional<ButtonType> respuesta = alerta.showAndWait();
                if (respuesta.isEmpty() || respuesta.get() != ButtonType.OK) {
                    lblMatricula.setText("Registro cancelado por el usuario para elegir un curso avanzado.");
                    return;
                }
            }

            Matricula m = new Matricula.Builder()
                    .conEstudiante(est)
                    .conPrograma(prog)
                    .conFechaInicio(periodo.getFecha())
                    .conDescuento(txtDescuento.getText().isBlank() ? 0 : Double.parseDouble(txtDescuento.getText()))
                    .conObservaciones(txtObservaciones.getText())
                    .build();

            academia.registrarMatricula(m);
            
            // Disminuir cupos disponibles en el programa oferta correspondiente
            ProgramaOferta programaOferta = periodo.encontrarProgramaOferta(prog);
            if (programaOferta != null) {
                programaOferta.ocuparCupo();
            }
            
            lblMatricula.setText("Matrícula indexada correctamente.");
            refrescar();
            
            // Notificar al MainController para refrescar la pestaña de ofertas
            if (mainController != null) {
                mainController.notificarCambioOfertas();
            }
        } catch (Exception ex) {
            lblMatricula.setText("Error: " + ex.getMessage());
        }
    }

    public void refrescar() {
        if (academia == null) return;
        cmbEstudiante.setItems(FXCollections.observableArrayList(academia.getEstudiantes()));
        cmbPeriodoOferta.setItems(FXCollections.observableArrayList(academia.getOfertasPeriodos()));
        lstServicios.setItems(FXCollections.observableArrayList(academia.getServicios()));
        tblMatriculas.setItems(FXCollections.observableArrayList(academia.getMatriculas()));
        // Programas se cargan según el período seleccionado
        filtrarProgramasPorPeriodo();
    }
}
