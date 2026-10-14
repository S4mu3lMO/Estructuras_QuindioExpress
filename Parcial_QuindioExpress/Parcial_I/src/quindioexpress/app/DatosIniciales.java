package quindioexpress.app;

import quindioexpress.modelo.Repartidor;
import quindioexpress.servicio.CentroDistribucion;

public final class DatosIniciales {
    private DatosIniciales() { }

    public static CentroDistribucion crearCentro() {
        CentroDistribucion centro = new CentroDistribucion();
        cargarPaquetes(centro);
        cargarRepartidores(centro);
        return centro;
    }

    private static void cargarPaquetes(CentroDistribucion c) {
        //                  código   destino        peso  prio  tiempo
        c.registrarPaquete("PQ731", "Armenia",      4.5,  5,    45);
        c.registrarPaquete("PQ105", "Salento",     12.0,  5,    25);
        c.registrarPaquete("PQ942", "Armenia",      3.2,  4,    20);
        c.registrarPaquete("PQ318", "Calarcá",     18.7,  2,    35);
        c.registrarPaquete("PQ567", "Salento",      7.4,  3,    50);
        c.registrarPaquete("PQ829", "Armenia",      5.1,  1,    15);
        c.registrarPaquete("PQ420", "Calarcá",      2.8,  3,    30);
        c.registrarPaquete("PQ214", "Montenegro",   6.3,  4,    40);
        c.registrarPaquete("PQ421", "Quimbaya",     1.9,  2,    55);
        c.registrarPaquete("PQ842", "Circasia",     9.6,  5,    60);
        c.registrarPaquete("PQ156", "Filandia",     3.7,  3,    70);
        c.registrarPaquete("PQ603", "Armenia",     11.2,  4,    20);
        c.registrarPaquete("PQ377", "Montenegro",   2.4,  1,    45);
        c.registrarPaquete("PQ918", "Quimbaya",     8.9,  5,    25);
        c.registrarPaquete("PQ264", "Calarcá",      4.1,  2,    30);
        c.registrarPaquete("PQ785", "Salento",      6.6,  4,    65);
        c.registrarPaquete("PQ039", "Armenia",      1.2,  3,    10);
        c.registrarPaquete("PQ451", "Circasia",    13.5,  2,    50);
        c.registrarPaquete("PQ690", "Filandia",     5.8,  1,    80);
        c.registrarPaquete("PQ512", "Montenegro",   7.7,  3,    35);
        c.registrarPaquete("PQ873", "Armenia",     10.4,  5,    30);
        c.registrarPaquete("PQ128", "Quimbaya",     2.2,  4,    45);
        c.registrarPaquete("PQ347", "Salento",     15.3,  2,    60);
        c.registrarPaquete("PQ996", "Calarcá",      3.9,  5,    40);
        c.registrarPaquete("PQ482", "Circasia",     0.8,  3,    25);
        c.registrarPaquete("PQ205", "Armenia",      6.9,  2,    15);
        c.registrarPaquete("PQ661", "Filandia",     4.4,  4,    55);
        c.registrarPaquete("PQ719", "Montenegro",   9.1,  1,    50);
        c.registrarPaquete("PQ087", "Quimbaya",     5.5,  3,    40);
        c.registrarPaquete("PQ354", "Armenia",      7.0,  4,    35);
    }

    private static void cargarRepartidores(CentroDistribucion c) {
        c.registrarRepartidor(new Repartidor("R001", "Carlos Gómez",    "Armenia",    true));
        c.registrarRepartidor(new Repartidor("R002", "Laura Restrepo",  "Armenia",    false));
        c.registrarRepartidor(new Repartidor("R003", "Andrés Ospina",   "Calarcá",    true));
        c.registrarRepartidor(new Repartidor("R004", "Diana Marín",     "Salento",    true));
        c.registrarRepartidor(new Repartidor("R005", "Jorge Castaño",   "Montenegro", true));
        c.registrarRepartidor(new Repartidor("R006", "Paula Giraldo",   "Quimbaya",   true));
        c.registrarRepartidor(new Repartidor("R007", "Miguel Arango",   "Circasia",   true));
        c.registrarRepartidor(new Repartidor("R008", "Sofía Londoño",   "Filandia",   true));
    }
}
