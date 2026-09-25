package co.edu.uniquindio.linguaplus.controlador;

import co.edu.uniquindio.linguaplus.modelo.Academia;
import co.edu.uniquindio.linguaplus.modelo.Docente;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class DocentesController {
    @FXML private TextField txtIdDoc, txtNombreDoc, txtIdiomaDoc, txtTelefonoDoc, txtTarifaDoc;
    @FXML private Button btnAccionDoc;
    @FXML private Label lblDocente;
    @FXML private TableView<Docente> tblDocentes;
    @FXML private TableColumn<Docente, String> colDocId, colDocNombre, colDocIdioma, colDocTelefono, colDocTarifa;

    private Academia academia;
    private Docente docenteSeleccionado;

    @FXML
    public void initialize() {
        colDocId.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getIdentificacion()));
        colDocNombre.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getNombre()));
        colDocIdioma.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getIdiomaEspecialidad()));
        colDocTelefono.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getTelefono()));
        colDocTarifa.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(String.format("$%.2f", d.getValue().getTarifaSesion())));

        tblDocentes.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                docenteSeleccionado = newSel;
                txtIdDoc.setText(newSel.getIdentificacion());
                txtNombreDoc.setText(newSel.getNombre());
                txtIdiomaDoc.setText(newSel.getIdiomaEspecialidad());
                txtTelefonoDoc.setText(newSel.getTelefono());
                txtTarifaDoc.setText(String.valueOf(newSel.getTarifaSesion()));
                btnAccionDoc.setText("Aceptar Cambios");
            }
        });
    }

    public void setContexto(Academia academia) {
        this.academia = academia;
        refrescar();
    }

    @FXML
    private void procesarAccionDocente() {
        try {
            Docente nuevo = new Docente(txtIdDoc.getText(), txtNombreDoc.getText(), txtIdiomaDoc.getText(),
                    txtTelefonoDoc.getText(), Double.parseDouble(txtTarifaDoc.getText()));

            if (docenteSeleccionado == null) {
                academia.registrarDocente(nuevo);
            } else {
                academia.eliminarDocente(docenteSeleccionado);
                academia.registrarDocente(nuevo);
            }
            limpiarCampos();
            refrescar();
        } catch (Exception ex) {
            lblDocente.setText("Error: " + ex.getMessage());
        }
    }

    @FXML
    private void eliminarDocente() {
        if (docenteSeleccionado == null) return;
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION, "¿Borrar docente?", ButtonType.YES, ButtonType.NO);
        confirmacion.showAndWait().ifPresent(res -> {
            if (res == ButtonType.YES) {
                academia.eliminarDocente(docenteSeleccionado);
                limpiarCampos();
                refrescar();
            }
        });
    }

    @FXML
    private void limpiarCampos() {
        docenteSeleccionado = null;
        txtIdDoc.clear(); txtNombreDoc.clear(); txtIdiomaDoc.clear(); txtTelefonoDoc.clear(); txtTarifaDoc.clear();
        btnAccionDoc.setText("Registrar");
        tblDocentes.getSelectionModel().clearSelection();
    }

    public void refrescar() {
        if (academia != null) tblDocentes.setItems(FXCollections.observableArrayList(academia.getDocentes()));
    }
}
