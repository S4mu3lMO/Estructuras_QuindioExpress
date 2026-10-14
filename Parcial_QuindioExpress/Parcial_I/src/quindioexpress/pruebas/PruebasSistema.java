package quindioexpress.pruebas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import quindioexpress.algoritmos.BusquedaBinaria;
import quindioexpress.algoritmos.DivideYVenceras;
import quindioexpress.algoritmos.Recursividad;
import quindioexpress.app.DatosIniciales;
import quindioexpress.estructuras.ListaSimplementeEnlazada;
import quindioexpress.modelo.CriteriosPaquete;
import quindioexpress.modelo.EstadoPaquete;
import quindioexpress.modelo.Paquete;
import quindioexpress.modelo.Repartidor;
import quindioexpress.servicio.CentroDistribucion;

public class PruebasSistema {
    private static int exitosas = 0;
    private static int fallidas = 0;

    public static void main(String[] args) {
        pruebasListaPropia();
        pruebasPaquete();
        pruebasCentro();
        pruebasRecursividad();
        pruebasBusquedaBinaria();
        pruebasDivideYVenceras();

        System.out.println();
        System.out.println("RESUMEN: " + exitosas + " exitosas, " + fallidas + " fallidas");
        if (fallidas > 0) {
            System.exit(1);
        }
    }

    private static void pruebasListaPropia() {
        seccion("Lista simplemente enlazada propia");

        ListaSimplementeEnlazada<String> l = new ListaSimplementeEnlazada<>();
        verificar("lista nueva está vacía", l.estaVacia() && l.tamanio() == 0);
        verificar("eliminar en lista vacía retorna false", !l.eliminar("A"));
        verificar("iterador de lista vacía no tiene siguiente", !l.iterator().hasNext());
        verificarExcepcion("obtener(0) en lista vacía", IndexOutOfBoundsException.class, () -> l.obtener(0));

        l.agregar("A");
        verificar("un elemento: tamaño 1", l.tamanio() == 1 && l.obtener(0).equals("A"));
        verificar("eliminar el único elemento", l.eliminar("A") && l.estaVacia());
        l.agregar("B");
        verificar("agregar después de vaciar (cola reiniciada bien)", l.tamanio() == 1 && l.obtener(0).equals("B"));
        l.eliminar("B");

        ListaSimplementeEnlazada<Integer> n = new ListaSimplementeEnlazada<>();
        for (int i = 1; i <= 5; i++) n.agregar(i);
        verificar("eliminar primero", n.eliminar(1) && n.obtener(0) == 2 && n.tamanio() == 4);
        verificar("eliminar intermedio", n.eliminar(3) && n.toString().equals("[2 -> 4 -> 5]"));
        verificar("eliminar último", n.eliminar(5) && n.toString().equals("[2 -> 4]"));
        n.agregar(9);
        verificar("agregar tras eliminar el último (cola actualizada)", n.toString().equals("[2 -> 4 -> 9]"));
        verificar("eliminar inexistente retorna false y no cambia", !n.eliminar(77) && n.tamanio() == 3);
        verificarExcepcion("obtener(-1)", IndexOutOfBoundsException.class, () -> n.obtener(-1));
        verificarExcepcion("obtener(tamaño)", IndexOutOfBoundsException.class, () -> n.obtener(3));
        verificarExcepcion("agregar null", IllegalArgumentException.class, () -> n.agregar(null));

        int suma = 0;
        for (int x : n) suma += x;
        verificar("for-each recorre todos (2+4+9=15)", suma == 15);

        Iterator<Integer> it = n.iterator();
        it.next(); it.next(); it.next();
        verificarExcepcion("next() sin elementos", NoSuchElementException.class, it::next);
        verificar("dos iteradores son independientes", n.iterator().next() == 2);

        ListaSimplementeEnlazada<Integer> dup = new ListaSimplementeEnlazada<>();
        dup.agregar(7); dup.agregar(8); dup.agregar(7);
        verificar("eliminar duplicado borra solo la primera ocurrencia",
                dup.eliminar(7) && dup.toString().equals("[8 -> 7]"));
    }

