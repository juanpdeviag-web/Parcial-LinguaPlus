package co.edu.uniquindio.linguaplus.modelo;

public class ServicioAdicional {
    private final String codigo;
    private final String nombre;
    private final String descripcion;
    private final double precio;
    private boolean disponible;

    public ServicioAdicional(String codigo, String nombre, String descripcion, double precio, boolean disponible) {
        if (codigo == null || codigo.isBlank()) throw new IllegalArgumentException("Codigo obligatorio");
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("Nombre obligatorio");
        if (precio < 0) throw new IllegalArgumentException("Precio invalido");
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.disponible = disponible;
    }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public double getPrecio() { return precio; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    @Override public String toString() { return nombre + " ($" + String.format("%.0f", precio) + ")"; }
}
