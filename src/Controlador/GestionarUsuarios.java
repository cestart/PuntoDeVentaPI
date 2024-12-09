package Controlador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import ConexionDB.BaseDatosUsuarios;
import Modelo.Usuario;

import java.awt.*;

public class GestionarUsuarios extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BaseDatosUsuarios baseDatosUsuarios;
    private Usuario usuarioActual;
    private DefaultTableModel model;

    public GestionarUsuarios(BaseDatosUsuarios baseDatosUsuarios, Usuario usuarioActual) {
        this.baseDatosUsuarios = baseDatosUsuarios;
        this.usuarioActual = usuarioActual;
        
        setTitle("Gestionar Usuarios");
        setSize(550, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        String[] columnNames = {"Nombre", "Rol"};
        model = new DefaultTableModel(columnNames, 0);

        for (Usuario usuario : baseDatosUsuarios.obtenerUsuarios()) {
            model.addRow(new Object[]{usuario.getNombre(), usuario.getRol()});
        }

        JTable table = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        JButton btnAgregar = new JButton("Agregar Usuario");
        JButton btnEliminar = new JButton("Eliminar Usuario");
        JButton btnModificar = new JButton("Modificar Usuario");

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnModificar);
        add(panelBotones, BorderLayout.SOUTH);

        btnAgregar.addActionListener(e -> agregarUsuario());
        btnEliminar.addActionListener(e -> eliminarUsuario(table));
        btnModificar.addActionListener(e -> modificarUsuario(table));
        setLocationRelativeTo(null);
    }

    private void agregarUsuario() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField txtNombre = new JTextField();
        JPasswordField txtContrasena = new JPasswordField();
        JComboBox<String> comboRol = new JComboBox<>(new String[]{"empleado", "gerente"});
   
        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Contraseña:"));
        panel.add(txtContrasena);
        panel.add(new JLabel("Rol:"));
        panel.add(comboRol);

        int result = JOptionPane.showConfirmDialog(this, panel, "Agregar Usuario", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String nombre = txtNombre.getText().trim();
            String contrasena = new String(txtContrasena.getPassword());
            String rol = (String) comboRol.getSelectedItem();

            if (!nombre.isEmpty() && !contrasena.isEmpty()) {
                Usuario nuevoUsuario = new Usuario(nombre, contrasena, rol);
                baseDatosUsuarios.agregarUsuario(nuevoUsuario);
                JOptionPane.showMessageDialog(this, "Usuario agregado exitosamente.");
                actualizarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
            }
        }
    }


    private void eliminarUsuario(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String nombre = (String) table.getValueAt(selectedRow, 0);

            if (nombre.equals(usuarioActual.getNombre())) {
                JOptionPane.showMessageDialog(this, "No puedes eliminar tu propio usuario.");
                return;
            }

            String rol = (String) table.getValueAt(selectedRow, 1);

            if ("gerente".equals(rol)) {
                String contrasena = JOptionPane.showInputDialog(this, "Ingrese la contraseña para eliminar al gerente:");
                if (contrasena != null && contrasena.equals(usuarioActual.getContrasena())) {
                    baseDatosUsuarios.eliminarUsuario(nombre);
                    JOptionPane.showMessageDialog(this, "Usuario eliminado.");
                    actualizarTabla();
                } else {
                    JOptionPane.showMessageDialog(this, "Contraseña incorrecta. No se puede eliminar el gerente.");
                }
            } else {
                baseDatosUsuarios.eliminarUsuario(nombre);
                JOptionPane.showMessageDialog(this, "Usuario eliminado.");
                actualizarTabla(); 
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para eliminar.");
        }
    }

    private void modificarUsuario(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String nombre = (String) table.getValueAt(selectedRow, 0);
            Usuario usuario = baseDatosUsuarios.obtenerUsuarios().stream()
                .filter(u -> u.getNombre().equals(nombre))
                .findFirst()
                .orElse(null);

            if (usuario != null) {
                JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
                JTextField txtNuevoNombre = new JTextField(usuario.getNombre());
                JPasswordField txtNuevaContrasena = new JPasswordField(usuario.getContrasena());
                JComboBox<String> comboNuevoRol = new JComboBox<>(new String[]{"empleado", "gerente"});
                comboNuevoRol.setSelectedItem(usuario.getRol());

                panel.add(new JLabel("Modificar nombre:"));
                panel.add(txtNuevoNombre);
                panel.add(new JLabel("Modificar contraseña:"));
                panel.add(txtNuevaContrasena);
                panel.add(new JLabel("Modificar rol:"));
                panel.add(comboNuevoRol);

                int result = JOptionPane.showConfirmDialog(this, panel, "Modificar Usuario",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    String nuevoNombre = txtNuevoNombre.getText().trim();
                    String nuevaContrasena = new String(txtNuevaContrasena.getPassword());
                    String nuevoRol = (String) comboNuevoRol.getSelectedItem();
                    if (!nuevoNombre.isEmpty() && !nuevaContrasena.isEmpty()) {
                        baseDatosUsuarios.modificarUsuario(usuario.getNombre(), nuevoNombre, nuevaContrasena, nuevoRol);
                        JOptionPane.showMessageDialog(this, "Usuario modificado exitosamente.");
                        actualizarTabla();
                    } else {
                        JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para modificar.");
        }
    }



    private void actualizarTabla() {
        model.setRowCount(0);

        for (Usuario usuario : baseDatosUsuarios.obtenerUsuarios()) {
            model.addRow(new Object[]{usuario.getNombre(), usuario.getRol()});
        }
    }
}
