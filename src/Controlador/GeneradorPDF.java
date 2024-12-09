package Controlador;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import Modelo.Producto;
import Modelo.Ticket;
import Modelo.Venta;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GeneradorPDF {
	
    public static void generarReporteTicket(Ticket ticket, String ruta) throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(ruta));
        document.open();

        Paragraph title = new Paragraph("Reporte de Ticket\n", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        String fechaConsulta = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Paragraph fecha = new Paragraph("Fecha de consulta: " + fechaConsulta + "\n", FontFactory.getFont(FontFactory.HELVETICA, 12));
        fecha.setAlignment(Element.ALIGN_CENTER);
        document.add(fecha);

        PdfPTable table = new PdfPTable(4);
        table.addCell("Producto");
        table.addCell("Cantidad");
        table.addCell("Precio");
        table.addCell("Total");

        for (Venta venta : ticket.getVentas()) {
            table.addCell(venta.getProducto());
            table.addCell(String.valueOf(venta.getCantidad()));
            table.addCell(String.valueOf(venta.getPrecio()));
            table.addCell(String.valueOf(venta.getTotal()));
        }

        document.add(table);

        document.close();
    }

	public static void generarReporteInventario(List<Producto> productos, String ruta) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(ruta));
            document.open();

            Paragraph title = new Paragraph("Reporte de Inventario\n", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            String fechaConsulta = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            Paragraph fecha = new Paragraph("Fecha de consulta: " + fechaConsulta + "\n", FontFactory.getFont(FontFactory.HELVETICA, 12));
            fecha.setAlignment(Element.ALIGN_CENTER);
            document.add(fecha);

            PdfPTable table = new PdfPTable(4);
            table.addCell("ID");
            table.addCell("Nombre");
            table.addCell("Precio");
            table.addCell("Stock");

            for (Producto producto : productos) {
                table.addCell(String.valueOf(producto.getId()));
                table.addCell(producto.getNombre());
                table.addCell(String.valueOf(producto.getPrecio()));
                table.addCell(String.valueOf(producto.getStock()));
            }

            document.add(table);

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }
}
