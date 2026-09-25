package co.edu.uniquindio.linguaplus.controlador;

import co.edu.uniquindio.linguaplus.modelo.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;

public class EstudiantesController {
    @FXML private TextField txtNombreEst, txtDocumentoEst, txtTelefonoEst, txtCorreoEst, txtEdadEst;
    @FXML private Button btnAccionEst;
    @FXML private Label lblEstudiante;
    @FXML private TableView<Estudiante> tblEstudiantes;
    @FXML private TableColumn<Estudiante, String> colEstNombre, colEstDocumento, colEstTelefono, colEstCorreo, colEstEdad;

    private Academia academia;
    private Estudiante estudianteSeleccionado;

    @FXML
    public void initialize() {
        colEstNombre.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getNombreCompleto()));
        colEstDocumento.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getDocumento()));
        colEstTelefono.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getTelefono()));
        colEstCorreo.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getCorreo()));
        colEstEdad.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(String.valueOf(d.getValue().getEdad())));

        tblEstudiantes.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                estudianteSeleccionado = newSel;
                txtNombreEst.setText(newSel.getNombreCompleto());
                txtDocumentoEst.setText(newSel.getDocumento());
                txtTelefonoEst.setText(newSel.getTelefono());
                txtCorreoEst.setText(newSel.getCorreo());
                txtEdadEst.setText(String.valueOf(newSel.getEdad()));
                btnAccionEst.setText("Aceptar Cambios");
            }
        });
    }

    public void setContexto(Academia academia) {
        this.academia = academia;
        refrescar();
    }

    @FXML
    private void procesarAccionEstudiante() {
        int edadParsed = 0;
        try {
            edadParsed = Integer.parseInt(txtEdadEst.getText());
        } catch (NumberFormatException nfe) {
            lblEstudiante.setText("Error: Por favor ingresa un número entero válido en el campo 'Edad'.");
            return;
        }

        try {
            Estudiante nuevo = new Estudiante(txtNombreEst.getText(), txtDocumentoEst.getText(), txtTelefonoEst.getText(),
                    txtCorreoEst.getText(), edadParsed, LocalDate.now());

            if (estudianteSeleccionado == null) {
                academia.registrarEstudiante(nuevo);
                lblEstudiante.setText("Estudiante registrado.");
            } else {
                academia.eliminarEstudiante(estudianteSeleccionado);
                academia.registrarEstudiante(nuevo);
                lblEstudiante.setText("Estudiante modificado correctamente.");
            }
            limpiarCampos();
            refrescar();
        } catch (Exception ex) {
            lblEstudiante.setText("Error: " + ex.getMessage());
        }
    }

    @FXML
    private void eliminarEstudiante() {
        if (estudianteSeleccionado == null) return;
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION, "¿Desea borrar este estudiante?", ButtonType.YES, ButtonType.NO);
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                academia.eliminarEstudiante(estudianteSeleccionado);
                limpiarCampos();
                refrescar();
            }
        });
    }

    @FXML
    private void limpiarCampos() {
        estudianteSeleccionado = null;
        txtNombreEst.clear(); txtDocumentoEst.clear(); txtTelefonoEst.clear(); txtCorreoEst.clear(); txtEdadEst.clear();
        btnAccionEst.setText("Registrar");
        tblEstudiantes.getSelectionModel().clearSelection();
    }

    public void refrescar() {
        if (academia != null) tblEstudiantes.setItems(FXCollections.observableArrayList(academia.getEstudiantes()));
    }
}
