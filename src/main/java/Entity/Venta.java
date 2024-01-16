package Entity;

import java.util.Date;
import javax.persistence.*;

@Entity
public class Venta {
    @Id
    private int idventa;
    private int numbotellas;
    private float importe;
    private Date fecha;

    @ManyToOne
    private Vino vino;

    @ManyToOne
    private Comercial comercial;

    @ManyToOne
    private Cliente cliente;
    
    public Venta(int idventa, Vino vino, Comercial comercial, Cliente cliente, int numbotellas, float importe, Date fecha) {
    this.idventa = idventa;
    this.vino = vino;
    this.comercial = comercial;
    this.cliente = cliente;
    this.numbotellas = numbotellas;
    this.importe = importe;
    this.fecha = fecha;
}

    public Venta() {
    }
    


    public int getIdventa() {
        return idventa;
    }

    public void setIdventa(int idventa) {
        this.idventa = idventa;
    }

    public int getNumbotellas() {
        return numbotellas;
    }

    public void setNumbotellas(int numbotellas) {
        this.numbotellas = numbotellas;
    }

    public float getImporte() {
        return importe;
    }

    public void setImporte(float importe) {
        this.importe = importe;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Vino getVino() {
        return vino;
    }

    public void setVino(Vino vino) {
        this.vino = vino;
    }

    public Comercial getComercial() {
        return comercial;
    }

    public void setComercial(Comercial comercial) {
        this.comercial = comercial;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    
}
