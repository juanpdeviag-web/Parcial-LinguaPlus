package co.edu.uniquindio.linguaplus.controlador;

import co.edu.uniquindio.linguaplus.modelo.modalidad.Estudiante;
import co.edu.uniquindio.linguaplus.servicio.ServicioConsultas;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;

public class LinguaPlusController {
    @FXML private TextField txtNombreEst, txtDocumentoEst, txtTelefonoEst, txtCorreoEst, txtEdadEst;
    @FXML private Label lblEstudiante;
    @FXML private TableView<Estudiante> tblEstudiantes;
    @FXML private TableColumn<Estudiante,String> colEstNombre, colEstDocumento, colEstTelefono;

    @FXML private ComboBox<Estudiante> cmbEstudiante;
    @FXML private ComboBox<ProgramaFormacion> cmbPrograma;
    @FXML private ComboBox<Docente> cmbTutor;
    @FXML private ListView<ServicioAdicional> lstServicios;
    @FXML private DatePicker dpInicio;
    @FXML private TextField txtDescuento, txtObservaciones;
    @FXML private Label lblMatricula;
    @FXML private TableView<Matricula> tblMatriculas;
    @FXML private TableColumn<Matricula,String> colMatNumero, colMatEstudiante, colMatPrograma, colMatTotal;

    @FXML private TextField txtTelefonoConsulta;
    @FXML private Label lblTelefonoConsulta;
    @FXML private DatePicker dpDesde, dpHasta;
    @FXML private Label lblIngresos;
    @FXML private ComboBox<Matricula> cmbMatriculaComprobante;
    @FXML private TextArea txtSalida;

    private final Academia academia = new Academia("LinguaPlus", "NIT-DEMO", "Direccion demo", "6060000000", "contacto@linguaplus.edu", "www.linguaplus.demo");
    private final ServicioConsultas consultas = new ServicioConsultas(academia);

    @FXML public void initialize() {
        colEstNombre.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getNombreCompleto()));
        colEstDocumento.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getDocumento()));
        colEstTelefono.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getTelefono()));
        colMatNumero.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(String.valueOf(d.getValue().getNumeroMatricula())));
        colMatEstudiante.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getEstudiante().getNombreCompleto()));
        colMatPrograma.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getPrograma().getNombre()));
        colMatTotal.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(String.format("$%.2f", d.getValue().calcularValorTotal())));
        lstServicios.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        cargarDatosDemostracion();
        refrescar();
    }

    private void cargarDatosDemostracion() {
        // El enunciado no suministra catalogo real; estos datos existen solo para demostrar la interfaz.
        ProgramaFormacion p1 = new ProgramaBasico("P01", "Ingles A1", "Ingles", "Basico", 4, 300000, EstadoPrograma.ACTIVO, List.of("Club de conversacion"));
        ProgramaFormacion p2 = new ProgramaIntensivo("P02", "Frances intensivo", "Frances", "Intensivo", 3, 420000, EstadoPrograma.ACTIVO, List.of("Plataforma virtual"));
        ProgramaFormacion p3 = new ProgramaPersonalizado("P03", "Ingles personalizado", "Ingles", "Plan individual", 3, 500000, EstadoPrograma.ACTIVO, List.of("Tutor"), 8, "B1", "Objetivos definidos con el estudiante");
        academia.registrarPrograma(p1); academia.registrarPrograma(p2); academia.registrarPrograma(p3);
        academia.registrarDocente(new Docente("D01", "Laura Ruiz", "Ingles", "3001112233", 60000));
        academia.registrarServicio(new ServicioAdicional("S01", "Examen de nivelacion", "Diagnostico", 80000, true));
        academia.registrarServicio(new ServicioAdicional("S02", "Simulacro de certificacion", "Practica", 120000, true));
    }

    @FXML private void registrarEstudiante() {
        try {
            Estudiante e = new Estudiante(txtNombreEst.getText(), txtDocumentoEst.getText(), txtTelefonoEst.getText(),
                    txtCorreoEst.getText(), Integer.parseInt(txtEdadEst.getText()), LocalDate.now());
            academia.registrarEstudiante(e);
            lblEstudiante.setText("Estudiante registrado");
            refrescar();
        } catch (Exception ex) { lblEstudiante.setText(ex.getMessage()); }
    }

    @FXML private void registrarMatricula() {
        try {
            Matricula.Builder b = new Matricula.Builder().conEstudiante(cmbEstudiante.getValue()).conPrograma(cmbPrograma.getValue())
                    .conFechaInicio(dpInicio.getValue()).conDescuento(txtDescuento.getText().isBlank() ? 0 : Double.parseDouble(txtDescuento.getText()))
                    .conObservaciones(txtObservaciones.getText());
            if (cmbTutor.getValue() != null) b.conDocenteTutor(cmbTutor.getValue());
            for (ServicioAdicional s : lstServicios.getSelectionModel().getSelectedItems()) b.agregarServicio(s);
            Matricula m = b.build();
            academia.registrarMatricula(m);
            lblMatricula.setText("Matricula #" + m.getNumeroMatricula() + " registrada. Total: $" + String.format("%.2f", m.calcularValorTotal()));
            refrescar();
        } catch (Exception ex) { lblMatricula.setText(ex.getMessage()); }
    }

    @FXML private void buscarTelefono() {
        String tel = txtTelefonoConsulta.getText();
        String persona = consultas.buscarPorTelefono(tel).map(Estudiante::getNombreCompleto).orElse("No encontrado");
        lblTelefonoConsulta.setText("Estudiante: " + persona + " | Numero perfecto: " + (consultas.telefonoEsNumeroPerfecto(tel) ? "SI" : "NO"));
    }

    @FXML private void calcularIngresos() {
        try { lblIngresos.setText("Ingresos: $" + String.format("%.2f", consultas.calcularIngresos(dpDesde.getValue(), dpHasta.getValue()))); }
        catch (Exception ex) { lblIngresos.setText(ex.getMessage()); }
    }

    @FXML private void comprobantePdf() {
        emitirComprobante(new GeneradorPdf());
    }
    @FXML private void comprobanteExcel() {
        emitirComprobante(new GeneradorExcel());
    }
    private void emitirComprobante(GeneradorComprobante generador) {
        Matricula m = cmbMatriculaComprobante.getValue();
        txtSalida.setText(m == null ? "Seleccione una matricula" : generador.emitir(m));
    }
    @FXML private void modalidadPresencial() { txtSalida.setText(new ServicioModalidad().preparar(new FabricaPresencial())); }
    @FXML private void modalidadVirtual() { txtSalida.setText(new ServicioModalidad().preparar(new FabricaVirtual())); }

    private void refrescar() {
        tblEstudiantes.setItems(FXCollections.observableArrayList(academia.getEstudiantes()));
        tblMatriculas.setItems(FXCollections.observableArrayList(academia.getMatriculas()));
        cmbEstudiante.setItems(FXCollections.observableArrayList(academia.getEstudiantes()));
        cmbPrograma.setItems(FXCollections.observableArrayList(academia.getProgramas()));
        cmbTutor.setItems(FXCollections.observableArrayList(academia.getDocentes()));
        lstServicios.setItems(FXCollections.observableArrayList(academia.getServicios()));
        cmbMatriculaComprobante.setItems(FXCollections.observableArrayList(academia.getMatriculas()));
    }
}
