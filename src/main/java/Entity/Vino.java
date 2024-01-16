package Entity;

import java.util.HashSet;
import javax.persistence.*;
import java.util.Set;

@Entity
public class Vino {

    @Id
    private int idvino;
    private String denominacion;
    private int año;
    private String tipo;
    private float precio;
    private int stock;

    @ManyToOne
    private Bodega bodega;

    @OneToMany(mappedBy = "vino", cascade = CascadeType.ALL)
    private Set<Venta> ventas;

    public Vino(int idvino, String denominacion, int año, String tipo, float precio, int stock, Bodega bodega) {
        this.idvino = idvino;
        this.denominacion = denominacion;
        this.año = año;
        this.tipo = tipo;
        this.precio = precio;
        this.stock = stock;
        this.bodega = bodega;
        this.ventas = new HashSet<>();
    }

    public Vino() {

    }

    public int getIdvino() {
        return idvino;
    }

    public void setIdvino(int idvino) {
        this.idvino = idvino;
    }

    public String getDenominacion() {
        return denominacion;
    }

    public void setDenominacion(String denominacion) {
        this.denominacion = denominacion;
    }

    public int getAño() {
        return año;
    }

    public void setAño(int año) {
        this.año = año;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Bodega getBodega() {
        return bodega;
    }

    public void setBodega(Bodega bodega) {
        this.bodega = bodega;
    }

    public Set<Venta> getVentas() {
        return ventas;
    }

    public void setVentas(Set<Venta> ventas) {
        this.ventas = ventas;
    }

}
