package Vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import Controlador.GeneradorPDF;
import Controlador.RegistroVentas;
import Modelo.Ticket;
import Modelo.Venta;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class VentanaRegistroVentas extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTable tablaTickets;
    private DefaultTableModel modeloTabla;
    private RegistroVentas registroVentas;
    private JTable tablaProductos;
    private DefaultTableModel modeloProductos;

    public VentanaRegistroVentas(RegistroVentas registroVentas) {
        this.registroVentas = registroVentas;
        setTitle("Registro de Ventas");
        setSize(800, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        modeloTabla = new DefaultTableModel(new Object[]{"ID", "Fecha", "Total", "IVA"}, 0) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        for (Ticket ticket : registroVentas.getTickets()) {
            modeloTabla.addRow(new Object[]{
                ticket.getId(),
                ticket.getFecha(),
                ticket.getTotal(),
                ticket.getIva()
            });
        }

        tablaTickets = new JTable(modeloTabla);
        JScrollPane scrollTickets = new JScrollPane(tablaTickets);
        add(scrollTickets, BorderLayout.NORTH);
        modeloProductos = new DefaultTableModel(new Object[]{"Producto", "Cantidad", "Total"}, 0) {
			@Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        
        
        tablaProductos = new JTable(modeloProductos);
        JScrollPane scrollProductos = new JScrollPane(tablaProductos);
        add(scrollProductos, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        JButton btnVerProductos = new JButton("Ver Productos");
        JButton btnDevolverProducto = new JButton("Devolucion Producto");
        JButton btnEliminarTicket = new JButton("Eliminar Ticket");
        JButton btnGenerarPDF = new JButton("Generar PDF");

        panelBotones.add(btnVerProductos);
        panelBotones.add(btnDevolverProducto);
        panelBotones.add(btnEliminarTicket);
        panelBotones.add(btnGenerarPDF);
        add(panelBotones, BorderLayout.SOUTH);

        btnVerProductos.addActionListener(e -> mostrarProductosEnTicketSeleccionado());
        btnDevolverProducto.addActionListener(e -> realizarDevolucionProducto());
        btnEliminarTicket.addActionListener(e -> eliminarTicket());
        btnGenerarPDF.addActionListener(e -> generarPDFTicket());
        
        setLocationRelativeTo(null);  
    }

    private void mostrarProductosEnTicketSeleccionado() {
        int selectedRow = tablaTickets.getSelectedRow();
        if (selectedRow != -1) {
            String idTicket = (String) modeloTabla.getValueAt(selectedRow, 0);
            for (Ticket ticket : registroVentas.getTickets()) {
                if (ticket.getId().equals(idTicket)) {
                    ArrayList<Venta> ventas = ticket.getVentas();
                    if (ventas == null || ventas.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "No hay productos en este ticket.");
                    } else {
                        modeloProductos.setRowCount(0);
                        for (Venta venta : ventas) {
                            modeloProductos.addRow(new Object[]{
                                venta.getProducto(),
                                venta.getCantidad(),
                                venta.getTotal()
                            });
                        }
                    }
                    return;
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un ticket para ver los productos.");
        }
    }

    private void realizarDevolucionProducto() {
        int selectedRow = tablaProductos.getSelectedRow();
        if (selectedRow != -1) {
            String producto = (String) modeloProductos.getValueAt(selectedRow, 0);
            int cantidad = (int) modeloProductos.getValueAt(selectedRow, 1);
            double total = (double) modeloProductos.getValueAt(selectedRow, 2);
            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de que desea devolver el producto: " + producto + "?",
                    "Confirmar Devolución", JOptionPane.YES_NO_OPTION);
            if (confirmacion == JOptionPane.YES_OPTION) {
                String idTicket = (String) modeloTabla.getValueAt(tablaTickets.getSelectedRow(), 0);
                registroVentas.devolverProducto(idTicket, producto, cantidad, total);
                mostrarProductosEnTicketSeleccionado(); 
                actualizarTablaTickets();

            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para devolver.");
        }
    }
    
    private void actualizarTablaTickets() {
        modeloTabla.setRowCount(0);
        for (Ticket ticket : registroVentas.getTickets()) {
            modeloTabla.addRow(new Object[]{
                ticket.getId(),
                ticket.getFecha(),
                ticket.getTotal(),
                ticket.getIva()
            });
        }
        modeloTabla.fireTableDataChanged();
    }


    private void eliminarTicket() {
        int selectedRow = tablaTickets.getSelectedRow();
        if (selectedRow != -1) {
            String idTicket = (String) modeloTabla.getValueAt(selectedRow, 0);
            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de que desea eliminar el ticket: " + idTicket + "?",
                    "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
            if (confirmacion == JOptionPane.YES_OPTION) {
                registroVentas.eliminarTicket(idTicket);
                JOptionPane.showMessageDialog(this, "Ticket eliminado con éxito.");
                modeloTabla.removeRow(selectedRow); 
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un ticket para eliminar.");
        }
    }

    private void generarPDFTicket() {
        int selectedRow = tablaTickets.getSelectedRow();
        if (selectedRow != -1) {
            String idTicket = (String) modeloTabla.getValueAt(selectedRow, 0);
            for (Ticket ticket : registroVentas.getTickets()) {
                if (ticket.getId().equals(idTicket)) {
                    String rutaPDF = "Ticket_" + ticket.getId() + ".pdf";
                    try {
                        GeneradorPDF.generarReporteTicket(ticket, rutaPDF);
                        JOptionPane.showMessageDialog(this, "PDF generado con éxito.");
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, "Error al generar PDF: " + e.getMessage());
                    }
                    return;
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un ticket para generar el PDF.");
        }
    }
}
