import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

class HashService {

    private static final String ALGORITHM = "SHA-256";

    public String calculateSHA256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] hashBytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error con el algoritmo " + ALGORITHM, e);
        }
    }

    public boolean verifyMatch(String msg1, String msg2) {
        return calculateSHA256(msg1).equalsIgnoreCase(calculateSHA256(msg2));
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02X", b));
        }
        return hexString.toString();
    }
}

public class Parte1 {

    private static final Scanner scanner = new Scanner(System.in);
    private static final HashService hashService = new HashService();

    public static void main(String[] args) {
        int opcion = -1;

        System.out.println("==================================================");
        System.out.println("   PRÁCTICA PSP - PARTE 1: CÁLCULO DE RESÚMENES  ");
        System.out.println("==================================================");

        do {
            mostrarMenu();
            try {
                System.out.print("Selecciona una opción (0-4): ");
                opcion = Integer.parseInt(scanner.nextLine().trim());

                switch (opcion) {
                    case 1:
                        calcularHashSimple();
                        break;
                    case 2:
                        compararDosMensajes();
                        break;
                    case 3:
                        demostrarEfectoAvalancha();
                        break;
                    case 4:
                        comprobarDeterminismo();
                        break;
                    case 0:
                        System.out.println("\nSaliendo del programa.");
                        break;
                    default:
                        System.out.println("\nOpción no válida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("\nPor favor, introduce un número válido.");
            }

            if (opcion != 0) {
                System.out.println("\nPresiona ENTER para continuar...");
                scanner.nextLine();
            }

        } while (opcion != 0);

        scanner.close();
    }

    private static void mostrarMenu() {
        System.out.println("\n----------------- MENÚ PRINCIPAL -----------------");
        System.out.println("1. Calcular hash SHA-256 de un mensaje.");
        System.out.println("2. Comparar dos mensajes mediante sus hashes.");
        System.out.println("3. Comprobar variación del hash al modificar un mensaje.");
        System.out.println("4. Comprobar consistencia del hash.");
        System.out.println("0. Salir.");
        System.out.println("--------------------------------------------------");
    }

    private static void calcularHashSimple() {
        System.out.println("\n--- 1. Cálculo de Hash SHA-256 ---");
        System.out.print("Introduce el mensaje: ");
        String mensaje = scanner.nextLine();

        System.out.println("\nMensaje: \"" + mensaje + "\"");
        System.out.println("Hash SHA-256: " + hashService.calculateSHA256(mensaje));
    }

    private static void compararDosMensajes() {
        System.out.println("\n--- 2. Comparación de Dos Mensajes ---");
        System.out.print("Introduce el primer mensaje: ");
        String msg1 = scanner.nextLine();

        System.out.print("Introduce el segundo mensaje: ");
        String msg2 = scanner.nextLine();

        String hash1 = hashService.calculateSHA256(msg1);
        String hash2 = hashService.calculateSHA256(msg2);

        System.out.println("\nHash 1: " + hash1);
        System.out.println("Hash 2: " + hash2);

        if (hashService.verifyMatch(msg1, msg2)) {
            System.out.println("\n-> RESULTADO: Los mensajes son iguales (los hashes coinciden).");
        } else {
            System.out.println("\n-> RESULTADO: Los mensajes son diferentes (los hashes no coinciden).");
        }
    }

    private static void demostrarEfectoAvalancha() {
        System.out.println("\n--- 3. Variación del Hash ---");
        System.out.print("Introduce el mensaje original: ");
        String original = scanner.nextLine();

        System.out.print("Introduce el mensaje modificado: ");
        String modificado = scanner.nextLine();

        System.out.println("\nOriginal:   \"" + original + "\"");
        System.out.println("Hash:       " + hashService.calculateSHA256(original));

        System.out.println("\nModificado: \"" + modificado + "\"");
        System.out.println("Hash:       " + hashService.calculateSHA256(modificado));
    }

    private static void comprobarDeterminismo() {
        System.out.println("\n--- 4. Comprobación de Consistencia ---");
        System.out.print("Introduce el mensaje: ");
        String mensaje = scanner.nextLine();

        int repeticiones = 5;
        System.out.println("\nCalculando el hash " + repeticiones + " veces...");

        String primerHash = null;
        boolean determinista = true;

        for (int i = 1; i <= repeticiones; i++) {
            String hash = hashService.calculateSHA256(mensaje);
            System.out.println("Iteración " + i + ": " + hash);

            if (primerHash == null) {
                primerHash = hash;
            } else if (!primerHash.equals(hash)) {
                determinista = false;
            }
        }

        if (determinista) {
            System.out.println("\n-> RESULTADO: Todos los hashes calculados son idénticos.");
        } else {
            System.out.println("\n-> RESULTADO: Error en el cálculo.");
        }
    }
}
