package es.udc.redes.webserver;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ServerThread extends Thread {

    private final Socket socket;

    public ServerThread(Socket s) {
        // Store the socket s
        this.socket = s;
    }

    public void run() {
        BufferedReader bReader;
        PrintWriter pWriter;
        BufferedOutputStream output;

        try {
            bReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pWriter = new PrintWriter(socket.getOutputStream(), true);
            output = new BufferedOutputStream(socket.getOutputStream());

            StringBuilder mensaje = new StringBuilder();
            String auxiliar = bReader.readLine();
            String[] headerTokens;
            String IfModifiedSince = null;

            if(auxiliar != null) {
                headerTokens = auxiliar.split(" ");

                while(!auxiliar.equals("")) {
                    auxiliar = bReader.readLine();

                    if(auxiliar.startsWith("If-Modified-Since: ")) {
                        IfModifiedSince = auxiliar;
                    }

                    mensaje = new StringBuilder(mensaje + auxiliar);

                    if(!auxiliar.equals("")) {
                        mensaje = new StringBuilder(mensaje + "\n");
                    }
                }

                if(headerTokens.length == 3) {
                    response(headerTokens, IfModifiedSince, pWriter, output);
                }
            }

            bReader.close();
            pWriter.close();

        } catch (SocketTimeoutException e) {
            System.err.println("Nothing received in 300 secs");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {

            if(socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }

    public Codes responseHeader(String []headerTokens, String IfModifiedSince, PrintWriter printWriter) {
        // Transformamos los String a rutas
        Path rootPath = Paths.get("p1-files").toAbsolutePath();

        // Concatenamos el padre con el hijo, y lo transformamos en una ruta
        String complete = rootPath + headerTokens[1];
        Path completePath = Paths.get(complete);

        // Obtenemos el fichero
        File file = completePath.toFile();

        // Obtenemos la fecha
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss z", Locale.ENGLISH); // Sat, 1 Jan 2000 12:00:15 GMT
        TimeZone timeZone = TimeZone.getTimeZone("GMT");
        format.setTimeZone(timeZone);

        // Creamos variables para almacenar los datos del código
        String codeWithInfo;
        Codes code;
        long antiguedadCliente = 0;

        if(IfModifiedSince != null) {
            try {
                String []IfModifiedSincenTokens = IfModifiedSince.split("If-Modified-Since: ");
                Date fechaCliente = format.parse(IfModifiedSincenTokens[1]);
                antiguedadCliente = fechaCliente.getTime();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        if(headerTokens[0].equals("GET") || headerTokens[0].equals("HEAD")) {
            if(file.exists()) {
                if((antiguedadCliente + 1000) < file.lastModified()) {
                    codeWithInfo = Codes.OK.getCode() + " " + Codes.OK;
                    code = Codes.OK;
                    complete = rootPath + headerTokens[1];

                } else {
                    codeWithInfo = Codes.NOT_MODIFIED.getCode() + " " + Codes.NOT_MODIFIED;
                    code = Codes.NOT_MODIFIED;
                }

            } else {
                codeWithInfo = Codes.NOT_FOUND.getCode() + " " + Codes.NOT_FOUND;
                code = Codes.NOT_FOUND;
                complete = rootPath + "/error404.html";

            }
        } else {
            codeWithInfo = Codes.BAD_REQUEST.getCode() + " " + Codes.BAD_REQUEST;
            code = Codes.BAD_REQUEST;
            complete = rootPath + "/error400.html";

        }

        // Establece el archivo de la ruta indicada
        if(code != Codes.OK) {
            completePath = Paths.get(complete);
            file = completePath.toFile();
        }

        // Establecemos una variable para almacenar el tipo de fichero
        String contentType;
        try {
            contentType = Files.probeContentType(completePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        printWriter.flush();
        printWriter.println("HTTP/1.0 " + codeWithInfo);
        printWriter.println("Date: " + format.format(date));
        printWriter.println("Server: WebServer [Windows]");
        printWriter.println("Content-Length: " + file.length());
        printWriter.println("Content-Type: " + contentType);
        printWriter.println("Last-Modified: " + format.format(file.lastModified()) + "\n");
        printWriter.flush();

        // Esto muestra las respuestas en el servidor, es omitible
        System.out.println("HTTP/1.0 " + codeWithInfo);
        System.out.println("Date: " + format.format(date));
        System.out.println("Server: WebServer [Windows]");
        System.out.println("Content-Length: " + file.length());
        System.out.println("Content-Type: " + contentType);
        System.out.println("Last-Modified: " + format.format(file.lastModified()) + "\n");
        //

        return code;
    }

    public void response(String []headerTokens, String IfModifiedSince, PrintWriter printWriter, BufferedOutputStream buffereOutputStream) {
        // Mandamos la cabecera y almacenamos su código
        Codes code = responseHeader(headerTokens, IfModifiedSince, printWriter);

        // Transformamos la ruta raiz path absoluto
        Path rootPath = Paths.get("p1-files").toAbsolutePath();

        // Concatenamos el padre con el hijo, y lo transformamos en una ruta
        StringBuilder complete = new StringBuilder();

        if(headerTokens[0].equals("HEAD") || code == Codes.NOT_MODIFIED) {
            return;
        }

        // En función de que código sea, escogemos que mostramos
        switch(code) {
            case OK -> // Concatenamos el padre con el hijo, y lo transformamos en una ruta
                    complete = new StringBuilder(rootPath + headerTokens[1]);

            case NOT_FOUND -> // Concatenamos el padre con el hijo, y lo transformamos en una ruta
                    complete = new StringBuilder(rootPath + "/error404.html");

            case BAD_REQUEST -> // Concatenamos el padre con el hijo, y lo transformamos en una ruta
                    complete = new StringBuilder(rootPath + "/error400.html");
        }

        // Convertimos la ruta completa a tipo Path
        Path completePath = Paths.get(complete.toString());

        // Obtenemos el fichero
        File file = completePath.toFile();
        try(FileInputStream fis = new FileInputStream(file)) {
            byte[] fileBytes = fis.readAllBytes(); // Lee los bytes y los guarda el array de bytes
            buffereOutputStream.flush();           // Borramos lo que pudiera haber en la caché
            buffereOutputStream.write(fileBytes); // Escribe los bytes en el output stream
            buffereOutputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

