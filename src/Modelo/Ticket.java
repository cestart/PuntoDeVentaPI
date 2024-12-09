package Modelo;
import java.util.ArrayList;
import java.util.Date;

public class Ticket {
    private String id;
    private Date fecha;
    private double total;
    private double iva;
    private ArrayList<Venta> ventas; 
    public Ticket(String id, Date fecha, double total, double iva, ArrayList<Venta> ventas) {
        this.id = id;
        this.fecha = fecha;
        this.total = total;
        this.iva = iva;
        this.ventas = (ventas != null) ? ventas : new ArrayList<>(); 
    }

    public String getId() {
        return id;
    }
    public Date getFecha() {
        return fecha;
    }
    public double getTotal() {
        return total;
    }
    public double getIva() {
        return iva;
    }
    public ArrayList<Venta> getVentas() { 
        return ventas;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id='" + id + '\'' +
                ", fecha=" + fecha +
                ", total=" + total +
                ", iva=" + iva +
                ", ventas=" + ventas +
                '}';
    }
}