    private static void pruebasPaquete() {
        seccion("Paquete, validaciones y comparadores");

        verificarExcepcion("peso 0", IllegalArgumentException.class, () -> new Paquete("PQ1", "Armenia", 0, 3, 10));
        verificarExcepcion("peso negativo", IllegalArgumentException.class, () -> new Paquete("PQ1", "Armenia", -2, 3, 10));
        verificarExcepcion("prioridad 0", IllegalArgumentException.class, () -> new Paquete("PQ1", "Armenia", 1, 0, 10));
        verificarExcepcion("prioridad 6", IllegalArgumentException.class, () -> new Paquete("PQ1", "Armenia", 1, 6, 10));
        verificarExcepcion("tiempo 0", IllegalArgumentException.class, () -> new Paquete("PQ1", "Armenia", 1, 3, 0));
        verificarExcepcion("destino null", IllegalArgumentException.class, () -> new Paquete("PQ1", null, 1, 3, 10));
        verificarExcepcion("destino vacío", IllegalArgumentException.class, () -> new Paquete("PQ1", "  ", 1, 3, 10));
        verificarExcepcion("código vacío", IllegalArgumentException.class, () -> new Paquete("", "Armenia", 1, 3, 10));

        Paquete a = new Paquete("pq105", "  la   TEBAIDA ", 1, 1, 1);
        verificar("normaliza código y municipio", a.getCodigo().equals("PQ105") && a.getDestino().equals("La Tebaida"));
        verificar("prioridades límite 1 y 5 válidas",
                new Paquete("X1", "A", 1, 1, 1).getPrioridad() == 1 && new Paquete("X2", "A", 1, 5, 1).getPrioridad() == 5);

        Paquete p1 = new Paquete("PQ200", "Armenia", 5, 3, 10);
        Paquete p2 = new Paquete("PQ200", "Salento", 9, 1, 99);
        verificar("equals/hashCode por código", p1.equals(p2) && p1.hashCode() == p2.hashCode());
        verificar("compareTo coherente con equals", p1.compareTo(p2) == 0);

        List<Paquete> lista = new ArrayList<>(Arrays.asList(
                new Paquete("PQ942", "A", 3.0, 4, 20), new Paquete("PQ105", "A", 9.0, 5, 25),
                new Paquete("PQ731", "A", 1.0, 5, 45), new Paquete("PQ420", "A", 5.0, 3, 30)));
        lista.sort(null);
        verificar("orden natural por código", codigos(lista).equals("PQ105,PQ420,PQ731,PQ942"));
        lista.sort(CriteriosPaquete.POR_PESO_DESC);
        verificar("peso desc", codigos(lista).equals("PQ105,PQ420,PQ942,PQ731"));
        lista.sort(CriteriosPaquete.POR_TIEMPO_ASC);
        verificar("tiempo asc", codigos(lista).equals("PQ942,PQ105,PQ420,PQ731"));
        lista.sort(CriteriosPaquete.DESPACHO);
        verificar("despacho (ejemplo enunciado)", codigos(lista).equals("PQ105,PQ731,PQ942,PQ420"));
        verificar("ordenar por peso no cambia el orden natural",
                lista.get(0).compareTo(lista.get(1)) < 0);
    }

