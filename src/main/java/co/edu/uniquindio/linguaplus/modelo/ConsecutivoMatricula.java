package co.edu.uniquindio.linguaplus.modelo;

public final class ConsecutivoMatricula {
    private static final ConsecutivoMatricula INSTANCIA = new ConsecutivoMatricula();
    private int ultimo = 0;
    private ConsecutivoMatricula() { }
    public static ConsecutivoMatricula getInstancia() { return INSTANCIA; }
    public synchronized int siguiente() { return ++ultimo; }
}
