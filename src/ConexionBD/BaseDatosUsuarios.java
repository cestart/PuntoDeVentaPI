package ConexionDB;
import java.sql.*;
import java.util.ArrayList;

import Modelo.Usuario;

public class BaseDatosUsuarios {
    private Connection conexion;
    public BaseDatosUsuarios() {
        try {
            conexion = Conexion.conectarMySQL(); 
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
        }
    }
     public void modificarUsuario(String nombreOriginal, String nuevoNombre, String nuevaContrasena, String nuevoRol) {
        String sql = "UPDATE Usuarios SET nombre_usuario = ?, contraseña = ?, rol = ? WHERE nombre_usuario = ?";   
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, nuevoNombre);
            pstmt.setString(2, nuevaContrasena);
            pstmt.setString(3, nuevoRol);
            pstmt.setString(4, nombreOriginal);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al modificar usuario: " + e.getMessage());
        }
    }
    
    public Usuario validarUsuario(String nombre, String contrasena) {
        String sql = "SELECT * FROM Usuarios WHERE nombre_usuario = ? AND contraseña = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, contrasena);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Usuario(
                    rs.getString("nombre_usuario"),
                    rs.getString("contraseña"),
                    rs.getString("rol")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error al validar usuario: " + e.getMessage());
        }
        return null;
    }

    public ArrayList<Usuario> obtenerUsuarios() {
        ArrayList<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM Usuarios";
        
        try (Statement stmt = conexion.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Usuario usuario = new Usuario(
                    rs.getString("nombre_usuario"),
                    rs.getString("contraseña"),
                    rs.getString("rol")
                );
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios: " + e.getMessage());
        }
        return usuarios;
    }
    public void agregarUsuario(Usuario usuario) {
        String sql = "INSERT INTO Usuarios (nombre_usuario, contraseña, rol) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getContrasena());
            pstmt.setString(3, usuario.getRol());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al agregar usuario: " + e.getMessage());
        }
    }
    public void eliminarUsuario(String nombre) {
        String sql = "DELETE FROM Usuarios WHERE nombre_usuario = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
        }
    }
    public void actualizarUsuario(Usuario usuario) {
        String sql = "UPDATE Usuarios SET contraseña = ?, rol = ? WHERE nombre_usuario = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, usuario.getContrasena());
            pstmt.setString(2, usuario.getRol());
            pstmt.setString(3, usuario.getNombre());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
        }
    }
}