    private static void pruebasCentro() {
        seccion("Centro de distribución");

        CentroDistribucion c = new CentroDistribucion();
        c.registrarPaquete("PQ731", "Armenia", 4.5, 5, 45);
        c.registrarPaquete("PQ105", "Salento", 12.0, 5, 25);
        c.registrarPaquete("PQ942", "Armenia", 3.2, 4, 20);
        c.registrarPaquete("PQ318", "Calarcá", 18.7, 2, 35);
        c.registrarPaquete("PQ567", "Salento", 7.4, 3, 50);
        c.registrarPaquete("PQ829", "armenia", 5.1, 1, 15);

        verificarExcepcion("código duplicado", IllegalArgumentException.class,
                () -> c.registrarPaquete("pq731", "Pijao", 1, 1, 1));
        verificar("duplicado no altera el registro", c.cantidadPaquetes() == 6);
        verificar("buscar existente", c.buscarPaquete("PQ318").getPeso() == 18.7);
        verificar("buscar inexistente = null", c.buscarPaquete("PQ000") == null && !c.existePaquete("PQ000"));
        verificar("municipios sin repetidos (3)", c.getMunicipios().size() == 3);
        verificar("municipios ordenados", String.join(",", c.getMunicipiosOrdenados()).equals("Armenia,Calarcá,Salento"));
        verificar("agrupación Armenia", codigos(c.paquetesDeMunicipio("Armenia")).equals("PQ731,PQ942,PQ829"));
        verificar("municipio sin paquetes -> lista vacía", c.paquetesDeMunicipio("Pijao").isEmpty());
        verificar("orden FIFO = orden de registro",
                codigos(c.pendientesPorLlegada()).equals("PQ731,PQ105,PQ942,PQ318,PQ567,PQ829"));

        List<Paquete> porPeso = c.ordenadosPor(CriteriosPaquete.POR_PESO_DESC);
        verificar("reporte por peso", porPeso.get(0).getCodigo().equals("PQ318"));
        verificar("ordenar no altera el registro", c.getRegistro().get(0).getCodigo().equals("PQ731"));
        verificarExcepcion("el registro expuesto es de solo lectura", UnsupportedOperationException.class,
                () -> c.getRegistro().clear());

        c.registrarRepartidor(new Repartidor("R1", "Ana", "Armenia", true));
        c.registrarRepartidor(new Repartidor("R2", "Beto", "Salento", true));
        c.registrarRepartidor(new Repartidor("R3", "Caro", "Armenia", false));
        verificarExcepcion("repartidor duplicado", IllegalArgumentException.class,
                () -> c.registrarRepartidor(new Repartidor("R1", "X", "Armenia", true)));

        Paquete d1 = c.despacharPorPrioridad();
        verificar("prioridad despacha PQ105", d1.getCodigo().equals("PQ105") && d1.getEstado() == EstadoPaquete.ASIGNADO);
        verificar("PQ105 asignado a R2 (Salento)", c.repartidorAsignado("PQ105").getIdentificacion().equals("R2"));
        verificar("R2 queda no disponible", !c.buscarRepartidor("R2").isDisponible());
        verificar("FIFO salta PQ105 (eliminación perezosa)",
                codigos(c.pendientesPorLlegada()).equals("PQ731,PQ942,PQ318,PQ567,PQ829"));

        verificarExcepcion("no disponible no recibe asignación", IllegalStateException.class,
                () -> c.asignarPaquete("PQ942", "R3"));
        verificarExcepcion("zona distinta al destino", IllegalStateException.class,
                () -> c.asignarPaquete("PQ318", "R1"));
        verificarExcepcion("entregar paquete pendiente", IllegalStateException.class,
                () -> c.registrarEntrega("PQ942"));

        c.registrarEntrega("PQ105");
        verificar("entrega libera al repartidor", c.buscarRepartidor("R2").isDisponible());
        verificarExcepcion("entregar dos veces", IllegalStateException.class, () -> c.registrarEntrega("PQ105"));

        c.asignarPaquete("PQ942", "R1");
        verificarExcepcion("asignar paquete ya asignado", IllegalStateException.class,
                () -> c.asignarPaquete("PQ942", "R1"));
        c.registrarEntrega("PQ942");

        Paquete d3 = c.despacharPorLlegada();
        verificar("llegada despacha PQ731 (primer pendiente)", d3.getCodigo().equals("PQ731"));
        c.registrarEntrega("PQ731");
        verificar("historial PQ105 -> PQ942 -> PQ731", codigos(c.getHistorialEntregas()).equals("PQ105,PQ942,PQ731"));
        verificar("historial obtener(2)", c.getHistorialEntregas().obtener(2).getCodigo().equals("PQ731"));
        verificar("siguiente por prioridad salta entregados", c.siguientePorPrioridad().getCodigo().equals("PQ567"));

        Paquete antes = c.siguientePorLlegada();
        verificarExcepcion("sin repartidor en Calarcá", IllegalStateException.class, c::despacharPorLlegada);
        verificar("el paquete conserva su turno", c.siguientePorLlegada() == antes
                && antes.getEstado() == EstadoPaquete.PENDIENTE);

        verificarExcepcion("activar repartidor ocupado", IllegalStateException.class, () -> {
            c.asignarPaquete("PQ829", "R1");
            c.cambiarDisponibilidad("R1", true);
        });

        CentroDistribucion full = DatosIniciales.crearCentro();
        verificar("30 paquetes", full.cantidadPaquetes() == 30);
        verificar(">= 5 destinos", full.getMunicipios().size() >= 5);
        verificar(">= 6 repartidores", full.getRepartidores().size() >= 6);
        verificar("orden de despacho tiene 30", full.ordenDeDespachoPrioritario().size() == 30);
        verificar("ordenDeDespacho no consume la cola", full.siguientePorPrioridad().getCodigo().equals("PQ105"));
        verificar("cada destino tiene al menos un repartidor disponible", cadaDestinoTieneRepartidor(full));

        int despachados = 0;
        while (full.siguientePorPrioridad() != null) {
            Paquete p = full.despacharPorPrioridad();
            full.registrarEntrega(p.getCodigo());
            despachados++;
        }
        verificar("jornada completa: 30 entregas en historial",
                despachados == 30 && full.getHistorialEntregas().tamanio() == 30);
        verificar("sin pendientes al final", full.siguientePorLlegada() == null && full.despacharPorLlegada() == null);
    }

