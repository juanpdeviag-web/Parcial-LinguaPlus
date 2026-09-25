package co.edu.uniquindio.linguaplus.modelo.comprobante;

import co.edu.uniquindio.linguaplus.modelo.Matricula;

public interface GeneradorComprobante {
    String emitir(Matricula matricula);
}
