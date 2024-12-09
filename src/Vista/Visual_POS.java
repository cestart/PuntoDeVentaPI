package Vista;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import ConexionDB.BaseDatosCaja;
import ConexionDB.BaseDatosProductos;
import ConexionDB.BaseDatosProveedores;
import ConexionDB.BaseDatosUsuarios;
import Controlador.AlertaStock;
import Controlador.GestionarUsuarios;
import Controlador.RegistroVentas;
import Modelo.Carrito;
import Modelo.Producto;
import Modelo.Proveedor;
import Modelo.Ticket;
import Modelo.Usuario;
import Modelo.Venta;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Visual_POS extends JFrame {

    private JTable tablaProductos;
    private DefaultTableModel modeloTablaProductos;
    private JList<String> listCarrito;
    private JLabel lblTotal;
    private Carrito carrito;
    private Usuario usuario;
    private BaseDatosProductos baseDatosProductos;
    private RegistroVentas registroVentas;
    private BaseDatosUsuarios baseDatosUsuarios;
    private AlertaStock alertaStock;
    private BaseDatosCaja baseDatosCaja;

    public Visual_POS(Usuario usuario, BaseDatosProductos baseDatosProductos, RegistroVentas registroVentas,
                      BaseDatosUsuarios baseDatosUsuarios, AlertaStock alertaStock, BaseDatosCaja baseDatosCaja) {
        this.usuario = usuario;
        this.carrito = new Carrito();
        this.baseDatosProductos = baseDatosProductos;
        this.registroVentas = registroVentas;
        this.baseDatosUsuarios = baseDatosUsuarios;
        this.alertaStock = alertaStock;
        this.baseDatosCaja = baseDatosCaja;

        setTitle("Sistema POS");
        setSize(960, 480);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        modeloTablaProductos = new DefaultTableModel(new Object[]{"ID", "Nombre", "Precio", "Categoría"}, 0);
        tablaProductos = new JTable(modeloTablaProductos);
        actualizarListaProductos();

        JScrollPane scrollProductos = new JScrollPane(tablaProductos);
        add(scrollProductos, BorderLayout.WEST);

        listCarrito = new JList<>(new DefaultListModel<>());
        listCarrito.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollCarrito = new JScrollPane(listCarrito);
        add(scrollCarrito, BorderLayout.CENTER);

        lblTotal = new JLabel("Total: $0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblTotal, BorderLayout.SOUTH);

        JButton btnCompras = new JButton("Realizar Compras");
        JPanel panelBotonesCarrito = new JPanel(new GridLayout(0, 1));
        JPanel panelBotones = new JPanel();
        JButton btnGestionarInventario = new JButton("Gestionar Inventario");
        JButton btnVerRegistro = new JButton("Registro de Ventas");
        JButton btnAgregar = new JButton("Agregar al Carrito");
        JButton btnAgregarPorId = new JButton("Agregar por ID");
        JButton btnEliminar = new JButton("Eliminar del Carrito");
        JButton btnVaciarCarrito = new JButton("Vaciar Carrito");
        JButton btnVerDineroCaja = new JButton("Ver Dinero en Caja");
        JButton btnFinalizar = new JButton("Finalizar Compra");
        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        JButton btnGestionarUsuarios = new JButton("Administrar Usuarios");

        panelBotones.add(btnCompras);
        panelBotones.add(btnGestionarInventario);
        panelBotonesCarrito.add(btnAgregar);
        panelBotonesCarrito.add(btnAgregarPorId);
        panelBotonesCarrito.add(btnEliminar);
        panelBotonesCarrito.add(btnVaciarCarrito);
        panelBotonesCarrito.add(btnVerDineroCaja);
        panelBotonesCarrito.add(btnFinalizar);

        if ("gerente".equals(usuario.getRol())) {
            panelBotones.add(btnVerRegistro);
            btnVerRegistro.addActionListener(e -> verRegistroVentas());
        }
        panelBotones.add(btnCerrarSesion);
        add(panelBotonesCarrito, BorderLayout.EAST);
        add(panelBotones, BorderLayout.NORTH);

        btnAgregar.addActionListener(e -> agregarAlCarrito());
        btnAgregarPorId.addActionListener(e -> agregarAlCarritoPorIdYCantidad());
        btnFinalizar.addActionListener(e -> finalizarCompra());
        btnCompras.addActionListener(e -> abrirVentanaCompras());
        btnGestionarInventario.addActionListener(e -> gestionarInventario());
        btnVaciarCarrito.addActionListener(e -> vaciarCarrito());
        btnEliminar.addActionListener(e -> eliminarProductoSeleccionadoDelCarrito());
        btnVerDineroCaja.addActionListener(e -> verDineroEnCaja());

        if ("gerente".equals(usuario.getRol())) {
            panelBotones.add(btnGestionarUsuarios);
            btnGestionarUsuarios.addActionListener(e -> {
                if (baseDatosUsuarios != null) {
                    GestionarUsuarios ventanaUsuarios = new GestionarUsuarios(baseDatosUsuarios, usuario);
                    ventanaUsuarios.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Base de datos de usuarios no disponible.");
                }
            });
        }
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        setLocationRelativeTo(null);
        actualizarTotal();
        alertaStock.verificarStock();
    }

    public void actualizarListaProductos() {
        modeloTablaProductos.setRowCount(0);
        for (Producto p : baseDatosProductos.obtenerProductos()) {
            modeloTablaProductos.addRow(new Object[]{p.getId(), p.getNombre(), p.getPrecio(), p.getCategoria()});
        }
    }

    private void cerrarSesion() {
        this.setVisible(false);
        Login login = new Login(baseDatosUsuarios, baseDatosProductos, registroVentas, alertaStock, baseDatosCaja);
        login.setVisible(true);
        this.dispose();
    }
    
    private void abrirVentanaCompras() {
        ArrayList<Proveedor> proveedores = BaseDatosProveedores.obtenerProveedores();
        
        ArrayList<String> nombresProveedores = new ArrayList<>();
        for (Proveedor proveedor : proveedores) {
            nombresProveedores.add(proveedor.getNombre());
        }        
        VentanaCompras ventanaCompras = new VentanaCompras(baseDatosProductos);
        ventanaCompras.setProveedores(nombresProveedores);
        ventanaCompras.setVisible(true);
    }


    private void finalizarCompra() {
        if (!carrito.getProductos().isEmpty()) {
            double totalConIVA = carrito.calcularTotalConIVA();
            double iva = totalConIVA * 0.16;
            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "El total con IVA es: $" + totalConIVA + "\n¿Confirmar compra?",
                    "Confirmación de Compra", JOptionPane.YES_NO_OPTION);
            if (confirmacion == JOptionPane.YES_OPTION) {
                String inputPago = JOptionPane.showInputDialog(this, "Ingrese el monto con el que pagará:");
                if (inputPago == null || inputPago.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Debe ingresar un monto.");
                    return;
                }

                double pago;
                try {
                    pago = Double.parseDouble(inputPago);
                    if (pago < totalConIVA) {
                        JOptionPane.showMessageDialog(this, "El monto ingresado es insuficiente.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Monto no válido.");
                    return;
                }

                double cambio = pago - totalConIVA;

                double dineroEnCajaActual = BaseDatosCaja.obtenerDineroEnCaja();
                double nuevoMontoCaja = dineroEnCajaActual + (pago - cambio);

                BaseDatosCaja.actualizarDineroEnCaja(nuevoMontoCaja);

                String id = UUID.randomUUID().toString();
                Date fechaActual = new Date();
                String fechaFormateada = fechaActual.toString();

                ArrayList<Venta> ventas = new ArrayList<>();
                for (Producto p : carrito.getProductos().keySet()) {
                    int cantidad = carrito.getProductos().get(p);
                    double totalProducto = p.getPrecio() * cantidad;
                    ventas.add(new Venta(fechaFormateada, p.getNombre(), cantidad, totalProducto));
                    p.setStock(p.getStock() - cantidad);
                    baseDatosProductos.actualizarProducto(p);
                }

                Ticket nuevoTicket = new Ticket(id, fechaActual, totalConIVA, iva, ventas);
                registroVentas.agregarTicket(nuevoTicket);

                carrito.vaciarCarrito();
                actualizarListaProductos();
                actualizarCarrito();
                actualizarTotal();

                JOptionPane.showMessageDialog(this, "Compra confirmada y finalizada!\n" +
                        "Cambio a devolver: $" + String.format("%.2f", cambio));
            }
        } else {
            JOptionPane.showMessageDialog(this, "El carrito está vacío.");
        }
    }


    private void verDineroEnCaja() {
        double dineroEnCaja = BaseDatosCaja.obtenerDineroEnCaja();
        JOptionPane.showMessageDialog(this, "Dinero disponible en caja: $" + dineroEnCaja);
    }

    private void agregarAlCarrito() {
        int selectedRow = tablaProductos.getSelectedRow();

        if (selectedRow != -1 && selectedRow < tablaProductos.getRowCount()) {
            String idProducto = (String) modeloTablaProductos.getValueAt(selectedRow, 0);
            Producto seleccionado = baseDatosProductos.obtenerProductoPorId(idProducto);

            if (seleccionado != null && seleccionado.getStock() > 0) {
                carrito.agregarProducto(seleccionado);
                seleccionado.setStock(seleccionado.getStock() - 1);
                baseDatosProductos.actualizarProducto(seleccionado);
                actualizarListaProductos();
                actualizarCarrito();
                actualizarTotal();

                if (selectedRow >= 0 && selectedRow < tablaProductos.getRowCount()) {
                    tablaProductos.setRowSelectionInterval(selectedRow, selectedRow);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Producto sin stock disponible.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Seleccione un producto para agregar al carrito.");
        }
    }

    private void agregarAlCarritoPorIdYCantidad() {
        String inputId = JOptionPane.showInputDialog(this, "Ingrese el ID del producto:");
        if (inputId == null || inputId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un ID.");
            return;
        }

        Producto productoSeleccionado = baseDatosProductos.obtenerProductoPorId(inputId);
        if (productoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Producto no encontrado.");
            return;
        }

        String inputCantidad = JOptionPane.showInputDialog(this, "Ingrese la cantidad:");
        if (inputCantidad == null || inputCantidad.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar una cantidad.");
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(inputCantidad);
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a cero.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Cantidad no válida.");
            return;
        }

        if (productoSeleccionado.getStock() < cantidad) {
            JOptionPane.showMessageDialog(this, "No hay suficiente stock para agregar esa cantidad.");
            return;
        }

        for (int i = 0; i < cantidad; i++) {
            carrito.agregarProducto(productoSeleccionado);
        }
        productoSeleccionado.setStock(productoSeleccionado.getStock() - cantidad);
        baseDatosProductos.actualizarProducto(productoSeleccionado);
        actualizarListaProductos();
        actualizarCarrito();
        actualizarTotal();
        JOptionPane.showMessageDialog(this, "Producto agregado al carrito.");
        int rowIndex = encontrarFilaPorId(productoSeleccionado.getId());
        if (rowIndex != -1) {
            tablaProductos.setRowSelectionInterval(rowIndex, rowIndex);
        } else {
            JOptionPane.showMessageDialog(this, "No se encontró el producto en la tabla.");
        }
    }

    private int encontrarFilaPorId(String id) {
        for (int i = 0; i < tablaProductos.getRowCount(); i++) {
            if (tablaProductos.getValueAt(i, 0).equals(id)) {
                return i;
            }
        }
        return -1;
    }

    private void gestionarInventario() {
        VisualInventario inventario = new VisualInventario(baseDatosProductos, this);
        inventario.setVisible(true);
    }

    private void verRegistroVentas() {
        VentanaRegistroVentas ventanaRegistro = new VentanaRegistroVentas(registroVentas);
        ventanaRegistro.setVisible(true);
    }

    private void vaciarCarrito() {
        if (!carrito.getProductos().isEmpty()) {
            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "¿Estás seguro de que deseas vaciar el carrito?",
                    "Confirmar Vaciar Carrito", JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                carrito.vaciarCarrito();
                actualizarListaProductos();
                actualizarCarrito();
                actualizarTotal();
                JOptionPane.showMessageDialog(this, "El carrito ha sido vaciado.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "El carrito ya está vacío.");
        }
    }

    private void eliminarProductoSeleccionadoDelCarrito() {
        int selectedIndex = listCarrito.getSelectedIndex();
        if (selectedIndex != -1) {
            String productoSeleccionadoString = listCarrito.getSelectedValue();
            Producto productoAEliminar = null;
            for (Producto producto : carrito.getProductos().keySet()) {
                if (productoSeleccionadoString.contains(producto.getNombre())) {
                    productoAEliminar = producto;
                    break;
                }
            }
            if (productoAEliminar != null) {
                carrito.eliminarProducto(productoAEliminar);
                productoAEliminar.setStock(productoAEliminar.getStock() + 1);
                baseDatosProductos.actualizarProducto(productoAEliminar);
                actualizarListaProductos();
                actualizarCarrito();
                actualizarTotal();
                int newSelectedIndex = selectedIndex;
                if (listCarrito.getModel().getSize() > 0) {
                    if (newSelectedIndex >= listCarrito.getModel().getSize()) {
                        newSelectedIndex = listCarrito.getModel().getSize() - 1;
                    }
                    listCarrito.setSelectedIndex(newSelectedIndex);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecciona un producto en el carrito para eliminarlo.");
        }
    }

    private void actualizarCarrito() {
        DefaultListModel<String> modeloCarrito = (DefaultListModel<String>) listCarrito.getModel();
        modeloCarrito.clear();

        for (Producto p : carrito.getProductos().keySet()) {
            int cantidad = carrito.getProductos().get(p);
            double precioTotalProducto = p.getPrecio() * cantidad;

            modeloCarrito.addElement(p.getNombre() + "- (" + cantidad + ") - $"
                    + String.format("%.2f", precioTotalProducto));
        }
    }

    private void actualizarTotal() {
        double total = carrito.calcularTotal();
        lblTotal.setText(String.format("Total: $%.2f", total));
    }
}
