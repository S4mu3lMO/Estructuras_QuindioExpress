package quindioexpress.app;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import quindioexpress.algoritmos.BusquedaBinaria;
import quindioexpress.algoritmos.DivideYVenceras;
import quindioexpress.algoritmos.Recursividad;
import quindioexpress.estructuras.Coleccion;
import quindioexpress.estructuras.ListaSimplementeEnlazada;
import quindioexpress.modelo.CriteriosPaquete;
import quindioexpress.modelo.Paquete;
import quindioexpress.modelo.Repartidor;
import quindioexpress.servicio.CentroDistribucion;

public final class Demostracion {
    private Demostracion() { }

    public static void ejecutar() {
        CentroDistribucion centro = DatosIniciales.crearCentro();

        titulo("1. DATOS INICIALES");
        System.out.println("Paquetes registrados: " + centro.cantidadPaquetes());
        System.out.println("Repartidores: " + centro.getRepartidores().size());
        imprimirCodigos("Orden de registro", centro.getRegistro());

        titulo("2. VALIDACIONES (reglas del negocio)");
        intentar("Código duplicado PQ731", () -> centro.registrarPaquete("PQ731", "Pijao", 2.0, 3, 20));
        intentar("Peso 0", () -> centro.registrarPaquete("PQ999", "Pijao", 0, 3, 20));
        intentar("Prioridad 7", () -> centro.registrarPaquete("PQ999", "Pijao", 2.0, 7, 20));
        intentar("Tiempo -5", () -> centro.registrarPaquete("PQ999", "Pijao", 2.0, 3, -5));
        intentar("Destino vacío", () -> centro.registrarPaquete("PQ999", "  ", 2.0, 3, 20));
        intentar("Repartidor duplicado R001",
                () -> centro.registrarRepartidor(new Repartidor("R001", "Otro", "Pijao", true)));

        titulo("3. CONSULTA POR CÓDIGO (HashMap, O(1) esperado)");
        System.out.println("¿Existe PQ318? " + centro.existePaquete("PQ318") + " -> " + centro.buscarPaquete("PQ318"));
        System.out.println("¿Existe PQ000? " + centro.existePaquete("PQ000") + " -> " + centro.buscarPaquete("PQ000"));

        titulo("4. MUNICIPIOS");
        System.out.println("Sin repetidos (HashSet):   " + centro.getMunicipios());
        System.out.println("Ordenados (TreeSet):       " + centro.getMunicipiosOrdenados());

        titulo("5. PAQUETES POR MUNICIPIO (Map<String, List<Paquete>>)");
        for (String municipio : centro.getMunicipiosOrdenados()) {
            imprimirCodigos(municipio, centro.paquetesDeMunicipio(municipio));
        }

        titulo("6. ATENCIÓN POR ORDEN DE LLEGADA (Queue / ArrayDeque)");
        imprimirCodigos("Pendientes FIFO", centro.pendientesPorLlegada());
        System.out.println("Siguiente por llegada: " + centro.siguientePorLlegada().getCodigo());

        titulo("7. DESPACHO PRIORITARIO (PriorityQueue + Comparator)");
        ejemploEnunciadoPrioridad();
        imprimirCodigos("Orden de despacho (30 paquetes)", centro.ordenDeDespachoPrioritario());
        System.out.println("Desempates: PQ105 y PQ918 (P5, 25 min) -> PQ105 por código;"
                + " PQ603 y PQ942 (P4, 20 min) -> PQ603 por código.");

        titulo("8. ORDEN NATURAL (Comparable) Y CRITERIOS (Comparator)");
        imprimirCodigos("Por código (natural)", centro.ordenadosPorCodigo());
        imprimirDetalle("Por prioridad desc (primeros 6)",
                centro.ordenadosPor(CriteriosPaquete.POR_PRIORIDAD_DESC).subList(0, 6));
        imprimirDetalle("Por peso desc (primeros 6)",
                centro.ordenadosPor(CriteriosPaquete.POR_PESO_DESC).subList(0, 6));
        imprimirDetalle("Por tiempo asc (primeros 6)",
                centro.ordenadosPor(CriteriosPaquete.POR_TIEMPO_ASC).subList(0, 6));
        imprimirCodigos("Registro intacto después de ordenar", centro.getRegistro());

        titulo("9. ASIGNACIÓN A REPARTIDORES Y ENTREGAS");
        Paquete p1 = centro.despacharPorPrioridad();
        System.out.println("Despachado por prioridad: " + p1.getCodigo() + " -> " + centro.repartidorAsignado(p1.getCodigo()));
        centro.registrarEntrega("PQ105");
        System.out.println("Entregado PQ105");

        intentar("Asignar PQ942 a R002 (no disponible)", () -> centro.asignarPaquete("PQ942", "R002"));
        intentar("Asignar PQ942 a R003 (zona Calarcá)", () -> centro.asignarPaquete("PQ942", "R003"));
        centro.asignarPaquete("PQ942", "R001");
        System.out.println("PQ942 asignado a R001");
        intentar("Asignar PQ731 a R001 (ocupado)", () -> centro.asignarPaquete("PQ731", "R001"));
        intentar("Entregar PQ731 sin asignar", () -> centro.registrarEntrega("PQ731"));
        centro.registrarEntrega("PQ942");
        System.out.println("Entregado PQ942");

        Paquete p3 = centro.despacharPorLlegada();
        System.out.println("Despachado por llegada: " + p3.getCodigo() + " -> " + centro.repartidorAsignado(p3.getCodigo()));
        centro.registrarEntrega("PQ731");
        System.out.println("Entregado PQ731");

        System.out.println("Siguiente por llegada ahora:   " + centro.siguientePorLlegada().getCodigo()
                + "  (PQ731 y PQ105 ya salieron)");
        System.out.println("Siguiente por prioridad ahora: " + centro.siguientePorPrioridad().getCodigo());

        titulo("10. HISTORIAL DE ENTREGAS (lista simplemente enlazada propia)");
        Coleccion<Paquete> historial = centro.getHistorialEntregas();
        StringBuilder sb = new StringBuilder();
        for (Paquete p : historial) {
            if (sb.length() > 0) sb.append(" -> ");
            sb.append(p.getCodigo());
        }
        System.out.println("Historial: " + sb + "   (tamaño " + historial.tamanio() + ")");
        System.out.println("historial.obtener(1) = " + historial.obtener(1).getCodigo());

        titulo("11. LISTA PROPIA GENÉRICA: otros tipos y casos borde");
        demoListaGenerica();

        titulo("12. RECURSIVIDAD: peso total por destino");
        List<Paquete> muestra = Arrays.asList(
                new Paquete("PQ101", "Armenia", 4.5, 3, 20),
                new Paquete("PQ203", "Salento", 3.0, 3, 20),
                new Paquete("PQ415", "Armenia", 7.2, 3, 20),
                new Paquete("PQ730", "Armenia", 2.3, 3, 20));
        double total = Recursividad.pesoTotalPorDestino(muestra, "Armenia");
        System.out.printf("Peso total Armenia (ejemplo enunciado) = %.1f kg%n", total);
        System.out.printf("Peso total Armenia (30 paquetes)       = %.1f kg%n",
                centro.pesoTotalPorDestino("Armenia"));

        titulo("13. RECURSIVIDAD: paquetes con prioridad >= mínimo");
        List<Paquete> prioridades = crearConPrioridades(5, 2, 4, 1, 5, 3, 4);
        int cuantos = Recursividad.contarPrioridadMinima(prioridades, 4);
        System.out.println("Prioridades 5,2,4,1,5,3,4 con mínimo 4 -> " + cuantos + " paquetes");
        System.out.println("En los 30 paquetes, prioridad >= 4 -> " + centro.contarPrioridadMinima(4));

        titulo("14. BÚSQUEDA BINARIA (precondición: ordenado por código)");
        List<Paquete> reporte = crearConCodigos("PQ105", "PQ214", "PQ318", "PQ421", "PQ567", "PQ731", "PQ842", "PQ942");
        System.out.println("¿Ordenado por código? " + BusquedaBinaria.estaOrdenadaPorCodigo(reporte));
        int pos = BusquedaBinaria.buscarPorCodigo(reporte, "PQ731");
        System.out.println("PQ731 encontrado en posición " + pos);
        int noEsta = BusquedaBinaria.buscarPorCodigo(reporte, "PQ500");
        System.out.println("PQ500 -> " + noEsta);
        System.out.println("Sobre los 30 paquetes: " + centro.buscarBinariaPorCodigo("PQ661"));

        titulo("15. DIVIDE Y VENCERÁS: paquete de mayor peso");
        List<Paquete> pesos = crearConPesos(4.5, 12.0, 3.2, 18.7, 7.4, 5.1);
        Paquete masPesado = DivideYVenceras.paqueteMasPesado(pesos);
        System.out.println("Mayor peso del ejemplo: " + masPesado.getPeso() + " kg");
        System.out.println("Mayor peso de los 30: " + centro.paqueteMasPesado());

        titulo("FIN DE LA DEMOSTRACIÓN");
    }

