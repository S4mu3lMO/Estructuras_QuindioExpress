package quindioexpress.app;

import java.util.Scanner;

import quindioexpress.modelo.CriteriosPaquete;
import quindioexpress.modelo.Paquete;
import quindioexpress.modelo.Repartidor;
import quindioexpress.servicio.CentroDistribucion;

public class Main {
    private static final Scanner IN = new Scanner(System.in);

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--demo")) {
            Demostracion.ejecutar();
            return;
        }

        CentroDistribucion centro = DatosIniciales.crearCentro();
        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Opción: ");
            try {
                ejecutarOpcion(centro, opcion);
            } catch (RuntimeException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        } while (opcion != 0);
        System.out.println("Fin de la jornada.");
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("========== QuindíoExpress ==========");
        System.out.println(" 1. Demostración completa (datos nuevos)");
        System.out.println(" 2. Registrar paquete");
        System.out.println(" 3. Consultar paquete por código");
        System.out.println(" 4. Listar paquetes en orden de registro");
        System.out.println(" 5. Registrar repartidor");
        System.out.println(" 6. Listar repartidores");
        System.out.println(" 7. Cambiar disponibilidad de repartidor");
        System.out.println(" 8. Municipios (sin repetidos y ordenados)");
        System.out.println(" 9. Paquetes por municipio");
        System.out.println("10. Despachar siguiente por orden de llegada");
        System.out.println("11. Despachar siguiente por prioridad");
        System.out.println("12. Asignar paquete a repartidor (manual)");
        System.out.println("13. Registrar entrega");
        System.out.println("14. Ver historial de entregas");
        System.out.println("15. Reportes ordenados (código / prioridad / peso / tiempo)");
        System.out.println("16. Peso total por destino (recursivo)");
        System.out.println("17. Contar paquetes con prioridad mínima (recursivo)");
        System.out.println("18. Búsqueda binaria por código");
        System.out.println("19. Paquete de mayor peso (divide y vencerás)");
        System.out.println(" 0. Salir");
    }

    private static void ejecutarOpcion(CentroDistribucion c, int opcion) {
        switch (opcion) {
            case 1:
                Demostracion.ejecutar();
                break;
            case 2: {
                String codigo = leerTexto("Código: ");
                String destino = leerTexto("Destino: ");
                double peso = leerDecimal("Peso (kg): ");
                int prioridad = leerEntero("Prioridad (1-5): ");
                int tiempo = leerEntero("Tiempo estimado (min): ");
                Paquete p = c.registrarPaquete(codigo, destino, peso, prioridad, tiempo);
                System.out.println("Registrado: " + p);
                break;
            }
            case 3: {
                Paquete p = c.buscarPaquete(leerTexto("Código: "));
                System.out.println(p == null ? "No existe un paquete con ese código" : p);
                break;
            }
            case 4:
                for (Paquete p : c.getRegistro()) System.out.println("  " + p);
                break;
            case 5: {
                String id = leerTexto("Identificación: ");
                String nombre = leerTexto("Nombre: ");
                String zona = leerTexto("Zona: ");
                boolean disp = leerTexto("¿Disponible? (s/n): ").equalsIgnoreCase("s");
                c.registrarRepartidor(new Repartidor(id, nombre, zona, disp));
                System.out.println("Repartidor registrado");
                break;
            }
            case 6:
                for (Repartidor r : c.getRepartidores()) System.out.println("  " + r);
                break;
            case 7: {
                String id = leerTexto("Identificación: ");
                boolean disp = leerTexto("¿Disponible? (s/n): ").equalsIgnoreCase("s");
                c.cambiarDisponibilidad(id, disp);
                System.out.println("Actualizado: " + c.buscarRepartidor(id));
                break;
            }
            case 8:
                System.out.println("Sin repetidos: " + c.getMunicipios());
                System.out.println("Ordenados:     " + c.getMunicipiosOrdenados());
                break;
            case 9:
                for (String m : c.getMunicipiosOrdenados()) {
                    Demostracion.imprimirCodigos(m, c.paquetesDeMunicipio(m));
                }
                break;
            case 10: {
                Paquete p = c.despacharPorLlegada();
                System.out.println(p == null ? "No hay pendientes"
                        : "Despachado " + p.getCodigo() + " -> " + c.repartidorAsignado(p.getCodigo()));
                break;
            }
            case 11: {
                Paquete p = c.despacharPorPrioridad();
                System.out.println(p == null ? "No hay pendientes"
                        : "Despachado " + p.getCodigo() + " -> " + c.repartidorAsignado(p.getCodigo()));
                break;
            }
            case 12:
                c.asignarPaquete(leerTexto("Código del paquete: "), leerTexto("Identificación del repartidor: "));
                System.out.println("Asignado");
                break;
            case 13:
                c.registrarEntrega(leerTexto("Código entregado: "));
                System.out.println("Entrega registrada");
                break;
            case 14:
                Demostracion.imprimirCodigos("Historial", c.getHistorialEntregas());
                System.out.println("Total entregados: " + c.getHistorialEntregas().tamanio());
                break;
            case 15:
                Demostracion.imprimirDetalle("Por código", c.ordenadosPorCodigo());
                Demostracion.imprimirDetalle("Por prioridad (mayor a menor)", c.ordenadosPor(CriteriosPaquete.POR_PRIORIDAD_DESC));
                Demostracion.imprimirDetalle("Por peso (mayor a menor)", c.ordenadosPor(CriteriosPaquete.POR_PESO_DESC));
                Demostracion.imprimirDetalle("Por tiempo (menor a mayor)", c.ordenadosPor(CriteriosPaquete.POR_TIEMPO_ASC));
                break;
            case 16: {
                String destino = leerTexto("Municipio: ");
                System.out.printf("Peso total: %.1f kg%n", c.pesoTotalPorDestino(destino));
                break;
            }
            case 17: {
                int min = leerEntero("Prioridad mínima: ");
                System.out.println("Cantidad: " + c.contarPrioridadMinima(min) + " paquetes");
                break;
            }
            case 18: {
                String codigo = leerTexto("Código: ");
                Paquete p = c.buscarBinariaPorCodigo(codigo);
                System.out.println(p == null ? "No encontrado" : "Encontrado: " + p);
                break;
            }
            case 19: {
                System.out.println("Más pesado: " + c.paqueteMasPesado());
                break;
            }
            case 0:
                break;
            default:
                System.out.println("Opción no válida");
        }
    }

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return IN.hasNextLine() ? IN.nextLine().trim() : "";
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            String linea = leerTexto(mensaje);
            if (linea.isEmpty() && !IN.hasNextLine()) return 0;
            try {
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un número entero");
            }
        }
    }

    private static double leerDecimal(String mensaje) {
        while (true) {
            String linea = leerTexto(mensaje);
            if (linea.isEmpty() && !IN.hasNextLine()) return 0;
            try {
                return Double.parseDouble(linea.replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un número (ej: 4.5)");
            }
        }
    }
}
