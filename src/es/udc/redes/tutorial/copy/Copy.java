package es.udc.redes.tutorial.copy;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class Copy {
    public static void main(String[] args) {
        FileReader origen = null;
        FileWriter destino = null;

        try {
            origen = new FileReader(args[0]); // Guardamos el nombre del fichero a copiar
            destino = new FileWriter(args[1]); // Guardamos el nombre del fichero de destino

            int c;
            while ((c = origen.read()) != -1) { // Se va leyendo caracter por caracter de inputStream y
                destino.write(c);              // guardándolo en c, para después escribirlo en outputStrem
            }

        } catch (IOException e) { // Si hay un error se muestra por pantalla
            System.out.println("Ocurrió un error");

        } finally { // Si los ficheros se abrieron, se cierran
            if (origen != null) {
                try {
                    origen.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (destino != null) {
                try {
                    destino.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
