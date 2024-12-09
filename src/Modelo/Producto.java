package Modelo;
import java.util.Objects;

public class Producto {
    private String id;
    private String nombre;
    private double precio;
    private int stock;
    private String categoria;

    public Producto(String id, String nombre, double precio, int stock, String categoria) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public int getStock() {
        return stock;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Producto producto = (Producto) obj;
        return Objects.equals(id, producto.id); 
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); 
    }

    @Override
    public String toString() {
        return "ID: " + id + " - " + nombre + " ($" + precio + ") - Categoria: " + categoria;
    }
}