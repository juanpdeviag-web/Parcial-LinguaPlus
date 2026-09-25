package co.edu.uniquindio.linguaplus.controlador;

import co.edu.uniquindio.linguaplus.modelo.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.Arrays;
import java.util.List;

public class ProgramasController {
    @FXML private ComboBox<String> cmbTipoProg;
    @FXML private TextField txtCodigoProg, txtNombreProg, txtIdiomaProg, txtDuracionProg, txtValorProg, txtDescripcionProg, txtBeneficiosProg;
    @FXML private TextField txtSesionesTutor, txtNivelRequerido, txtObjetivos;
    @FXML private Button btnAccionProg;
    @FXML private Label lblPrograma;
    @FXML private TableView<ProgramaFormacion> tblProgramas;
    @FXML private TableColumn<ProgramaFormacion, String> colProgCodigo, colProgNombre, colProgTipo, colProgIdioma, colProgValor, colProgDescripcion, colProgBeneficios, colProgSesiones, colProgNivel, colProgObjetivos;

    private Academia academia;
    private ProgramaFormacion programaSeleccionado;
    private MainController mainController;

    @FXML
    public void initialize() {
        cmbTipoProg.setItems(FXCollections.observableArrayList("Basico", "Intensivo", "Personalizado"));
        cmbTipoProg.getSelectionModel().selectFirst();

        colProgCodigo.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getCodigo()));
        colProgNombre.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getNombre()));
        colProgTipo.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getTipo()));
        colProgIdioma.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getIdioma()));
        colProgValor.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(String.format("$%.2f", d.getValue().calcularValorBase())));
        colProgDescripcion.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getDescripcion()));
        colProgBeneficios.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(String.join(", ", d.getValue().getBeneficios())));
        colProgSesiones.setCellValueFactory(d -> {
            if (d.getValue() instanceof ProgramaPersonalizado) {
                return new javafx.beans.property.SimpleStringProperty(String.valueOf(((ProgramaPersonalizado) d.getValue()).getSesionesTutor()));
            }
            return new javafx.beans.property.SimpleStringProperty("-");
        });
        colProgNivel.setCellValueFactory(d -> {
            if (d.getValue() instanceof ProgramaPersonalizado) {
                return new javafx.beans.property.SimpleStringProperty(((ProgramaPersonalizado) d.getValue()).getNivelRequerido());
            }
            return new javafx.beans.property.SimpleStringProperty("-");
        });
        colProgObjetivos.setCellValueFactory(d -> {
            if (d.getValue() instanceof ProgramaPersonalizado) {
                return new javafx.beans.property.SimpleStringProperty(((ProgramaPersonalizado) d.getValue()).getObjetivosEstudiante());
            }
            return new javafx.beans.property.SimpleStringProperty("-");
        });

        onTipoProgSeleccionado();

        tblProgramas.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                programaSeleccionado = newSel;
                txtCodigoProg.setText(newSel.getCodigo());
                txtNombreProg.setText(newSel.getNombre());
                txtIdiomaProg.setText(newSel.getIdioma());
                txtDuracionProg.setText(String.valueOf(newSel.getDuracionMeses()));
                txtValorProg.setText(String.valueOf(newSel.getValorMensual()));
                txtDescripcionProg.setText(newSel.getDescripcion());
                txtBeneficiosProg.setText(String.join(", ", newSel.getBeneficios()));
                cmbTipoProg.setValue(newSel.getTipo());

                if (newSel instanceof ProgramaPersonalizado) {
                    ProgramaPersonalizado pp = (ProgramaPersonalizado) newSel;
                    txtSesionesTutor.setText(String.valueOf(pp.getSesionesTutor()));
                    txtNivelRequerido.setText(pp.getNivelRequerido());
                    txtObjetivos.setText(pp.getObjetivosEstudiante());
                }
                btnAccionProg.setText("Aceptar Cambios");
            }
        });
    }

    public void setContexto(Academia academia, MainController mainController) {
        this.academia = academia;
        this.mainController = mainController;
        refrescar();
    }

    @FXML
    private void onTipoProgSeleccionado() {
        boolean esPersonalizado = "Personalizado".equals(cmbTipoProg.getValue());
        txtSesionesTutor.setDisable(!esPersonalizado);
        txtNivelRequerido.setDisable(!esPersonalizado);
        txtObjetivos.setDisable(!esPersonalizado);
    }

    @FXML
    private void procesarAccionPrograma() {
        int duracion = 0;
        double valor = 0;
        int sesiones = 0;

        try {
            duracion = Integer.parseInt(txtDuracionProg.getText());
        } catch (NumberFormatException e) {
            lblPrograma.setText("Error: Ingrese un número entero adecuado en 'Duración'.");
            return;
        }

        try {
            valor = Double.parseDouble(txtValorProg.getText());
        } catch (NumberFormatException e) {
            lblPrograma.setText("Error: Ingrese un valor numérico adecuado en 'Valor Mensual'.");
            return;
        }

        if ("Personalizado".equals(cmbTipoProg.getValue())) {
            try {
                sesiones = Integer.parseInt(txtSesionesTutor.getText());
            } catch (NumberFormatException e) {
                lblPrograma.setText("Error: Ingrese un número entero adecuado en 'Sesiones Tutor'.");
                return;
            }
        }

        try {
            List<String> ben = Arrays.asList(txtBeneficiosProg.getText().split("\\s*,\\s*"));
            ProgramaFormacion nuevo;

            // Patron Simple Factory
            if ("Personalizado".equals(cmbTipoProg.getValue())) {
                nuevo = new ProgramaPersonalizado(txtCodigoProg.getText(), txtNombreProg.getText(), txtIdiomaProg.getText(),
                        txtDescripcionProg.getText(), duracion, valor, EstadoPrograma.ACTIVO, ben, sesiones, txtNivelRequerido.getText(), txtObjetivos.getText());
            } else if ("Intensivo".equals(cmbTipoProg.getValue())) {
                nuevo = new ProgramaIntensivo(txtCodigoProg.getText(), txtNombreProg.getText(), txtIdiomaProg.getText(),
                        txtDescripcionProg.getText(), duracion, valor, EstadoPrograma.ACTIVO, ben);
            } else {
                nuevo = new ProgramaBasico(txtCodigoProg.getText(), txtNombreProg.getText(), txtIdiomaProg.getText(),
                        txtDescripcionProg.getText(), duracion, valor, EstadoPrograma.ACTIVO, ben);
            }

            if (programaSeleccionado != null) {
                // Si es edición, actualizar el programa
                academia.actualizarPrograma(programaSeleccionado.getCodigo(), nuevo);
            } else {
                // Si es nuevo programa, registramos directamente
                academia.registrarPrograma(nuevo);
            }
            lblPrograma.setText("Programa guardado.");
            limpiarCampos();
            refrescar();
            
            // Notificar al MainController para actualizar la pestaña de ofertas
            if (mainController != null) {
                mainController.notificarCambioProgramas();
            }
        } catch (Exception ex) {
            lblPrograma.setText("Error: " + ex.getMessage());
        }
    }

    @FXML
    private void eliminarPrograma() {
        if (programaSeleccionado == null) return;
        academia.eliminarPrograma(programaSeleccionado);
        limpiarCampos();
        refrescar();
        
        // Notificar al MainController para actualizar la pestaña de ofertas
        if (mainController != null) {
            mainController.notificarCambioProgramas();
        }
    }

    @FXML
    private void limpiarCampos() {
        programaSeleccionado = null;
        txtCodigoProg.clear(); txtNombreProg.clear(); txtIdiomaProg.clear(); txtDuracionProg.clear();
        txtValorProg.clear(); txtDescripcionProg.clear(); txtBeneficiosProg.clear();
        txtSesionesTutor.clear(); txtNivelRequerido.clear(); txtObjetivos.clear();
        btnAccionProg.setText("Registrar");
        tblProgramas.getSelectionModel().clearSelection();
    }

    public void refrescar() {
        if (academia != null) tblProgramas.setItems(FXCollections.observableArrayList(academia.getProgramas()));
    }
}
