package Modelo;

public class Venta {
    private String fecha;
    private String producto;
    private int cantidad;
    private double total;
    private double precio;
    public Venta(String fecha, String producto, int cantidad, double total) {
        this.fecha = fecha;
        this.producto = producto;
        this.cantidad = cantidad;
        this.total = total;
    }
    
    public String getFecha() {
		return fecha;
    }

    public String getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getTotal() {
        return total;
    }

    public double getPrecio() {
        if (cantidad != 0) {
            return total / cantidad;  
        } else {
            return 0.0;
        }
    }
    @Override
    public String toString() {
        return "Fecha: " + fecha + ", Producto: " + producto + ", Cantidad: " + cantidad + ", Total: " + total + ", Precio: " + precio;
    }
}
