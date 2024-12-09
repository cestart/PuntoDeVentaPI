package ConexionDB;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class Conexion {
    private static final String urlMySQL = "jdbc:mysql://localhost:3306/tiendadb"; 
    private static final String usuarioMySQL = "root";
    private static final String passwordMySQL = "contraseña";

    public static Connection conectarMySQL() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("No se pudo cargar el driver de MySQL: " + e.getMessage());
            return null;
        }
        return DriverManager.getConnection(urlMySQL, usuarioMySQL, passwordMySQL);
    }
}
