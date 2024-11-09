package io.github.sosaian.farmacli;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String CSV_PATH = System.getenv("FARMACLI_CSV_PATH");
        System.out.println("Bienvenido a FarmaCLI!");

        // Listado de tareas para realizar con la aplicación.
        List<String> opciones = List.of(
                "1. Mostrar el contenido del CSV",
                "2. Salir de la aplicación"
        );

        boolean seguirEnLaApp = true;
        int opcionActual;
        Scanner scanner = new Scanner(System.in);

        while (seguirEnLaApp) {
            System.out.println("Menú principal - Escriba el número de la tarea a realizar:");
            for (String opcion : opciones) { System.out.println(opcion); }

            opcionActual = scanner.nextInt();

            while ((opcionActual < 1) || (opcionActual > 2)) {
                System.out.println("Por favor escriba el número de la tarea a realizar:");
                for (String opcion : opciones) { System.out.println(opcion); }

                opcionActual = scanner.nextInt();
            }

            scanner.nextLine();  // Limpia el salto de línea del buffer

            switch (opcionActual) {
                case 1:
                    cargarDatosDesdeCSV(CSV_PATH);
                    break;

                case 2:
                    seguirEnLaApp = false;
                    continue; // Terminar iteración del while(seguirEnLaApp) para cerrar la aplicación.

                default:
                    System.out.println("ERROR. De alguna forma la validación no funcionó!");
            }

            System.out.println("----------------");
            seguirEnLaApp = confirmarSeguirEnLaApp();
        }

        System.out.println("Cerrando aplicación. Hasta luego!");

        scanner.close(); // Cierro el scanner para prevenir filtrado de información
    }

    public static void cargarDatosDesdeCSV(String archivo) {
        // Dado que el archivo CSV provisto oficialmente acorde al README.md está codificado en ANSI, haciendo uso del ISO 8859-1 se puede hacer correctamente la lectura del archivo.

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(archivo), StandardCharsets.ISO_8859_1))) {
            String linea = br.readLine(); // linea inicializa con ENCABEZADO del CSV
            System.out.println(linea); // Acá se maneja la carga de cada columna

            while ((linea = br.readLine()) != null) {
                linea = linea.trim();

                if (linea.isEmpty()) { continue; }

                System.out.println(linea); // Acá se procesa cada línea del CSV
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    public static boolean confirmarSeguirEnLaApp() {
        Scanner scanner = new Scanner(System.in);

        System.out.println();

        System.out.println("¿Desea continuar en la app? sí/no ");
        String respuesta = scanner.nextLine();

        while ( !(respuesta.equalsIgnoreCase("si")) && !(respuesta.equalsIgnoreCase("sí")) && !(respuesta.equalsIgnoreCase("no")) ) {
            System.out.println("Por favor ingrese `sí` para continuar o `no` para salir.");
            respuesta = scanner.nextLine();
        }

        System.out.println();
        System.out.println("----------------");

        return ( respuesta.equalsIgnoreCase("si") || respuesta.equalsIgnoreCase("sí") );
    }
}