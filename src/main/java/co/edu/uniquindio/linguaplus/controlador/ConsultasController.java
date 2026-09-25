package co.edu.uniquindio.linguaplus.controlador;

import co.edu.uniquindio.linguaplus.modelo.Academia;
import co.edu.uniquindio.linguaplus.modelo.Estudiante;
import co.edu.uniquindio.linguaplus.modelo.Matricula;
import co.edu.uniquindio.linguaplus.modelo.ProgramaFormacion;
import co.edu.uniquindio.linguaplus.modelo.comprobante.GeneradorComprobante;
import co.edu.uniquindio.linguaplus.modelo.comprobante.GeneradorExcel;
import co.edu.uniquindio.linguaplus.modelo.comprobante.GeneradorPdf;
import co.edu.uniquindio.linguaplus.modelo.modalidad.FabricaPresencial;
import co.edu.uniquindio.linguaplus.modelo.modalidad.FabricaVirtual;
import co.edu.uniquindio.linguaplus.modelo.modalidad.ServicioModalidad;
import co.edu.uniquindio.linguaplus.servicio.ServicioConsultas;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class ConsultasController {
    @FXML private TextField txtTelefonoConsulta;
    @FXML private Label lblTelefonoConsulta, lblIngresos;
    @FXML private DatePicker dpDesde, dpHasta;
    @FXML private ComboBox<Estudiante> cmbEstudiante;
    @FXML private ComboBox<Matricula> cmbMatriculaComprobante;
    @FXML private Label lblProgNombre, lblProgCodigo, lblProgIdioma, lblProgTipo, lblProgValor, lblProgDescripcion;
    @FXML private TextArea txtSalida;

    private Academia academia;
    private ServicioConsultas consultas;

    public void setContexto(Academia academia) {
        this.academia = academia;
        this.consultas = new ServicioConsultas(academia);
        refrescar();
        
        // Listener para filtrar matrículas cuando se selecciona un estudiante
        cmbEstudiante.setOnAction(e -> filtrarMatriculasPorEstudiante());
        
        // Listener para mostrar datos del programa cuando se selecciona una matrícula
        cmbMatriculaComprobante.setOnAction(e -> mostrarDatosPrograma());
    }

    @FXML
    private void buscarTelefono() {
        String tel = txtTelefonoConsulta.getText();
        String persona = consultas.buscarPorTelefono(tel).map(Estudiante::getNombreCompleto).orElse("No encontrado");
        lblTelefonoConsulta.setText("Estudiante: " + persona + " | Número perfecto: " + (consultas.telefonoEsNumeroPerfecto(tel) ? "SÍ" : "NO"));
    }

    @FXML
    private void calcularIngresos() {
        try {
            lblIngresos.setText("Ingresos: $" + String.format("%.2f", consultas.calcularIngresos(dpDesde.getValue(), dpHasta.getValue())));
        } catch (Exception ex) {
            lblIngresos.setText(ex.getMessage());
        }
    }

    // GESTIÓN DE EXPORTACIÓN Y DESCARGA REAL EN DISCO
    @FXML 
    private void comprobantePdf() { 
        // CORRECCIÓN: Se cambia a extensión .txt para que el bloc de notas lo abra inmediatamente sin corromperse
        ejecutarExportacion(new GeneradorPdf(), ".txt", "Comprobante de Texto (*.txt)"); 
    }
    
    @FXML 
    private void comprobanteExcel() { 
        // Se usa la extensión .csv para que Excel lo abra con celdas nativas sin librerías externas complejas
        ejecutarExportacion(new GeneradorExcel(), ".csv", "Archivo de Excel (*.csv)"); 
    }

    private void ejecutarExportacion(GeneradorComprobante generador, String extension, String filtroDescripcion) {
        Matricula seleccionada = cmbMatriculaComprobante.getValue();
        if (seleccionada == null) {
            txtSalida.setText("Error: Seleccione una matrícula de la lista desplegable antes de exportar.");
            return;
        }

        String contenidoComprobante = generador.emitir(seleccionada);
        txtSalida.setText(contenidoComprobante); // Mostrar previsualización en pantalla

        // 2. Configurar la ventana de selección de ruta del Sistema Operativo
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Recibo de Pago - Estudiante: " + seleccionada.getEstudiante().getNombreCompleto());
        fileChooser.setInitialFileName("Comprobante_Matricula_" + seleccionada.getNumeroMatricula() + extension);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(filtroDescripcion, "*" + extension));

        // 3. Abrir cuadro de diálogo y escribir físicamente en el disco duro del equipo
        Stage stageActual = (Stage) txtSalida.getScene().getWindow();
        File archivoDestino = fileChooser.showSaveDialog(stageActual);

        if (archivoDestino != null) {
            // SOLUCCIÓN CONSERVAR ENCODING: Usamos FileOutputStream para escribir bytes exactos en UTF-8
            try (FileOutputStream fos = new FileOutputStream(archivoDestino);
                 OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                 BufferedWriter escritor = new BufferedWriter(osw)) {
                
                // CORRECCIÓN CSV (Tildes en Excel): Inyectar el BOM UTF-8 (\uFEFF) al inicio si el archivo es CSV
                if (extension.equals(".csv")) {
                    escritor.write('\uFEFF');
                }
                
                escritor.write(contenidoComprobante);
                escritor.flush();
                
                // Mensaje informativo de éxito
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Exportación Exitosa");
                alert.setHeaderText(null);
                alert.setContentText("El archivo se ha descargado correctamente en:\n" + archivoDestino.getAbsolutePath());
                alert.showAndWait();
            } catch (Exception ex) {
                txtSalida.setText("Error crítico escribiendo el archivo en disco: " + ex.getMessage());
            }
        } else {
            lblIngresos.setText("Descarga cancelada por el usuario.");
        }
    }

    @FXML private void modalidadPresencial() { txtSalida.setText(new ServicioModalidad().preparar(new FabricaPresencial())); }
    @FXML private void modalidadVirtual() { txtSalida.setText(new ServicioModalidad().preparar(new FabricaVirtual())); }

    @FXML
    private void filtrarMatriculasPorEstudiante() {
        Estudiante estudiante = cmbEstudiante.getValue();
        if (estudiante != null) {
            var matriculasFiltradas = academia.getMatriculas().stream()
                .filter(m -> m.getEstudiante().getDocumento().equals(estudiante.getDocumento()))
                .toList();
            cmbMatriculaComprobante.setItems(FXCollections.observableArrayList(matriculasFiltradas));
        } else {
            cmbMatriculaComprobante.setItems(FXCollections.observableArrayList(academia.getMatriculas()));
        }
        limpiarDatosPrograma();
    }

    @FXML
    private void mostrarDatosPrograma() {
        Matricula m = cmbMatriculaComprobante.getValue();
        if (m != null) {
            ProgramaFormacion prog = m.getPrograma();
            lblProgNombre.setText("Nombre: " + prog.getNombre());
            lblProgCodigo.setText("Código: " + prog.getCodigo());
            lblProgIdioma.setText("Idioma: " + prog.getIdioma());
            lblProgTipo.setText("Tipo: " + prog.getTipo());
            lblProgValor.setText("Valor: $" + String.format("%.2f", prog.calcularValorBase()));
            lblProgDescripcion.setText("Descripción: " + prog.getDescripcion());
        } else {
            limpiarDatosPrograma();
        }
    }

    private void limpiarDatosPrograma() {
        lblProgNombre.setText("Nombre: -");
        lblProgCodigo.setText("Código: -");
        lblProgIdioma.setText("Idioma: -");
        lblProgTipo.setText("Tipo: -");
        lblProgValor.setText("Valor: -");
        lblProgDescripcion.setText("Descripción: -");
    }

    public void refrescar() {
        if (academia != null) {
            cmbEstudiante.setItems(FXCollections.observableArrayList(academia.getEstudiantes()));
            cmbMatriculaComprobante.setItems(FXCollections.observableArrayList(academia.getMatriculas()));
        }
    }
}
