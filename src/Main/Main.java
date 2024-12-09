package Main;

import ConexionDB.BaseDatosCaja;
import ConexionDB.BaseDatosProductos;
import ConexionDB.BaseDatosUsuarios;
import Controlador.AlertaStock;
import Controlador.RegistroVentas;
import Vista.Login;


public class Main {
    public static void main(String[] args) {
        BaseDatosProductos baseDatosProductos = new BaseDatosProductos();
        baseDatosProductos.obtenerProductos();

        BaseDatosCaja baseDatosCaja = new BaseDatosCaja();
        BaseDatosCaja.obtenerDineroEnCaja();
        BaseDatosUsuarios baseDatosUsuarios = new BaseDatosUsuarios();
        RegistroVentas registroVentas = new RegistroVentas(); 
        AlertaStock alertaStock = new AlertaStock(baseDatosProductos.obtenerProductos(), 5); 

        Login login = new Login(baseDatosUsuarios, baseDatosProductos, registroVentas, alertaStock, baseDatosCaja);
        login.setVisible(true);
    }
}
