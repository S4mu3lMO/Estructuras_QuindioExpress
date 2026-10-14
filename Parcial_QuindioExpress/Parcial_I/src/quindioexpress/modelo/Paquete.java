package quindioexpress.modelo;

import java.util.Locale;

public class Paquete implements Comparable<Paquete> {
    public static final int PRIORIDAD_MINIMA = 1;
    public static final int PRIORIDAD_MAXIMA = 5;

    private final String codigo;
    private final String destino;
    private final double peso;
    private final int prioridad;
    private final int tiempoEstimado;
    private EstadoPaquete estado;

    public Paquete(String codigo, String destino, double peso, int prioridad, int tiempoEstimado) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código no puede ser nulo ni vacío");
        }
        if (destino == null || destino.isBlank()) {
            throw new IllegalArgumentException("El destino no puede ser nulo ni vacío");
        }
        if (peso <= 0) {
            throw new IllegalArgumentException("El peso debe ser mayor que cero: " + peso);
        }
        if (prioridad < PRIORIDAD_MINIMA || prioridad > PRIORIDAD_MAXIMA) {
            throw new IllegalArgumentException("La prioridad debe estar entre 1 y 5: " + prioridad);
        }
        if (tiempoEstimado <= 0) {
            throw new IllegalArgumentException("El tiempo estimado debe ser mayor que cero: " + tiempoEstimado);
        }
        this.codigo = codigo.trim().toUpperCase();
        this.destino = normalizarMunicipio(destino);
        this.peso = peso;
        this.prioridad = prioridad;
        this.tiempoEstimado = tiempoEstimado;
        this.estado = EstadoPaquete.PENDIENTE;
    }

    public String getCodigo() { return codigo; }
    public String getDestino() { return destino; }
    public double getPeso() { return peso; }
    public int getPrioridad() { return prioridad; }
    public int getTiempoEstimado() { return tiempoEstimado; }
    public EstadoPaquete getEstado() { return estado; }

    public void setEstado(EstadoPaquete estado) { this.estado = estado; }

    public boolean estaPendiente() { return estado == EstadoPaquete.PENDIENTE; }

    public static String normalizarMunicipio(String texto) {
        String[] palabras = texto.trim().toLowerCase(Locale.ROOT).split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String palabra : palabras) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(Character.toUpperCase(palabra.charAt(0))).append(palabra.substring(1));
        }
        return sb.toString();
    }

    @Override
    public int compareTo(Paquete otro) {
        return this.codigo.compareTo(otro.codigo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Paquete)) return false;
        Paquete otro = (Paquete) o;
        return codigo.equals(otro.codigo);
    }

    @Override
    public int hashCode() {
        return codigo.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s[%s, %.1f kg, P%d, %d min, %s]",
                codigo, destino, peso, prioridad, tiempoEstimado, estado);
    }
}
