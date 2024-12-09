package ConexionDB;

import java.sql.Connection;
import java.sql.SQLException;

public class Testeo {
    public static void main(String[] args) {
        try {
            Connection conexion = Conexion.conectarMySQL();
            if (conexion != null) {
                System.out.println("Conexión establecida correctamente.");
                conexion.close();
            } else {
                System.err.println("No se pudo establecer la conexión.");
            }
        } catch (SQLException e) {
            System.err.println("Error al conectar: " + e.getMessage());
        }
    }
}
