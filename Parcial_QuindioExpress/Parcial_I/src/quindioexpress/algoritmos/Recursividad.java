package quindioexpress.algoritmos;

import java.util.List;

import quindioexpress.modelo.Paquete;

public final class Recursividad {
    private Recursividad() { }

    public static double pesoTotalPorDestino(List<Paquete> paquetes, String destino) {
        return pesoTotal(paquetes, Paquete.normalizarMunicipio(destino), 0);
    }

    private static double pesoTotal(List<Paquete> paquetes, String destino, int indice) {
        if (indice == paquetes.size()) {  // Caso base
            return 0.0;
        }
        Paquete actual = paquetes.get(indice);
        double aporte = actual.getDestino().equals(destino) ? actual.getPeso() : 0.0;
        return aporte + pesoTotal(paquetes, destino, indice + 1);  // Caso recursivo
    }

    public static int contarPrioridadMinima(List<Paquete> paquetes, int prioridadMinima) {
        return contar(paquetes, prioridadMinima, 0);
    }

    private static int contar(List<Paquete> paquetes, int prioridadMinima, int indice) {
        if (indice == paquetes.size()) {  // Caso base
            return 0;
        }
        int cumple = paquetes.get(indice).getPrioridad() >= prioridadMinima ? 1 : 0;
        return cumple + contar(paquetes, prioridadMinima, indice + 1);  // Caso recursivo
    }
}
