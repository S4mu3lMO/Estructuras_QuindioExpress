package quindioexpress.servicio;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import quindioexpress.algoritmos.BusquedaBinaria;
import quindioexpress.algoritmos.DivideYVenceras;
import quindioexpress.algoritmos.Recursividad;
import quindioexpress.estructuras.Coleccion;
import quindioexpress.estructuras.ListaSimplementeEnlazada;
import quindioexpress.modelo.CriteriosPaquete;
import quindioexpress.modelo.EstadoPaquete;
import quindioexpress.modelo.Paquete;
import quindioexpress.modelo.Repartidor;

public class CentroDistribucion {
    private final List<Paquete> registro = new ArrayList<>();
    private final Map<String, Paquete> paquetesPorCodigo = new HashMap<>();
    private final Set<String> municipios = new HashSet<>();
    private final Set<String> municipiosOrdenados = new TreeSet<>();
    private final Map<String, List<Paquete>> paquetesPorMunicipio = new HashMap<>();
    private final Queue<Paquete> colaLlegada = new ArrayDeque<>();
    private final PriorityQueue<Paquete> colaPrioridad = new PriorityQueue<>(CriteriosPaquete.DESPACHO);
    private final ListaSimplementeEnlazada<Paquete> historialEntregas = new ListaSimplementeEnlazada<>();

    private final Map<String, Repartidor> repartidores = new LinkedHashMap<>();
    private final Map<String, Repartidor> asignaciones = new HashMap<>();   // código del paquete -> repartidor

