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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Declaración de variables globales de la aplicación
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

        // Antes de iniciar, extraer información del CSV
        cargarDatosDesdeCSV(CSV_PATH, SEPARADOR, CATEGORIAS, VADEMECUM, maxAnchos, hashMapPorCodigo, hashMapPorNombre);

        System.out.println("Bienvenido a FarmaCLI!\n");

        // Listado de tareas para realizar con la aplicación.
        final List<String> OPCIONES = List.of(
                "1. Buscar por código de medicamento",
                "2. Buscar por nombre de medicamento",
                "3. Salir de la aplicación"
        );

        boolean seguirEnLaApp = true;
        Scanner scanner = new Scanner(System.in);

        while (seguirEnLaApp) {
            int opcionActual = handleMenuPrincipal(scanner, OPCIONES);

            switch (opcionActual) {
                case 1:
                    handleBusquedaPorCodigo(scanner, RESULT_PATH, CATEGORIAS, VADEMECUM, hashMapPorCodigo);
                    break;

                case 2:
                    handleBusquedaPorNombre(scanner, RESULT_PATH, CATEGORIAS, VADEMECUM, hashMapPorNombre);
                    break;

                case 3:
                    seguirEnLaApp = false;
                    continue; // Terminar iteración del while(seguirEnLaApp) para cerrar la aplicación.

                case 9:
                    mostrarVademecum(CATEGORIAS, VADEMECUM, maxAnchos);
                    break;

                case 0:
                    mostrarHashMaps(hashMapPorCodigo, hashMapPorNombre);
                    break;

                default:
                    System.out.println("ERROR. De alguna forma la validación del menú principal no funcionó!");
            }

            System.out.println("\n" + "-".repeat(16) + "\n");
            seguirEnLaApp = confirmarSeguirEnLaApp(scanner);
        }

        System.out.println("\n" + "-".repeat(16) + "\n\nCerrando aplicación. Hasta luego!");

        scanner.close(); // Cierro el scanner para prevenir filtrado de información
    }

    public static int handleMenuPrincipal(Scanner scanner, List<String> OPCIONES) {
        // Nota: Este menú principal asume que OPCIONES.size() es de al menos 1.
        System.out.println("Menú principal - Escriba el número de la tarea a realizar:");
        for (String opcion : OPCIONES) { System.out.println(opcion); }

        final int MAX_OPCIONES = OPCIONES.size();
        int opcionActual = -1;
        boolean opcionInvalida;

        // OPCIONES EXTRA para métodos auxiliares para desarrolladores.
        // 9. Mostrar el listado de medicamentos
        // 0. Mostrar los hashmaps

        do {
            if (scanner.hasNextInt()) {
                opcionActual = scanner.nextInt();

                if (opcionActual == 0 || opcionActual == 9) {
                    return opcionActual;
                }

                opcionInvalida = opcionActual < 1 || opcionActual > MAX_OPCIONES;

                if (opcionInvalida) {
                    System.out.println("\nPor favor, ingrese una opción válida (entre 1 y " + MAX_OPCIONES + "):");
                }
            } else {
                // El input no es un entero
                System.out.println("\nEntrada no válida. Por favor ingresa un número entero.");

                opcionInvalida = true; // Mantenemos el ciclo activo, ya que no es una opción válida
            }

            scanner.nextLine(); // Limpia el búfer de entrada en cada iteración
        } while (opcionInvalida);

        return opcionActual;
    }

    public static void cargarDatosDesdeCSV(String ARCHIVO, String SEPARADOR, ArrayList<String> CATEGORIAS, ArrayList<String[]> VADEMECUM, ArrayList<Integer> maxAnchos, HashMap<String, Integer> hashMapPorCodigo, HashMap<String, List<Integer>> hashMapPorNombre) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(ARCHIVO), StandardCharsets.ISO_8859_1))) {
            String linea = br.readLine(); // linea inicializa con ENCABEZADO del CSV

            if (linea.endsWith(",")) { linea = linea.substring(0, linea.length() - 1); }

            CATEGORIAS.addAll(Arrays.asList(linea.split(SEPARADOR)));

            for (String categoria : CATEGORIAS) { maxAnchos.add(categoria.length()); }

            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) { continue; }

                if (linea.endsWith(",")) { linea = linea.substring(0, linea.length() - 1); }

                String[] entrada = linea.split(SEPARADOR);

                for (int i = 0; i < entrada.length; i++) {
                    maxAnchos.set( i, Math.max( maxAnchos.get(i), entrada[i].length() ) );
                }

                VADEMECUM.add(entrada);

                Integer indiceEnVademecum = VADEMECUM.size() - 1;
                String nombreMedicamento = entrada[1].toLowerCase(); // Guardamos los nombres en minúsculas

                hashMapPorCodigo.put(entrada[0], indiceEnVademecum);

                List<Integer> indices = hashMapPorNombre.computeIfAbsent(nombreMedicamento, k -> new ArrayList<>());
                indices.add(indiceEnVademecum);
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    public static String[] buscarPorCodigo(String codigoMedicamento, ArrayList<String[]> VADEMECUM, HashMap<String, Integer> hashMapPorCodigo) {
        Integer indiceResultado = hashMapPorCodigo.get(codigoMedicamento);

        if (indiceResultado == null) { return null; }

        return VADEMECUM.get(indiceResultado);
    }

    public static List<String[]> buscarPorNombre(String nombreMedicamento, ArrayList<String[]> VADEMECUM, HashMap<String, List<Integer>> hashMapPorNombre) {
        List<Integer> indicesResultado = hashMapPorNombre.get(nombreMedicamento);

        if (indicesResultado == null || indicesResultado.isEmpty()) { return null; }

        // Creamos un listado de medicamentos aplicando un flujo de datos (Stream) en una sola línea:
        // 1. `.stream()`: convierte indicesResultado en un flujo de sus elementos.
        // 2. `.map(VADEMECUM::get)`: para cada índice en indicesResultado, obtenemos el elemento en VADEMECUM.
        // 3. `.toList()`: recopila todos los elementos convertidos en una lista inmutable. (propio de Java 16)

        // Con este listado podemos simplificar el proceso de mostrar por consola y también enviar los resultados.

        return indicesResultado.stream().map(VADEMECUM::get).toList();
    }

    public static void guardarResultadosEnArchivo(String RESULT_PATH, String NOMBRE_ARCHIVO, List<String[]> RESULTADOS, ArrayList<String> CATEGORIAS) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RESULT_PATH + "\\" + NOMBRE_ARCHIVO))) {
            for (int i = 0; i < RESULTADOS.size(); i++) {
                String[] resultado = RESULTADOS.get(i);

                if (i != 0) { writer.newLine(); }

                for (int j = 0; j < CATEGORIAS.size(); j++) {
                    writer.write(CATEGORIAS.get(j));
                    writer.write(": ");
                    writer.write(resultado[j]);

                    if (j < CATEGORIAS.size() - 1) {
                        writer.newLine();
                    }
                }

                writer.newLine();
                writer.write("-".repeat(40));
                writer.newLine();
            }
            System.out.println("\nResultados de esta búsqueda guardados en: " + RESULT_PATH + "\\" + NOMBRE_ARCHIVO);
        } catch (IOException e) {
            System.out.println("\nError al guardar los resultados: " + e.getMessage());
        }
    }

    public static void handleBusquedaPorCodigo(Scanner scanner, String RESULT_PATH, ArrayList<String> CATEGORIAS, ArrayList<String[]> VADEMECUM, HashMap<String, Integer> hashMapPorCodigo) {
        System.out.print("\nIngrese el código del medicamento a buscar (o escriba 'salir' para cancelar): ");
        String codigoMedicamento = scanner.nextLine().trim().toLowerCase();

        if (codigoMedicamento.equalsIgnoreCase("salir")) {
            System.out.println("\nSaliendo del proceso de búsqueda.");
            return;
        }

        // BÚSQUEDA

        final String[] RESULTADO = buscarPorCodigo(codigoMedicamento, VADEMECUM, hashMapPorCodigo);

        assert RESULTADO != null;
        if (RESULTADO.length == 0) {
            System.out.println("No se encontraron resultados para el código de medicamento \"" + codigoMedicamento + "\".");
            return;
        }

        // RESULTADO

        System.out.println("Se encontró el siguiente medicamento:");
        for (int i = 0; i < CATEGORIAS.size(); i++) {
            System.out.print(CATEGORIAS.get(i));
            System.out.print(": ");
            System.out.println(RESULTADO[i]);
        }

        // GUARDAR ARCHIVO
        String nombreArchivo = "resultados_codigo_medicamento_" + codigoMedicamento + ".txt";

        System.out.println("\n¿Quieres guardar el resultado en un archivo aparte? (sí/no)");

        boolean seguir;

        do {
            String respuesta = scanner.nextLine().trim().toLowerCase();

            if (respuesta.equals("sí") || respuesta.equals("si")) {
                guardarResultadosEnArchivo(RESULT_PATH, nombreArchivo, Collections.singletonList(RESULTADO), CATEGORIAS);
                seguir = false;
            } else if (respuesta.equals("no")) {
                seguir = false;
            } else {
                System.out.println("\nPor favor responde 'sí' o 'no'.\n" + "-".repeat(16));
                seguir = true;
            }
        } while (seguir);
    }

    public static void handleBusquedaPorNombre(Scanner scanner, String RESULT_PATH, ArrayList<String> CATEGORIAS, ArrayList<String[]> VADEMECUM, HashMap<String, List<Integer>> hashMapPorNombre) {
        System.out.print("\nIngrese el nombre del medicamento a buscar (o escriba 'salir' para cancelar): ");
        String nombreMedicamento = scanner.nextLine().trim().toLowerCase();

        if (nombreMedicamento.equalsIgnoreCase("salir")) {
            System.out.println("\nSaliendo del proceso de búsqueda.");
            return;
        }

        // BÚSQUEDA
        final List<String[]> RESULTADOS = buscarPorNombre(nombreMedicamento, VADEMECUM, hashMapPorNombre);

        if (RESULTADOS == null) {
            System.out.println("No se encontraron resultados para el medicamento \"" + nombreMedicamento + "\".");
            return;
        }

        // RESULTADOS
        System.out.println("Se encontraron los siguientes resultados:");

        for (int i = 0; i < RESULTADOS.size();  i++) {
            String[] medicamento = RESULTADOS.get(i);

            if (i != 0) { System.out.println(); }

            for (int j = 0; j < CATEGORIAS.size(); j++) {
                System.out.print(CATEGORIAS.get(j));
                System.out.print(": ");
                System.out.print(medicamento[j]);

                if (j < CATEGORIAS.size() - 1) {
                    System.out.println();
                }
            }

            System.out.println("\n" + "-".repeat(40));
        }

        // GUARDAR ARCHIVO
        String nombreArchivo = "resultados_nombre_medicamento_" + nombreMedicamento + ".txt";

        System.out.println("\n¿Quieres guardar los resultados en un archivo aparte? (sí/no)");

        boolean seguir;

        do {
            String respuesta = scanner.nextLine().trim().toLowerCase();

            if (respuesta.equals("sí") || respuesta.equals("si")) {
                guardarResultadosEnArchivo(RESULT_PATH, nombreArchivo, RESULTADOS, CATEGORIAS);
                seguir = false;
            } else if (respuesta.equals("no")) {
                seguir = false;
            } else {
                System.out.println("\nPor favor responde 'sí' o 'no'.\n" + "-".repeat(16));
                seguir = true;
            }
        } while (seguir);
    }

    public static boolean confirmarSeguirEnLaApp(Scanner scanner) {
        System.out.print("¿Quieres realizar otra acción? (sí/no): ");

        while (true) {
            String respuesta = scanner.nextLine().trim().toLowerCase();

            if (respuesta.equals("sí") || respuesta.equals("si")) {
                return true;
            } else if (respuesta.equals("no")) {
                return false;
            } else {
                System.out.print("\nPor favor responde 'sí' o 'no': ");
            }
        }
    }

    // Métodos auxiliares para desarrolladores.

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
            if (i < CATEGORIAS.size() - 1) { System.out.print("| "); }
        }

        System.out.println();

        for (int j = 0; j < maxAnchos.size(); j++) {
            System.out.print("-".repeat(maxAnchos.get(j) + 1));
            if (j < maxAnchos.size() - 1) {
                System.out.print("+-");
            }
        }

        System.out.println();

        for (String[] fila : VADEMECUM) {
            for (int j = 0; j < fila.length; j++) {
                System.out.printf("%-" + (maxAnchos.get(j) + 1) + "s", fila[j]);
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

        System.out.println("\nhashMapPorCodigo: \n");
        hashMapPorCodigo.forEach((key, value) -> System.out.println(key + " - " + value));

        if (hashMapPorNombre.isEmpty()) {
            System.out.println("Error: hashmap por nombre no inicializado aún!");
            return;
        }

        System.out.println("\n" + "-".repeat(8) + "hashMapPorNombre: \n");
        hashMapPorNombre.forEach((key, value) -> System.out.println(key + " - " + value));
    }
}
