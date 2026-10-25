import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Parte2 {

    private static final String ALGORITHM = "SHA-256";
    private static final String DEFAULT_MESSAGE_FILE = "missatge.txt";
    private static final String HASH_FILE = "missatge.hash";
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("  PRÁCTICA PSP - PARTE 2: VERIFICACIÓN DE INTEGRIDAD     ");
        System.out.println("==========================================================");

        try {
            // Emisor
            System.out.println("\n[1] FASE EMISOR");
            System.out.print("Introduce el mensaje a enviar: ");
            String mensaje = scanner.nextLine();

            Files.writeString(Path.of(DEFAULT_MESSAGE_FILE), mensaje, StandardCharsets.UTF_8);
            System.out.println("-> Mensaje guardado en '" + DEFAULT_MESSAGE_FILE + "'.");

            // Versión modificada de prueba
            Files.writeString(Path.of("missatgeCanviat.txt"), mensaje + ".", StandardCharsets.UTF_8);

            String hashGenerado = calculateSHA256(mensaje.getBytes(StandardCharsets.UTF_8));
            Files.writeString(Path.of(HASH_FILE), hashGenerado, StandardCharsets.UTF_8);
            System.out.println("-> Hash guardado en '" + HASH_FILE + "': " + hashGenerado);

            // Receptor
            System.out.println("\n----------------------------------------------------------");
            System.out.println("[2] FASE RECEPTOR");
            System.out.print("Introduce el nombre del archivo a verificar: ");
            String archivoRecibido = scanner.nextLine().trim();

            Path rutaRecibido = Path.of(archivoRecibido);
            Path rutaHash = Path.of(HASH_FILE);

            if (!Files.exists(rutaRecibido)) {
                System.out.println("\nError: El archivo '" + archivoRecibido + "' no existe.");
                return;
            }
            if (!Files.exists(rutaHash)) {
                System.out.println("\nError: El archivo '" + HASH_FILE + "' no existe.");
                return;
            }

            byte[] bytesRecibidos = Files.readAllBytes(rutaRecibido);
            String textoRecibido = new String(bytesRecibidos, StandardCharsets.UTF_8);
            String hashEsperado = Files.readString(rutaHash, StandardCharsets.UTF_8).trim();

            String hashRecibido = calculateSHA256(bytesRecibidos);

            System.out.println("\nContenido leído: \"" + textoRecibido + "\"");
            System.out.println("Hash calculado: " + hashRecibido);
            System.out.println("Hash esperado:  " + hashEsperado);

            System.out.println("\n----------------- RESULTADO -----------------");
            if (hashRecibido.equalsIgnoreCase(hashEsperado)) {
                System.out.println("-> Integridad verificada: Los hashes coinciden.");
            } else {
                System.out.println("-> Modificación detectada: Los hashes no coinciden.");
            }
            System.out.println("---------------------------------------------");

        } catch (IOException e) {
            System.err.println("\nError al procesar archivos: " + e.getMessage());
        }

        scanner.close();
    }

    public static String calculateSHA256(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] hashBytes = digest.digest(data);
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algoritmo no disponible: " + ALGORITHM, e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02X", b));
        }
        return hexString.toString();
    }
}
