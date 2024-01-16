package Entity;

import java.util.HashSet;
import javax.persistence.*;
import java.util.Set;

@Entity
public class Bodega {

    @Id
    private String idbodega;
    private String nombodega;
    private String localidad;

    @OneToMany(mappedBy = "bodega", cascade = CascadeType.ALL)
    private Set<Vino> vinos;

    @OneToMany(mappedBy = "bodega", cascade = CascadeType.ALL)
    private Set<Comercial> comerciales;

    public Bodega(String idbodega, String nombodega, String localidad) {
        this.idbodega = idbodega;
        this.nombodega = nombodega;
        this.localidad = localidad;
        this.vinos = new HashSet<>();
        this.comerciales = new HashSet<>();
    }
    
    public Bodega () {
        
    }

    public String getIdbodega() {
        return idbodega;
    }

    public void setIdbodega(String idbodega) {
        this.idbodega = idbodega;
    }

    public String getNombodega() {
        return nombodega;
    }

    public void setNombodega(String nombodega) {
        this.nombodega = nombodega;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public Set<Vino> getVinos() {
        return vinos;
    }

    public void setVinos(Set<Vino> vinos) {
        this.vinos = vinos;
    }

    public Set<Comercial> getComerciales() {
        return comerciales;
    }

    public void setComerciales(Set<Comercial> comerciales) {
        this.comerciales = comerciales;
    }

}
