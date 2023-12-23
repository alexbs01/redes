package es.udc.redes.tutorial.tcp.server;

import java.io.*;
import java.net.*;

/**
 * MonoThread TCP echo server.
 */
public class MonoThreadTcpServer {

    public static void main(String []argv) {
        if (argv.length != 1) {
            System.err.println("Format: es.udc.redes.tutorial.tcp.server.MonoThreadTcpServer <port>");
            System.exit(-1);
        }

        ServerSocket sSocket = null;
        Socket cSocket = null;
        int port = Integer.parseInt(argv[0]); // Creamos una variable para almacenar el puerto
        BufferedReader bReader;
        PrintWriter pWriter;

        try {

            // Create a server socket
            sSocket = new ServerSocket(port);
            
            // Set a timeout of 300 secs
            sSocket.setSoTimeout(300000);
            
            while (true) {
                // Wait for connections
                cSocket = sSocket.accept();


                // Set the input channel
                bReader = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));

                // Set the output channel
                pWriter = new PrintWriter(cSocket.getOutputStream(), true);

                // Receive the client message
                String mensaje = bReader.readLine();

                System.out.println("SERVER: Received " + mensaje
                        + " to "
                        + sSocket.getInetAddress().getHostAddress() + ":"
                        + sSocket.getLocalPort());

                // Send response to the client
                pWriter.println(mensaje);

                System.out.println("SERVER: Sending " + mensaje
                        + " to "
                        + sSocket.getInetAddress().getHostAddress() + ":"
                        + sSocket.getLocalPort());

                // Close the streams
                bReader.close();
                pWriter.close();
            }
        // Uncomment next catch clause after implementing the logic            
        } catch (SocketTimeoutException e) {
            System.err.println("Nothing received in 300 secs ");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();

        } finally {
            try {
                if (sSocket != null) {
                    sSocket.close();
                }

                if (cSocket != null) {
                    cSocket.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
