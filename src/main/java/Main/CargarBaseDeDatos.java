package Main;

import Entity.Bodega;
import Entity.Cliente;
import Entity.Comercial;
import Entity.Venta;
import Entity.Vino;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class CargarBaseDeDatos {

    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("bodegas.odb");
    static EntityManager em = emf.createEntityManager();

    public static void main(String[] args) throws ParseException {
        limpiarBaseDeDatos();
        cargarBodega();
        cargasVinos();
        cargarComercial();
        cargarClientes();
        cargarVenta();
    }

    private static void cargarBodega() {
        em.getTransaction().begin();

        Bodega[] bodegas = {
            new Bodega("ARTU", "Bodegas Artuke", "Baños de Ebro"),
            new Bodega("BRET", "Bodegas Bretón", "Logroño"),
            new Bodega("VALE", "Bodegas Valenciso", "Ollauri"),
            new Bodega("ONDA", "Bodegas Ondalán", "Oyón")
        };
        for (Bodega bodega : bodegas) {
            em.persist(bodega);
        }
        em.getTransaction().commit();
    }

    private static void cargasVinos() {
        em.getTransaction().begin();

        Vino[] vinos = {
            new Vino(1, "Valenciso I", 2009, "Tinto", 12, 30, em.find(Bodega.class, "VALE")),
            new Vino(2, "Valenciso II", 2010, "Tinto", 14, 1000, em.find(Bodega.class, "VALE")),
            new Vino(3, "La Finca", 2011, "Blanco", 15, 300, em.find(Bodega.class, "BRET")),
            new Vino(4, "Artuke", 2011, "Tinto", 28, 1500, em.find(Bodega.class, "ARTU")),
            new Vino(5, "Pies Negros", 2010, "Tinto", 16, 2000, em.find(Bodega.class, "ARTU")),
            new Vino(6, "Alba", 2004, "Tinto", 18, 200, em.find(Bodega.class, "BRET")),
            new Vino(7, "100 Abades", 2012, "Tinto", 14, 900, em.find(Bodega.class, "ONDA")),
            new Vino(8, "Ondalán", 2013, "Blanco", 9, 1200, em.find(Bodega.class, "ONDA"))
        };
        for (Vino vino : vinos) {
            em.persist(vino);
        }
        em.getTransaction().commit();
    }

    private static void cargarComercial() {
        em.getTransaction().begin();
        Comercial[] comerciales = {
            new Comercial(1111, "Jesús López", 4, 0, em.find(Bodega.class, "ARTU")),
            new Comercial(2222, "Lucía Lorente", 5, 0, em.find(Bodega.class, "BRET")),
            new Comercial(3333, "María Martínez", 4, 0, em.find(Bodega.class, "VALE")),
            new Comercial(4444, "Pedro González", 4, 0, em.find(Bodega.class, "ONDA")),
            new Comercial(5555, "Marta Bárcena", 5, 0, em.find(Bodega.class, "ARTU")),
            new Comercial(6666, "Juan Gómez", 4, 0, em.find(Bodega.class, "BRET")),
            new Comercial(7777, "Pilar Pérez", 4, 0, em.find(Bodega.class, "VALE")),
            new Comercial(8888, "Andrés Simón", 4, 0, em.find(Bodega.class, "ONDA"))
        };

        for (Comercial comercial : comerciales) {
            em.persist(comercial);
        }
        em.getTransaction().commit();

    }

    private static void cargarClientes() {
        em.getTransaction().begin();
        Cliente[] clientes = {
            new Cliente("c-10", "Carlos Abad", "Pamplona"),
            new Cliente("c-20", "Alberto Ruiz", "Logroño"),
            new Cliente("c-30", "Santiago Ramos", "Pamplona"),
            new Cliente("c-40", "Luis Segura", "Logroño"),
            new Cliente("c-50", "María Sánchez", "Najera"),
            new Cliente("c-60", "Carmen San Miguel", "Logroño")
        };

        for (Cliente cliente : clientes) {
            em.persist(cliente);
        }
        em.getTransaction().commit();

    }

    private static void cargarVenta() throws ParseException {
        em.getTransaction().begin();

        // Crear formato de fecha
        DateFormat format = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH);

        // Insertar datos en la tabla VENTA
        Venta[] ventas = {
            new Venta(1, em.find(Vino.class, 4), em.find(Comercial.class, 1111), em.find(Cliente.class, "c-40"), 1000, 28000, format.parse("1/2/18")),
            new Venta(2, em.find(Vino.class, 4), em.find(Comercial.class, 1111), em.find(Cliente.class, "c-10"), 500, 14000, format.parse("11/2/18"))
        };

        for (Venta venta : ventas) {
            em.persist(venta);
        }

        em.getTransaction().commit();
    }
     private static void limpiarBaseDeDatos() {
        em.getTransaction().begin();

        // Limpiar cada tabla
        em.createQuery("DELETE FROM Venta").executeUpdate();
        em.createQuery("DELETE FROM Cliente").executeUpdate();
        em.createQuery("DELETE FROM Comercial").executeUpdate();
        em.createQuery("DELETE FROM Vino").executeUpdate();
        em.createQuery("DELETE FROM Bodega").executeUpdate();

        em.getTransaction().commit();
    }


}