    private static void pruebasRecursividad() {
        seccion("Recursividad");

        List<Paquete> muestra = Arrays.asList(
                new Paquete("PQ101", "Armenia", 4.5, 3, 20),
                new Paquete("PQ203", "Salento", 3.0, 3, 20),
                new Paquete("PQ415", "Armenia", 7.2, 3, 20),
                new Paquete("PQ730", "Armenia", 2.3, 3, 20));
        verificar("peso Armenia = 14.0", cerca(Recursividad.pesoTotalPorDestino(muestra, "Armenia"), 14.0));
        verificar("peso Salento = 3.0", cerca(Recursividad.pesoTotalPorDestino(muestra, "salento"), 3.0));
        verificar("peso municipio inexistente = 0", cerca(Recursividad.pesoTotalPorDestino(muestra, "Pijao"), 0.0));
        verificar("peso lista vacía = 0 (caso base inmediato)",
                cerca(Recursividad.pesoTotalPorDestino(new ArrayList<>(), "Armenia"), 0.0));

        List<Paquete> prio = new ArrayList<>();
        int[] valores = {5, 2, 4, 1, 5, 3, 4};
        for (int i = 0; i < valores.length; i++) prio.add(new Paquete("PX" + i, "A", 1, valores[i], 1));
        verificar("prioridad >= 4 -> 4", Recursividad.contarPrioridadMinima(prio, 4) == 4);
        verificar("prioridad >= 1 -> todos (7)", Recursividad.contarPrioridadMinima(prio, 1) == 7);
        verificar("prioridad >= 6 -> 0", Recursividad.contarPrioridadMinima(prio, 6) == 0);

        CentroDistribucion full = DatosIniciales.crearCentro();
        verificar("Armenia en 30 paquetes = 49.5", cerca(full.pesoTotalPorDestino("Armenia"), 49.5));
        verificar("prioridad >= 4 en 30 paquetes = 13", full.contarPrioridadMinima(4) == 13);
    }

    private static void pruebasBusquedaBinaria() {
        seccion("Búsqueda binaria");

        String[] cods = {"PQ105", "PQ214", "PQ318", "PQ421", "PQ567", "PQ731", "PQ842", "PQ942"};
        List<Paquete> ord = new ArrayList<>();
        for (String c : cods) ord.add(new Paquete(c, "A", 1, 1, 1));

        boolean todos = true;
        for (int i = 0; i < cods.length; i++) {
            todos &= BusquedaBinaria.buscarPorCodigo(ord, cods[i]) == i
                    && BusquedaBinaria.buscarPorCodigoIterativo(ord, cods[i]) == i;
        }
        verificar("encuentra cada código en su posición (recursiva e iterativa)", todos);
        verificar("PQ731 -> posición 5", BusquedaBinaria.buscarPorCodigo(ord, "PQ731") == 5);
        verificar("menor que todos -> -1", BusquedaBinaria.buscarPorCodigo(ord, "PQ001") == -1);
        verificar("mayor que todos -> -1", BusquedaBinaria.buscarPorCodigo(ord, "PQ999") == -1);
        verificar("intermedio inexistente -> -1", BusquedaBinaria.buscarPorCodigo(ord, "PQ500") == -1);
        verificar("lista vacía -> -1", BusquedaBinaria.buscarPorCodigo(new ArrayList<>(), "PQ105") == -1);
        verificar("acepta minúsculas", BusquedaBinaria.buscarPorCodigo(ord, "pq942") == 7);

        CentroDistribucion full = DatosIniciales.crearCentro();
        List<Paquete> registro = full.getRegistro();
        verificar("precondición: el registro NO está ordenado", !BusquedaBinaria.estaOrdenadaPorCodigo(registro));
        verificar("en la copia ordenada sí se cumple", BusquedaBinaria.estaOrdenadaPorCodigo(full.ordenadosPorCodigo()));
        boolean encuentraTodos = true;
        for (Paquete p : registro) {
            encuentraTodos &= full.buscarBinariaPorCodigo(p.getCodigo()) == p;
        }
        verificar("encuentra los 30 paquetes del centro", encuentraTodos);
    }

