package es.udc.redes.tutorial.udp.server;

import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 * Implements a UDP echo server.
 */
public class UdpServer {

    public static void main(String[] argv) {
        if (argv.length != 1) {
            System.err.println("Format: es.udc.redes.tutorial.udp.server.UdpServer <port_number>");
            System.exit(-1);
        }

        int port = Integer.parseInt(argv[0]); // Creamos una variable para almacenar el puerto

        // Create a server socket
        try(DatagramSocket socketDatagram = new DatagramSocket(port)) {

            // Set maximum timeout to 300 secs, si está más de 300 s, se corta la escucha
            socketDatagram.setSoTimeout(300000);

            while(true) {
                // Prepare datagram for reception. Asignamos un tamaño máximo del paquete que recibiremos
                byte[] paqueteRecepcion = new byte[1024];

                //Creamos un datagrama
                DatagramPacket recibeDatagram = new DatagramPacket(paqueteRecepcion, paqueteRecepcion.length);

                // Receive the message. Hacemos que el socket que creamos guarde en recibaDatagrama lo que reciba
                socketDatagram.receive(recibeDatagram);

                // Guardamos en mensaje la cadena recibida del datagrama
                String mensaje = new String(paqueteRecepcion, 0, recibeDatagram.getLength(), StandardCharsets.UTF_8);

                System.out.println("SERVER: Received " + mensaje
                        + " to "
                        + recibeDatagram.getAddress().toString() + ":"
                        + recibeDatagram.getPort());

                // RESPONSE
                // Prepare datagram to send response
                byte[] paqueteEnvio = mensaje.getBytes(StandardCharsets.UTF_8);
                DatagramPacket responseDatagram = new DatagramPacket(paqueteEnvio, paqueteEnvio.length, recibeDatagram.getAddress(), recibeDatagram.getPort());

                // Send response
                socketDatagram.send(responseDatagram);

                System.out.println("SERVER: Sending " + new String(responseDatagram.getData())
                        + " to "
                        + responseDatagram.getAddress().toString() + ":"
                        + responseDatagram.getPort());
            }

            //Uncomment next catch clause after implementing the logic
        } catch (SocketTimeoutException e) {
            System.err.println("No requests received in 300 secs ");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
