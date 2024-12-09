package Vista;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import Modelo.Compra;

import java.awt.*;
import java.util.List;

public class RegistroCompras extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RegistroCompras(List<Compra> compras) {
        setTitle("Registro de Compras");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        DefaultTableModel modeloTabla = new DefaultTableModel(
                new Object[]{"Fecha", "Producto", "Proveedor", "Método de Pago", "Cantidad"}, 0
        ) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Compra compra : compras) {
            modeloTabla.addRow(new Object[]{
                compra.getFecha(),
                compra.getProducto().getNombre(),
                compra.getProveedor(),
                compra.getMetodoPago(),
                compra.getCantidad()
            });
        }

        JTable tablaCompras = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaCompras);
        setLocationRelativeTo(null);
        add(scrollPane, BorderLayout.CENTER);
    }
}
