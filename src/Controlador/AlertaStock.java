package Controlador;
import javax.swing.JOptionPane;

import Modelo.Producto;

import java.util.ArrayList;

public class AlertaStock {
    private ArrayList<Producto> productos;
    private int limiteStock;

    public AlertaStock(ArrayList<Producto> productos, int limiteStock) {
        this.productos = productos;
        this.limiteStock = limiteStock;
    }

    public void verificarStock() {
        StringBuilder mensaje = new StringBuilder("Alerta: Los siguientes productos tienen bajo stock:\n");

        boolean hayProductosBajoStock = false;
        for (Producto producto : productos) {
            if (producto.getStock() < limiteStock) {
                mensaje.append("- ").append(producto.getNombre()).append(" (Stock: ").append(producto.getStock()).append(")\n");
                hayProductosBajoStock = true;
            }
        }
        if (hayProductosBajoStock) {
            JOptionPane.showMessageDialog(null, mensaje.toString(), "Alerta de Bajo Stock", JOptionPane.WARNING_MESSAGE);
        }
    }
}
