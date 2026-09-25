package co.edu.uniquindio.linguaplus.controlador;

import co.edu.uniquindio.linguaplus.modelo.Academia;
import co.edu.uniquindio.linguaplus.modelo.OfertaPeriodo;
import co.edu.uniquindio.linguaplus.modelo.ProgramaFormacion;
import co.edu.uniquindio.linguaplus.modelo.ProgramaOferta;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.ArrayList;
import java.util.List;

public class OfertasController {
    @FXML private DatePicker dpFechaInicio, dpFechaFin;
    @FXML private ComboBox<ProgramaFormacion> cmbProgramaOferta;
    @FXML private ComboBox<String> cmbHorarioOferta;
    @FXML private ComboBox<String> cmbModalidad;
    @FXML private Spinner<Integer> spnCuposOferta;
    @FXML private Label lblOferta;
    @FXML private TableView<ProgramaOferta> tblOfertas;
    @FXML private TableColumn<ProgramaOferta, String> colOfertaInicio, colOfertaFin, colOfertaProg, colOfertaHorario, colOfertaModalidad, colOfertaBeneficios, colOfertaCupos;

    private Academia academia;
    private MainController mainController; // Enlace para notificar a otras pestañas

    @FXML
    public void initialize() {
        spnCuposOferta.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 10));
        cmbHorarioOferta.setItems(FXCollections.observableArrayList("Mañana (08:00 - 12:00)", "Tarde (14:00 - 18:00)", "Noche (18:30 - 21:30)"));
        cmbModalidad.setItems(FXCollections.observableArrayList("Presencial", "Virtual"));
        cmbModalidad.getSelectionModel().selectFirst();

        // CORRECCIÓN DE ENLAZADO: Extrae los datos reales del objeto de la fila en lugar de la pantalla
        colOfertaInicio.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getFechaInicio() != null ? d.getValue().getFechaInicio().toString() : ""
        ));
        
        colOfertaFin.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getFechaFin() != null ? d.getValue().getFechaFin().toString() : ""
        ));

        colOfertaProg.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getPrograma().getNombre()));
        colOfertaHorario.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getHorario()));
        colOfertaModalidad.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getModalidad()));
        colOfertaBeneficios.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getBeneficiosModalidad()));
        colOfertaCupos.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(String.valueOf(d.getValue().getCuposDisponibles())));
    }

    public void setContexto(Academia academia, MainController main) {
        this.academia = academia;
        this.mainController = main;
        refrescar();
    }

    @FXML
    private void crearOfertaPeriodo() {
        try {
            if (dpFechaInicio.getValue() == null || cmbProgramaOferta.getValue() == null || cmbHorarioOferta.getValue() == null || cmbModalidad.getValue() == null) {
                throw new IllegalArgumentException("Campos de Período, Programa, Horario y Modalidad son obligatorios.");
            }

            int cupos = spnCuposOferta.getValue();
            String modalidad = cmbModalidad.getValue();
            ProgramaOferta po = new ProgramaOferta(cmbProgramaOferta.getValue(), cmbHorarioOferta.getValue(), cupos, modalidad);
            // Inyectamos las fechas de la pantalla al nuevo registro creado manualmente
            po.setFechasVigencia(dpFechaInicio.getValue(), dpFechaFin.getValue());
            
            OfertaPeriodo nueva = new OfertaPeriodo(dpFechaInicio.getValue());
            if (dpFechaFin.getValue() != null) {
                nueva.setFechaFin(dpFechaFin.getValue());
            }
            nueva.agregarPrograma(po);

            academia.registrarOfertaPeriodo(nueva);
            lblOferta.setText("Oferta y cupos creados exitosamente.");

            refrescar();

            // Notificación al coordinador central para propagar los cambios a la pestaña Matrículas
            if (mainController != null) {
                mainController.notificarCambioOfertas();
            }
        } catch (Exception ex) {
            lblOferta.setText("Error: " + ex.getMessage());
        }
    }

    public void refrescar() {
        if (academia == null) return;
        cmbProgramaOferta.setItems(FXCollections.observableArrayList(academia.getProgramas()));

        // Aplanamos todas las sub-ofertas de programas para listarlas en la tabla visual
        List<ProgramaOferta> listaAplanada = new ArrayList<>();
        for (OfertaPeriodo op : academia.getOfertasPeriodos()) {
            listaAplanada.addAll(op.getProgramas());
        }
        tblOfertas.setItems(FXCollections.observableArrayList(listaAplanada));
    }
}
