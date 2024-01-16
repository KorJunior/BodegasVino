/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Repositorio;

import Entity.Comercial;
import javax.persistence.*;

/**
 *
 * @author junit
 */
public class ComercialRepositorio {
    EntityManager em;

    public ComercialRepositorio(EntityManager em) {
        this.em = em;
    }
    public boolean insertarComercialVentas(Comercial comercial,float venta){
        float importeVentasTotales;
        
        em.getTransaction().begin();
        importeVentasTotales=comercial.getTotalventas();
        importeVentasTotales+=venta;
        comercial.setTotalventas(importeVentasTotales);
        System.out.println(importeVentasTotales);
        em.persist(comercial);
        em.getTransaction().commit();
        
        return false;
    }
    
}
