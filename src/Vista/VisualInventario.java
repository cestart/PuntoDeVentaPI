package Vista;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import ConexionDB.BaseDatosProductos;
import Controlador.GeneradorPDF;
import Modelo.Producto;

import java.awt.*;
import java.awt.event.ActionEvent;

public class VisualInventario extends JFrame {
    private static final long serialVersionUID = 1L;
    private BaseDatosProductos baseDatosProductos;
    private Visual_POS visualPOS;
    private JComboBox<String> categoriaComboBox;

    public VisualInventario(BaseDatosProductos baseDatosProductos, Visual_POS visualPOS) {
        this.baseDatosProductos = baseDatosProductos;
        this.visualPOS = visualPOS;

        setTitle("Gestionar Inventario");
        setSize(800, 400);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        String[] columnNames = {"ID", "Nombre", "Precio", "Stock", "Categoría"};
        
        DefaultTableModel modeloTabla = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        actualizarTabla(modeloTabla);

        JPanel panelBotonesArriba = new JPanel();
        panelBotonesArriba.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JButton btnAgregarProducto = new JButton("Agregar Producto");
        JButton btnModificarStock = new JButton("Modificar Stock");
        JButton btnModificarPrecio = new JButton("Modificar Precio");
        JButton btnEliminarProducto = new JButton("Eliminar Producto por ID");
        JButton btnAgregarCategoria = new JButton("Agregar Categoria");

        categoriaComboBox = new JComboBox<>(baseDatosProductos.getCategorias().toArray(new String[0]));

        btnAgregarProducto.addActionListener((ActionEvent e) -> {
            String nombre = JOptionPane.showInputDialog("Nombre del producto:");
            double precio = Double.parseDouble(JOptionPane.showInputDialog("Precio del producto:"));
            int stock = Integer.parseInt(JOptionPane.showInputDialog("Stock del producto:"));

            int seleccion = JOptionPane.showOptionDialog(null, categoriaComboBox, "Selecciona una categoría",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);

            if (seleccion != JOptionPane.CLOSED_OPTION) {
                String categoriaSeleccionada = (String) categoriaComboBox.getSelectedItem();
                String id = String.valueOf(baseDatosProductos.obtenerProductos().size() + 1);
                Producto nuevoProducto = new Producto(id, nombre, precio, stock, categoriaSeleccionada);
                baseDatosProductos.agregarProducto(nuevoProducto);

                actualizarTabla(modeloTabla);

                JOptionPane.showMessageDialog(null, "Producto agregado.");
            }
        });

        btnModificarStock.addActionListener((ActionEvent e) -> {
            String id = JOptionPane.showInputDialog("ID del producto a modificar:");
            for (Producto p : baseDatosProductos.obtenerProductos()) {
                if (p.getId().equals(id)) {
                    int nuevoStock = Integer.parseInt(JOptionPane.showInputDialog("Nuevo stock:"));
                    p.setStock(nuevoStock);
                    baseDatosProductos.actualizarProducto(p); 
                    JOptionPane.showMessageDialog(null, "Stock modificado.");
                    actualizarTabla(modeloTabla);
                    return;
                }
            }
            JOptionPane.showMessageDialog(null, "Producto no encontrado.");
        });

        btnModificarPrecio.addActionListener((ActionEvent e) -> {
            String id = JOptionPane.showInputDialog("ID del producto a modificar el precio:");
            for (Producto p : baseDatosProductos.obtenerProductos()) {
                if (p.getId().equals(id)) {
                    double nuevoPrecio = Double.parseDouble(JOptionPane.showInputDialog("Nuevo precio:"));
                    p.setPrecio(nuevoPrecio);
                    baseDatosProductos.actualizarProducto(p);
                    JOptionPane.showMessageDialog(null, "Precio modificado.");
                    actualizarTabla(modeloTabla);
                    return;
                }
            }
            JOptionPane.showMessageDialog(null, "Producto no encontrado.");
        });

        btnEliminarProducto.addActionListener((ActionEvent e) -> {
            String id = JOptionPane.showInputDialog("ID del producto a eliminar:");
            Producto productoAEliminar = null;
            for (Producto p : baseDatosProductos.obtenerProductos()) {
                if (p.getId().equals(id)) {
                    productoAEliminar = p;
                    break;
                }
            }

            if (productoAEliminar != null) {
                baseDatosProductos.eliminarProducto(productoAEliminar.getId()); 
                JOptionPane.showMessageDialog(null, "Producto eliminado.");
                actualizarTabla(modeloTabla);
            } else {
                JOptionPane.showMessageDialog(null, "Producto no encontrado.");
            }
        });
        
        btnAgregarCategoria.addActionListener((ActionEvent e) -> {
            String nuevaCategoria = JOptionPane.showInputDialog(this, "Ingrese el nombre de la nueva categoría:");
            if (nuevaCategoria != null && !nuevaCategoria.trim().isEmpty()) {
                nuevaCategoria = nuevaCategoria.trim(); 
                if (baseDatosProductos.getCategorias().contains(nuevaCategoria)) {
                    JOptionPane.showMessageDialog(this, "La categoría ya existe.");
                } else {
                    baseDatosProductos.agregarCategoria(nuevaCategoria);
                    categoriaComboBox.addItem(nuevaCategoria);
                    JOptionPane.showMessageDialog(this, "Categoría agregada con éxito.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "El nombre de la categoría no puede estar vacío.");
            }
        });



        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panelBotonesArriba.add(btnAgregarProducto, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 1;
        panelBotonesArriba.add(btnModificarStock, gbc);

        gbc.gridx = 3;
        panelBotonesArriba.add(btnModificarPrecio, gbc);

        gbc.gridx = 4;
        panelBotonesArriba.add(btnEliminarProducto, gbc);

        gbc.gridx = 5;
        panelBotonesArriba.add(btnAgregarCategoria, gbc);

        add(panelBotonesArriba, BorderLayout.NORTH);

        JPanel panelBotonesAbajo = new JPanel();
        JButton btnGenerarPDF = new JButton("Generar Reporte PDF");
        btnGenerarPDF.addActionListener((ActionEvent e) -> {
            try {
                GeneradorPDF.generarReporteInventario(baseDatosProductos.obtenerProductos(), "reporte_inventario.pdf");
                JOptionPane.showMessageDialog(null, "Reporte PDF generado correctamente.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error al generar el PDF: " + ex.getMessage());
            }
        });
        panelBotonesAbajo.add(btnGenerarPDF);
        setLocationRelativeTo(null);
        add(panelBotonesAbajo, BorderLayout.SOUTH);
    }

    private void actualizarTabla(DefaultTableModel modeloTabla) {
        modeloTabla.setRowCount(0);
        for (Producto p : baseDatosProductos.obtenerProductos()) {
            modeloTabla.addRow(new Object[]{p.getId(), p.getNombre(), p.getPrecio(), p.getStock(), p.getCategoria()});
        }
    }
    
    
}