    private static void ejemploEnunciadoPrioridad() {
        CentroDistribucion ejemplo = new CentroDistribucion();
        ejemplo.registrarPaquete("PQ420", "Armenia", 1.0, 3, 30);
        ejemplo.registrarPaquete("PQ731", "Armenia", 1.0, 5, 45);
        ejemplo.registrarPaquete("PQ105", "Armenia", 1.0, 5, 25);
        ejemplo.registrarPaquete("PQ942", "Armenia", 1.0, 4, 20);
        imprimirCodigos("Ejemplo del enunciado", ejemplo.ordenDeDespachoPrioritario());
    }

    private static void demoListaGenerica() {
        ListaSimplementeEnlazada<String> nombres = new ListaSimplementeEnlazada<>();
        System.out.println("Vacía: " + nombres + " estaVacia=" + nombres.estaVacia()
                + " eliminar(\"X\")=" + nombres.eliminar("X"));
        nombres.agregar("Ana");
        System.out.println("Un elemento: " + nombres + " -> eliminar(\"Ana\")=" + nombres.eliminar("Ana")
                + " -> " + nombres + " tamaño=" + nombres.tamanio());

        ListaSimplementeEnlazada<Integer> numeros = new ListaSimplementeEnlazada<>();
        for (int i = 1; i <= 5; i++) numeros.agregar(i * 10);
        System.out.println("Integer: " + numeros);
        numeros.eliminar(10);
        System.out.println("Eliminar primero (10): " + numeros);
        numeros.eliminar(30);
        System.out.println("Eliminar intermedio (30): " + numeros);
        numeros.eliminar(50);
        System.out.println("Eliminar último (50): " + numeros);
        System.out.println("Eliminar inexistente (99): " + numeros.eliminar(99) + " -> " + numeros);
        numeros.agregar(60);
        System.out.println("Agregar después de eliminar el último (60): " + numeros
                + " tamaño=" + numeros.tamanio() + " obtener(2)=" + numeros.obtener(2));

        Iterator<Integer> it = numeros.iterator();
        StringBuilder sb = new StringBuilder("Recorrido con Iterator: ");
        while (it.hasNext()) sb.append(it.next()).append(' ');
        System.out.println(sb.toString().trim());
        intentar("obtener(10)", () -> numeros.obtener(10));
    }

