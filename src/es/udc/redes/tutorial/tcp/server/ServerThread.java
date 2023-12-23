package es.udc.redes.tutorial.tcp.server;
import java.net.*;
import java.io.*;

/** Thread that processes an echo server connection. */

public class ServerThread extends Thread {

    private Socket socket;

    public ServerThread(Socket s) {
      this.socket = s;
    }

    public void run() {
        BufferedReader bReader;
        PrintWriter pWriter;

            try {
                // Set the input channel
                bReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Set the output channel
                pWriter = new PrintWriter(socket.getOutputStream(), true);

                // Receive the message from the client
                String mensaje = bReader.readLine();

                System.out.println("SERVER: Received " + mensaje
                        + " to "
                        + socket.getInetAddress().getHostAddress() + ":"
                        + socket.getLocalPort());

                // Sent the echo message to the client
                pWriter.println(mensaje);

                System.out.println("SERVER: Sending " + mensaje
                        + " to "
                        + socket.getInetAddress().getHostAddress() + ":"
                        + socket.getLocalPort());

                // Close the streams
                bReader.close();
                pWriter.close();

              // Uncomment next catch clause after implementing the logic
            } catch (SocketTimeoutException e) {
                System.err.println("Nothing received in 300 secs");
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            } finally {
                try {
                    if (socket != null) {
                      socket.close();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
    }
}
