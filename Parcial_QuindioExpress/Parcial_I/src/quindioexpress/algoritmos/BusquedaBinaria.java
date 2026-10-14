package quindioexpress.algoritmos;

import java.util.List;

import quindioexpress.modelo.Paquete;

public final class BusquedaBinaria {
    private BusquedaBinaria() { }

    // Requiere la lista ordenada por código
    public static int buscarPorCodigo(List<Paquete> ordenados, String codigo) {
        return buscar(ordenados, codigo.trim().toUpperCase(), 0, ordenados.size() - 1);
    }

    private static int buscar(List<Paquete> lista, String codigo, int inicio, int fin) {
        if (inicio > fin) {  // Caso base: no está
            return -1;
        }
        int medio = inicio + (fin - inicio) / 2;
        int cmp = lista.get(medio).getCodigo().compareTo(codigo);
        if (cmp == 0) {  // Caso base: encontrado
            return medio;
        } else if (cmp < 0) {
            return buscar(lista, codigo, medio + 1, fin);
        } else {
            return buscar(lista, codigo, inicio, medio - 1);
        }
    }

    public static int buscarPorCodigoIterativo(List<Paquete> ordenados, String codigo) {
        String buscado = codigo.trim().toUpperCase();
        int inicio = 0;
        int fin = ordenados.size() - 1;
        while (inicio <= fin) {
            int medio = inicio + (fin - inicio) / 2;
            int cmp = ordenados.get(medio).getCodigo().compareTo(buscado);
            if (cmp == 0) return medio;
            if (cmp < 0) inicio = medio + 1;
            else fin = medio - 1;
        }
        return -1;
    }

    public static boolean estaOrdenadaPorCodigo(List<Paquete> lista) {
        for (int i = 1; i < lista.size(); i++) {
            if (lista.get(i - 1).compareTo(lista.get(i)) > 0) {
                return false;
            }
        }
        return true;
    }
}