    public void registrarPaquete(Paquete paquete) {
        if (paquete == null) {
            throw new IllegalArgumentException("El paquete no puede ser nulo");
        }
        if (paquetesPorCodigo.containsKey(paquete.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un paquete con el código " + paquete.getCodigo());
        }
        String destino = paquete.getDestino();

        registro.add(paquete);
        paquetesPorCodigo.put(paquete.getCodigo(), paquete);
        municipios.add(destino);
        municipiosOrdenados.add(destino);
        paquetesPorMunicipio.computeIfAbsent(destino, d -> new ArrayList<>()).add(paquete);
        colaLlegada.offer(paquete);
        colaPrioridad.offer(paquete);
    }

    public Paquete registrarPaquete(String codigo, String destino, double peso, int prioridad, int tiempo) {
        Paquete p = new Paquete(codigo, destino, peso, prioridad, tiempo);
        registrarPaquete(p);
        return p;
    }

    public boolean existePaquete(String codigo) {
        return codigo != null && paquetesPorCodigo.containsKey(codigo.trim().toUpperCase());
    }

    public Paquete buscarPaquete(String codigo) {
        if (codigo == null) {
            return null;
        }
        return paquetesPorCodigo.get(codigo.trim().toUpperCase());
    }

    public List<Paquete> getRegistro() {
        return Collections.unmodifiableList(registro);
    }

    public int cantidadPaquetes() {
        return registro.size();
    }

    public void registrarRepartidor(Repartidor repartidor) {
        if (repartidor == null) {
            throw new IllegalArgumentException("El repartidor no puede ser nulo");
        }
        if (repartidores.containsKey(repartidor.getIdentificacion())) {
            throw new IllegalArgumentException(
                    "Ya existe un repartidor con la identificación " + repartidor.getIdentificacion());
        }
        repartidores.put(repartidor.getIdentificacion(), repartidor);
    }

    public Repartidor buscarRepartidor(String identificacion) {
        return identificacion == null ? null : repartidores.get(identificacion.trim());
    }

    public List<Repartidor> getRepartidores() {
        return new ArrayList<>(repartidores.values());
    }

    public void cambiarDisponibilidad(String identificacion, boolean disponible) {
        Repartidor r = buscarRepartidor(identificacion);
        if (r == null) {
            throw new IllegalArgumentException("No existe el repartidor " + identificacion);
        }
        if (disponible && asignaciones.containsValue(r)) {
            throw new IllegalStateException(r.getNombre() + " tiene un paquete en curso; registre la entrega primero");
        }
        r.setDisponible(disponible);
    }

    public Set<String> getMunicipios() {
        return Collections.unmodifiableSet(municipios);
    }

    public Set<String> getMunicipiosOrdenados() {
        return Collections.unmodifiableSet(municipiosOrdenados);
    }

    public List<Paquete> paquetesDeMunicipio(String municipio) {
        List<Paquete> lista = paquetesPorMunicipio.get(Paquete.normalizarMunicipio(municipio));
        return lista == null ? Collections.emptyList() : Collections.unmodifiableList(lista);
    }

    public Map<String, List<Paquete>> getPaquetesPorMunicipio() {
        return Collections.unmodifiableMap(paquetesPorMunicipio);
    }

    public Paquete siguientePorLlegada() {
        descartarNoPendientes(colaLlegada);
        return colaLlegada.peek();
    }

    public Paquete siguientePorPrioridad() {
        descartarNoPendientes(colaPrioridad);
        return colaPrioridad.peek();
    }

    public Paquete despacharPorLlegada() {
        return despacharDesde(colaLlegada);
    }

    public Paquete despacharPorPrioridad() {
        return despacharDesde(colaPrioridad);
    }

    private Paquete despacharDesde(Queue<Paquete> cola) {
        descartarNoPendientes(cola);
        Paquete siguiente = cola.peek();
        if (siguiente == null) {
            return null;
        }
        Repartidor repartidor = buscarRepartidorDisponible(siguiente.getDestino());
        if (repartidor == null) {
            throw new IllegalStateException("No hay repartidor disponible en " + siguiente.getDestino()
                    + " para " + siguiente.getCodigo() + "; el paquete conserva su turno");
        }
        cola.poll();   // solo sale de la cola si se pudo asignar
        asignar(siguiente, repartidor);
        return siguiente;
    }

    public void asignarPaquete(String codigo, String idRepartidor) {
        Paquete p = buscarPaquete(codigo);
        if (p == null) {
            throw new IllegalArgumentException("No existe el paquete " + codigo);
        }
        Repartidor r = buscarRepartidor(idRepartidor);
        if (r == null) {
            throw new IllegalArgumentException("No existe el repartidor " + idRepartidor);
        }
        asignar(p, r);
    }

    private void asignar(Paquete p, Repartidor r) {
        if (!p.estaPendiente()) {
            throw new IllegalStateException(p.getCodigo() + " no está pendiente (estado " + p.getEstado() + ")");
        }
        if (!r.isDisponible()) {
            throw new IllegalStateException(r.getNombre() + " no está disponible para una nueva asignación");
        }
        if (!r.getZona().equals(p.getDestino())) {
            throw new IllegalStateException("La zona de " + r.getNombre() + " (" + r.getZona()
                    + ") no corresponde al destino de " + p.getCodigo() + " (" + p.getDestino() + ")");
        }
        p.setEstado(EstadoPaquete.ASIGNADO);
        r.setDisponible(false);
        asignaciones.put(p.getCodigo(), r);
    }

    private Repartidor buscarRepartidorDisponible(String zona) {
        for (Repartidor r : repartidores.values()) {
            if (r.isDisponible() && r.getZona().equals(zona)) {
                return r;
            }
        }
        return null;
    }

    public Repartidor repartidorAsignado(String codigo) {
        return codigo == null ? null : asignaciones.get(codigo.trim().toUpperCase());
    }

    // Saca del frente los paquetes que ya se despacharon por la otra cola
    private void descartarNoPendientes(Queue<Paquete> cola) {
        while (!cola.isEmpty() && !cola.peek().estaPendiente()) {
            cola.poll();
        }
    }

    public List<Paquete> pendientesPorLlegada() {
        List<Paquete> resultado = new ArrayList<>();
        for (Paquete p : colaLlegada) {
            if (p.estaPendiente()) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public List<Paquete> ordenDeDespachoPrioritario() {
        PriorityQueue<Paquete> copia = new PriorityQueue<>(colaPrioridad);   // no vacía la cola real
        List<Paquete> resultado = new ArrayList<>();
        while (!copia.isEmpty()) {
            Paquete p = copia.poll();
            if (p.estaPendiente()) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public void registrarEntrega(String codigo) {
        Paquete p = buscarPaquete(codigo);
        if (p == null) {
            throw new IllegalArgumentException("No existe el paquete " + codigo);
        }
        if (p.getEstado() != EstadoPaquete.ASIGNADO) {
            throw new IllegalStateException(p.getCodigo() + " no se puede entregar: estado " + p.getEstado());
        }
        p.setEstado(EstadoPaquete.ENTREGADO);
        Repartidor r = asignaciones.remove(p.getCodigo());
        r.setDisponible(true);
        historialEntregas.agregar(p);
    }

    public Coleccion<Paquete> getHistorialEntregas() {
        return historialEntregas;
    }

    public List<Paquete> ordenadosPorCodigo() {
        List<Paquete> copia = new ArrayList<>(registro);
        Collections.sort(copia);
        return copia;
    }

    public List<Paquete> ordenadosPor(Comparator<Paquete> criterio) {
        List<Paquete> copia = new ArrayList<>(registro);
        copia.sort(criterio);
        return copia;
    }

    public double pesoTotalPorDestino(String destino) {
        return Recursividad.pesoTotalPorDestino(registro, destino);
    }

    public int contarPrioridadMinima(int prioridadMinima) {
        return Recursividad.contarPrioridadMinima(registro, prioridadMinima);
    }

    public Paquete buscarBinariaPorCodigo(String codigo) {
        List<Paquete> ordenados = ordenadosPorCodigo();   // la búsqueda binaria requiere orden por código
        int pos = BusquedaBinaria.buscarPorCodigo(ordenados, codigo);
        return pos == -1 ? null : ordenados.get(pos);
    }

    public Paquete paqueteMasPesado() {
        return DivideYVenceras.paqueteMasPesado(registro);
    }
}
