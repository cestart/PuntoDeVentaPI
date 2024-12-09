package Controlador;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import Modelo.Ticket;
import Modelo.Venta;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class RegistroVentas {
    private ArrayList<Ticket> tickets;
    private static final String FILE_PATH = "registro_ventas.json";
    private ArrayList<Ticket> ticketsDevolucion;

    public RegistroVentas() {
        tickets = new ArrayList<>();
        ticketsDevolucion = new ArrayList<>();
        cargarTickets();
    }

    public ArrayList<Ticket> getTickets() {
        return tickets;
    }

    public void agregarTicket(Ticket ticket) {
        tickets.add(ticket);
        guardarTickets();
    }

    private void guardarTickets() {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            Gson gson = new Gson();
            gson.toJson(tickets, writer);
            System.out.println("Tickets guardados en JSON: " + tickets.size() + " tickets.");
        } catch (IOException e) {
            System.out.println("Error al guardar tickets en JSON: " + e.getMessage());
        }
    }

    private void cargarTickets() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try (Reader reader = new FileReader(FILE_PATH)) {
                Gson gson = new Gson();
                Type ticketListType = new TypeToken<ArrayList<Ticket>>() {}.getType();
                tickets = gson.fromJson(reader, ticketListType);
                
                if (tickets == null) {
                    tickets = new ArrayList<>();
                }

            } catch (IOException e) {
                System.out.println("Error al cargar tickets desde JSON: " + e.getMessage());
                tickets = new ArrayList<>();
            }
        } else {
            System.out.println("Archivo de registro no encontrado, se creará uno nuevo al guardar.");
            tickets = new ArrayList<>();
        }
    }
    
    public void devolverProducto(String idTicket, String producto, int cantidad, double total) {
        for (int i = 0; i < tickets.size(); i++) {
            Ticket ticket = tickets.get(i);
            if (ticket.getId().equals(idTicket)) {
                ArrayList<Venta> ventas = ticket.getVentas();
                Venta ventaADevolver = null;
                for (Venta venta : ventas) {
                    if (venta.getProducto().equals(producto)) {
                        ventaADevolver = venta;
                        break;
                    }
                }
                if (ventaADevolver != null) {
                    if (ventaADevolver.getCantidad() >= cantidad) {
                        int nuevaCantidad = ventaADevolver.getCantidad() - cantidad;
                        double nuevoTotal = ventaADevolver.getTotal() - total;
                        ventas.remove(ventaADevolver); 
                        ventas.add(new Venta(ventaADevolver.getFecha(), ventaADevolver.getProducto(), nuevaCantidad, nuevoTotal));
                        double nuevoTotalTicket = ticket.getTotal() - total;
                        Ticket nuevoTicket = new Ticket(ticket.getId(), ticket.getFecha(), nuevoTotalTicket, ticket.getIva(), ventas);
                        tickets.set(i, nuevoTicket);
                        guardarTickets();
                        JOptionPane.showMessageDialog(null, "Devolución realizada con éxito.");
                        return;  
                    } else {
                        JOptionPane.showMessageDialog(null, "La cantidad a devolver es mayor a la cantidad vendida.");
                        return;  
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "El producto no existe en el ticket.");
                    return;  
                }
            }
        }

        JOptionPane.showMessageDialog(null, "Ticket no encontrado.");
    }



    public void eliminarTicket(String id) {
        Ticket ticketAEliminar = null;
        for (Ticket ticket : tickets) {
            if (ticket.getId().equals(id)) {
                ticketAEliminar = ticket;
                break;
            }
        }
        if (ticketAEliminar != null) {
            tickets.remove(ticketAEliminar);
            guardarTickets(); 
            System.out.println("Ticket eliminado: " + ticketAEliminar.getId());
        }
    }

    public ArrayList<Ticket> getTicketsDevolucion() {
        return ticketsDevolucion;
    }

}
