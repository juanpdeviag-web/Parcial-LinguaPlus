package co.edu.uniquindio.linguaplus.modelo.modalidad;
public class FabricaVirtual implements FabricaModalidad {
    public MaterialCurso crearMaterial() { return new LicenciaPlataforma(); }
    public Carnet crearCarnet() { return new CarnetDigital(); }
}
