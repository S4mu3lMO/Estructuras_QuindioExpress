package quindioexpress.algoritmos;

import java.util.List;

import quindioexpress.modelo.Paquete;

public final class DivideYVenceras {
    private DivideYVenceras() { }

    public static Paquete paqueteMasPesado(List<Paquete> paquetes) {
        if (paquetes == null || paquetes.isEmpty()) {
            throw new IllegalArgumentException("No hay paquetes para analizar");
        }
        return masPesado(paquetes, 0, paquetes.size() - 1);
    }

    private static Paquete masPesado(List<Paquete> p, int inicio, int fin) {
        if (inicio == fin) {  // Caso base: un solo paquete
            return p.get(inicio);
        }
        int medio = inicio + (fin - inicio) / 2;  // Dividir
        Paquete izquierdo = masPesado(p, inicio, medio);  // Subproblema izquierdo
        Paquete derecho = masPesado(p, medio + 1, fin);  // Subproblema derecho
        return izquierdo.getPeso() >= derecho.getPeso() ? izquierdo : derecho;  // Combinar
    }
}
