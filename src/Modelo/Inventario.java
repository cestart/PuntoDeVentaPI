package Modelo;
import java.util.ArrayList;

public class Inventario {
    private ArrayList<Producto> productos;

    public Inventario() {
        productos = new ArrayList<>();
    }

    public void agregarProducto(Producto producto) {
        productos.add(producto);
    }

    public ArrayList<Producto> getProductos() {
        return productos;
    }

    public void verificarStock() {
        for (Producto p : productos) {
            if (p.getStock() < 5) {
                System.out.println("Alerta: Producto " + p.getNombre() + " tiene bajo stock.");
            }
        }
    }
}
