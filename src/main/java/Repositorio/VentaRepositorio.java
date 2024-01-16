/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Repositorio;

import Entity.Venta;
import javax.persistence.*;

/**
 *
 * @author junit
 */
public class VentaRepositorio {
    
    EntityManager em;

    public VentaRepositorio(EntityManager em) {
        this.em = em;
    }
    public boolean insertarVenta(Venta venta){
        em.getTransaction().begin();
        em.persist(venta);
        em.getTransaction().commit();
        
        return false;
    }
    
}
