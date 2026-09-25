package co.edu.uniquindio.linguaplus.modelo.modalidad;
public class ServicioModalidad {
    public String preparar(FabricaModalidad fabrica) {
        MaterialCurso material = fabrica.crearMaterial();
        Carnet carnet = fabrica.crearCarnet();
        return material.descripcion() + " + " + carnet.descripcion();
    }
}
