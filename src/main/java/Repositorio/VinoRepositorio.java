/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Repositorio;

import Entity.Vino;
import javax.persistence.EntityManager;

/**
 *
 * @author junit
 */
public class VinoRepositorio {

    EntityManager em;

    public VinoRepositorio(EntityManager em) {
        this.em = em;
    }

    public void actualizarVentaVinoStock(Vino vino, int cantidadSustraido) {
        int nuevoStock;
                
        em.getTransaction().begin();
        nuevoStock = vino.getStock() - cantidadSustraido;
        vino.setStock(nuevoStock);
        em.getTransaction().commit();

    }
}
