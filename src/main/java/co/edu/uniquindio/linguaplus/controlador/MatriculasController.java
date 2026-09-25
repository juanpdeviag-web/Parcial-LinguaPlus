package co.edu.uniquindio.linguaplus.controlador;

import co.edu.uniquindio.linguaplus.modelo.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.GridPane;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class MatriculasController {
    @FXML private ComboBox<Estudiante> cmbEstudiante;
    @FXML private ComboBox<ProgramaFormacion> cmbPrograma;
    @FXML private ComboBox<OfertaPeriodo> cmbPeriodoOferta; // Restringe la selección a períodos válidos
    @FXML private ComboBox<Docente> cmbTutor;
    @FXML private TableView<ServicioSeleccionable> tblServicios;
    @FXML private TableColumn<ServicioSeleccionable, Boolean> colServSeleccion;
    @FXML private TableColumn<ServicioSeleccionable, String> colServCodigo, colServNombre, colServDescripcion, colServPrecio, colServDisponible;
    @FXML private TextField txtDescuento, txtObservaciones;
    @FXML private Label lblMatricula, lblTotalEstimado;
    @FXML private TableView<Matricula> tblMatriculas;
    @FXML private TableColumn<Matricula, String> colMatNumero, colMatEstudiante, colMatPrograma, colMatFechaInicio, colMatFechaFin, colMatTotal, colMatServicios;

    private Academia academia;
    private MainController mainController;

    @FXML
    public void initialize() {
        cmbTutor.setDisable(true);
        
        // Configurar columnas de la tabla de servicios
        colServSeleccion.setCellValueFactory(d -> d.getValue().seleccionadoProperty());
        colServSeleccion.setCellFactory(tc -> new TableCell<ServicioSeleccionable, Boolean>() {
            private final CheckBox checkBox = new CheckBox();
            
            {
                checkBox.setOnAction(e -> {
                    if (getTableRow() != null && getTableRow().getItem() != null) {
                        getTableRow().getItem().setSeleccionado(checkBox.isSelected());
                    }
                });
            }
            
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    ServicioSeleccionable ss = getTableRow().getItem();
                    checkBox.setSelected(item);
                    checkBox.setDisable(!ss.isDisponible());
                    if (!ss.isDisponible() && item) {
                        // Si el servicio no está disponible y estaba seleccionado, deseleccionarlo
                        ss.setSeleccionado(false);
                        checkBox.setSelected(false);
                    }
                    setGraphic(checkBox);
                }
            }
        });
        colServCodigo.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getCodigo()));
        colServNombre.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getNombre()));
        colServDescripcion.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getDescripcion()));
        colServPrecio.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(String.format("$%.2f", d.getValue().getPrecio())));
        colServDisponible.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().isDisponible() ? "Sí" : "No"));
        
        // Configurar columnas de la tabla de matrículas
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
        
        // Listeners para actualizar el total estimado
        cmbPrograma.setOnAction(e -> actualizarTotalEstimado());
        txtDescuento.textProperty().addListener((obs, oldVal, newVal) -> actualizarTotalEstimado());
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
        actualizarTotalEstimado();
    }

    private void actualizarTotalEstimado() {
        ProgramaFormacion programa = cmbPrograma.getValue();
        double descuento = 0;
        try {
            descuento = txtDescuento.getText().isBlank() ? 0 : Double.parseDouble(txtDescuento.getText());
        } catch (NumberFormatException e) {
            descuento = 0;
        }
        
        double subtotal = 0;
        if (programa != null) {
            subtotal = programa.calcularValorBase();
        }
        
        // Sumar servicios seleccionados
        for (ServicioSeleccionable ss : tblServicios.getItems()) {
            if (ss.isSeleccionado()) {
                subtotal += ss.getPrecio();
            }
        }
        
        double total = subtotal * (1.0 - descuento / 100.0);
        lblTotalEstimado.setText(String.format("$%.2f", total));
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

            // Agregar servicios seleccionados
            Matricula.Builder matriculaBuilder = new Matricula.Builder()
                    .conEstudiante(est)
                    .conPrograma(prog)
                    .conFechaInicio(periodo.getFecha())
                    .conDescuento(txtDescuento.getText().isBlank() ? 0 : Double.parseDouble(txtDescuento.getText()))
                    .conObservaciones(txtObservaciones.getText());
            
            for (ServicioSeleccionable ss : tblServicios.getItems()) {
                if (ss.isSeleccionado()) {
                    matriculaBuilder.agregarServicio(ss.getServicio());
                }
            }
            
            Matricula m = matriculaBuilder.build();

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
        
        // Cargar servicios con listeners para actualizar el total
        var serviciosSeleccionables = academia.getServicios().stream()
            .map(ServicioSeleccionable::new)
            .toList();
        tblServicios.setItems(FXCollections.observableArrayList(serviciosSeleccionables));
        
        // Agregar listeners a cada servicio para actualizar el total cuando cambie la selección
        for (ServicioSeleccionable ss : tblServicios.getItems()) {
            ss.seleccionadoProperty().addListener((obs, oldVal, newVal) -> actualizarTotalEstimado());
        }
        
        tblMatriculas.setItems(FXCollections.observableArrayList(academia.getMatriculas()));
        // Programas se cargan según el período seleccionado
        filtrarProgramasPorPeriodo();
        actualizarTotalEstimado();
    }

    @FXML
    private void crearServicio() {
        try {
            // Crear diálogo para ingresar datos del servicio
            Dialog<ServicioAdicional> dialog = new Dialog<>();
            dialog.setTitle("Crear Servicio Adicional");
            dialog.setHeaderText("Ingrese los datos del nuevo servicio");

            // Configurar botones
            ButtonType crearButtonType = new ButtonType("Crear", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(crearButtonType, ButtonType.CANCEL);

            // Crear campos de entrada
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField txtCodigo = new TextField();
            TextField txtNombre = new TextField();
            TextField txtDescripcion = new TextField();
            TextField txtPrecio = new TextField();
            CheckBox chkDisponible = new CheckBox();
            chkDisponible.setSelected(true);

            grid.add(new Label("Código:"), 0, 0);
            grid.add(txtCodigo, 1, 0);
            grid.add(new Label("Nombre:"), 0, 1);
            grid.add(txtNombre, 1, 1);
            grid.add(new Label("Descripción:"), 0, 2);
            grid.add(txtDescripcion, 1, 2);
            grid.add(new Label("Precio:"), 0, 3);
            grid.add(txtPrecio, 1, 3);
            grid.add(new Label("Disponible:"), 0, 4);
            grid.add(chkDisponible, 1, 4);

            dialog.getDialogPane().setContent(grid);

            // Convertir resultado cuando se presiona Crear
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == crearButtonType) {
                    try {
                        String codigo = txtCodigo.getText();
                        String nombre = txtNombre.getText();
                        String descripcion = txtDescripcion.getText();
                        double precio = Double.parseDouble(txtPrecio.getText());
                        boolean disponible = chkDisponible.isSelected();
                        return new ServicioAdicional(codigo, nombre, descripcion, precio, disponible);
                    } catch (NumberFormatException e) {
                        lblMatricula.setText("Error: Precio inválido");
                        return null;
                    }
                }
                return null;
            });

            Optional<ServicioAdicional> resultado = dialog.showAndWait();
            resultado.ifPresent(servicio -> {
                try {
                    academia.registrarServicio(servicio);
                    lblMatricula.setText("Servicio creado exitosamente.");
                    refrescar();
                } catch (IllegalArgumentException ex) {
                    lblMatricula.setText("Error: " + ex.getMessage());
                }
            });
        } catch (Exception ex) {
            lblMatricula.setText("Error: " + ex.getMessage());
        }
    }

    @FXML
    private void editarServicio() {
        ServicioSeleccionable servicioSeleccionable = tblServicios.getSelectionModel().getSelectedItem();
        if (servicioSeleccionable == null) {
            lblMatricula.setText("Error: Debe seleccionar un servicio para editar.");
            return;
        }
        ServicioAdicional servicioSeleccionado = servicioSeleccionable.getServicio();

        try {
            Dialog<ServicioAdicional> dialog = new Dialog<>();
            dialog.setTitle("Editar Servicio Adicional");
            dialog.setHeaderText("Modifique los datos del servicio");

            ButtonType guardarButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(guardarButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField txtCodigo = new TextField(servicioSeleccionado.getCodigo());
            txtCodigo.setDisable(true); // El código no se puede modificar
            TextField txtNombre = new TextField(servicioSeleccionado.getNombre());
            TextField txtDescripcion = new TextField(servicioSeleccionado.getDescripcion());
            TextField txtPrecio = new TextField(String.valueOf(servicioSeleccionado.getPrecio()));
            CheckBox chkDisponible = new CheckBox();
            chkDisponible.setSelected(servicioSeleccionado.isDisponible());

            grid.add(new Label("Código:"), 0, 0);
            grid.add(txtCodigo, 1, 0);
            grid.add(new Label("Nombre:"), 0, 1);
            grid.add(txtNombre, 1, 1);
            grid.add(new Label("Descripción:"), 0, 2);
            grid.add(txtDescripcion, 1, 2);
            grid.add(new Label("Precio:"), 0, 3);
            grid.add(txtPrecio, 1, 3);
            grid.add(new Label("Disponible:"), 0, 4);
            grid.add(chkDisponible, 1, 4);

            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == guardarButtonType) {
                    try {
                        String codigo = servicioSeleccionado.getCodigo();
                        String nombre = txtNombre.getText();
                        String descripcion = txtDescripcion.getText();
                        double precio = Double.parseDouble(txtPrecio.getText());
                        boolean disponible = chkDisponible.isSelected();
                        return new ServicioAdicional(codigo, nombre, descripcion, precio, disponible);
                    } catch (NumberFormatException e) {
                        lblMatricula.setText("Error: Precio inválido");
                        return null;
                    }
                }
                return null;
            });

            Optional<ServicioAdicional> resultado = dialog.showAndWait();
            resultado.ifPresent(servicio -> {
                try {
                    academia.actualizarServicio(servicioSeleccionado.getCodigo(), servicio);
                    lblMatricula.setText("Servicio actualizado exitosamente.");
                    refrescar();
                } catch (Exception ex) {
                    lblMatricula.setText("Error: " + ex.getMessage());
                }
            });
        } catch (Exception ex) {
            lblMatricula.setText("Error: " + ex.getMessage());
        }
    }

    @FXML
    private void eliminarServicio() {
        ServicioSeleccionable servicioSeleccionable = tblServicios.getSelectionModel().getSelectedItem();
        if (servicioSeleccionable == null) {
            lblMatricula.setText("Error: Debe seleccionar un servicio para eliminar.");
            return;
        }
        ServicioAdicional servicioSeleccionado = servicioSeleccionable.getServicio();

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar eliminación");
        alerta.setHeaderText("¿Está seguro de eliminar el servicio?");
        alerta.setContentText("Servicio: " + servicioSeleccionado.getNombre());

        Optional<ButtonType> respuesta = alerta.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            try {
                academia.eliminarServicio(servicioSeleccionado);
                lblMatricula.setText("Servicio eliminado exitosamente.");
                refrescar();
            } catch (Exception ex) {
                lblMatricula.setText("Error: " + ex.getMessage());
            }
        }
    }

    @FXML
    private void editarMatricula() {
        Matricula matriculaSeleccionada = tblMatriculas.getSelectionModel().getSelectedItem();
        if (matriculaSeleccionada == null) {
            lblMatricula.setText("Error: Debe seleccionar una matrícula para editar.");
            return;
        }

        try {
            Dialog<Matricula> dialog = new Dialog<>();
            dialog.setTitle("Editar Matrícula");
            dialog.setHeaderText("Modifique los datos de la matrícula");

            ButtonType guardarButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(guardarButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            ComboBox<Estudiante> cmbEstEdit = new ComboBox<>(FXCollections.observableArrayList(academia.getEstudiantes()));
            cmbEstEdit.setValue(matriculaSeleccionada.getEstudiante());
            cmbEstEdit.setDisable(true); // No cambiar estudiante
            ComboBox<ProgramaFormacion> cmbProgEdit = new ComboBox<>(FXCollections.observableArrayList(academia.getProgramas()));
            cmbProgEdit.setValue(matriculaSeleccionada.getPrograma());
            cmbProgEdit.setDisable(true); // No cambiar programa
            DatePicker dpFechaEdit = new DatePicker(matriculaSeleccionada.getFechaInicio());
            dpFechaEdit.setDisable(true); // No cambiar fecha
            TextField txtDescuentoEdit = new TextField(String.valueOf(matriculaSeleccionada.getDescuentoPorcentaje()));
            TextField txtObservacionesEdit = new TextField(matriculaSeleccionada.getObservaciones());

            grid.add(new Label("Estudiante:"), 0, 0);
            grid.add(cmbEstEdit, 1, 0);
            grid.add(new Label("Programa:"), 0, 1);
            grid.add(cmbProgEdit, 1, 1);
            grid.add(new Label("Fecha Inicio:"), 0, 2);
            grid.add(dpFechaEdit, 1, 2);
            grid.add(new Label("Descuento %:"), 0, 3);
            grid.add(txtDescuentoEdit, 1, 3);
            grid.add(new Label("Observaciones:"), 0, 4);
            grid.add(txtObservacionesEdit, 1, 4);

            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == guardarButtonType) {
                    try {
                        double descuento = txtDescuentoEdit.getText().isBlank() ? 0 : Double.parseDouble(txtDescuentoEdit.getText());
                        return new Matricula.Builder()
                            .conEstudiante(matriculaSeleccionada.getEstudiante())
                            .conPrograma(matriculaSeleccionada.getPrograma())
                            .conFechaInicio(matriculaSeleccionada.getFechaInicio())
                            .conDocenteTutor(matriculaSeleccionada.getDocenteTutor())
                            .conDescuento(descuento)
                            .conObservaciones(txtObservacionesEdit.getText())
                            .build();
                    } catch (Exception e) {
                        lblMatricula.setText("Error: " + e.getMessage());
                        return null;
                    }
                }
                return null;
            });

            Optional<Matricula> resultado = dialog.showAndWait();
            resultado.ifPresent(nuevaMatricula -> {
                try {
                    academia.actualizarMatricula(matriculaSeleccionada.getNumeroMatricula(), nuevaMatricula);
                    lblMatricula.setText("Matrícula actualizada exitosamente.");
                    refrescar();
                } catch (Exception ex) {
                    lblMatricula.setText("Error: " + ex.getMessage());
                }
            });
        } catch (Exception ex) {
            lblMatricula.setText("Error: " + ex.getMessage());
        }
    }

    @FXML
    private void eliminarMatricula() {
        Matricula matriculaSeleccionada = tblMatriculas.getSelectionModel().getSelectedItem();
        if (matriculaSeleccionada == null) {
            lblMatricula.setText("Error: Debe seleccionar una matrícula para eliminar.");
            return;
        }

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar eliminación");
        alerta.setHeaderText("¿Está seguro de eliminar la matrícula?");
        alerta.setContentText("Matrícula #" + matriculaSeleccionada.getNumeroMatricula() + 
            "\nEstudiante: " + matriculaSeleccionada.getEstudiante().getNombreCompleto() +
            "\nPrograma: " + matriculaSeleccionada.getPrograma().getNombre() +
            "\n\nSe liberará un cupo en la oferta correspondiente.");

        Optional<ButtonType> respuesta = alerta.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            try {
                academia.eliminarMatricula(matriculaSeleccionada);
                lblMatricula.setText("Matrícula eliminada exitosamente. Cupo liberado.");
                refrescar();
                
                // Notificar al MainController para refrescar la pestaña de ofertas
                if (mainController != null) {
                    mainController.notificarCambioOfertas();
                }
            } catch (Exception ex) {
                lblMatricula.setText("Error: " + ex.getMessage());
            }
        }
    }
}
