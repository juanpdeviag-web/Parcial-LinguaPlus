package co.edu.uniquindio.linguaplus.controlador;

import co.edu.uniquindio.linguaplus.modelo.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private javafx.collections.ObservableList<ProgramaOferta> listaOfertas;

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
        colOfertaCupos.setCellValueFactory(d -> d.getValue().cuposDisponiblesProperty().asString());
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

        // Crear ObservableList con extractor para observar cambios en cuposDisponibles
        listaOfertas = FXCollections.observableArrayList(
            po -> new javafx.beans.Observable[] { po.cuposDisponiblesProperty() }
        );
        listaOfertas.addAll(listaAplanada);
        tblOfertas.setItems(listaOfertas);
    }

    @FXML
    private void editarOfertaPeriodo() {
        ProgramaOferta ofertaSeleccionada = tblOfertas.getSelectionModel().getSelectedItem();
        if (ofertaSeleccionada == null) {
            lblOferta.setText("Error: Debe seleccionar una oferta para editar.");
            return;
        }

        try {
            Dialog<ProgramaOferta> dialog = new Dialog<>();
            dialog.setTitle("Editar Oferta de Periodo");
            dialog.setHeaderText("Modifique los datos de la oferta");

            ButtonType guardarButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(guardarButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            DatePicker dpInicioEdit = new DatePicker(ofertaSeleccionada.getFechaInicio());
            DatePicker dpFinEdit = new DatePicker(ofertaSeleccionada.getFechaFin());
            ComboBox<ProgramaFormacion> cmbProgEdit = new ComboBox<>(FXCollections.observableArrayList(academia.getProgramas()));
            cmbProgEdit.setValue(ofertaSeleccionada.getPrograma());
            cmbProgEdit.setDisable(true); // No cambiar el programa
            ComboBox<String> cmbHorarioEdit = new ComboBox<>(FXCollections.observableArrayList("Mañana (08:00 - 12:00)", "Tarde (14:00 - 18:00)", "Noche (18:30 - 21:30)"));
            cmbHorarioEdit.setValue(ofertaSeleccionada.getHorario());
            ComboBox<String> cmbModalidadEdit = new ComboBox<>(FXCollections.observableArrayList("Presencial", "Virtual"));
            cmbModalidadEdit.setValue(ofertaSeleccionada.getModalidad());
            Spinner<Integer> spnCuposEdit = new Spinner<>(1, 100, ofertaSeleccionada.getCuposDisponibles());

            grid.add(new Label("Fecha Inicio:"), 0, 0);
            grid.add(dpInicioEdit, 1, 0);
            grid.add(new Label("Fecha Fin:"), 0, 1);
            grid.add(dpFinEdit, 1, 1);
            grid.add(new Label("Programa:"), 0, 2);
            grid.add(cmbProgEdit, 1, 2);
            grid.add(new Label("Horario:"), 0, 3);
            grid.add(cmbHorarioEdit, 1, 3);
            grid.add(new Label("Modalidad:"), 0, 4);
            grid.add(cmbModalidadEdit, 1, 4);
            grid.add(new Label("Cupos:"), 0, 5);
            grid.add(spnCuposEdit, 1, 5);

            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == guardarButtonType) {
                    try {
                        ProgramaOferta nuevaOferta = new ProgramaOferta(
                            ofertaSeleccionada.getPrograma(),
                            cmbHorarioEdit.getValue(),
                            spnCuposEdit.getValue(),
                            cmbModalidadEdit.getValue()
                        );
                        nuevaOferta.setFechasVigencia(dpInicioEdit.getValue(), dpFinEdit.getValue());
                        return nuevaOferta;
                    } catch (Exception e) {
                        lblOferta.setText("Error: " + e.getMessage());
                        return null;
                    }
                }
                return null;
            });

            Optional<ProgramaOferta> resultado = dialog.showAndWait();
            resultado.ifPresent(nuevaOferta -> {
                try {
                    // Encontrar y actualizar la oferta en el periodo correspondiente
                    for (OfertaPeriodo op : academia.getOfertasPeriodos()) {
                        if (op.getFecha().equals(ofertaSeleccionada.getFechaInicio())) {
                            // Reemplazar el programa oferta en la lista
                            for (int i = 0; i < op.getProgramas().size(); i++) {
                                if (op.getProgramas().get(i).getPrograma().getCodigo().equals(ofertaSeleccionada.getPrograma().getCodigo())) {
                                    // Nota: No podemos modificar directamente la lista inmutable, necesitamos recrear el periodo
                                    OfertaPeriodo nuevoPeriodo = new OfertaPeriodo(dpInicioEdit.getValue());
                                    if (dpFinEdit.getValue() != null) {
                                        nuevoPeriodo.setFechaFin(dpFinEdit.getValue());
                                    }
                                    // Agregar todos los programas excepto el editado
                                    for (ProgramaOferta po : op.getProgramas()) {
                                        if (!po.getPrograma().getCodigo().equals(ofertaSeleccionada.getPrograma().getCodigo())) {
                                            nuevoPeriodo.agregarPrograma(po);
                                        }
                                    }
                                    nuevoPeriodo.agregarPrograma(nuevaOferta);
                                    academia.actualizarOfertaPeriodo(op.getFecha(), nuevoPeriodo);
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    lblOferta.setText("Oferta actualizada exitosamente.");
                    refrescar();
                    if (mainController != null) {
                        mainController.notificarCambioOfertas();
                    }
                } catch (Exception ex) {
                    lblOferta.setText("Error: " + ex.getMessage());
                }
            });
        } catch (Exception ex) {
            lblOferta.setText("Error: " + ex.getMessage());
        }
    }

    @FXML
    private void eliminarOfertaPeriodo() {
        ProgramaOferta ofertaSeleccionada = tblOfertas.getSelectionModel().getSelectedItem();
        if (ofertaSeleccionada == null) {
            lblOferta.setText("Error: Debe seleccionar una oferta para eliminar.");
            return;
        }

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar eliminación");
        alerta.setHeaderText("¿Está seguro de eliminar la oferta?");
        alerta.setContentText("Programa: " + ofertaSeleccionada.getPrograma().getNombre() + "\nHorario: " + ofertaSeleccionada.getHorario());

        Optional<ButtonType> respuesta = alerta.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            try {
                // Encontrar y eliminar el periodo que contiene esta oferta
                for (OfertaPeriodo op : academia.getOfertasPeriodos()) {
                    if (op.getFecha().equals(ofertaSeleccionada.getFechaInicio())) {
                        academia.eliminarOfertaPeriodo(op);
                        break;
                    }
                }
                lblOferta.setText("Oferta eliminada exitosamente.");
                refrescar();
                if (mainController != null) {
                    mainController.notificarCambioOfertas();
                }
            } catch (Exception ex) {
                lblOferta.setText("Error: " + ex.getMessage());
            }
        }
    }
}
