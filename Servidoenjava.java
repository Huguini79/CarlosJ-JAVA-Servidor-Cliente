import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import java.io.*;
import java.net.*;
import javax.swing.JOptionPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;

public class Servidorenjava {
    public static String operación;

    public static void sumar(double a, double e) {
        double suma = a + e;
        JOptionPane.showMessageDialog(null, "El resultado de la suma es: " + suma);
    }

    public static void restar(double a, double e) {
        double resta = a - e;
        JOptionPane.showMessageDialog(null, "El resultado de la resta es: " + resta);
    }

    public static void multi(double a, double e) {
        double multi = a * e;
        JOptionPane.showMessageDialog(null, "El resultado de la multiplicación es: " + multi);
    }

    public static void divi(double a, double e) {
        double divi = a / e;
        JOptionPane.showMessageDialog(null, "El resultado de la división es: " + divi);
    }

    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("Servidor");
        JButton botón = new JButton("Iniciar servidor");
        JButton botón2 = new JButton("Calculadora");
        frame.requestFocusInWindow();
        frame.setVisible(true);
        frame.setSize(660, 550);
        frame.getContentPane().setBackground(Color.YELLOW);
        frame.add(botón);
        frame.add(botón2);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        botón.setPreferredSize(new Dimension(123, 80));

        // Mover la inicialización del texto a su contexto correspondiente
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        botón.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    ServerSocket servidor = new ServerSocket(3122);
                    System.out.println("Servidor TCP iniciado en el puerto 3122");

                    // Bucle para aceptar múltiples conexiones
                    while (true) {
                        Socket socketCliente = servidor.accept(); // Acepta la conexión de un cliente
                        System.out.println("Cliente conectado: " + socketCliente.getInetAddress());

                        // Iniciar un nuevo hilo para manejar la conexión del cliente
                        ClienteHandler manejadorCliente = new ClienteHandler(socketCliente);
                        new Thread(manejadorCliente).start();
                    }
                } catch (IOException ex) {
                    System.err.println("Hubo un error al iniciar el servidor.");
                }
            }
        });

        botón2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame frame2 = new JFrame("Calculadora gui");
                JLabel label = new JLabel("Introduce un número:");
                JTextField texto = new JTextField(); // Mover declaración de 'texto' aquí
                JButton botónsuma = new JButton("+");
                JButton botónresta = new JButton("-");
                JButton botónmulti = new JButton("*");
                JButton botóndivi = new JButton("/");
                JLabel label2 = new JLabel("Introduce el último número:");
                JTextField texto2 = new JTextField();
                JButton botónenvi = new JButton("=");

                frame2.requestFocusInWindow();
                frame2.setVisible(true);
                frame2.setSize(660, 550);
                frame2.getContentPane().setBackground(Color.YELLOW);
                frame2.setLayout(new BoxLayout(frame2.getContentPane(), BoxLayout.Y_AXIS));
                frame2.add(label);
                frame2.add(texto);
                frame2.add(botónsuma);
                frame2.add(botónresta);
                frame2.add(botónmulti);
                frame2.add(botóndivi);
                frame2.add(texto2);
                frame2.add(botónenvi);
                label.setPreferredSize(new Dimension(300, 80));
                texto.setPreferredSize(new Dimension(123, 80));
                botónsuma.setPreferredSize(new Dimension(123, 80));
                botónresta.setPreferredSize(new Dimension(123, 80));
                botónmulti.setPreferredSize(new Dimension(123, 80));
                botóndivi.setPreferredSize(new Dimension(123, 80));
                label2.setPreferredSize(new Dimension(300, 80));
                texto2.setPreferredSize(new Dimension(123, 80));
                botónenvi.setPreferredSize(new Dimension(123, 80));

                botónsuma.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        operación = "suma";
                    }
                });

                botónresta.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        operación = "resta";
                    }
                });

                botónmulti.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        operación = "multi";
                    }
                });

                botóndivi.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        operación = "divi";
                    }
                });

                botónenvi.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Double num1 = Double.parseDouble(texto.getText());
                        Double num2 = Double.parseDouble(texto2.getText());
                        switch (operación) {
                            case "suma":
                                sumar(num1, num2);
                                break;
                            case "resta":
                                restar(num1, num2);
                                break;
                            case "multi":
                                multi(num1, num2);
                                break;
                            case "divi":
                                divi(num1, num2);
                                break;
                            default:
                                JOptionPane.showMessageDialog(null, "Operación no válida.");
                                break;
                        }
                    }
                });
            }
        });
    }
}

// Clase para manejar la comunicación con un cliente en un hilo separado
class ClienteHandler implements Runnable {
    private Socket socketCliente;

    public ClienteHandler(Socket socketCliente) {
        this.socketCliente = socketCliente;
    }

    @Override
    public void run() {
        try {
            // Crear hilos para leer y escribir simultáneamente
            Thread hiloLectura = new Thread(new LecturaCliente(socketCliente));
            Thread hiloEscritura = new Thread(new EscrituraServidor(socketCliente));

            hiloLectura.start();  // Comienza a escuchar los mensajes del cliente
            hiloEscritura.start(); // Comienza a enviar mensajes al cliente

            // Espera a que los hilos terminen
            hiloLectura.join();
            hiloEscritura.join();
        } catch (InterruptedException e) {
            System.err.println("Error en la comunicación con el cliente: " + e.getMessage());
        } finally {
            try {
                socketCliente.close(); // Cerrar el socket del cliente al finalizar la conexión
            } catch (IOException e) {
                System.err.println("Error al cerrar el socket del cliente: " + e.getMessage());
            }
        }
    }
}

// Hilo para leer mensajes del cliente
class LecturaCliente implements Runnable {
    private Socket socketCliente;

    public LecturaCliente(Socket socketCliente) {
        this.socketCliente = socketCliente;
    }

    @Override
    public void run() {
        try (BufferedReader entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()))) {
            String mensaje;
            // Bucle para recibir mensajes del cliente
            while ((mensaje = entrada.readLine()) != null) {
                System.out.println("Cliente: " + mensaje);

                if (mensaje.equalsIgnoreCase("salir")) {
                    System.out.println("Cliente desconectado: " + socketCliente.getInetAddress());
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer del cliente: " + e.getMessage());
        }
    }
}

// Hilo para enviar mensajes al cliente
class EscrituraServidor implements Runnable {
    private Socket socketCliente;

    public EscrituraServidor(Socket socketCliente) {
        this.socketCliente = socketCliente;
    }

    @Override
    public void run() {
        try (PrintWriter salida = new PrintWriter(socketCliente.getOutputStream(), true);
             BufferedReader consola = new BufferedReader(new InputStreamReader(System.in))) {

            String mensaje;
            // Bucle para enviar mensajes al cliente
            while (true) {
                System.out.print("Servidor: ");
                mensaje = consola.readLine(); // Leer mensaje desde la consola del servidor

                salida.println(mensaje); // Enviar mensaje al cliente

                if (mensaje.equalsIgnoreCase("salir")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error al escribir al cliente: " + e.getMessage());
        }
    }
}
