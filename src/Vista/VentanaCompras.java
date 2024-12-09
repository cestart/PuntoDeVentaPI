package Vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import ConexionDB.BaseDatosProductos;
import ConexionDB.BaseDatosProveedores;
import ConexionDB.Conexion;
import Modelo.Compra;
import Modelo.Producto;
import Modelo.Proveedor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class VentanaCompras extends JFrame {

	private static final long serialVersionUID = 1L;
	private BaseDatosProductos baseDatosProductos;
    private ArrayList<String> proveedores;
    private ArrayList<Compra> comprasRealizadas;
    private JTable tablaProductos;
    private DefaultTableModel modeloTablaProductos;
    private TableRowSorter<DefaultTableModel> sorter;
    private JComboBox<String> comboBoxProveedores;
    private JComboBox<String> comboBoxMetodoPago;
    private JLabel lblTotal;
    private JTextField txtBusqueda;

    public VentanaCompras(BaseDatosProductos baseDatosProductos) {
        this.baseDatosProductos = baseDatosProductos;
        this.proveedores = new ArrayList<>();
        this.comprasRealizadas = new ArrayList<>();

        setTitle("Realizar Compras");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        modeloTablaProductos = new DefaultTableModel(new Object[]{"ID", "Nombre", "Stock"}, 0) {
            /**
			 * 
			 */
			private static final long serialVersionUID = -5138793704795227324L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaProductos = new JTable(modeloTablaProductos);
        sorter = new TableRowSorter<>(modeloTablaProductos);
        tablaProductos.setRowSorter(sorter);
        actualizarTablaProductos();

        JScrollPane scrollProductos = new JScrollPane(tablaProductos);
        add(scrollProductos, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new BorderLayout());
        lblTotal = new JLabel("");
        panelInferior.add(lblTotal, BorderLayout.WEST);

        JButton btnRealizarCompra = new JButton("Confirmar Compra");
        btnRealizarCompra.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarCompra();
            }
        });
        panelInferior.add(btnRealizarCompra, BorderLayout.EAST);

        JButton btnVerRegistroCompras = new JButton("Ver Registro de Compras");
        btnVerRegistroCompras.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarRegistroCompras();
            }
        });
        panelInferior.add(btnVerRegistroCompras, BorderLayout.CENTER);

        add(panelInferior, BorderLayout.SOUTH);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtBusqueda = new JTextField(20);
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> filtrarProductos(txtBusqueda.getText()));

        panelBusqueda.add(new JLabel("Buscar:"));
        panelBusqueda.add(txtBusqueda);
        panelBusqueda.add(btnBuscar);

        comboBoxProveedores = new JComboBox<>();
        comboBoxMetodoPago = new JComboBox<>(new String[]{"Efectivo", "Tarjeta"});
        panelSuperior.add(panelBusqueda, BorderLayout.WEST);
        panelSuperior.add(comboBoxProveedores, BorderLayout.CENTER);
        panelSuperior.add(comboBoxMetodoPago, BorderLayout.EAST);
        setLocationRelativeTo(null);
        add(panelSuperior, BorderLayout.NORTH);
        
        cargarProveedoresDesdeBaseDatos();
        cargarComprasDesdeBaseDeDatos(); 
    }

    private void cargarComprasDesdeBaseDeDatos() {
        String consulta = "SELECT producto_id, proveedor, metodo_pago, cantidad, fecha FROM Compras";

        try (Connection conexion = Conexion.conectarMySQL();
             PreparedStatement stmt = conexion.prepareStatement(consulta);
             ResultSet rs = stmt.executeQuery()) {

            comprasRealizadas.clear();

            while (rs.next()) {
                String productoId = rs.getString("producto_id");
                String proveedor = rs.getString("proveedor");
                String metodoPago = rs.getString("metodo_pago");
                int cantidad = rs.getInt("cantidad");
                Date fecha = rs.getDate("fecha");

                Producto producto = baseDatosProductos.obtenerProductoPorId(productoId);
                if (producto != null) {
                    Compra compra = new Compra(producto, proveedor, metodoPago, cantidad);
                    comprasRealizadas.add(compra);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar compras desde la base de datos: " + e.getMessage());
        }
    }

    
    private void cargarProveedoresDesdeBaseDatos() {
        ArrayList<Proveedor> listaProveedores = BaseDatosProveedores.obtenerProveedores();

        proveedores.clear();
        comboBoxProveedores.removeAllItems();

        for (Proveedor proveedor : listaProveedores) {
            proveedores.add(proveedor.getNombre());
            comboBoxProveedores.addItem(proveedor.getNombre());
        }
    }

    public void setProveedores(ArrayList<String> proveedores) {
        this.proveedores = proveedores;
        comboBoxProveedores.removeAllItems();
        for (String proveedor : proveedores) {
            comboBoxProveedores.addItem(proveedor);
        }
    }

    private void filtrarProductos(String criterio) {
        if (criterio.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + criterio));
        }
    }

    public void realizarCompra() {
        int selectedRow = tablaProductos.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = tablaProductos.convertRowIndexToModel(selectedRow);
            String idProducto = (String) modeloTablaProductos.getValueAt(modelRow, 0);
            Producto productoSeleccionado = baseDatosProductos.obtenerProductoPorId(idProducto);
            
            if (productoSeleccionado != null) {
                String proveedorSeleccionado = (String) comboBoxProveedores.getSelectedItem();
                String metodoPagoSeleccionado = (String) comboBoxMetodoPago.getSelectedItem();

                if (proveedorSeleccionado != null && metodoPagoSeleccionado != null) {
                    int cantidad = solicitarCantidad();
                    if (cantidad > 0) {
                        productoSeleccionado.setStock(productoSeleccionado.getStock() + cantidad);
                        baseDatosProductos.actualizarProducto(productoSeleccionado);
                        actualizarTablaProductos();
                        Compra compra = new Compra(productoSeleccionado, proveedorSeleccionado, metodoPagoSeleccionado, cantidad);
                        comprasRealizadas.add(compra);

                        registrarCompraEnBaseDeDatos(compra);

                        lblTotal.setText("Compra realizada con " + proveedorSeleccionado);
                        JOptionPane.showMessageDialog(this, "Compra completada.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Seleccione un proveedor y un método de pago.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla.");
        }
    }

    private void registrarCompraEnBaseDeDatos(Compra compra) {
        String consulta = "INSERT INTO Compras (producto_id, proveedor, metodo_pago, cantidad, fecha) VALUES (?, ?, ?, ?, ?)";
        Connection conexion = null;
        PreparedStatement stmt = null;

        try {
            conexion = Conexion.conectarMySQL();
            conexion.setAutoCommit(false);
            stmt = conexion.prepareStatement(consulta);
            stmt.setString(1, compra.getProducto().getId());
            stmt.setString(2, compra.getProveedor());
            stmt.setString(3, compra.getMetodoPago());
            stmt.setInt(4, compra.getCantidad());
            stmt.setDate(5, new java.sql.Date(new Date().getTime()));
            stmt.executeUpdate();
            conexion.commit();            
            System.out.println("Compra registrada exitosamente.");
        } catch (SQLException e) {
            if (conexion != null) {
                try {
                    conexion.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            System.err.println("Error al registrar la compra: " + e.getMessage());
        } finally {
            try {
                if (conexion != null) {
                    conexion.setAutoCommit(true);
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conexion != null) {
                    conexion.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void actualizarTablaProductos() {
        modeloTablaProductos.setRowCount(0);
        for (Producto producto : baseDatosProductos.obtenerProductos()) {
            modeloTablaProductos.addRow(new Object[]{producto.getId(), producto.getNombre(), producto.getStock()});
        }
    }

    private int solicitarCantidad() {
        String input = JOptionPane.showInputDialog(this, "Ingrese la cantidad a comprar:");
        if (input != null && !input.isEmpty()) {
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Cantidad inválida.");
            }
        }
        return 0;
    }

    private void mostrarRegistroCompras() {
        new RegistroCompras(comprasRealizadas).setVisible(true);
    }
}
