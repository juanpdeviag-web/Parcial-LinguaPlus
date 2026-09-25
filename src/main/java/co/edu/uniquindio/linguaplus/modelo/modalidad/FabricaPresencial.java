package co.edu.uniquindio.linguaplus.modelo.modalidad;
public class FabricaPresencial implements FabricaModalidad {
    public MaterialCurso crearMaterial() { return new MaterialImpreso(); }
    public Carnet crearCarnet() { return new CarnetFisico(); }
}
