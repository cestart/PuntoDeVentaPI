package Modelo;

public class Compra {
    private Producto producto;
    private String proveedor;
    private String metodoPago;
    private int cantidad;
    private String fecha;
    public Compra(Producto producto, String proveedor, String metodoPago, int cantidad) {
        this.producto = producto;
        this.proveedor = proveedor;
        this.metodoPago = metodoPago;
        this.cantidad = cantidad;
        this.fecha = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date());
    }

    public Producto getProducto() {
        return producto;
    }
    public String getProveedor() {
        return proveedor;
    }
    public String getMetodoPago() {
        return metodoPago;
    }
    public int getCantidad() {
        return cantidad;
    }
    public String getFecha() {
        return fecha;
    }
    @Override
    public String toString() {
        return String.format("Fecha: %s\nProducto: %s\nProveedor: %s\nMétodo de Pago: %s\nCantidad: %d\n", 
                             fecha, producto.getNombre(), proveedor, metodoPago, cantidad);
    }
}
