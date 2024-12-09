package Vista;
import Controlador.AlertaStock;
import Controlador.RegistroVentas;
import Modelo.Usuario;

import javax.swing.*;

import ConexionDB.BaseDatosCaja;
import ConexionDB.BaseDatosProductos;
import ConexionDB.BaseDatosUsuarios;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login extends JFrame {
    private BaseDatosUsuarios baseDatosUsuarios;
    private BaseDatosProductos baseDatosProductos;
    private RegistroVentas registroVentas;
    private AlertaStock alertaStock;
    private JTextField txtUsuario;
    private JPasswordField txtContraseña;
	private BaseDatosCaja baseDatosCaja;

    public Login(BaseDatosUsuarios baseDatosUsuarios, BaseDatosProductos baseDatosProductos, RegistroVentas registroVentas, AlertaStock alertaStock, BaseDatosCaja baseDatosCaja) {
        this.baseDatosUsuarios = baseDatosUsuarios;
        this.baseDatosProductos = baseDatosProductos;
        this.registroVentas = registroVentas;
        this.alertaStock = alertaStock;
        this.baseDatosCaja = baseDatosCaja;

        setTitle("Login");
        setSize(300, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JLabel lblUsuario = new JLabel("Usuario:");
        txtUsuario = new JTextField();
        JLabel lblContraseña = new JLabel("Contraseña:");
        txtContraseña = new JPasswordField();

        JButton btnLogin = new JButton("Iniciar Sesión");

        panel.add(lblUsuario);
        panel.add(txtUsuario);
        panel.add(lblContraseña);
        panel.add(txtContraseña);
        panel.add(new JLabel());
        panel.add(btnLogin);

        add(panel, BorderLayout.CENTER);

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usuario = txtUsuario.getText();
                String contraseña = new String(txtContraseña.getPassword());
                Usuario user = baseDatosUsuarios.validarUsuario(usuario, contraseña);
                if (user != null) {
                    Visual_POS visualPos = new Visual_POS(user, baseDatosProductos, registroVentas, baseDatosUsuarios, alertaStock, baseDatosCaja); // Ajuste aquí
                    visualPos.setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos.");
                }
            }
        });
    }
}
