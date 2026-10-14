package quindioexpress.estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ListaSimplementeEnlazada<T> implements Coleccion<T> {
    private static class Nodo<T> {
        private T valor;
        private Nodo<T> siguiente;

        Nodo(T valor) {
            this.valor = valor;
            this.siguiente = null;
        }
    }

    private Nodo<T> cabeza;
    private Nodo<T> cola;          // permite agregar al final sin recorrer la lista
    private int tamanio;

    public ListaSimplementeEnlazada() {
        cabeza = null;
        cola = null;
        tamanio = 0;
    }

    @Override
    public void agregar(T elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException("No se permiten elementos nulos");
        }
        Nodo<T> nuevo = new Nodo<>(elemento);
        if (estaVacia()) {
            cabeza = nuevo;
            cola = nuevo;
        } else {
            cola.siguiente = nuevo;
            cola = nuevo;
        }
        tamanio++;
    }

    @Override
    public boolean eliminar(T elemento) {
        if (estaVacia() || elemento == null) {
            return false;
        }

        // El elemento está en la cabeza
        if (cabeza.valor.equals(elemento)) {
            cabeza = cabeza.siguiente;
            if (cabeza == null) {
                cola = null;
            }
            tamanio--;
            return true;
        }

        // Busca el nodo anterior al que se va a eliminar
        Nodo<T> anterior = cabeza;
        while (anterior.siguiente != null && !anterior.siguiente.valor.equals(elemento)) {
            anterior = anterior.siguiente;
        }

        if (anterior.siguiente == null) {
            return false;
        }

        Nodo<T> aEliminar = anterior.siguiente;
        anterior.siguiente = aEliminar.siguiente;
        if (aEliminar == cola) {   // era el último
            cola = anterior;
        }
        tamanio--;
        return true;
    }

    @Override
    public T obtener(int posicion) {
        if (posicion < 0 || posicion >= tamanio) {
            throw new IndexOutOfBoundsException(
                    "Posición " + posicion + " inválida para tamaño " + tamanio);
        }
        Nodo<T> actual = cabeza;
        for (int i = 0; i < posicion; i++) {
            actual = actual.siguiente;
        }
        return actual.valor;
    }

    @Override
    public int tamanio() {
        return tamanio;
    }

    @Override
    public boolean estaVacia() {
        return tamanio == 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new IteradorLista();
    }

    private class IteradorLista implements Iterator<T> {
        private Nodo<T> actual = cabeza;

        @Override
        public boolean hasNext() {
            return actual != null;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No hay más elementos");
            }
            T valor = actual.valor;
            actual = actual.siguiente;
            return valor;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Nodo<T> actual = cabeza;
        while (actual != null) {
            sb.append(actual.valor);
            if (actual.siguiente != null) {
                sb.append(" -> ");
            }
            actual = actual.siguiente;
        }
        return sb.append("]").toString();
    }
}
