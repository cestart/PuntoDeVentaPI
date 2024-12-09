package ConexionDB;

import java.sql.*;

public class BaseDatosCaja {
    public static double obtenerDineroEnCaja() {
        double dineroEnCaja = 0.00;
        String consulta = "SELECT dinero_en_caja FROM Caja WHERE id = 1";

        try (Connection conexion = Conexion.conectarMySQL();
             Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(consulta)) {

            if (rs.next()) {
                dineroEnCaja = rs.getDouble("dinero_en_caja");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al obtener dinero en caja: " + e.getMessage());
        }
        return dineroEnCaja;
    }

    public static void actualizarDineroEnCaja(double nuevoMonto) {
        String actualizacion = "UPDATE Caja SET dinero_en_caja = ? WHERE id = 1";

        try (Connection conexion = Conexion.conectarMySQL(); 
             PreparedStatement stmt = conexion.prepareStatement(actualizacion)) {

            stmt.setDouble(1, nuevoMonto);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al actualizar dinero en caja: " + e.getMessage());
        }
    }
}
