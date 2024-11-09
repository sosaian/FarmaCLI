package io.github.sosaian.farmacli;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Declaración de variables globales de la aplicación*
        String CSV_PATH = System.getenv("FARMACLI_CSV_PATH");
        String RESULT_PATH = System.getenv("FARMACLI_RESULT_PATH");
        String SEPARADOR = ";"; // Para poder rápidamente cambiarlo acorde al formato del archivo.

        // Se almacenan las categorías en un ArrayList aparte para mejorar la eficiencia de los demás métodos.
        ArrayList<String> CATEGORIAS = new ArrayList<>();

        // Acá se guardan todos los medicamentos extraídos del CSV
        ArrayList<String[]> VADEMECUM = new ArrayList<>();

        ArrayList<Integer> maxAnchos = new ArrayList<>();

        // Al buscar por código, la búsqueda resuelve en O(1) entregando el índice en VADEMECUM.
        HashMap<String, Integer> hashMapPorCodigo = new HashMap<>();

        // Al buscar por nombre, la búsqueda muestra todas las presentaciones del medicamento en VADEMECUM.
        HashMap<String, List<Integer>> hashMapPorNombre = new HashMap<>();
        // ***************************************************

        // Antes de iniciar, extraer información del CSV
        cargarDatosDesdeCSV(CSV_PATH, SEPARADOR, CATEGORIAS, VADEMECUM, maxAnchos, hashMapPorCodigo, hashMapPorNombre);

        System.out.println("Bienvenido a FarmaCLI!");

        // Listado de tareas para realizar con la aplicación.
        List<String> opciones = List.of(
                "1. Mostrar todo el listado de medicamentos",
                "2. Mostrar los hashmaps",
                "3. Buscar por código de medicamento",
                "4. Buscar por nombre de medicamento",
                "5. Salir de la aplicación"
        );

        boolean seguirEnLaApp = true;
        int opcionActual;
        Scanner scanner = new Scanner(System.in);

        while (seguirEnLaApp) {
            System.out.println();
            System.out.println("Menú principal - Escriba el número de la tarea a realizar:");
            for (String opcion : opciones) { System.out.println(opcion); }

            opcionActual = scanner.nextInt();

            while ((opcionActual < 1) || (opcionActual > 5)) {
                System.out.println("Por favor escriba el número de la tarea a realizar:");
                for (String opcion : opciones) { System.out.println(opcion); }

                opcionActual = scanner.nextInt();
            }

            scanner.nextLine();  // Limpia el salto de línea del buffer

            switch (opcionActual) {
                case 1:
                    mostrarVademecum(CATEGORIAS, VADEMECUM, maxAnchos);
                    break;

                case 2:
                    mostrarHashMaps(hashMapPorCodigo, hashMapPorNombre);
                    break;

                case 3:
                    buscarPorCodigo(RESULT_PATH, CATEGORIAS, VADEMECUM, hashMapPorCodigo);
                    break;

                case 4:
                    buscarPorNombre(RESULT_PATH, CATEGORIAS, VADEMECUM, hashMapPorNombre);
                    break;

                case 5:
                    seguirEnLaApp = false;
                    continue; // Terminar iteración del while(seguirEnLaApp) para cerrar la aplicación.

                default:
                    System.out.println("ERROR. De alguna forma la validación no funcionó!");
            }

            System.out.println();
            System.out.println("----------------");
            seguirEnLaApp = confirmarSeguirEnLaApp();
        }

        System.out.println();
        System.out.println("Cerrando aplicación. Hasta luego!");

        scanner.close(); // Cierro el scanner para prevenir filtrado de información
    }

    public static void cargarDatosDesdeCSV(String ARCHIVO, String SEPARADOR, ArrayList<String> CATEGORIAS, ArrayList<String[]> VADEMECUM, ArrayList<Integer> maxAnchos, HashMap<String, Integer> hashMapPorCodigo, HashMap<String, List<Integer>> hashMapPorNombre) {
        // Dado que el archivo CSV provisto oficialmente acorde al README.md está codificado en ANSI, haciendo uso del ISO 8859-1 se puede hacer correctamente la lectura del archivo.

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(ARCHIVO), StandardCharsets.ISO_8859_1))) {
            String linea = br.readLine(); // linea inicializa con ENCABEZADO del CSV

            // Remover coma final si existe
            if (linea.endsWith(",")) { linea = linea.substring(0, linea.length() - 1); }

            // Aprovechando métodos propios de los ArrayList agrego cada uno de los strings al hacer linea.split()
            CATEGORIAS.addAll(Arrays.asList(linea.split(SEPARADOR)));

            for (String categoria : CATEGORIAS) { maxAnchos.add(categoria.length()); }

            while ((linea = br.readLine()) != null) {
                linea = linea.trim();

                if (linea.isEmpty()) { continue; } // Previene errores si hay líneas en blanco en el CSV

                // Remover coma final si existe
                if (linea.endsWith(",")) { linea = linea.substring(0, linea.length() - 1); }

                String[] entrada = linea.split(SEPARADOR);

                for (int i = 0; i < entrada.length; i++) {
                    maxAnchos.set( i, Math.max( maxAnchos.get(i), entrada[i].length() ) );
                }

                VADEMECUM.add(entrada); // En VADEMECUM se guarda toda la información de cada medicamento.

                Integer indiceEnVademecum = VADEMECUM.size() - 1;
                String nombreMedicamento = entrada[1];

                hashMapPorCodigo.put(entrada[0], indiceEnVademecum);

                // El metodo computeIfAbsent permite devolver la lista asociada si existe, o sino crearla allí mismo.
                List<Integer> indices = hashMapPorNombre.computeIfAbsent(nombreMedicamento, k -> new ArrayList<>());

                indices.add(indiceEnVademecum);
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    public static void mostrarVademecum(ArrayList<String> CATEGORIAS, ArrayList<String[]> VADEMECUM, ArrayList<Integer> maxAnchos) {
        if (CATEGORIAS.isEmpty()) {
            System.out.println("Error: Listado de categorías no inicializado aún!");
            return;
        }

        if (VADEMECUM.isEmpty()) {
            System.out.println("Error: Vademecum no inicializado aún!");
            return;
        }

        for (int i = 0; i < CATEGORIAS.size(); i++) {
            String fila = CATEGORIAS.get(i);

            System.out.printf("%-" + (maxAnchos.get(i) + 1) + "s", fila);

            // Añadimos un separador " | " solo entre columnas
            if (i < CATEGORIAS.size() - 1) { System.out.print("| "); }
        }

        System.out.println();

        // Luego del encabezado, imprimimos una línea separadora1
        for (int j = 0; j < maxAnchos.size(); j++) {
            System.out.print("-".repeat(maxAnchos.get(j) + 1));

            // Añadimos un separador solo entre columnas
            if (j < maxAnchos.size() - 1) {
                System.out.print("+-");
            }
        }

        System.out.println();

        for (String[] fila : VADEMECUM) {
            // Imprimimos cada celda de la fila con el ancho calculado y bordes entre columnas
            for (int j = 0; j < fila.length; j++) {
                System.out.printf("%-" + (maxAnchos.get(j) + 1) + "s", fila[j]);

                // Añadimos un separador " | " solo entre columnas
                if (j < fila.length - 1) {
                    System.out.print("| ");
                }
            }
            System.out.println();
        }
    }

    public static void mostrarHashMaps(HashMap<String, Integer> hashMapPorCodigo, HashMap<String, List<Integer>> hashMapPorNombre) {
        if (hashMapPorCodigo.isEmpty()) {
            System.out.println("Error: hashmap por codigo no inicializado aún!");
            return;
        }

        System.out.println();
        System.out.println("hashMapPorCodigo: ");
        System.out.println();
        hashMapPorCodigo.forEach((key, value) -> System.out.println(key + " - " + value));

        if (hashMapPorNombre.isEmpty()) {
            System.out.println("Error: hashmap por nombre no inicializado aún!");
            return;
        }

        System.out.println();
        System.out.println("-----------------------------------------------------------------------");
        System.out.println();
        System.out.println("hashMapPorNombre: ");
        System.out.println();
        hashMapPorNombre.forEach((key, value) -> System.out.println(key + " - " + value));
    }

    public static void buscarPorCodigo(String RESULT_PATH, ArrayList<String> CATEGORIAS, ArrayList<String[]> VADEMECUM, HashMap<String, Integer> hashMapPorCodigo) {
        Scanner scanner = new Scanner(System.in);

        System.out.println();
        System.out.print("Ingrese el código del medicamento a buscar: ");
        String codigoMedicamento = scanner.nextLine();

        Integer indiceResultado = hashMapPorCodigo.get(codigoMedicamento);

        System.out.println();

        if (indiceResultado == null) {
            System.out.println("No se encontró ninguna coincidencia!");
            return;
        }

        String[] medicamento = VADEMECUM.get(indiceResultado);

        //
        //
        // Mostrar resultado en consola
        //
        //

        for (int i = 0; i < CATEGORIAS.size(); i++) {
            System.out.print(CATEGORIAS.get(i));
            System.out.print(": ");
            System.out.print(medicamento[i]);

            if (i < CATEGORIAS.size() - 1) { System.out.println(); }
        }

        System.out.println();

        //
        //
        // Guardar resultado en un archivo .txt
        //
        //

        String nombreArchivo = "resultado_codigo_medicamento_" + codigoMedicamento + ".txt";

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(RESULT_PATH + "\\" + nombreArchivo))) {
            for (int i = 0; i < CATEGORIAS.size(); i++) {
                bufferedWriter.write(CATEGORIAS.get(i));
                bufferedWriter.write(": ");
                bufferedWriter.write(medicamento[i]);

                if (i < CATEGORIAS.size() - 1) { bufferedWriter.newLine(); }
            }

            System.out.println();
            System.out.println("Resultados de esta búsqueda guardados en: " + RESULT_PATH + "\\" + nombreArchivo);
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }

    public static void buscarPorNombre(String RESULT_PATH, ArrayList<String> CATEGORIAS, ArrayList<String[]> VADEMECUM, HashMap<String, List<Integer>> hashMapPorNombre) {
        Scanner scanner = new Scanner(System.in);

        System.out.println();
        System.out.print("Ingrese el nombre del medicamento a buscar: ");
        String nombreMedicamento = scanner.nextLine();

        List<Integer> indicesResultado = hashMapPorNombre.get(nombreMedicamento);

        System.out.println();

        // Agregar isEmpty() hace a este método agnóstico de alguna implementación específica de cargarDatosDesdeCSV()
        if (indicesResultado == null || indicesResultado.isEmpty()) {
            System.out.println("No se encontró ninguna coincidencia!");
            return;
        }

        // Creamos listadoMedicamentos aplicando un flujo de datos (Stream) en una sola línea:
        // 1. `.stream()`: convierte indicesResultado en un flujo de sus elementos.
        // 2. `.map(VADEMECUM::get)`: para cada índice en indicesResultado, obtenemos el elemento en VADEMECUM.
        // 3. `.toList()`: recopila todos los elementos convertidos en una lista inmutable. (propio de Java 16)

        List<String[]> listadoMedicamentos = indicesResultado.stream().map(VADEMECUM::get).toList();

        //
        //
        // Mostrar resultado en consola
        //
        //

        for (int i = 0; i < listadoMedicamentos.size();  i++) {
            String[] medicamento = listadoMedicamentos.get(i);

            if (i != 0) { System.out.println(); }

            for (int j = 0; j < CATEGORIAS.size(); j++) {
                System.out.print(CATEGORIAS.get(j));
                System.out.print(": ");
                System.out.print(medicamento[j]);

                if (j < CATEGORIAS.size() - 1) {
                    System.out.println();
                }
            }

            System.out.println();
            System.out.println("-".repeat(40));
        }

        //
        //
        // Guardar resultado en un archivo .txt
        //
        //

        String nombreArchivo = "resultados_nombre_medicamento_" + nombreMedicamento + ".txt";

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(RESULT_PATH + "\\" + nombreArchivo))) {
            for (int i = 0; i < listadoMedicamentos.size();  i++) {
                String[] medicamento = listadoMedicamentos.get(i);

                if (i != 0) { bufferedWriter.newLine(); }

                for (int j = 0; j < CATEGORIAS.size(); j++) {
                    bufferedWriter.write(CATEGORIAS.get(j));
                    bufferedWriter.write(": ");
                    bufferedWriter.write(medicamento[j]);

                    if (j < CATEGORIAS.size() - 1) {
                        bufferedWriter.newLine();
                    }
                }

                bufferedWriter.newLine();
                bufferedWriter.write("-".repeat(40));
                bufferedWriter.newLine();
            }

            System.out.println();
            System.out.println("Resultados de esta búsqueda guardados en: " + RESULT_PATH + "\\" + nombreArchivo);
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
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