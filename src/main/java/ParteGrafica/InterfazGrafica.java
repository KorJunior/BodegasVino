package ParteGrafica;

import BaseDatos.BaseDatos;
import Entity.Bodega;
import Entity.Cliente;
import Entity.Comercial;
import Entity.Venta;
import Entity.Vino;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public final class InterfazGrafica extends javax.swing.JFrame {

    private final BaseDatos _baseDatos;
    private boolean _habilitadoCampoNumBotellas = false;

    private String _cantBotellas;
    private float _importeBotellas;

    private int _cantBotellasDisponibles;

    private final String _rutaIconoApp = "src/main/java/ParteGrafica/logoApp.png";
    private final String _rutaIconoAviso = "src/main/java/ParteGrafica/aviso.png";

    public InterfazGrafica() {
        super("Winery Sales Manager 2023 | BETA 1.0.01 - Developer version");

        PantallaCarga pantallaCarga = new PantallaCarga();

        _baseDatos = new BaseDatos();
        if (_baseDatos.conectarBD()) {
            initComponents();

            cargarFormulario();
            pantallaCarga.terminarPantalla();
            mostrarFormulario();
        } else {
            //finally;
        }
    }

    /**
     * Carga y configura diversos elementos en el formulario principal.
     */
    private void cargarFormulario() {
        // Cargar el ícono de la aplicación en la barra de título
        ImageIcon iconoApp = new ImageIcon(_rutaIconoApp);
        setIconImage(iconoApp.getImage());

        // Configurar la ventana principal
        setResizable(false);
        setLocationRelativeTo(null);

        // Configurar la ventana de avisos
        jDialog_VentanaAvisos.setIconImage(iconoApp.getImage());
        jDialog_VentanaAvisos.setResizable(false);
        jDialog_VentanaAvisos.setLocationRelativeTo(null);

        // Cargar elementos en la primera ventana
        cargarComboBox_ListadoBodegas_VentasVino();
        cargarTabla_Vinos_VentaVino();
        cargarTabla_Comerciales_VentaVino();
        cargarComboBox_Clientes_VentaVino();
        cargarFechaActual();
        establecerConfiguraciones();

        // Cargar elementos en la segunda ventana
        cargarComboBox_ListadoBodegas_ConsultarVentas();
        cargarTabla_Comerciales_ConsultarVentas();
    }

    private void mostrarFormulario() {
        this.setVisible(true);
    }

    /**
     * Carga los elementos de un JComboBox a partir de una lista y selecciona el
     * primer elemento. Si la lista está vacía, muestra un mensaje de error.
     *
     * @param comboBoxACargar JComboBox a cargar.
     * @param itemsAñadir Lista de elementos a añadir al JComboBox.
     * @param idMensajeError Identificador del mensaje de error.
     */
    private void cargarComboBox(JComboBox<String> comboBoxACargar, ArrayList<String> itemsAñadir, byte idMensajeError) {
        try {
            // Verificar si la lista de elementos no está vacía
            if (itemsAñadir != null && !itemsAñadir.isEmpty()) {
                // Agregar cada elemento al JComboBox
                for (String item : itemsAñadir) {
                    comboBoxACargar.addItem(item);
                }
                // Seleccionar el primer elemento en el JComboBox
                comboBoxACargar.setSelectedIndex(0);
            } else {
                // Mostrar mensaje de error si la lista está vacía
                mensajeError(idMensajeError);
            }
        } catch (Exception e) {
            // Imprimir la traza de la excepción en caso de error
            e.printStackTrace();
        }
    }

    /**
     * Carga el listado de bodegas en el JComboBox de la ventana de ventas de
     * vino.
     */
    private void cargarComboBox_ListadoBodegas_VentasVino() {
        // Obtener el listado de bodegas desde la base de datos
        ArrayList<String> listadoBodegas = _baseDatos.obtenerListadoBodegas();
        // Llamar al método general para cargar el JComboBox con el listado de bodegas
        cargarComboBox(jComboBox_Bodegas_VentaVino, listadoBodegas, (byte) -1);
    }

    /**
     * Carga el listado de bodegas en el JComboBox de la ventana de consultar
     * ventas.
     */
    private void cargarComboBox_ListadoBodegas_ConsultarVentas() {
        // Obtener el listado de bodegas desde la base de datos
        ArrayList<String> listadoBodegas = _baseDatos.obtenerListadoBodegas();
        // Llamar al método general para cargar el JComboBox con el listado de bodegas
        cargarComboBox(jComboBox_Bodegas_ConsultaVentas, listadoBodegas, (byte) -2);
    }

    /**
     * Carga el listado de clientes en el JComboBox de la ventana de ventas de
     * vino.
     */
    private void cargarComboBox_Clientes_VentaVino() {
        // Obtener el listado de clientes desde la base de datos
        ArrayList<String> listadoClientes = _baseDatos.obtenerListadoClientes();
        // Llamar al método general para cargar el JComboBox con el listado de clientes
        cargarComboBox(jComboBox_Clientes_VentaVino, listadoClientes, (byte) -3);
    }

    /**
     * Carga y configura una tabla a partir de una lista de elementos.
     *
     * @param tablaACargar Tabla a cargar.
     * @param cantColumnasTabla Cantidad de columnas en la tabla.
     * @param itemsAñadir Lista de elementos a añadir a la tabla.
     * @param idMensajeError Identificador del mensaje de error.
     * @param <T> Tipo de elementos en la lista.
     */
    private <T> void cargarTabla(JTable tablaACargar, byte cantColumnasTabla, List<T> itemsAñadir, byte idMensajeError) {
        try {
            // Limpiar la selección y contenido de la tabla
            tablaACargar.clearSelection();
            limpiarTabla(tablaACargar);

            int cantFilasTabla, filaTabla = 0;

            // Verificar si la lista de elementos no está vacía
            if (itemsAñadir != null && !itemsAñadir.isEmpty()) {
                // Establecer el tamaño del modelo de la tabla
                cantFilasTabla = establecerTamañoModeloTabla(tablaACargar, cantColumnasTabla, itemsAñadir.size());

                // Añadir elementos a la tabla
                if (cantColumnasTabla == 1) {
                    for (; filaTabla < cantFilasTabla; filaTabla++) {
                        tablaACargar.setValueAt(itemsAñadir.get(filaTabla), filaTabla, 0);
                    }
                } else {
                    for (T info : itemsAñadir) {
                        Object[] datosInfoSeparados = info instanceof String ? ((String) info).split(" - ") : new Object[]{info};
                        for (byte columnaTabla = 0; columnaTabla < cantColumnasTabla && columnaTabla < datosInfoSeparados.length; columnaTabla++) {
                            tablaACargar.setValueAt(datosInfoSeparados[columnaTabla], filaTabla, columnaTabla);
                        }
                        filaTabla++;
                    }
                }

            } else {
                // Mostrar mensaje de error si la lista está vacía
                if (idMensajeError != -7) {
                    mensajeError(idMensajeError);
                }
            }
        } catch (Exception e) {
            // Imprimir la traza de la excepción en caso de error
            e.printStackTrace();
        }
    }

    /**
     * Limpia el contenido de una tabla.
     *
     * @param tablaALimpiar Tabla a limpiar.
     */
    private void limpiarTabla(JTable tablaALimpiar) {
        DefaultTableModel modeloTabla = (DefaultTableModel) tablaALimpiar.getModel();
        modeloTabla.setRowCount(0);
    }

    /**
     * Establece el tamaño del modelo de una tabla según la cantidad de filas
     * requeridas.
     *
     * @param tablaAModificar Tabla a modificar.
     * @param cantColumnas Cantidad de columnas en la tabla.
     * @param cantTotalFilasTabla Cantidad total de filas que debería tener la
     * tabla.
     * @return Número de filas después de la modificación.
     */
    private int establecerTamañoModeloTabla(JTable tablaAModificar, int cantColumnas, int cantTotalFilasTabla) {
        int numFilasTabla = tablaAModificar.getRowCount();
        DefaultTableModel modeloTabla = (DefaultTableModel) tablaAModificar.getModel();
        int cantFilasAñadir = cantTotalFilasTabla - numFilasTabla;

        // Verificar si se deben añadir filas adicionales al modelo de la tabla
        if (cantFilasAñadir > 0) {
            for (int contFilasAñadir = 0; contFilasAñadir < cantFilasAñadir; contFilasAñadir++) {
                Object[] fila = new Object[cantColumnas];
                modeloTabla.addRow(fila);
            }
            return modeloTabla.getRowCount();
        }
        // Devolver el número actual de filas en la tabla
        return numFilasTabla;
    }

    /**
     * Carga y configura la tabla de vinos en la ventana de ventas de vino.
     * Utiliza la base de datos para obtener el listado de vinos según la bodega
     * seleccionada.
     */
    private void cargarTabla_Vinos_VentaVino() {
        // Obtener el listado de vinos desde la base de datos según la bodega seleccionada
        ArrayList<String> listadoVinos = _baseDatos.obtenerListadoVinos_VentaVino(obtenerBodegaSeleccionada(jComboBox_Bodegas_VentaVino));
        // Llamar al método general para cargar y configurar la tabla de vinos
        cargarTabla(jTable_Vinos_VentaVino, (byte) 1, listadoVinos, (byte) -4);
    }

    /**
     * Carga y configura la tabla de comerciales en la ventana de ventas de
     * vino. Utiliza la base de datos para obtener el listado de comerciales
     * según la bodega seleccionada.
     */
    private void cargarTabla_Comerciales_VentaVino() {
        // Obtener el listado de comerciales desde la base de datos según la bodega seleccionada
        ArrayList<String> listadoComerciales = _baseDatos.obtenerListadoComerciales_VentaVino(obtenerBodegaSeleccionada(jComboBox_Bodegas_VentaVino));
        // Llamar al método general para cargar y configurar la tabla de comerciales
        cargarTabla(jTable_Comerciales_VentaVino, (byte) 3, listadoComerciales, (byte) -5);
    }

    /**
     * Carga y configura la tabla de comerciales en la ventana de consultar
     * ventas. Utiliza la base de datos para obtener el listado de comerciales
     * según la bodega seleccionada.
     */
    private void cargarTabla_Comerciales_ConsultarVentas() {
        // Obtener el listado de comerciales desde la base de datos según la bodega seleccionada
        ArrayList<String> listadoComerciales = _baseDatos.obtenerListadoComerciales_ConsultarVentas(obtenerBodegaSeleccionada(jComboBox_Bodegas_ConsultaVentas));
        // Llamar al método general para cargar y configurar la tabla de comerciales en la ventana de consultar ventas
        cargarTabla(jTable_Comerciales_ConsultaVentas, (byte) 1, listadoComerciales, (byte) -6);
    }

    /**
     * Carga y configura la tabla de ventas en la ventana de consultar ventas.
     * Utiliza la base de datos para obtener el listado de ventas según el
     * comercial seleccionado.
     */
    private void cargarTabla_Ventas_ConsultarVentas() {
        // Obtener el listado de ventas del comercial seleccionado desde la base de datos
        List<Venta> listadoVentasComercial = _baseDatos.cargarTablaVentas_ConsultarVentas(obtenerComercialSeleccionado(jTable_Comerciales_ConsultaVentas, (byte) 1));
        // Llamar al método general para cargar y configurar la tabla de ventas en la ventana de consultar ventas
        cargarTabla(jTable_Ventas_ConsultarVentas, (byte) 4, prepararDatosParaTablaVentas(listadoVentasComercial), (byte) -7);
    }

    /**
     * Prepara los datos de ventas para ser mostrados en la tabla.
     *
     * @param listado Listado de ventas a preparar.
     * @return Listado de datos de ventas en formato de cadena.
     */
    private ArrayList<String> prepararDatosParaTablaVentas(List<Venta> listado) {
        ArrayList<String> listadoDatosVenta = new ArrayList<>();
        // Preparar cada dato de venta para la tabla
        for (Venta datosVenta : listado) {
            listadoDatosVenta.add(datosVenta.getCliente().getNomcliente() + " - "
                    + datosVenta.getVino().getDenominacion() + " - "
                    + datosVenta.getNumbotellas() + " - "
                    + datosVenta.getImporte());
        }
        return listadoDatosVenta;
    }

    /**
     * Obtiene la bodega seleccionada en un JComboBox.
     *
     * @param comboBox JComboBox que contiene las opciones de bodegas.
     * @return Nombre de la bodega seleccionada.
     */
    private String obtenerBodegaSeleccionada(JComboBox comboBox) {
        Object bodega = comboBox.getSelectedItem();
        String datosSeparados[];

        // Verificar si la bodega seleccionada no es nula
        if (bodega != null) {
            datosSeparados = bodega.toString().split(" -");
            return datosSeparados[0];
        } else {
            return null;
        }
    }

    /**
     * Obtiene el identificador del comercial seleccionado en una tabla.
     *
     * @param tabla Tabla que contiene la información de comerciales.
     * @param cantColumnas Cantidad de columnas en la tabla.
     * @return Identificador del comercial seleccionado.
     */
    private int obtenerComercialSeleccionado(JTable tabla, byte cantColumnas) {
        Object comercial = tabla.getValueAt(tabla.getSelectedRow(), 0);
        // Verificar la cantidad de columnas en la tabla para determinar el formato del comercial seleccionado
        if (cantColumnas == 1) {
            String[] datosComercial = comercial.toString().split(" - ");
            return Integer.parseInt(datosComercial[0]);
        } else {
            return Integer.parseInt(comercial.toString());
        }
    }

    /**
     * Carga la fecha actual en un campo de texto.
     */
    private void cargarFechaActual() {
        jTextField_FechaCompra.setText(LocalDate.now().toString());
    }

    /**
     * Establece configuraciones específicas para varios elementos en la
     * ventana.
     */
    private void establecerConfiguraciones() {
        configuracionTextField_Botellas();
        configuracionTextField_Importe();
        configuracionTextField_PrecioBotella();
    }

    /**
     * Configura el campo de texto relacionado a la cantidad de botellas. El
     * tooltip cambia dependiendo de si el campo está habilitado o no.
     */
    private void configuracionTextField_Botellas() {
        String toolTipBotellas_Inactivo = "Primero has de seleccionar un vino";
        String toolTipBotellas_Activo = "Introduce un número (entre 0 y " + _cantBotellasDisponibles + ")";

        jTextField_NumBotellas.setEnabled(_habilitadoCampoNumBotellas);
        if (_habilitadoCampoNumBotellas) {
            jTextField_NumBotellas.setToolTipText(toolTipBotellas_Activo);
        } else {
            jTextField_NumBotellas.setToolTipText(toolTipBotellas_Inactivo);
        }
    }

    /**
     * Configura el campo de texto relacionado al importe total. Muestra un
     * tooltip con información sobre cómo se calcula el importe.
     */
    private void configuracionTextField_Importe() {
        String toolTipImporte = "Se calcula automáticamente dependiendo del precio y la cantidad de botellas que has seleccionado";
        jTextField_ImporteTotal.setToolTipText(toolTipImporte);
    }

    /**
     * Configura el campo de texto relacionado al precio por botella. Muestra un
     * tooltip con información sobre la dependencia del precio de la botella
     * seleccionada.
     */
    private void configuracionTextField_PrecioBotella() {
        String toolTipImporte = "Depende de la botella seleccionada";
        jTextField_PrecioBotella.setToolTipText(toolTipImporte);
    }

    /**
     * Restablece todos los elementos en la ventana a sus valores por defecto.
     * Limpia y reinicia los campos, selecciona opciones predeterminadas y
     * actualiza las tablas.
     */
    private void restablecerTodosElementos() {
        jComboBox_Bodegas_VentaVino.setSelectedIndex(0);
        jComboBox_Bodegas_ConsultaVentas.setSelectedIndex(0);
        jComboBox_Clientes_VentaVino.setSelectedIndex(0);

        jTable_Vinos_VentaVino.clearSelection();
        jTable_Comerciales_VentaVino.clearSelection();
        jTable_Comerciales_ConsultaVentas.clearSelection();

        cargarTabla_Comerciales_VentaVino();
        cargarTabla_Comerciales_ConsultarVentas();
        cargarTabla_Vinos_VentaVino();

        jTextField_NumBotellas.setText("");
        jTextField_ImporteTotal.setText("");
        jTextField_PrecioBotella.setText("");

        cargarFechaActual();

        _habilitadoCampoNumBotellas = false;
        establecerConfiguraciones();
    }

    /**
     * Restablece el contenido de los campos de texto relacionados a la venta de
     * vinos.
     */
    private void restablecerCamposTexto() {
        jTextField_NumBotellas.setText("");
        jTextField_ImporteTotal.setText("");
        jTextField_PrecioBotella.setText("");
    }

    /**
     * Cambia el estado del campo de botellas según la habilitación actual.
     * Llama a la configuración del campo de botellas si está habilitado.
     */
    private void cambiarEstadoCampoBotellas() {
        jTextField_NumBotellas.setEnabled(_habilitadoCampoNumBotellas);
        if (_habilitadoCampoNumBotellas) {
            configuracionTextField_Botellas();
        }
    }

    /**
     * Establece el precio por botella y la cantidad de botellas disponibles
     * según la selección en la tabla de vinos. Actualiza el campo de texto de
     * precio por botella y la cantidad de botellas disponibles.
     */
    private void establecerPrecioBotella() {
        int filaSeleccionada = jTable_Vinos_VentaVino.getSelectedRow();
        String[] datosVinoSeleccionado;
        Object datosVino;

        if (filaSeleccionada != -1) {
            datosVino = jTable_Vinos_VentaVino.getValueAt(filaSeleccionada, 0);

            if (datosVino != null) {
                datosVinoSeleccionado = datosVino.toString().split(": ");
                datosVinoSeleccionado = datosVinoSeleccionado[1].split(" ");
                _importeBotellas = Float.parseFloat(datosVinoSeleccionado[0]);

                jTextField_PrecioBotella.setText(datosVinoSeleccionado[0] + "€");
                _cantBotellasDisponibles = Integer.parseInt(datosVinoSeleccionado[1].substring(1, datosVinoSeleccionado[1].length() - 1));
            }
        } else {
            jTextField_PrecioBotella.setText("");
            _cantBotellasDisponibles = -1;
        }
    }

    /**
     * Calcula e actualiza el importe total de la compra según la cantidad de
     * botellas y el precio por botella.
     */
    private void calcularImporteTotalCompra() {
        int cantBotellas = Integer.parseInt(jTextField_NumBotellas.getText());
        float importeTotal = cantBotellas * _importeBotellas;
        jTextField_ImporteTotal.setText(importeTotal + "€");
    }

    /**
     * Verifica y comprueba los datos antes de grabar una venta.
     *
     * @return True si los datos son válidos, False si hay algún problema.
     */
    private boolean comprobarDatosAntesGrabarVenta() {
        // Comprobar si se ha seleccionado un vino en la tabla de vinos
        if (jTable_Vinos_VentaVino.getSelectedRow() < 0) {
            mensajeError((byte) 4);
            return false;
        }

        try {
            // Obtener y comprobar la cantidad de botellas ingresada
            _cantBotellas = jTextField_NumBotellas.getText();
            int numBotellas = Integer.parseInt(_cantBotellas);
            if (numBotellas > _cantBotellasDisponibles) {
                mensajeError((byte) 1);
                return false;
            } else if (numBotellas <= 0) {
                mensajeError((byte) 2);
                return false;
            }
        } catch (NumberFormatException e) {
            // Mostrar mensaje de error si la cantidad de botellas no es un número válido
            mensajeError((byte) 3);
            return false;
        }

        // Comprobar si se ha seleccionado un comercial en la tabla de comerciales
        if (jTable_Comerciales_VentaVino.getSelectedRow() < 0) {
            mensajeError((byte) 5);
            return false;
        }

        // Todos los datos son válidos
        return true;
    }

    /**
     * Obtiene los datos introducidos por el usuario en la ventana de ventas y
     * crea un objeto Venta con esos datos.
     *
     * @return Objeto Venta con los datos introducidos por el usuario.
     * @throws ParseException Si hay un error al convertir la cadena de fecha a
     * un objeto Date.
     */
    private Venta obtenerDatosIntroducidos() throws ParseException {
        // Crear una nueva instancia de Venta para almacenar los datos introducidos
        Venta datosVenta = new Venta();

        // Obtener la cantidad de botellas y el importe de la venta
        datosVenta.setNumbotellas(Integer.parseInt(jTextField_NumBotellas.getText()));
        datosVenta.setImporte(obtenerImporteVenta());

        // Obtener y parsear la fecha de compra
        String fechaCompra = jTextField_FechaCompra.getText();
        String formatoFecha = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(formatoFecha);
        datosVenta.setFecha(sdf.parse(fechaCompra));

        // Obtener los datos del vino, comercial y cliente
        datosVenta.setVino(obtenerDatosVino());
        datosVenta.setComercial(obtenerDatosComercial());
        datosVenta.setCliente(obtenerDatosCliente());

        return datosVenta;
    }

    /**
     * Obtiene el importe de la venta desde el campo de texto y lo convierte a
     * un valor numérico.
     *
     * @return Valor numérico del importe de la venta.
     */
    private float obtenerImporteVenta() {
        String importeSinFormatear = jTextField_ImporteTotal.getText();
        return Float.parseFloat(importeSinFormatear.substring(0, jTextField_ImporteTotal.getText().length() - 1));
    }

    /**
     * Obtiene los datos del vino seleccionado en la tabla de vinos de la
     * ventana de ventas.
     *
     * @return Objeto Vino con los datos del vino seleccionado.
     */
    private Vino obtenerDatosVino() {
        // Crear una nueva instancia de Vino para almacenar los datos
        Vino vinoSeleccionado = new Vino();
        String[] datosVinoSeleccionado;

        // Obtener la fila seleccionada en la tabla de vinos
        int filaSeleccionada = jTable_Vinos_VentaVino.getSelectedRow();

        if (filaSeleccionada != -1) {
            // Obtener el valor de la primera columna en la fila seleccionada
            Object valorColumna1 = jTable_Vinos_VentaVino.getValueAt(filaSeleccionada, 0);

            if (valorColumna1 != null) {
                // Dividir los datos del vino según el formato "Denominación - Tipo: ..."
                datosVinoSeleccionado = valorColumna1.toString().split(" - ");
                vinoSeleccionado.setDenominacion(datosVinoSeleccionado[0]);

                // Dividir los datos del tipo de vino según el formato "Tipo: ..."
                datosVinoSeleccionado = datosVinoSeleccionado[1].split(": ");
                vinoSeleccionado.setTipo(datosVinoSeleccionado[0]);
            }
        }

        // Obtener los datos de la bodega asociada al vino
        vinoSeleccionado.setBodega(obtenerDatosBodega());
        return vinoSeleccionado;
    }

    /**
     * Obtiene los datos de la bodega seleccionada en el ComboBox de bodegas.
     *
     * @return Objeto Bodega con los datos de la bodega seleccionada.
     */
    private Bodega obtenerDatosBodega() {
        // Crear una nueva instancia de Bodega para almacenar los datos
        Bodega bodegaSeleccionada = new Bodega();

        // Obtener el objeto bodega seleccionado en el ComboBox
        Object bodega = jComboBox_Bodegas_VentaVino.getSelectedItem();
        String[] datosBodega = bodega.toString().split(" - ");

        // Establecer los datos de la bodega
        bodegaSeleccionada.setNombodega(datosBodega[0]);
        bodegaSeleccionada.setLocalidad(datosBodega[1]);

        return bodegaSeleccionada;
    }

    /**
     * Obtiene los datos del comercial seleccionado en la tabla de comerciales
     * de la ventana de ventas.
     *
     * @return Objeto Comercial con los datos del comercial seleccionado.
     */
    private Comercial obtenerDatosComercial() {
        // Crear una nueva instancia de Comercial para almacenar los datos
        Comercial comercialSeleccionado = new Comercial();
        String[] datosComercialSeleccionado;
        Object datosColumna;

        // Obtener la fila seleccionada en la tabla de comerciales
        int filaSeleccionada = jTable_Comerciales_VentaVino.getSelectedRow();

        if (filaSeleccionada != -1) {
            // Obtener los datos de la primera columna en la fila seleccionada
            datosColumna = jTable_Comerciales_VentaVino.getValueAt(filaSeleccionada, 0);
            comercialSeleccionado.setDni(Integer.parseInt(datosColumna.toString()));

            // Obtener los datos de la segunda columna en la fila seleccionada
            datosColumna = jTable_Comerciales_VentaVino.getValueAt(filaSeleccionada, 1);
            comercialSeleccionado.setNomcomercial(datosColumna.toString());
        }
        return comercialSeleccionado;
    }

    /**
     * Obtiene los datos del cliente seleccionado en el ComboBox de clientes de
     * la ventana de ventas.
     *
     * @return Objeto Cliente con los datos del cliente seleccionado.
     */
    private Cliente obtenerDatosCliente() {
        // Crear una nueva instancia de Cliente para almacenar los datos
        Cliente clienteSeleccionado = new Cliente();

        // Obtener el objeto cliente seleccionado en el ComboBox
        Object cliente = jComboBox_Clientes_VentaVino.getSelectedItem();
        String[] datosCliente = cliente.toString().split(" - ");

        // Establecer los datos del cliente
        clienteSeleccionado.setNomcliente(datosCliente[0]);
        clienteSeleccionado.setPoblacion(datosCliente[1]);

        return clienteSeleccionado;
    }

    /**
     * Quita las filas seleccionadas en las tablas de vinos y comerciales de la
     * ventana de ventas. Deshabilita el campo de número de botellas si se
     * deselecciona un vino.
     */
    private void quitarFilasSeleccionadas_TablasVinosComerciales() {
        // Verificar si hay una fila seleccionada en la tabla de vinos
        if (jTable_Vinos_VentaVino.getSelectedRow() != -1) {
            jTable_Vinos_VentaVino.clearSelection();
            _habilitadoCampoNumBotellas = false;
            cambiarEstadoCampoBotellas();
        }

        // Verificar si hay una fila seleccionada en la tabla de comerciales
        if (jTable_Comerciales_VentaVino.getSelectedRow() != -1) {
            jTable_Comerciales_VentaVino.clearSelection();
        }
    }

    /**
     * Carga la comisión del comercial seleccionado en la tabla de comerciales
     * de la ventana de Consultar Ventas. Muestra el resultado en el campo de
     * texto correspondiente.
     */
    private void cargarComisionComercial() {
        // Formatear la comisión a dos decimales
        DecimalFormat df = new DecimalFormat("#.##");
        String numeroRedondeado = df.format(_baseDatos.calcularComision(obtenerComercialSeleccionado(jTable_Comerciales_ConsultaVentas, (byte) 1)));

        // Mostrar la comisión en el campo de texto
        jTextField_ComisionComercial_ConsultarVentas.setText(numeroRedondeado + "€");
    }

    /**
    * Muestra mensajes de error o avisos en un JLabel según el código de error proporcionado.
    * Configura la apariencia y contenido del mensaje antes de mostrarlo.
    *
    * @param idError Código de error que determina el tipo de mensaje a mostrar.
    */
   private void mensajeError(byte idError) {
       // Configurar la apariencia y el contenido del mensaje de aviso
       establecerConfiguracionMensajeAviso(idError);

       // Mostrar mensajes de error o avisos según el código de error
       switch (idError) {
           // Errores internos relacionados con bodegas, clientes y tablas
           case -1 -> jLabel_MensajeAviso.setText("<html><b>ERROR INTERNO:</b> Hay un error con las bodegas de la ventana<br>de Venta Vinos</html>");
           case -2 -> jLabel_MensajeAviso.setText("<html><b>ERROR INTERNO:</b> Hay un error con las bodegas de la ventana<br>de Consultar Ventas</html>");
           case -3 -> jLabel_MensajeAviso.setText("<html><b>ERROR INTERNO:</b> Hay un error con los clientes de Venta Vinos</html>");
           case -4 -> jLabel_MensajeAviso.setText("<html><b>ERROR INTERNO:</b> Se ha producido un error al rellenar la tabla<br>de vinos de la ventana de Venta Vinos</html>");
           case -5 -> jLabel_MensajeAviso.setText("<html><b>ERROR INTERNO:</b> Se ha producido un error al rellenar la tabla<br>de comerciales de la ventana de Venta Vinos</html>");
           case -6 -> jLabel_MensajeAviso.setText("<html><b>ERROR INTERNO:</b> Se ha producido un error al rellenar la tabla<br>de comerciales de la ventana de Consultar Ventas</html>");
           case -7 -> jLabel_MensajeAviso.setText("<html><b>ERROR INTERNO:</b> Se ha producido un error al rellenar la tabla<br>de ventas de la ventana de Consultar Ventas</html>");

           // Avisos relacionados con la venta de vinos
           case 1 -> jLabel_MensajeAviso.setText("<html><b>AVISO:</b> Estás superando el stock de botellas disponibles <b><u>(" + _cantBotellas + ")</u></b> para vender</html>");
           case 2 -> jLabel_MensajeAviso.setText("<html><b>AVISO:</b> Estás vendiendo una cantidad de botellas que no es válida <b><u>(" + _cantBotellas + ")</u></b>.<br>Recuerda que el número ha de ser mayor que 0</html>");
           case 3 -> jLabel_MensajeAviso.setText("<html><b>AVISO:</b> El valor que has introducido <b><u>(" + _cantBotellas + ")</u></b> no es válido.<br>Recuerda que ha de ser un número mayor que 0</html>");
           case 4 -> jLabel_MensajeAviso.setText("<html><b>AVISO:</b> Has de seleccionar un vino antes de grabar la venta</html>");
           case 5 -> jLabel_MensajeAviso.setText("<html><b>AVISO:</b> Has de seleccionar un comercial antes de grabar la venta</html>");

           // Confirmación de venta exitosa
           case 0 -> jLabel_MensajeAviso.setText("<html><b>AVISO:</b> La venta ha sido registrada en el sistema</html>");
       }
   }

   /**
    * Establece la configuración visual del cuadro de diálogo de mensajes de aviso antes de mostrarlo.
    *
    * @param idError Código de error que determina el tipo de mensaje a configurar.
    */
   private void establecerConfiguracionMensajeAviso(byte idError) {
       // Crear un icono para el cuadro de diálogo de mensajes de aviso
       ImageIcon iconoApp = new ImageIcon(_rutaIconoAviso);

       // Configurar la apariencia y propiedades del cuadro de diálogo de mensajes de aviso
       jDialog_VentanaAvisos.setIconImage(iconoApp.getImage());
       jDialog_VentanaAvisos.setTitle("AVISO DEL SISTEMA");
       jDialog_VentanaAvisos.setResizable(true);
       jDialog_VentanaAvisos.setLocationRelativeTo(null);

       // Hacer visible el cuadro de diálogo de mensajes de aviso
       jDialog_VentanaAvisos.setVisible(true);
   }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialog_VentanaAvisos = new javax.swing.JDialog();
        jLabel_MensajeAviso = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jTabbedPane_Ventanas = new javax.swing.JTabbedPane();
        jPanel_VentaVino = new javax.swing.JPanel();
        jPanel_PanelSuperior_VentaVino = new javax.swing.JPanel();
        jComboBox_Bodegas_VentaVino = new javax.swing.JComboBox<>();
        jLabel_Bodega_VentaVino = new javax.swing.JLabel();
        jLabel_Vino = new javax.swing.JLabel();
        jTable_Vinos_VentaVino = new javax.swing.JTable();
        jLabel_Comerciales_VentaVino = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable_Comerciales_VentaVino = new javax.swing.JTable();
        jPanel_PanelInferiorIzquierdo_VentaVino = new javax.swing.JPanel();
        jLabel_Botellas = new javax.swing.JLabel();
        jLabel_Cliente1 = new javax.swing.JLabel();
        jLabel_Fecha = new javax.swing.JLabel();
        jLabel_Importe = new javax.swing.JLabel();
        jTextField_NumBotellas = new javax.swing.JTextField();
        jTextField_ImporteTotal = new javax.swing.JTextField();
        jTextField_FechaCompra = new javax.swing.JTextField();
        jTextField_PrecioBotella = new javax.swing.JTextField();
        jLabel_x = new javax.swing.JLabel();
        jLabel_igual = new javax.swing.JLabel();
        jComboBox_Clientes_VentaVino = new javax.swing.JComboBox<>();
        jPanel_InferiorDerecho_VentaVinos = new javax.swing.JPanel();
        jLabel_Cliente2 = new javax.swing.JLabel();
        jButton_LimpiarCampos = new javax.swing.JButton();
        jButton_GrabarVenta = new javax.swing.JButton();
        jPanel_ConsultarVentas = new javax.swing.JPanel();
        jPanel_Interno = new javax.swing.JPanel();
        jLabel_Bodega_ConsultaVentas = new javax.swing.JLabel();
        jComboBox_Bodegas_ConsultaVentas = new javax.swing.JComboBox<>();
        jLabel_Comerciales_ConsultaVentas = new javax.swing.JLabel();
        jTable_Comerciales_ConsultaVentas = new javax.swing.JTable();
        jLabel_Ventas = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_Ventas_ConsultarVentas = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jTextField_ComisionComercial_ConsultarVentas = new javax.swing.JTextField();

        jDialog_VentanaAvisos.setMinimumSize(new java.awt.Dimension(400, 150));

        jLabel_MensajeAviso.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        jLabel_MensajeAviso.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jButton1.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        jButton1.setText("ACEPTAR");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cerrarJDialog(evt);
            }
        });
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jDialog_VentanaAvisosLayout = new javax.swing.GroupLayout(jDialog_VentanaAvisos.getContentPane());
        jDialog_VentanaAvisos.getContentPane().setLayout(jDialog_VentanaAvisosLayout);
        jDialog_VentanaAvisosLayout.setHorizontalGroup(
            jDialog_VentanaAvisosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog_VentanaAvisosLayout.createSequentialGroup()
                .addGroup(jDialog_VentanaAvisosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDialog_VentanaAvisosLayout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(jLabel_MensajeAviso, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jDialog_VentanaAvisosLayout.createSequentialGroup()
                        .addGap(147, 147, 147)
                        .addComponent(jButton1)))
                .addGap(25, 25, 25))
        );
        jDialog_VentanaAvisosLayout.setVerticalGroup(
            jDialog_VentanaAvisosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog_VentanaAvisosLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel_MensajeAviso, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                .addGap(10, 10, 10)
                .addComponent(jButton1)
                .addGap(10, 10, 10))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedPane_Ventanas.setFont(new java.awt.Font("Arial", 1, 15)); // NOI18N

        jPanel_PanelSuperior_VentaVino.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        jComboBox_Bodegas_VentaVino.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        jComboBox_Bodegas_VentaVino.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cambiarDatosBodega_VentaVino(evt);
            }
        });

        jLabel_Bodega_VentaVino.setFont(new java.awt.Font("Arial Black", 0, 15)); // NOI18N
        jLabel_Bodega_VentaVino.setText("Bodegas");

        jLabel_Vino.setFont(new java.awt.Font("Arial Black", 0, 15)); // NOI18N
        jLabel_Vino.setText("Vinos");

        jTable_Vinos_VentaVino.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        jTable_Vinos_VentaVino.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "null"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_Vinos_VentaVino.getTableHeader().setReorderingAllowed(false);
        jTable_Vinos_VentaVino.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                habilitarCampos_NumBotellasImporteBotella(evt);
            }
        });

        jLabel_Comerciales_VentaVino.setFont(new java.awt.Font("Arial Black", 0, 15)); // NOI18N
        jLabel_Comerciales_VentaVino.setText("Comerciales");

        jTable_Comerciales_VentaVino.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        jTable_Comerciales_VentaVino.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "DNI", "NOMBRE", "VENTAS"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_Comerciales_VentaVino.getTableHeader().setReorderingAllowed(false);
        jTable_Comerciales_VentaVino.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_Comerciales_VentaVinocargarEquiposParaResultadoPartido(evt);
            }
        });
        jScrollPane2.setViewportView(jTable_Comerciales_VentaVino);
        if (jTable_Comerciales_VentaVino.getColumnModel().getColumnCount() > 0) {
            jTable_Comerciales_VentaVino.getColumnModel().getColumn(0).setResizable(false);
            jTable_Comerciales_VentaVino.getColumnModel().getColumn(0).setPreferredWidth(10);
            jTable_Comerciales_VentaVino.getColumnModel().getColumn(2).setResizable(false);
            jTable_Comerciales_VentaVino.getColumnModel().getColumn(2).setPreferredWidth(10);
        }

        javax.swing.GroupLayout jPanel_PanelSuperior_VentaVinoLayout = new javax.swing.GroupLayout(jPanel_PanelSuperior_VentaVino);
        jPanel_PanelSuperior_VentaVino.setLayout(jPanel_PanelSuperior_VentaVinoLayout);
        jPanel_PanelSuperior_VentaVinoLayout.setHorizontalGroup(
            jPanel_PanelSuperior_VentaVinoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_PanelSuperior_VentaVinoLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel_PanelSuperior_VentaVinoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_PanelSuperior_VentaVinoLayout.createSequentialGroup()
                        .addComponent(jLabel_Bodega_VentaVino)
                        .addGap(25, 25, 25)
                        .addComponent(jComboBox_Bodegas_VentaVino, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_PanelSuperior_VentaVinoLayout.createSequentialGroup()
                        .addGroup(jPanel_PanelSuperior_VentaVinoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel_Vino)
                            .addComponent(jTable_Vinos_VentaVino, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(50, 50, 50)
                        .addGroup(jPanel_PanelSuperior_VentaVinoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 355, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel_Comerciales_VentaVino))))
                .addGap(25, 25, 25))
        );
        jPanel_PanelSuperior_VentaVinoLayout.setVerticalGroup(
            jPanel_PanelSuperior_VentaVinoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_PanelSuperior_VentaVinoLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel_PanelSuperior_VentaVinoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel_Bodega_VentaVino)
                    .addComponent(jComboBox_Bodegas_VentaVino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel_PanelSuperior_VentaVinoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel_Vino)
                    .addComponent(jLabel_Comerciales_VentaVino, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel_PanelSuperior_VentaVinoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jTable_Vinos_VentaVino, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE))
                .addGap(25, 25, 25))
        );

        if (jTable_Vinos_VentaVino.getColumnModel().getColumnCount() > 0) {
            jTable_Vinos_VentaVino.getColumnModel().getColumn(0).setResizable(false);
        }

        jPanel_PanelInferiorIzquierdo_VentaVino.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        jLabel_Botellas.setFont(new java.awt.Font("Arial Black", 0, 15)); // NOI18N
        jLabel_Botellas.setText("Cant. botellas");

        jLabel_Cliente1.setFont(new java.awt.Font("Arial Black", 0, 15)); // NOI18N
        jLabel_Cliente1.setText("Clientes");

        jLabel_Fecha.setFont(new java.awt.Font("Arial Black", 0, 15)); // NOI18N
        jLabel_Fecha.setText("Fecha");

        jLabel_Importe.setFont(new java.awt.Font("Arial Black", 0, 15)); // NOI18N
        jLabel_Importe.setText("Importe");

        jTextField_NumBotellas.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        jTextField_NumBotellas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calcularImporte_DependiendoNumBotellas(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                filtrarEntradaTexto(evt);
            }
        });

        jTextField_ImporteTotal.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        jTextField_ImporteTotal.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextField_ImporteTotal.setEnabled(false);

        jTextField_FechaCompra.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        jTextField_FechaCompra.setText("2023-10-10");
        jTextField_FechaCompra.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextField_FechaCompra.setEnabled(false);

        jTextField_PrecioBotella.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        jTextField_PrecioBotella.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextField_PrecioBotella.setEnabled(false);

        jLabel_x.setFont(new java.awt.Font("Arial Black", 0, 15)); // NOI18N
        jLabel_x.setText("x");

        jLabel_igual.setFont(new java.awt.Font("Arial Black", 0, 15)); // NOI18N
        jLabel_igual.setText("=");

        jComboBox_Clientes_VentaVino.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N

        javax.swing.GroupLayout jPanel_PanelInferiorIzquierdo_VentaVinoLayout = new javax.swing.GroupLayout(jPanel_PanelInferiorIzquierdo_VentaVino);
        jPanel_PanelInferiorIzquierdo_VentaVino.setLayout(jPanel_PanelInferiorIzquierdo_VentaVinoLayout);
        jPanel_PanelInferiorIzquierdo_VentaVinoLayout.setHorizontalGroup(
            jPanel_PanelInferiorIzquierdo_VentaVinoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_PanelInferiorIzquierdo_VentaVinoLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel_PanelInferiorIzquierdo_VentaVinoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_PanelInferiorIzquierdo_VentaVinoLayout.createSequentialGroup()
                        .addComponent(jLabel_Cliente1)
                        .addGap(25, 25, 25)
                        .addComponent(jComboBox_Clientes_VentaVino, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_PanelInferiorIzquierdo_VentaVinoLayout.createSequentialGroup()
                        .addGroup(jPanel_PanelInferiorIzquierdo_VentaVinoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel_Botellas, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel_PanelInferiorIzquierdo_VentaVinoLayout.createSequentialGroup()
                                .addComponent(jTextField_NumBotellas, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(jLabel_x)
                                .addGap(10, 10, 10)
                                .addComponent(jTextField_PrecioBotella, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(10, 10, 10)
                        .addComponent(jLabel_igual)
                        .addGap(10, 10, 10)
                        .addGroup(jPanel_PanelInferiorIzquierdo_VentaVinoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel_Importe)
                            .addComponent(jTextField_ImporteTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addGroup(jPanel_PanelInferiorIzquierdo_VentaVinoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField_FechaCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel_Fecha))))
                .addGap(25, 25, 25))
        );
        jPanel_PanelInferiorIzquierdo_VentaVinoLayout.setVerticalGroup(
            jPanel_PanelInferiorIzquierdo_VentaVinoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_PanelInferiorIzquierdo_VentaVinoLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel_PanelInferiorIzquierdo_VentaVinoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel_Cliente1)
                    .addComponent(jComboBox_Clientes_VentaVino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel_PanelInferiorIzquierdo_VentaVinoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel_Botellas)
                    .addComponent(jLabel_Importe)
                    .addComponent(jLabel_Fecha))
                .addGap(10, 10, 10)
                .addGroup(jPanel_PanelInferiorIzquierdo_VentaVinoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField_NumBotellas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField_PrecioBotella, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel_x)
                    .addComponent(jLabel_igual)
                    .addComponent(jTextField_ImporteTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField_FechaCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27))
        );

        jPanel_InferiorDerecho_VentaVinos.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        jLabel_Cliente2.setFont(new java.awt.Font("Arial Black", 0, 15)); // NOI18N
        jLabel_Cliente2.setText("Acciones");

        jButton_LimpiarCampos.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        jButton_LimpiarCampos.setText("LIMPIAR CAMPOS");
        jButton_LimpiarCampos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                limpiarTodosCampos(evt);
            }
        });

        jButton_GrabarVenta.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        jButton_GrabarVenta.setText("GRABAR VENTA");
        jButton_GrabarVenta.setActionCommand("");
        jButton_GrabarVenta.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                grabarVenta(evt);
            }
        });

        javax.swing.GroupLayout jPanel_InferiorDerecho_VentaVinosLayout = new javax.swing.GroupLayout(jPanel_InferiorDerecho_VentaVinos);
        jPanel_InferiorDerecho_VentaVinos.setLayout(jPanel_InferiorDerecho_VentaVinosLayout);
        jPanel_InferiorDerecho_VentaVinosLayout.setHorizontalGroup(
            jPanel_InferiorDerecho_VentaVinosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_InferiorDerecho_VentaVinosLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel_InferiorDerecho_VentaVinosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel_Cliente2)
                    .addComponent(jButton_LimpiarCampos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton_GrabarVenta, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(25, 25, 25))
        );
        jPanel_InferiorDerecho_VentaVinosLayout.setVerticalGroup(
            jPanel_InferiorDerecho_VentaVinosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_InferiorDerecho_VentaVinosLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel_Cliente2)
                .addGap(10, 10, 10)
                .addComponent(jButton_LimpiarCampos)
                .addGap(10, 10, 10)
                .addComponent(jButton_GrabarVenta)
                .addGap(25, 25, 25))
        );

        javax.swing.GroupLayout jPanel_VentaVinoLayout = new javax.swing.GroupLayout(jPanel_VentaVino);
        jPanel_VentaVino.setLayout(jPanel_VentaVinoLayout);
        jPanel_VentaVinoLayout.setHorizontalGroup(
            jPanel_VentaVinoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_VentaVinoLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel_VentaVinoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel_PanelSuperior_VentaVino, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel_VentaVinoLayout.createSequentialGroup()
                        .addComponent(jPanel_PanelInferiorIzquierdo_VentaVino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25)
                        .addComponent(jPanel_InferiorDerecho_VentaVinos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(25, 25, 25))
        );
        jPanel_VentaVinoLayout.setVerticalGroup(
            jPanel_VentaVinoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_VentaVinoLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jPanel_PanelSuperior_VentaVino, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(20, 20, 20)
                .addGroup(jPanel_VentaVinoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_PanelInferiorIzquierdo_VentaVino, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel_InferiorDerecho_VentaVinos, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25))
        );

        jTabbedPane_Ventanas.addTab("VENTA DE VINO", jPanel_VentaVino);

        jPanel_Interno.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        jLabel_Bodega_ConsultaVentas.setFont(new java.awt.Font("Arial Black", 0, 15)); // NOI18N
        jLabel_Bodega_ConsultaVentas.setText("Bodegas");

        jComboBox_Bodegas_ConsultaVentas.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        jComboBox_Bodegas_ConsultaVentas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cambiarDatosBodega_ConsultarVentas(evt);
            }
        });

        jLabel_Comerciales_ConsultaVentas.setFont(new java.awt.Font("Arial Black", 0, 15)); // NOI18N
        jLabel_Comerciales_ConsultaVentas.setText("Comerciales");

        jTable_Comerciales_ConsultaVentas.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        jTable_Comerciales_ConsultaVentas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_Comerciales_ConsultaVentas.getTableHeader().setReorderingAllowed(false);
        jTable_Comerciales_ConsultaVentas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rellenarTablaVentas_ConsultarVentas(evt);
            }
        });

        jLabel_Ventas.setFont(new java.awt.Font("Arial Black", 0, 15)); // NOI18N
        jLabel_Ventas.setText("Ventas");

        jTable_Ventas_ConsultarVentas.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        jTable_Ventas_ConsultarVentas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Cliente", "Vino", "Botellas", "Importe"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_Ventas_ConsultarVentas.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable_Ventas_ConsultarVentas);

        jLabel1.setFont(new java.awt.Font("Arial", 1, 15)); // NOI18N
        jLabel1.setText("Comision Comercial:");

        jTextField_ComisionComercial_ConsultarVentas.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        jTextField_ComisionComercial_ConsultarVentas.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextField_ComisionComercial_ConsultarVentas.setEnabled(false);

        javax.swing.GroupLayout jPanel_InternoLayout = new javax.swing.GroupLayout(jPanel_Interno);
        jPanel_Interno.setLayout(jPanel_InternoLayout);
        jPanel_InternoLayout.setHorizontalGroup(
            jPanel_InternoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_InternoLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel_InternoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1)
                    .addComponent(jLabel_Ventas)
                    .addGroup(jPanel_InternoLayout.createSequentialGroup()
                        .addGroup(jPanel_InternoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jComboBox_Bodegas_ConsultaVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel_Bodega_ConsultaVentas)
                            .addGroup(jPanel_InternoLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(10, 10, 10)
                                .addComponent(jTextField_ComisionComercial_ConsultarVentas)))
                        .addGap(20, 20, 20)
                        .addGroup(jPanel_InternoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel_Comerciales_ConsultaVentas)
                            .addComponent(jTable_Comerciales_ConsultaVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(25, 25, 25))
        );
        jPanel_InternoLayout.setVerticalGroup(
            jPanel_InternoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_InternoLayout.createSequentialGroup()
                .addGroup(jPanel_InternoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_InternoLayout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(jLabel_Comerciales_ConsultaVentas))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_InternoLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel_Bodega_ConsultaVentas)))
                .addGap(10, 10, 10)
                .addGroup(jPanel_InternoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_InternoLayout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addGroup(jPanel_InternoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jTextField_ComisionComercial_ConsultarVentas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel_InternoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jComboBox_Bodegas_ConsultaVentas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTable_Comerciales_ConsultaVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(20, 20, 20)
                .addComponent(jLabel_Ventas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );

        if (jTable_Comerciales_ConsultaVentas.getColumnModel().getColumnCount() > 0) {
            jTable_Comerciales_ConsultaVentas.getColumnModel().getColumn(0).setResizable(false);
        }

        javax.swing.GroupLayout jPanel_ConsultarVentasLayout = new javax.swing.GroupLayout(jPanel_ConsultarVentas);
        jPanel_ConsultarVentas.setLayout(jPanel_ConsultarVentasLayout);
        jPanel_ConsultarVentasLayout.setHorizontalGroup(
            jPanel_ConsultarVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_ConsultarVentasLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jPanel_Interno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );
        jPanel_ConsultarVentasLayout.setVerticalGroup(
            jPanel_ConsultarVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_ConsultarVentasLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jPanel_Interno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );

        jTabbedPane_Ventanas.addTab("CONSULTAR VENTAS", jPanel_ConsultarVentas);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane_Ventanas, javax.swing.GroupLayout.PREFERRED_SIZE, 751, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane_Ventanas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable_Comerciales_VentaVinocargarEquiposParaResultadoPartido(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_Comerciales_VentaVinocargarEquiposParaResultadoPartido
        if (evt.getButton() == MouseEvent.BUTTON3) {
            jTable_Comerciales_VentaVino.clearSelection();
        }
    }//GEN-LAST:event_jTable_Comerciales_VentaVinocargarEquiposParaResultadoPartido

    private void cambiarDatosBodega_VentaVino(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cambiarDatosBodega_VentaVino
        quitarFilasSeleccionadas_TablasVinosComerciales();

        cargarTabla_Vinos_VentaVino();
        cargarTabla_Comerciales_VentaVino();

        restablecerCamposTexto();
    }//GEN-LAST:event_cambiarDatosBodega_VentaVino

    private void habilitarCampos_NumBotellasImporteBotella(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_habilitarCampos_NumBotellasImporteBotella
        if (evt.getButton() == MouseEvent.BUTTON1) {
            restablecerCamposTexto();
            _habilitadoCampoNumBotellas = true;
            establecerPrecioBotella();
        } else {
            _habilitadoCampoNumBotellas = false;
            jTable_Vinos_VentaVino.clearSelection();
            restablecerCamposTexto();
        }
        cambiarEstadoCampoBotellas();
    }//GEN-LAST:event_habilitarCampos_NumBotellasImporteBotella

    private void filtrarEntradaTexto(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_filtrarEntradaTexto
        if (!Character.isDigit(evt.getKeyChar())) {
            evt.consume();
        }
    }//GEN-LAST:event_filtrarEntradaTexto

    private void limpiarTodosCampos(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_limpiarTodosCampos
        restablecerTodosElementos();
    }//GEN-LAST:event_limpiarTodosCampos

    private void grabarVenta(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_grabarVenta
        if (comprobarDatosAntesGrabarVenta()) {
            try {
                _baseDatos.grabarResultadoVenta(obtenerDatosIntroducidos());
                mensajeError((byte) 0);
            } catch (ParseException ex) {
                Logger.getLogger(InterfazGrafica.class.getName()).log(Level.SEVERE, null, ex);
            }
            restablecerTodosElementos();
        }
    }//GEN-LAST:event_grabarVenta

    private void calcularImporte_DependiendoNumBotellas(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_calcularImporte_DependiendoNumBotellas
        if (!jTextField_NumBotellas.getText().isEmpty() && jTextField_NumBotellas.getText().matches("\\d+")) {
            calcularImporteTotalCompra();

            if (!jTextField_NumBotellas.getText().isEmpty()) {
                try {
                    _cantBotellas = jTextField_NumBotellas.getText();
                } catch (NumberFormatException e) {
                    mensajeError((byte) 3);
                }
            }
        } else {
            jTextField_ImporteTotal.setText("");
            evt.consume();
        }
    }//GEN-LAST:event_calcularImporte_DependiendoNumBotellas

    private void cambiarDatosBodega_ConsultarVentas(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cambiarDatosBodega_ConsultarVentas
        jTable_Comerciales_ConsultaVentas.clearSelection();
        jTable_Ventas_ConsultarVentas.clearSelection();

        jTextField_ComisionComercial_ConsultarVentas.setText("");

        limpiarTabla(jTable_Ventas_ConsultarVentas);
        cargarTabla_Comerciales_ConsultarVentas();
    }//GEN-LAST:event_cambiarDatosBodega_ConsultarVentas

    private void rellenarTablaVentas_ConsultarVentas(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rellenarTablaVentas_ConsultarVentas
        if (evt.getButton() == MouseEvent.BUTTON1) {
            cargarComisionComercial();
            cargarTabla_Ventas_ConsultarVentas();
        } else {
            jTable_Comerciales_ConsultaVentas.clearSelection();
            jTextField_ComisionComercial_ConsultarVentas.setText("");
            limpiarTabla(jTable_Ventas_ConsultarVentas);
        }
    }//GEN-LAST:event_rellenarTablaVentas_ConsultarVentas

    private void cerrarJDialog(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cerrarJDialog
        jDialog_VentanaAvisos.setVisible(false);
    }//GEN-LAST:event_cerrarJDialog

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(InterfazGrafica.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InterfazGrafica.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InterfazGrafica.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InterfazGrafica.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new InterfazGrafica().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton_GrabarVenta;
    private javax.swing.JButton jButton_LimpiarCampos;
    private javax.swing.JComboBox<String> jComboBox_Bodegas_ConsultaVentas;
    private javax.swing.JComboBox<String> jComboBox_Bodegas_VentaVino;
    private javax.swing.JComboBox<String> jComboBox_Clientes_VentaVino;
    private javax.swing.JDialog jDialog_VentanaAvisos;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel_Bodega_ConsultaVentas;
    private javax.swing.JLabel jLabel_Bodega_VentaVino;
    private javax.swing.JLabel jLabel_Botellas;
    private javax.swing.JLabel jLabel_Cliente1;
    private javax.swing.JLabel jLabel_Cliente2;
    private javax.swing.JLabel jLabel_Comerciales_ConsultaVentas;
    private javax.swing.JLabel jLabel_Comerciales_VentaVino;
    private javax.swing.JLabel jLabel_Fecha;
    private javax.swing.JLabel jLabel_Importe;
    private javax.swing.JLabel jLabel_MensajeAviso;
    private javax.swing.JLabel jLabel_Ventas;
    private javax.swing.JLabel jLabel_Vino;
    private javax.swing.JLabel jLabel_igual;
    private javax.swing.JLabel jLabel_x;
    private javax.swing.JPanel jPanel_ConsultarVentas;
    private javax.swing.JPanel jPanel_InferiorDerecho_VentaVinos;
    private javax.swing.JPanel jPanel_Interno;
    private javax.swing.JPanel jPanel_PanelInferiorIzquierdo_VentaVino;
    private javax.swing.JPanel jPanel_PanelSuperior_VentaVino;
    private javax.swing.JPanel jPanel_VentaVino;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane_Ventanas;
    private javax.swing.JTable jTable_Comerciales_ConsultaVentas;
    private javax.swing.JTable jTable_Comerciales_VentaVino;
    private javax.swing.JTable jTable_Ventas_ConsultarVentas;
    private javax.swing.JTable jTable_Vinos_VentaVino;
    private javax.swing.JTextField jTextField_ComisionComercial_ConsultarVentas;
    private javax.swing.JTextField jTextField_FechaCompra;
    private javax.swing.JTextField jTextField_ImporteTotal;
    private javax.swing.JTextField jTextField_NumBotellas;
    private javax.swing.JTextField jTextField_PrecioBotella;
    // End of variables declaration//GEN-END:variables
}
