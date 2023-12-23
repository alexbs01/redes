package es.udc.redes.tutorial.tcp.server;

import java.io.IOException;
import java.net.*;

/** Multithread TCP echo server. */

public class TcpServer {

    public static void main(String[] argv) {
        if (argv.length != 1) {
          System.err.println("Format: es.udc.redes.tutorial.tcp.server.TcpServer <port>");
          System.exit(-1);
        }

        ServerSocket sSocket = null;

        int port = Integer.parseInt(argv[0]); // Creamos una variable para almacenar el puerto

        // Create a server socket
        try {
            sSocket = new ServerSocket(port);

            // Set a timeout of 300 secs
            sSocket.setSoTimeout(300000);

            while (true) {
                // Wait for connections
                Socket cSocket = sSocket.accept();

                // Create a ServerThread object, with the new connection as parameter
                ServerThread sThread = new ServerThread(cSocket);

                // Initiate thread using the start() method
                sThread.start();
            }
        // Uncomment next catch clause after implementing the logic
        } catch (SocketTimeoutException e) {
          System.err.println("Nothing received in 300 secs");

        } catch (Exception e) {
          System.err.println("Error: " + e.getMessage());
          e.printStackTrace();

        } finally {

            try {
                if (sSocket != null) {
                    sSocket.close();
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
