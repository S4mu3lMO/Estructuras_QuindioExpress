package quindioexpress.modelo;

import java.util.Comparator;

public final class CriteriosPaquete {
    private CriteriosPaquete() { }

    public static final Comparator<Paquete> POR_PRIORIDAD_DESC =
            (a, b) -> Integer.compare(b.getPrioridad(), a.getPrioridad());

    public static final Comparator<Paquete> POR_PESO_DESC =
            (a, b) -> Double.compare(b.getPeso(), a.getPeso());

    public static final Comparator<Paquete> POR_TIEMPO_ASC =
            (a, b) -> Integer.compare(a.getTiempoEstimado(), b.getTiempoEstimado());

    public static final Comparator<Paquete> DESPACHO =
            POR_PRIORIDAD_DESC
                    .thenComparing(POR_TIEMPO_ASC)
                    .thenComparing(Comparator.naturalOrder());
}
