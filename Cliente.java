import java.io.*;
import java.net.*;

public class Cliente {
    public static void main(String[] args) {
        String servidor = "127.0.0.1"; // Cambia esto si el servidor está en otra máquina
        int puerto = 3122; // Puerto que estás usando en el servidor

        try (Socket socketCliente = new Socket(servidor, puerto);
             PrintWriter salida = new PrintWriter(socketCliente.getOutputStream(), true);
             BufferedReader entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
             BufferedReader consola = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Conectado al servidor en " + servidor + " en el puerto " + puerto);

            String mensaje;
            // Bucle para enviar mensajes al servidor
            while (true) {
                System.out.print("Cliente: ");
                mensaje = consola.readLine(); // Leer mensaje desde la consola del cliente
                salida.println(mensaje); // Enviar mensaje al servidor

                // Salir si el mensaje es "salir"
                if (mensaje.equalsIgnoreCase("salir")) {
                    break;
                }

                // Leer la respuesta del servidor
                String respuesta = entrada.readLine();
                if (respuesta != null) {
                    System.out.println("Servidor: " + respuesta);
                }
            }

        } catch (IOException e) {
            System.err.println("Error en la comunicación con el servidor: " + e.getMessage());
        }
    }
}
