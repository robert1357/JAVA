import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Matriz {
private List<List<Object>> datos;
private List<String> nombresColumnas;
public Matriz() {
    this.datos = new ArrayList<>();
    this.nombresColumnas = new ArrayList<>();
}

public void leerDesdeCSV(String archivoCSV) {
    try (BufferedReader br = new BufferedReader(new FileReader(archivoCSV))) {
        String linea;
        boolean primeraLinea = true;
        while ((linea = br.readLine()) != null) {
            String[] partes = linea.split(",");
            if (primeraLinea) {
                // NOMBRE NUMERO
                nombresColumnas.addAll(Arrays.asList(partes));
                primeraLinea = false;
            } else {
                for (int i = 0; i < partes.length; i++) {
                    if (datos.size() <= i) {
                        datos.add(new ArrayList<>());
                    }
                    try {
                        double valor = limpiarNumero(partes[i]);
                        datos.get(i).add(valor);
                    } catch (NumberFormatException e) {
                        datos.get(i).add(partes[i]);
                    }
                }
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}

public void calcularEstadisticas() {
    int numColumnas = datos.size();
    for (int i = 0; i < numColumnas; i++) {
        List<Object> columna = datos.get(i);
        String nombreColumna = nombresColumnas.get(i); // NOMBRE PALABRA


        if (columna.get(0) instanceof Double) {
            calcularEstadisticasNumericas(columna, nombreColumna);
        } else if (columna.get(0) instanceof String) {
            calcularModaYPorcentaje(columna, nombreColumna);
        }
    }
}

private void calcularEstadisticasNumericas(List<Object> columna, String nombreColumna) {
    List<Double> numeros = new ArrayList<>();
    for (Object valor : columna) {
        numeros.add((Double) valor);
    }

    // P
    double sum = 0;
    for (double valor : numeros) {
        sum += valor;
    }
    double promedio = sum / numeros.size();

    // M
    double[] columnaArray = numeros.stream().mapToDouble(Double::doubleValue).toArray();
    Arrays.sort(columnaArray);
    double mediana;
    int n = columnaArray.length;
    if (n % 2 == 0) {
        mediana = (columnaArray[n / 2 - 1] + columnaArray[n / 2]) / 2.0;
    } else {
        mediana = columnaArray[n / 2];
    }

    // V
    double mean = promedio;
    double temp = 0;
    for (double valor : numeros) {
        temp += (valor - mean) * (valor - mean);
    }
    double varianza = temp / numeros.size();

    // RESULTADO NUMEROS
    System.out.println("Resultados de " + nombreColumna + ":");
    System.out.printf("Promedio: %.2f\n", promedio);
    System.out.printf("Mediana: %.2f\n", mediana);
    System.out.printf("Varianza: %.2f\n", varianza);
    System.out.println();
}

private void calcularModaYPorcentaje(List<Object> columna, String nombreColumna) {
    Map<Object, Integer> frecuencias = new HashMap<>();
    // FRECUENCIA
    for (Object valor : columna) {
        frecuencias.put(valor, frecuencias.getOrDefault(valor, 0) + 1);
    }

    Object moda = null;
    int maxFrecuencia = 0;
    for (Map.Entry<Object, Integer> entry : frecuencias.entrySet()) {
        if (entry.getValue() > maxFrecuencia) {
            moda = entry.getKey();
            maxFrecuencia = entry.getValue();
        }
    }
    double porcentaje = (double) maxFrecuencia / columna.size() * 100;

    // RESULTADO DE PALABRAS
    System.out.println("Resultados de " + nombreColumna + ":");
    System.out.println("Mayor porcentaje: " + moda);
    System.out.printf("Porcentaje: %.2f%%\n", porcentaje);
    System.out.println();
}

private double limpiarNumero(String numero) throws NumberFormatException {
    String limpio = numero.trim().replaceAll("[^\\d.\\-]", "");
    return Double.parseDouble(limpio);
}

public static void main(String[] args) {
    Matriz matriz = new Matriz();

    matriz.leerDesdeCSV("Libro.csv");
    matriz.calcularEstadisticas();
}
}