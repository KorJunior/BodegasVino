package Entity;

import java.util.HashSet;
import javax.persistence.*;
import java.util.Set;

@Entity
public class Cliente {

    @Id
    private String idcliente;
    private String nomcliente;
    private String poblacion;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private Set<Venta> ventas;

    public Cliente(String idcliente, String nomcliente, String poblacion) {
        this.idcliente = idcliente;
        this.nomcliente = nomcliente;
        this.poblacion = poblacion;
        this.ventas = new HashSet<>();
    }
    
    public Cliente () {
        
    }

    public String getIdcliente() {
        return idcliente;
    }

    public void setIdcliente(String idcliente) {
        this.idcliente = idcliente;
    }

    public String getNomcliente() {
        return nomcliente;
    }

    public void setNomcliente(String nomcliente) {
        this.nomcliente = nomcliente;
    }

    public String getPoblacion() {
        return poblacion;
    }

    public void setPoblacion(String poblacion) {
        this.poblacion = poblacion;
    }

    public Set<Venta> getVentas() {
        return ventas;
    }

    public void setVentas(Set<Venta> ventas) {
        this.ventas = ventas;
    }

}
