import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VentasApp {

    public static void main(String[] args) {
        // Lectura del archivo CSV y creación de la lista de objetos Ventas
        List<Ventas> ventasList = leerArchivoCSV("sales_data.csv");

        // Colección de reducción mutable para realizar consultas
        Map<String, List<Ventas>> ventasPorCiudad = generarVentasPorCiudad(ventasList);

        // Consultas específicas para la ciudad de New York
        List<Ventas> ventasNY = obtenerVentasPorCiudad(ventasPorCiudad, "NY");
        double totalVentasNY = obtenerTotalVentas(ventasNY);
        List<Ventas> autosClasicosNY = obtenerVentasPorProducto(ventasNY, "Auto Clásico");
        double totalAutosClasicosNY = obtenerTotalVentas(autosClasicosNY);
        List<Ventas> motocicletasNY = obtenerVentasPorProducto(ventasNY, "Motocicleta");
        double totalMotocicletasNY = obtenerTotalVentas(motocicletasNY);
        String clienteMasAutosNY = obtenerClienteMasCompro(autosClasicosNY);

        // Consultas para todo el archivo de ventas
        String clienteMasCompro = obtenerClienteMasCompro(ventasList);
        String clienteMenosCompro = obtenerClienteMenosCompro(ventasList);
    }

    private static List<Ventas> leerArchivoCSV(String archivo) {
        List<Ventas> ventasList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                Ventas ventas = new Ventas(datos[0], datos[1], datos[2], datos[3], Double.parseDouble(datos[4]), Integer.parseInt(datos[5]));
                ventasList.add(ventas);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ventasList;
    }

    private static Map<String, List<Ventas>> generarVentasPorCiudad(List<Ventas> ventasList) {
        Map<String, List<Ventas>> ventasPorCiudad = new HashMap<>();
        for (Ventas ventas : ventasList) {
            String ciudad = ventas.getCiudad();
            if (ventasPorCiudad.containsKey(ciudad)) {
                ventasPorCiudad.get(ciudad).add(ventas);
            } else {
                List<Ventas> listaVentas = new ArrayList<>();
                listaVentas.add(ventas);
                ventasPorCiudad.put(ciudad, listaVentas);
            }
        }
        return ventasPorCiudad;
    }

    private static List<Ventas> obtenerVentasPorCiudad(Map<String, List<Ventas>> ventasPorCiudad, String ciudad) {
        return ventasPorCiudad.getOrDefault(ciudad, new ArrayList<>());
    }

    private static double obtenerTotalVentas(List<Ventas> ventasList) {
        return ventasList.stream().mapToDouble(Ventas::getMonto).sum();
    }

        private static List<Ventas> obtenerVentasPorProducto(List<Ventas> ventasList, String producto) {
        return ventasList.stream().filter(v -> v.getProducto().equals(producto)).toList();
    }

    private static String obtenerClienteMasCompro(List<Ventas> ventasList) {
        Optional<Map.Entry<String, Long>> clienteMax = ventasList.stream()
                .collect(Collectors.groupingBy(Ventas::getCliente, Collectors.counting()))
                .entrySet().stream()
                .max(Comparator.comparingLong(Map.Entry::getValue));
        return clienteMax.map(Map.Entry::getKey).orElse("");
    }

    private static String obtenerClienteMasCompro(List<Ventas> ventasList, String ciudad) {
        List<Ventas> ventasCiudad = obtenerVentasPorCiudad(generarVentasPorCiudad(ventasList), ciudad);
        return obtenerClienteMasCompro(ventasCiudad);
    }

    private static String obtenerClienteMasCompro(Map<String, List<Ventas>> ventasPorCiudad, String ciudad) {
        List<Ventas> ventasCiudad = obtenerVentasPorCiudad(ventasPorCiudad, ciudad);
        return obtenerClienteMasCompro(ventasCiudad);
    }

    private static String obtenerClienteMasCompro(Map<String, List<Ventas>> ventasPorCiudad) {
        Optional<Map.Entry<String, Double>> clienteMax = ventasPorCiudad.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> obtenerTotalVentas(e.getValue())))
                .entrySet().stream()
                .max(Comparator.comparingDouble(Map.Entry::getValue));
        return clienteMax.map(Map.Entry::getKey).orElse("");
    }

    private static String obtenerClienteMenosCompro(List<Ventas> ventasList) {
        Optional<Map.Entry<String, Long>> clienteMin = ventasList.stream()
                .collect(Collectors.groupingBy(Ventas::getCliente, Collectors.counting()))
                .entrySet().stream()
                .min(Comparator.comparingLong(Map.Entry::getValue));
        return clienteMin.map(Map.Entry::getKey).orElse("");
    }

    private static String obtenerClienteMenosCompro(List<Ventas> ventasList, String ciudad) {
        List<Ventas> ventasCiudad = obtenerVentasPorCiudad(generarVentasPorCiudad(ventasList), ciudad);
        return obtenerClienteMenosCompro(ventasCiudad);
    }

    private static String obtenerClienteMenosCompro(Map<String, List<Ventas>> ventasPorCiudad, String ciudad) {
        List<Ventas> ventasCiudad = obtenerVentasPorCiudad(ventasPorCiudad, ciudad);
        return obtenerClienteMenosCompro(ventasCiudad);
    }

    private static String obtenerClienteMenosCompro(Map<String, List<Ventas>> ventasPorCiudad) {
        Optional<Map.Entry<String, Double>> clienteMin = ventasPorCiudad.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> obtenerTotalVentas(e.getValue())))
                .entrySet().stream()
                .min(Comparator.comparingDouble(Map.Entry::getValue));
        return clienteMin.map(Map.Entry::getKey).orElse("");
    }

}

