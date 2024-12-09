package ConexionDB;
import java.sql.*;
import java.util.ArrayList;

import Modelo.Producto;
import Modelo.Proveedor;

public class BaseDatosProveedores {
    private static final String URL = "jdbc:mysql://localhost:3306/tiendadb";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "contraseña";

    public static ArrayList<Proveedor> obtenerProveedores() {
        ArrayList<Proveedor> proveedores = new ArrayList<>();
        String consulta = "SELECT id, nombre FROM Proveedores";
        try (Connection conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
             Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(consulta)) {

            while (rs.next()) {
                String id = rs.getString("id");
                String nombre = rs.getString("nombre");
                proveedores.add(new Proveedor(id, nombre));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al obtener proveedores: " + e.getMessage());
        }
        return proveedores;
    }
    
    public void actualizarProducto(Producto producto) {
        String consulta = "UPDATE Productos SET stock = ? WHERE id = ?";
        try (Connection conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
             PreparedStatement stmt = conexion.prepareStatement(consulta)) {
            
            stmt.setInt(1, producto.getStock());
            stmt.setString(2, producto.getId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al actualizar producto: " + e.getMessage());
        }
    }

}