    private static void pruebasDivideYVenceras() {
        seccion("Divide y vencerás");

        List<Paquete> p = new ArrayList<>();
        double[] pesos = {4.5, 12.0, 3.2, 18.7, 7.4, 5.1};
        for (int i = 0; i < pesos.length; i++) p.add(new Paquete("PW" + i, "A", pesos[i], 1, 1));
        verificar("ejemplo enunciado = 18.7", cerca(DivideYVenceras.paqueteMasPesado(p).getPeso(), 18.7));
        verificar("un solo paquete (caso base)",
                DivideYVenceras.paqueteMasPesado(p.subList(2, 3)).getCodigo().equals("PW2"));
        verificar("máximo en el primer lugar",
                DivideYVenceras.paqueteMasPesado(p.subList(3, 6)).getCodigo().equals("PW3"));
        verificarExcepcion("lista vacía", IllegalArgumentException.class,
                () -> DivideYVenceras.paqueteMasPesado(new ArrayList<>()));

        Random rnd = new Random(42);
        boolean coincide = true;
        for (int prueba = 0; prueba < 500; prueba++) {
            int n = 1 + rnd.nextInt(40);
            List<Paquete> lista = new ArrayList<>();
            double max = 0;
            for (int i = 0; i < n; i++) {
                double w = 0.1 + rnd.nextInt(500) / 10.0;
                max = Math.max(max, w);
                lista.add(new Paquete("R" + i, "A", w, 1, 1));
            }
            coincide &= cerca(DivideYVenceras.paqueteMasPesado(lista).getPeso(), max);
        }
        verificar("500 listas aleatorias: coincide con el máximo lineal", coincide);

        CentroDistribucion full = DatosIniciales.crearCentro();
        verificar("más pesado de los 30 = PQ318", full.paqueteMasPesado().getCodigo().equals("PQ318"));
    }

    private static boolean cadaDestinoTieneRepartidor(CentroDistribucion c) {
        for (String m : c.getMunicipios()) {
            boolean hay = false;
            for (Repartidor r : c.getRepartidores()) {
                if (r.getZona().equals(m) && r.isDisponible()) hay = true;
            }
            if (!hay) return false;
        }
        return true;
    }

    private static String codigos(Iterable<Paquete> paquetes) {
        StringBuilder sb = new StringBuilder();
        for (Paquete p : paquetes) {
            if (sb.length() > 0) sb.append(',');
            sb.append(p.getCodigo());
        }
        return sb.toString();
    }

    private static boolean cerca(double a, double b) {
        return Math.abs(a - b) < 1e-9;
    }

    private static void seccion(String nombre) {
        System.out.println();
        System.out.println("--- " + nombre + " ---");
    }

    private static void verificar(String descripcion, boolean condicion) {
        if (condicion) {
            exitosas++;
            System.out.println("  OK    " + descripcion);
        } else {
            fallidas++;
            System.out.println("  FALLA " + descripcion);
        }
    }

    private static void verificarExcepcion(String descripcion, Class<? extends Throwable> esperada, Runnable accion) {
        try {
            accion.run();
            verificar(descripcion + " (debía lanzar " + esperada.getSimpleName() + ")", false);
        } catch (Throwable t) {
            verificar(descripcion + " -> " + t.getClass().getSimpleName(), esperada.isInstance(t));
        }
    }
}
