package ParteGrafica;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class PantallaCarga extends JFrame {

    private final String _rutaIconoApp = "src/main/java/ParteGrafica/pantallaCarga.png";

    public PantallaCarga() {
        initComponents();
    }

    private void initComponents() {
        // Configurar el JFrame
        setUndecorated(true); // Eliminar la barra de título
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 300);
        setLocationRelativeTo(null); // Centrar en la pantalla

        // Configurar el JPanel principal con fondo negro
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.BLACK);

        // Crear el ImageIcon desde la ruta del archivo de imagen
        ImageIcon icono = new ImageIcon(_rutaIconoApp);

        // Escalar el icono si es necesario
        Image imagenEscalada = icono.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        icono = new ImageIcon(imagenEscalada);

        // Crear el JLabel con el icono
        JLabel labelIcono = new JLabel(icono);
        labelIcono.setHorizontalAlignment(JLabel.CENTER);

        // Crear el JLabel con el texto "Cargando..."
        JLabel labelCargando = new JLabel("Cargando...");
        labelCargando.setHorizontalAlignment(JLabel.CENTER);
        labelCargando.setForeground(Color.WHITE); // Configurar el color del texto
        labelCargando.setFont(new Font("Arial", Font.BOLD, 16)); // Configurar la fuente del texto

        // Configurar el layout del JPanel
        panel.add(labelIcono, BorderLayout.CENTER);
        panel.add(labelCargando, BorderLayout.SOUTH);

        // Agregar el JPanel al JFrame
        getContentPane().add(panel);

        // Hacer visible el JFrame
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PantallaCarga();
        });
    }
    
    public void terminarPantalla () {
        this.setVisible(false);
    }
}
