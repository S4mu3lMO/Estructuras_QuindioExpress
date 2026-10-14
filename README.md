# QuindíoExpress – Sistema de gestión de paquetes

Parcial 1 – Estructuras de Datos (2026-2).
Sistema en Java para registrar, consultar, clasificar, priorizar, asignar y entregar los paquetes de una jornada.

## Requisitos

- Java 17 o superior (probado con OpenJDK 21).
- No usa librerías externas, archivos ni bases de datos.

## Cómo compilar y ejecutar

### Linux / macOS / Git Bash
```bash
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out quindioexpress.app.Main            # menú interactivo
java -cp out quindioexpress.app.Main --demo     # demostración completa
java -cp out quindioexpress.pruebas.PruebasSistema   # 99 pruebas automáticas
```

### Windows (PowerShell)
```powershell
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp out quindioexpress.app.Main --demo
```
Si las tildes salen mal en la consola de Windows, ejecute antes `chcp 65001`
o agregue `-Dstdout.encoding=UTF-8` al comando `java`.

### IDE (IntelliJ / NetBeans / VS Code / Eclipse)
Marque `src` como carpeta de fuentes y ejecute `quindioexpress.app.Main`
(para la demo, agregue el argumento `--demo` en la configuración de ejecución).

## Estructura del proyecto

```
src/quindioexpress/
├── modelo/
│   ├── Paquete.java              Comparable<Paquete> (orden natural = código), validaciones
│   ├── Repartidor.java           identificación única, zona, disponibilidad
│   ├── EstadoPaquete.java        PENDIENTE -> ASIGNADO -> ENTREGADO
│   └── CriteriosPaquete.java     Comparator: prioridad desc, peso desc, tiempo asc, DESPACHO
├── estructuras/
│   ├── Coleccion.java            interfaz genérica (agregar, eliminar, obtener, tamanio, estaVacia) + Iterable
│   └── ListaSimplementeEnlazada.java   lista propia genérica con Nodo e Iterator internos
├── algoritmos/
│   ├── Recursividad.java         peso total por destino, conteo por prioridad mínima
│   ├── BusquedaBinaria.java      recursiva (+ iterativa como alternativa)
│   └── DivideYVenceras.java      paquete de mayor peso
├── servicio/
│   └── CentroDistribucion.java   lógica del negocio y todas las colecciones
├── app/
│   ├── Main.java                 menú interactivo (19 opciones)
│   ├── Demostracion.java         recorrido automático de todas las operaciones
│   └── DatosIniciales.java       30 paquetes, 7 destinos, 8 repartidores
└── pruebas/
    └── PruebasSistema.java       pruebas automáticas sin frameworks
```

## Estructuras utilizadas

| Necesidad | Estructura | Operación principal | Complejidad |
|---|---|---|---|
| Orden exacto de registro (auditoría) | `ArrayList<Paquete>` | `add` al final | O(1) amortizado |
| 1. Pendientes en orden de llegada | `Queue` → `ArrayDeque<Paquete>` | `offer` / `poll` | O(1) |
| 2. Consulta rápida por código | `HashMap<String, Paquete>` | `get` / `containsKey` | O(1) esperado |
| 3. Municipios sin duplicados | `HashSet<String>` | `add` | O(1) esperado |
| 4. Municipios ordenados sin duplicados | `TreeSet<String>` | `add` / recorrido | O(log m) / O(m) |
| Paquetes por municipio | `HashMap<String, List<Paquete>>` | `get` + `add` | O(1) esperado |
| 5. Despacho por prioridad | `PriorityQueue<Paquete>` con `CriteriosPaquete.DESPACHO` | `offer` / `poll` | O(log n) |
| 6. Historial de entregas | `ListaSimplementeEnlazada<Paquete>` (propia) | `agregar` al final | O(1) |
| 7. Código → Paquete | el mismo `HashMap<String, Paquete>` | `put` / `get` | O(1) esperado |
| Repartidores por identificación | `LinkedHashMap<String, Repartidor>` | `get` / `put` | O(1) esperado |

## Reglas implementadas

- Código de paquete e identificación de repartidor únicos.
- Peso > 0, prioridad entre 1 y 5, tiempo estimado > 0, destino no nulo ni vacío.
- Un repartidor no disponible no recibe asignaciones; su zona debe coincidir con el destino.
- Despacho prioritario: mayor prioridad → menor tiempo estimado → código alfabético.
- El historial conserva el orden real de entrega y usa la lista propia genérica, que se recorre con `Iterable`/`Iterator`.
- Las operaciones recursivas no usan ciclos. Divide y vencerás muestra sus etapas: caso base, división, subproblemas, combinación y resultado.
