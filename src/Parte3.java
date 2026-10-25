import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class Usuario {
    private final String username;
    private final String saltHex;
    private final String hashHex;

    public Usuario(String username, String saltHex, String hashHex) {
        this.username = username;
        this.saltHex = saltHex;
        this.hashHex = hashHex;
    }

    public String getUsername() {
        return username;
    }

    public String getSaltHex() {
        return saltHex;
    }

    public String getHashHex() {
        return hashHex;
    }
}

public class Parte3 {

    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH_BYTES = 16;

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Map<String, Usuario> baseDeDatos = new HashMap<>();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int opcion = -1;

        System.out.println("==========================================================");
        System.out.println("  PRÁCTICA PSP - PARTE 3: REGISTRO DE USUARIOS CON SALT   ");
        System.out.println("==========================================================");

        do {
            System.out.println("\n----------------- MENÚ DE GESTIÓN -----------------");
            System.out.println("1. Registrar nuevo usuario.");
            System.out.println("2. Listar usuarios registrados.");
            System.out.println("0. Salir.");
            System.out.println("---------------------------------------------------");

            try {
                System.out.print("Selecciona una opción (0-2): ");
                opcion = Integer.parseInt(scanner.nextLine().trim());

                switch (opcion) {
                    case 1:
                        registrarUsuario();
                        break;
                    case 2:
                        listarUsuarios();
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

        } while (opcion != 0);

        scanner.close();
    }

    private static void registrarUsuario() {
        System.out.println("\n--- Registro de Nuevo Usuario ---");
        System.out.print("Nombre de usuario: ");
        String username = scanner.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("\nEl nombre de usuario no puede estar vacío.");
            return;
        }

        if (baseDeDatos.containsKey(username.toLowerCase())) {
            System.out.println("\nError: Ya existe un usuario registrado con ese nombre.");
            return;
        }

        System.out.print("Contraseña: ");
        String password = scanner.nextLine();

        byte[] salt = generarSalt(SALT_LENGTH_BYTES);
        String saltHex = bytesToHex(salt);
        String hashHex = calcularHashConSalt(password, salt);

        baseDeDatos.put(username.toLowerCase(), new Usuario(username, saltHex, hashHex));

        System.out.println("\nUsuario '" + username + "' registrado.");
        System.out.println("Salt: " + saltHex);
        System.out.println("Hash: " + hashHex);
    }

    private static void listarUsuarios() {
        System.out.println("\n--- Usuarios Almacenados ---");
        if (baseDeDatos.isEmpty()) {
            System.out.println("No hay ningún usuario registrado.");
            return;
        }

        for (Usuario user : baseDeDatos.values()) {
            System.out.println("Usuario: " + user.getUsername());
            System.out.println("Salt:    " + user.getSaltHex());
            System.out.println("Hash:    " + user.getHashHex());
            System.out.println("---------------------------------------------------");
        }
    }

    private static byte[] generarSalt(int numBytes) {
        byte[] salt = new byte[numBytes];
        secureRandom.nextBytes(salt);
        return salt;
    }

    private static String calcularHashConSalt(String password, byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            digest.update(salt);
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error: Algoritmo no soportado: " + ALGORITHM, e);
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
