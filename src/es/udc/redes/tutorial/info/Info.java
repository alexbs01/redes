package es.udc.redes.tutorial.info;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

/*tamaño, fecha de última modificación, nombre, extensión, tipo
de fichero (image, text, directory, unknown), ruta absoluta.*/
public class Info {
    public static void main(String[] args) {
        Path rutaRelativa = Paths.get(args[0]); // Ruta relativa introducida por parámetros
        File archivo = new File(rutaRelativa.getFileName().toString()); // Archivo de la ruta
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); // Formato para la fecha de última modificación
        String nombreArchivo = archivo.getName(); // Nombre del archivo con extensión
        String extension = nombreArchivo.substring(nombreArchivo.lastIndexOf(".") + 1);
        String nombreSinExtension = nombreArchivo.substring(0, nombreArchivo.lastIndexOf("."));

        System.out.println("Ruta relativa: " + rutaRelativa);
        System.out.println("Tamaño: " + archivo.length());
        System.out.println("Fecha de última modificación: " + formato.format(archivo.lastModified()));
        System.out.println("Nombre: " + nombreSinExtension);
        System.out.println("Extensión: " + extension);

        try {
            System.out.println("Tipo de fichero: " + Files.probeContentType(rutaRelativa));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Ruta absoluta: " + archivo.getAbsolutePath());

    }
    
}
