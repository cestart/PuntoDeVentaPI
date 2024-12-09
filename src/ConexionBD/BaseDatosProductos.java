package ConexionDB;
import java.sql.*;
import java.util.ArrayList;

import Modelo.Producto;

public class BaseDatosProductos {

    private Connection conexion;

    public BaseDatosProductos() {
        try {
            conexion = Conexion.conectarMySQL();
            inicializarCategorias();  
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
        }
    }

    private void inicializarCategorias() {
        ArrayList<String> categoriasExistentes = getCategorias(); 

        String[] categoriasPredefinidas = {"Lácteos", "Panadería", "Bebidas"};
        
        for (String categoria : categoriasPredefinidas) {
            if (!categoriasExistentes.contains(categoria)) {
                agregarCategoria(categoria); 
            }
        }
    }

    public ArrayList<Producto> obtenerProductos() {
        ArrayList<Producto> productos = new ArrayList<>();
        if (conexion == null) {
            System.err.println("La conexión a la base de datos no está disponible.");
            return productos;
        }
        String sql = "SELECT * FROM Productos";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Producto producto = new Producto(
                    rs.getString("id_producto"),
                    rs.getString("nombre_producto"),
                    rs.getDouble("precio"),
                    rs.getInt("stock"),
                    rs.getString("categoria")
                );
                productos.add(producto);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
        }
        return productos;
    }

    public Producto obtenerProductoPorId(String id) {
        String sql = "SELECT * FROM Productos WHERE id_producto = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Producto(
                    rs.getString("id_producto"),
                    rs.getString("nombre_producto"),
                    rs.getDouble("precio"),
                    rs.getInt("stock"),
                    rs.getString("categoria")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener producto por ID: " + e.getMessage());
        }
        return null;
    }

    public void agregarProducto(Producto producto) {
        String sqlUltimoId = "SELECT MAX(id_producto) AS max_id FROM Productos";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sqlUltimoId)) {
            
            String nuevoId = "1";
            if (rs.next()) {
                int ultimoId = rs.getInt("max_id");
                nuevoId = String.valueOf(ultimoId + 1); 
            }

            String sql = "INSERT INTO Productos (id_producto, nombre_producto, precio, stock, categoria) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
                pstmt.setString(1, nuevoId);
                pstmt.setString(2, producto.getNombre());
                pstmt.setDouble(3, producto.getPrecio());
                pstmt.setInt(4, producto.getStock());
                pstmt.setString(5, producto.getCategoria());
                pstmt.executeUpdate();
            }
            
        } catch (SQLException e) {
            System.err.println("Error al agregar producto: " + e.getMessage());
        }
    }


    public String generarNuevoId() {
        String sql = "SELECT MAX(id_producto) AS max_id FROM Productos";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int maxId = rs.getInt("max_id");
                return String.valueOf(maxId + 1); 
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener el último ID: " + e.getMessage());
        }
        return "1"; 
    }

    
    public void eliminarProducto(String id) {
        String sql = "DELETE FROM Productos WHERE id_producto = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
        }
    }


    public void actualizarProducto(Producto producto) {
        String sql = "UPDATE Productos SET nombre_producto = ?, precio = ?, stock = ?, categoria = ? WHERE id_producto = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, producto.getNombre());
            pstmt.setDouble(2, producto.getPrecio());
            pstmt.setInt(3, producto.getStock());
            pstmt.setString(4, producto.getCategoria());
            pstmt.setString(5, producto.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
        }
    }

    public void agregarCategoria(String categoria) {
        String sql = "INSERT INTO Categorias (nombre_categoria) VALUES (?)";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, categoria);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al agregar la categoría: " + e.getMessage());
        }
    }

    public ArrayList<String> getCategorias() {
        ArrayList<String> categorias = new ArrayList<>();
        String sql = "SELECT DISTINCT categoria FROM Productos";  

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categorias.add(rs.getString("categoria"));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener categorías: " + e.getMessage());
        }
        return categorias;
    }
}
