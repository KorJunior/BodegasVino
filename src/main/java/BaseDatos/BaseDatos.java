package BaseDatos;

import Entity.Bodega;
import Entity.Cliente;
import Entity.Comercial;
import Entity.Venta;
import Entity.Vino;
import Repositorio.ComercialRepositorio;
import Repositorio.VentaRepositorio;
import Repositorio.VinoRepositorio;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

public class BaseDatos {

    private EntityManagerFactory emf;
    private EntityManager em;

    public BaseDatos() {

    }

    public boolean conectarBD() {
        //Gestion de la conexion a la Base de Datos
        if (conectarLaBD()) {
            System.out.println("SISTEMA: Conexion con la BD realizada correctamente.");
            return true;
        } else {
            System.out.println("ERROR: No se ha podido conectar con la Base de Datos");
            return false;
        }
    }

    public void desconectarBD() {
        if (desconectarLaBD()) {
            System.out.println("SISTEMA: Desconexion de la BD realizada correctamente.");
        } else {
            System.out.println("ERROR: No se ha podido desconectar con la Base de Datos");
            return;
        }
    }

    public ArrayList<String> obtenerListadoBodegas() {
        return obtenerListadoBodegas_BD();
    }

    public ArrayList<String> obtenerListadoVinos_VentaVino(String bodegaSeleccionada) {
        return obtenerListadoVinos_DependiendoBodegaSeleccionada_BD(bodegaSeleccionada);
    }

    public ArrayList<String> obtenerListadoComerciales_VentaVino(String bodegaSeleccionada) {
        return obtenerListadoComerciales_VentaVino_BD(bodegaSeleccionada);
    }

    public ArrayList<String> obtenerListadoComerciales_ConsultarVentas(String bodegaSeleccionada) {
        return obtenerListadoComerciales_ConsultarVentas_BD(bodegaSeleccionada);
    }

    public ArrayList<String> obtenerListadoClientes() {
        return obtenerListadoClientes_BD();
    }

    public void grabarResultadoVenta(Venta datosVenta) {
        grabarResultadoVenta_BD(datosVenta);
    }

    public List<Comercial> cargarTablaComerciales_ConsultarVentas(String bodegaSeleccionada) {
        return cargarTablaComerciales_ConsultarVentas_BD(bodegaSeleccionada);
    }

    public List<Venta> cargarTablaVentas_ConsultarVentas(int dniComercialSeleccionado) {
        return cargarTablaVentas_ConsultarVentas_BD(dniComercialSeleccionado);
    }

    public double calcularComision(int dniComercial) {
        return calcularComision_BD(dniComercial);
    }

    private boolean conectarLaBD() {
        try {
            emf = Persistence.createEntityManagerFactory("bodegas.odb");
            em = emf.createEntityManager();
            return true;
        } catch (Exception e) {

            return false;
        }
    }

