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
        Scanner scanner = new Scanner(System.in);

        while (seguirEnLaApp) {
            System.out.println();
            System.out.println("Menú principal - Escriba el número de la tarea a realizar:");
            for (String opcion : opciones) { System.out.println(opcion); }

            int opcionActual = obtenerOpcionValida(scanner);  // Validación de entrada para la opción

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
            seguirEnLaApp = confirmarSeguirEnLaApp(scanner);
        }

        System.out.println();
        System.out.println("Cerrando aplicación. Hasta luego!");

        scanner.close(); // Cierro el scanner para prevenir filtrado de información
    }

    // Método para obtener una opción válida del usuario
    public static int obtenerOpcionValida(Scanner scanner) {
        int opcion = -1;
        while (opcion < 1 || opcion > 5) {
            System.out.println("Por favor ingrese una opción válida:");
            try {
                opcion = Integer.parseInt(scanner.nextLine()); // Intentamos leer un número
            } catch (NumberFormatException e) {
                System.out.println("¡Error! Debe ingresar un número válido.");
                continue; // Si no es un número, volvemos a pedir la opción
            }
            
            if (opcion < 1 || opcion > 5) {
                System.out.println("Opción no válida. Elija entre 1 y 5.");
            }
        }
        return opcion;
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

        System.out.println("hashMapPorCodigo: ");
        hashMapPorCodigo.forEach((key, value) -> System.out.println(key + " - " + value));

        if (hashMapPorNombre.isEmpty()) {
            System.out.println("Error: hashmap por nombre no inicializado aún!");
            return;
        }

        System.out.println("hashMapPorNombre: ");
        hashMapPorNombre.forEach((key, value) -> System.out.println(key + " - " + value));
    }

    public static void buscarPorCodigo(String RESULT_PATH, ArrayList<String> CATEGORIAS, ArrayList<String[]> VADEMECUM, HashMap<String, Integer> hashMapPorCodigo) {
        Scanner scanner = new Scanner(System.in);

        System.out.println();
        System.out.print("Ingrese el código del medicamento a buscar: ");
        String codigoMedicamento = scanner.nextLine();

        Integer indiceResultado = hashMapPorCodigo.get(codigoMedicamento);

        if (indiceResultado == null) {
            System.out.println("No se encontró ninguna coincidencia!");
            return;
        }

        String[] medicamento = VADEMECUM.get(indiceResultado);

        for (int i = 0; i < CATEGORIAS.size(); i++) {
            System.out.print(CATEGORIAS.get(i));
            System.out.print(": ");
            System.out.println(medicamento[i]);
        }

        guardarResultadosEnArchivo(scanner, RESULT_PATH, medicamento);
    }

    public static void buscarPorNombre(String RESULT_PATH, ArrayList<String> CATEGORIAS, ArrayList<String[]> VADEMECUM, HashMap<String, List<Integer>> hashMapPorNombre) {
        Scanner scanner = new Scanner(System.in);

        System.out.println();
        System.out.print("Ingrese el nombre del medicamento a buscar (o escriba 'salir' para cancelar): ");
        String nombreMedicamento = scanner.nextLine().trim();

        if (nombreMedicamento.equalsIgnoreCase("salir")) {
            System.out.println("Saliendo del proceso de búsqueda.");
            return;
        }

        nombreMedicamento = nombreMedicamento.toLowerCase(); // Convertimos a minúsculas
        List<Integer> indicesResultado = hashMapPorNombre.get(nombreMedicamento);

        if (indicesResultado == null || indicesResultado.isEmpty()) {
            System.out.println("No se encontraron resultados para el medicamento \"" + nombreMedicamento + "\".");
            return;
        }

        System.out.println("Se encontraron los siguientes resultados:");
        for (Integer indice : indicesResultado) {
            String[] medicamento = VADEMECUM.get(indice);

            for (int i = 0; i < CATEGORIAS.size(); i++) {
                System.out.print(CATEGORIAS.get(i));
                System.out.print(": ");
                System.out.println(medicamento[i]);
            }

            System.out.println("-----------------------------");
        }

        guardarResultadosEnArchivo(scanner, RESULT_PATH, indicesResultado, VADEMECUM, CATEGORIAS);
    }

    public static void guardarResultadosEnArchivo(Scanner scanner, String RESULT_PATH, String[] medicamento) {
        System.out.println("¿Desea guardar los resultados en un archivo? (si/no)");
        String respuestaGuardar = scanner.nextLine().trim().toLowerCase();

        if (respuestaGuardar.equals("si")) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(RESULT_PATH))) {
                for (String campo : medicamento) {
                    writer.write(campo + ";");
                }
                writer.newLine();
                System.out.println("Resultados guardados correctamente en " + RESULT_PATH);
            } catch (IOException e) {
                System.out.println("Error al guardar los resultados: " + e.getMessage());
            }
        }
    }

    public static void guardarResultadosEnArchivo(Scanner scanner, String RESULT_PATH, List<Integer> indicesResultado, ArrayList<String[]> VADEMECUM, ArrayList<String> CATEGORIAS) {
        System.out.println("¿Desea guardar los resultados en un archivo? (si/no)");
        String respuestaGuardar = scanner.nextLine().trim().toLowerCase();

        if (respuestaGuardar.equals("si")) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(RESULT_PATH))) {
                for (Integer indice : indicesResultado) {
                    String[] medicamento = VADEMECUM.get(indice);
                    for (String campo : medicamento) {
                        writer.write(campo + ";");
                    }
                    writer.newLine();
                }
                System.out.println("Resultados guardados correctamente en " + RESULT_PATH);
            } catch (IOException e) {
                System.out.println("Error al guardar los resultados: " + e.getMessage());
            }
        }
    }

    public static boolean confirmarSeguirEnLaApp(Scanner scanner) {
        while (true) {
            System.out.println("¿Quieres realizar otra acción? (sí/no)");
            String respuesta = scanner.nextLine().trim().toLowerCase();

            if (respuesta.equals("sí") || respuesta.equals("si")) {
                return true;
            } else if (respuesta.equals("no")) {
                return false;
            } else {
                System.out.println("Por favor responde 'sí' o 'no'.");
            }
        }
    }
}
