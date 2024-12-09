package Modelo;
import java.util.HashMap;

public class Carrito {
    private HashMap<Producto, Integer> productos;

    public Carrito() {
        productos = new HashMap<>();
    }

    public void agregarProducto(Producto producto) {
        if (productos.containsKey(producto)) {
            productos.put(producto, productos.get(producto) + 1); 
        } else {
            productos.put(producto, 1); 
        }
    }

    public boolean eliminarProducto(Producto producto) {
        if (productos.containsKey(producto)) {
            int cantidad = productos.get(producto);
            if (cantidad > 1) {
                productos.put(producto, cantidad - 1);
            } else {
                productos.remove(producto); 
            }
            return true;
        }
        return false;
    }

    public HashMap<Producto, Integer> getProductos() {
        return productos;
    }

    public double calcularTotal() {
        double total = 0;
        for (Producto producto : productos.keySet()) {
            total += producto.getPrecio() * productos.get(producto);
        }
        return total;
    }

    public void vaciarCarrito() {
        productos.clear();
    }

    public double calcularTotalConIVA() {
        double total = 0;
        for (Producto producto : productos.keySet()) {
            total += producto.getPrecio() * productos.get(producto);
        }
        return total * 1.16; 
    }
}
