package Controlador;

import java.util.ArrayList;
import java.util.List;
import Modelo.Compra;

public class GestorCompras {
    private List<Compra> compras;

    public GestorCompras() {
        this.compras = new ArrayList<>();
    }

    public void registrarCompra(Compra compra) {
        compras.add(compra);
    }

    public List<Compra> getCompras() {
        return compras;
    }
}