    private boolean desconectarLaBD() {
        try {

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private ArrayList<String> obtenerListadoBodegas_BD() {
        List<Bodega> bodegas = em.createQuery("SELECT b FROM Bodega b", Bodega.class).getResultList();
        ArrayList<String> listadoBodegas = new ArrayList<>();

        //Extraemos todas las bodegas que nos han devuelto, despues guardamos esos datos
        for (Bodega bodega : bodegas) {
            listadoBodegas.add(bodega.getNombodega() + " - " + bodega.getLocalidad());
        }
        return listadoBodegas;
    }

    private ArrayList<String> obtenerListadoVinos_DependiendoBodegaSeleccionada_BD(String bodegaSeleccionada) {
        //EL FORMATO DE CADA ELEMENTO DE LA LISTA ES ASI:
        // La Finca - Blanco: 15(300) (no se a que dato corresponde ese 15)
        ArrayList<String> listaVinos = new ArrayList<>();
        List<Vino> vinos;

        //Obtenemos la bodega seleccionada
        Bodega bodega = em.createQuery("SELECT b FROM Bodega b WHERE b.nombodega = :nombreBodega", Bodega.class)
                .setParameter("nombreBodega", bodegaSeleccionada)
                .getSingleResult();
        //Obtenemos los vinos de las bodegas seleccionada
        vinos = em.createQuery("SELECT v FROM Vino v WHERE v.bodega = :bodega", Vino.class)
                .setParameter("bodega", bodega)
                .getResultList();
        //Extraemos los datos de los vinos
        for (Vino vino : vinos) {
            listaVinos.add(vino.getDenominacion() + " - " + vino.getTipo() + ": " + vino.getPrecio() + " (" + vino.getStock() + ")");
        }

        return listaVinos;

    }

    private ArrayList<String> obtenerListadoComerciales_VentaVino_BD(String bodegaSeleccionada) {
        //EL FORMATO DE CADA ELEMENTO DE LA LISTA ES ASI:
        // 6666-Juan Lopez-0 -> Necesito este formato para splitear
        ArrayList<String> listaComerciales = new ArrayList<>();
        List<Comercial> comerciales;
        Bodega bodega;

        bodega = em.createQuery("SELECT b FROM Bodega b WHERE b.nombodega = :nombreBodega", Bodega.class)
                .setParameter("nombreBodega", bodegaSeleccionada)
                .getSingleResult();

        //Obtenemos los comerciales de la bodega seleccionada
        comerciales = em.createQuery("SELECT c FROM Comercial c WHERE c.bodega = :bodega", Comercial.class)
                .setParameter("bodega", bodega)
                .getResultList();
        for (Comercial comercial : comerciales) {
            listaComerciales.add(comercial.getDni() + " - " + comercial.getNomcomercial() + " - " + comercial.getTotalventas());
        }

        return listaComerciales;

    }

    private ArrayList<String> obtenerListadoComerciales_ConsultarVentas_BD(String bodegaSeleccionada) {
        //EL FORMATO DE CADA ELEMENTO DE LA LISTA ES ASI:
        // 6666-Juan Lopez-0 -> Necesito este formato para splitear
        ArrayList<String> listaComerciales = new ArrayList<>();
        List<Comercial> comerciales;
        Bodega bodega;

        bodega = em.createQuery("SELECT b FROM Bodega b WHERE b.nombodega = :nombreBodega", Bodega.class)
                .setParameter("nombreBodega", bodegaSeleccionada)
                .getSingleResult();

        comerciales = em.createQuery("SELECT c FROM Comercial c WHERE c.bodega = :bodega", Comercial.class)
                .setParameter("bodega", bodega)
                .getResultList();
        for (Comercial comercial : comerciales) {
            listaComerciales.add(comercial.getDni() + " - " + comercial.getNomcomercial() + " : " + comercial.getTotalventas());
        }

        return listaComerciales;

    }

    private ArrayList<String> obtenerListadoClientes_BD() {
        //EL FORMATO DE CADA ELEMENTO DE LA LISTA ES ASI:
        // Luis Segura - Logroño
        ArrayList<String> listadoClientes = new ArrayList();
        List<Cliente> clientes = em.createQuery("SELECT c FROM Cliente c", Cliente.class).getResultList();

        for (Cliente cliente : clientes) {
            listadoClientes.add(cliente.getNomcliente() + " - " + cliente.getPoblacion());
        }

        return listadoClientes;
    }

    private void grabarResultadoVenta_BD(Venta datosVenta) {
        int idVenta;
        // Creación de repositorios para manejar las entidades de ventas, vinos y comerciales.
        VentaRepositorio ventaRepository = new VentaRepositorio(em);
        VinoRepositorio vinoRepository = new VinoRepositorio(em);
        ComercialRepositorio comercialRepository = new ComercialRepositorio(em);

        // Obtención de un nuevo ID para la venta.
        idVenta = obtenerIdventaNuevaVenta();
        // Obtención del vino asociado a la venta por nombre.
        Vino vino = obtenerVinoPorNombre(datosVenta.getVino().getDenominacion());
        // Obtención del comercial asociado a la venta por DNI.
        Comercial comercial = obtenerComercialPorDNI(datosVenta.getComercial().getDni());
        // Obtención del cliente asociado a la venta por nombre.
        Cliente cliente = obtenerClientePorNombre(datosVenta.getCliente().getNomcliente());

        // Actualización del stock del vino vendido.
        vinoRepository.actualizarVentaVinoStock(vino, datosVenta.getNumbotellas());
        // Refresco del estado del vino en el contexto de persistencia.
        em.refresh(vino);
        // Inserción del registro de ventas en la entidad comercial.
        comercialRepository.insertarComercialVentas(comercial, datosVenta.getImporte());
        // Refresco del estado del comercial en el contexto de persistencia.
        em.refresh(comercial);
        // Creación de un nuevo objeto Venta con los detalles de la venta.
        Venta venta = new Venta(idVenta, vino, comercial, cliente, datosVenta.getNumbotellas(), datosVenta.getImporte(), datosVenta.getFecha());
        // Inserción de la venta en el repositorio de ventas.
        ventaRepository.insertarVenta(venta);
    }

    private int obtenerIdventaNuevaVenta() {
        Integer ultimoIdVenta = em.createQuery("SELECT MAX(v.idventa) FROM Venta v", Integer.class).getSingleResult();

        if (ultimoIdVenta == null) {
            return 1;  // Si no hay ventas, el primer ID será 1
        } else {
            return ultimoIdVenta + 1;  // Si hay ventas, incrementamos el último ID
        }
    }

    private Vino obtenerVinoPorNombre(String nombreVino) {
        Vino vino = em.createQuery("SELECT v FROM Vino v WHERE v.denominacion = :nombreVino", Vino.class)
                .setParameter("nombreVino", nombreVino)
                .getSingleResult();
        return vino;
    }

    private Comercial obtenerComercialPorDNI(int dniComercial) {
        Comercial comercial = em.find(Comercial.class, dniComercial);
        return comercial;
    }

    private Cliente obtenerClientePorNombre(String nombreCliente) {
        Cliente cliente = em.createQuery("SELECT c FROM Cliente c WHERE c.nomcliente = :nombreCliente", Cliente.class)
                .setParameter("nombreCliente", nombreCliente)
                .getSingleResult();

        return cliente;
    }

    private List<Comercial> cargarTablaComerciales_ConsultarVentas_BD(String bodegaSeleccionada) {
        TypedQuery<Bodega> queryBodega = em.createQuery("SELECT b FROM Bodega b WHERE b.nombre = :nombreBodega", Bodega.class);
        queryBodega.setParameter("nombreBodega", bodegaSeleccionada.trim());

        // Ejecutar consulta y obtener la bodega
        Bodega bodega = queryBodega.getSingleResult();

        // Crear la consulta para obtener los comerciales de la bodega
        TypedQuery<Comercial> queryComerciales = em.createQuery("SELECT c FROM Comercial c WHERE c.bodega = :bodega", Comercial.class);
        queryComerciales.setParameter("bodega", bodega);

        // Ejecutar consulta y obtener la lista de comerciales
        List<Comercial> comerciales = queryComerciales.getResultList();

        return comerciales;
    }

    private List<Venta> cargarTablaVentas_ConsultarVentas_BD(int comercialSeleccionado) {
        // Crear la consulta para obtener el comercial por DNI
        TypedQuery<Comercial> queryComercial = em.createQuery("SELECT c FROM Comercial c WHERE c.dni = :dniComercial", Comercial.class);
        queryComercial.setParameter("dniComercial", comercialSeleccionado);

        // Ejecutar consulta y obtener el comercial
        Comercial comercial = queryComercial.getSingleResult();

        // Crear la consulta para obtener las ventas del comercial
        TypedQuery<Venta> queryVentas = em.createQuery("SELECT v FROM Venta v WHERE v.comercial = :comercial", Venta.class);
        queryVentas.setParameter("comercial", comercial);

        // Ejecutar consulta y obtener la lista de ventas
        List<Venta> ventas = queryVentas.getResultList();

        return ventas;
    }

    private double calcularComision_BD(int dniComercial) {
        Comercial comercial = em.find(Comercial.class, dniComercial);

        // Consulta para obtener las ventas del comercial
        TypedQuery<Venta> queryVentas = em.createQuery("SELECT v FROM Venta v WHERE v.comercial = :comercial", Venta.class);
        queryVentas.setParameter("comercial", comercial);

        // Obtener las ventas y sumar los importes
        List<Venta> ventas = queryVentas.getResultList();
        double totalImporte = ventas.stream().mapToDouble(Venta::getImporte).sum();

        // Calcular la comisión
        double comision = totalImporte * (comercial.getComision() / 100);

        return comision;
    }
}
