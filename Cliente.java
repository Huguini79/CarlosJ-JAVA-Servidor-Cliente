import java.io.*;
import java.net.*;

public class ClienteTCP {
    private Socket socket;
    private PrintWriter salida;
    private BufferedReader entrada;
 
    public ClienteTCP(String host, int puerto) throws IOException {
        socket = new Socket(host, puerto);
        salida = new PrintWriter(socket.getOutputStream(), true);
        entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void iniciar() {
        new Thread(new LecturaServidor()).start();
        new Thread(new EscrituraServidor()).start();
    }

    class LecturaServidor implements Runnable {
        @Override
        public void run() {
            try {
                String mensaje;
                while ((mensaje = entrada.readLine()) != null) {
                    System.out.println("Servidor: " + mensaje);
                }
            } catch (IOException e) {
                System.err.println("Error en la comunicación con el servidor: " + e.getMessage());
            }
        }
    }

    class EscrituraServidor implements Runnable {
        @Override
        public void run() {
            try (BufferedReader consola = new BufferedReader(new InputStreamReader(System.in))) {
                String mensaje;
                while (true) {
                    mensaje = consola.readLine();
                    salida.println(mensaje);
                    if (mensaje.equalsIgnoreCase("salir")) {
                        break;
                    }
                }
            } catch (IOException e) {
                System.err.println("Error al escribir al servidor: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        try {
            ClienteTCP cliente = new ClienteTCP("localhost", 3122);
            cliente.iniciar();
        } catch (IOException e) {
            System.err.println("Error al conectar al servidor: " + e.getMessage());
        }
    }
}