    private static List<Paquete> crearConPrioridades(int... prioridades) {
        Paquete[] arr = new Paquete[prioridades.length];
        for (int i = 0; i < prioridades.length; i++) {
            arr[i] = new Paquete("PX" + (100 + i), "Armenia", 1.0, prioridades[i], 10);
        }
        return Arrays.asList(arr);
    }

    private static List<Paquete> crearConCodigos(String... codigos) {
        Paquete[] arr = new Paquete[codigos.length];
        for (int i = 0; i < codigos.length; i++) {
            arr[i] = new Paquete(codigos[i], "Armenia", 1.0, 3, 10);
        }
        return Arrays.asList(arr);
    }

    private static List<Paquete> crearConPesos(double... pesos) {
        Paquete[] arr = new Paquete[pesos.length];
        for (int i = 0; i < pesos.length; i++) {
            arr[i] = new Paquete("PW" + (100 + i), "Armenia", pesos[i], 3, 10);
        }
        return Arrays.asList(arr);
    }

    static void titulo(String texto) {
        System.out.println();
        System.out.println("==================================================================");
        System.out.println(" " + texto);
        System.out.println("==================================================================");
    }

    static void imprimirCodigos(String etiqueta, Iterable<Paquete> paquetes) {
        StringBuilder sb = new StringBuilder();
        for (Paquete p : paquetes) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(p.getCodigo());
        }
        System.out.println(etiqueta + ": " + sb);
    }

    static void imprimirDetalle(String etiqueta, List<Paquete> paquetes) {
        System.out.println(etiqueta + ":");
        for (Paquete p : paquetes) {
            System.out.println("   " + p);
        }
    }

    static void intentar(String descripcion, Runnable accion) {
        try {
            accion.run();
            System.out.println("  [!] " + descripcion + ": NO lanzó error");
        } catch (RuntimeException e) {
            System.out.println("  [OK] " + descripcion + " rechazado: " + e.getMessage());
        }
    }
}
