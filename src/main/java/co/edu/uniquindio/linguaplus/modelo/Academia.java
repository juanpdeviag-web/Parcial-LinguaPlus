package co.edu.uniquindio.linguaplus.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

public class Academia {
    private final String nombreComercial;
    private final String nit;
    private final String direccion;
    private final String telefono;
    private final String correoElectronico;
    private final String paginaWeb;
    private final List<Estudiante> estudiantes = new ArrayList<>();
    private final List<Docente> docentes = new ArrayList<>();
    private final List<ProgramaFormacion> programas = new ArrayList<>();
    private final List<ServicioAdicional> servicios = new ArrayList<>();
    private final List<Matricula> matriculas = new ArrayList<>();
    private final List<OfertaPeriodo> ofertasPeriodos = new ArrayList<>(); // Soporte para periodos de oferta

    public Academia(String nombreComercial, String nit, String direccion, String telefono,
                    String correoElectronico, String paginaWeb) {
        this.nombreComercial = nombreComercial; this.nit = nit; this.direccion = direccion;
        this.telefono = telefono; this.correoElectronico = correoElectronico; this.paginaWeb = paginaWeb;
    }

    // REGISTROS CON VALIDACIÓN DE DUPLICADOS
    public void registrarEstudiante(Estudiante e) {
        if (estudiantes.stream().anyMatch(est -> est.getDocumento().equals(e.getDocumento()))) {
            throw new IllegalArgumentException("Ya existe un estudiante registrado con el documento: " + e.getDocumento());
        }
        estudiantes.add(e);
    }

    public void registrarDocente(Docente d) {
        if (docentes.stream().anyMatch(doc -> doc.getIdentificacion().equals(d.getIdentificacion()))) {
            throw new IllegalArgumentException("Ya existe un docente registrado con la identificación: " + d.getIdentificacion());
        }
        docentes.add(d);
    }

    public void registrarPrograma(ProgramaFormacion p) {
        if (programas.stream().anyMatch(prog -> prog.getCodigo().equals(p.getCodigo()))) {
            throw new IllegalArgumentException("Ya existe un programa registrado con el código: " + p.getCodigo());
        }
        programas.add(p);
    }
    public void registrarServicio(ServicioAdicional s) {
        if (servicios.stream().anyMatch(serv -> serv.getCodigo().equals(s.getCodigo()))) {
            throw new IllegalArgumentException("Ya existe un servicio registrado con el código: " + s.getCodigo());
        }
        servicios.add(s);
    }
    public void registrarMatricula(Matricula m) { matriculas.add(m); }
    public void registrarOfertaPeriodo(OfertaPeriodo o) { ofertasPeriodos.add(o); }

    // MÉTODOS DE ELIMINACIÓN Y MODIFICACIÓN (REEMPLAZO POR INMUTABILIDAD)
    public void eliminarEstudiante(Estudiante e) { estudiantes.remove(e); }
    public void actualizarEstudiante(String documentoOriginal, Estudiante nuevoEstudiante) {
        for (int i = 0; i < estudiantes.size(); i++) {
            if (estudiantes.get(i).getDocumento().equals(documentoOriginal)) {
                estudiantes.set(i, nuevoEstudiante);
                return;
            }
        }
    }

    public void eliminarDocente(Docente d) { docentes.remove(d); }
    public void actualizarDocente(String idOriginal, Docente nuevoDocente) {
        for (int i = 0; i < docentes.size(); i++) {
            if (docentes.get(i).getIdentificacion().equals(idOriginal)) {
                docentes.set(i, nuevoDocente);
                return;
            }
        }
    }

    public void eliminarServicio(ServicioAdicional s) { servicios.remove(s); }
    public void actualizarServicio(String codigoOriginal, ServicioAdicional nuevoServicio) {
        for (int i = 0; i < servicios.size(); i++) {
            if (servicios.get(i).getCodigo().equals(codigoOriginal)) {
                servicios.set(i, nuevoServicio);
                return;
            }
        }
    }

    public void eliminarOfertaPeriodo(OfertaPeriodo o) { ofertasPeriodos.remove(o); }
    public void actualizarOfertaPeriodo(LocalDate fechaOriginal, OfertaPeriodo nuevaOferta) {
        for (int i = 0; i < ofertasPeriodos.size(); i++) {
            if (ofertasPeriodos.get(i).getFecha().equals(fechaOriginal)) {
                ofertasPeriodos.set(i, nuevaOferta);
                return;
            }
        }
    }

    public void eliminarPrograma(ProgramaFormacion p) { programas.remove(p); }

    public void actualizarPrograma(String codigoOriginal, ProgramaFormacion nuevoPrograma) {
        for (int i = 0; i < programas.size(); i++) {
            if (programas.get(i).getCodigo().equals(codigoOriginal)) {
                programas.set(i, nuevoPrograma);
                return;
            }
        }
    }

    public void eliminarMatricula(Matricula m) {
        // Liberar cupo en el programa oferta correspondiente
        OfertaPeriodo periodoEncontrado = null;
        for (OfertaPeriodo op : ofertasPeriodos) {
            if (op.getFecha().equals(m.getFechaInicio())) {
                periodoEncontrado = op;
                break;
            }
        }
        if (periodoEncontrado != null) {
            ProgramaOferta programaOferta = periodoEncontrado.encontrarProgramaOferta(m.getPrograma());
            if (programaOferta != null) {
                programaOferta.liberarCupo();
            }
        }
        matriculas.remove(m);
    }

    public void actualizarMatricula(int numeroMatriculaOriginal, Matricula nuevaMatricula) {
        for (int i = 0; i < matriculas.size(); i++) {
            if (matriculas.get(i).getNumeroMatricula() == numeroMatriculaOriginal) {
                matriculas.set(i, nuevaMatricula);
                return;
            }
        }
    }

    public Optional<Estudiante> buscarEstudiantePorTelefono(String telefono) {
        return estudiantes.stream().filter(e -> e.getTelefono().equals(telefono)).findFirst();
    }
    public List<Estudiante> getEstudiantes() { return Collections.unmodifiableList(estudiantes); }
    public List<Docente> getDocentes() { return Collections.unmodifiableList(docentes); }
    public List<ProgramaFormacion> getProgramas() { return Collections.unmodifiableList(programas); }
    public List<ServicioAdicional> getServicios() { return Collections.unmodifiableList(servicios); }
    public List<Matricula> getMatriculas() { return Collections.unmodifiableList(matriculas); }
    public List<OfertaPeriodo> getOfertasPeriodos() { return Collections.unmodifiableList(ofertasPeriodos); }
    public String getNombreComercial() { return nombreComercial; }
    public String getNit() { return nit; }
    public String getDireccion() { return direccion; }
    public String getTelefono() { return telefono; }
    public String getCorreoElectronico() { return correoElectronico; }
    public String getPaginaWeb() { return paginaWeb; }
}
