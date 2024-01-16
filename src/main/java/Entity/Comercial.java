package Entity;

/**
 *
 * @author junit
 */
import java.util.HashSet;
import javax.persistence.*;
import java.util.Set;

@Entity
public class Comercial {

    @Id
    private int dni;
    private String nomcomercial;
    private float comision;
    private float totalventas;

    @ManyToOne
    private Bodega bodega;

    @OneToMany(mappedBy = "comercial", cascade = CascadeType.ALL)
    private Set<Venta> ventas;

    public Comercial(int dni, String nomcomercial, int comision, float totalventas, Bodega bodega) {
        this.dni = dni;
        this.nomcomercial = nomcomercial;
        this.comision = comision;
        this.totalventas = totalventas;
        this.bodega = bodega;
        this.ventas = new HashSet<>();
    }
    
    public Comercial () {
        
    }

    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        this.dni = dni;
    }

    public String getNomcomercial() {
        return nomcomercial;
    }

    public void setNomcomercial(String nomcomercial) {
        this.nomcomercial = nomcomercial;
    }

    public float getComision() {
        return comision;
    }

    public void setComision(float comision) {
        this.comision = comision;
    }

    public float getTotalventas() {
        return totalventas;
    }

    public void setTotalventas(float totalventas) {
        this.totalventas = totalventas;
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
