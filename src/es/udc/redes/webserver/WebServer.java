package es.udc.redes.webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class WebServer {
    
    public static void main(String[] args) throws RuntimeException, IOException {
        if (args.length != 1) {
            System.err.println("Format: es.udc.redes.webserver.ServerThread <port>");
            System.exit(-1);
        }

        int port = Integer.parseInt(args[0]); // Creamos una variable para almacenar el puerto
        try (ServerSocket sSocket = new ServerSocket(port)) {
            // Asignamos puerto al socket del cliente
            sSocket.setSoTimeout(300000); // Establecemos un tiempo máximo de 300 segundos de escucha

            while(true) {
                Socket cSocket = sSocket.accept(); // Esperamos por una conexión con el cliente
                ServerThread sThread = new ServerThread(cSocket); // Creamos conexión con el cliente
                sThread.start(); // Inicializamos el hilo
            }

        } catch (SocketTimeoutException e) {
            System.err.println("Nothing received in 300 secs");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();

        }

    }
    
}
