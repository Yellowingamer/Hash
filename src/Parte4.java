import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Parte4 {

    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_BYTES = 16;

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Scanner scanner = new Scanner(System.in);
    private static final Map<String, UsuarioCredenciales> baseDeDatos = new HashMap<>();

    static class UsuarioCredenciales {
        private final String username;
        private final byte[] salt;
        private final String hashHex;

        public UsuarioCredenciales(String username, byte[] salt, String hashHex) {
            this.username = username;
            this.salt = salt;
            this.hashHex = hashHex;
        }

        public String getUsername() {
            return username;
        }

        public byte[] getSalt() {
            return salt;
        }

        public String getHashHex() {
            return hashHex;
        }
    }

    public static void main(String[] args) {
        cargarUsuariosIniciales();

        int opcion = -1;
        System.out.println("==========================================================");
        System.out.println("  PRÁCTICA PSP - PARTE 4: INICIO DE SESIÓN Y VERIFICACIÓN ");
        System.out.println("==========================================================");

        do {
            System.out.println("\n----------------- MENÚ PRINCIPAL -----------------");
            System.out.println("1. Iniciar sesión.");
            System.out.println("2. Registrar nuevo usuario.");
            System.out.println("3. Listar usuarios.");
            System.out.println("0. Salir.");
            System.out.println("--------------------------------------------------");

            try {
                System.out.print("Selecciona una opción (0-3): ");
                opcion = Integer.parseInt(scanner.nextLine().trim());

                switch (opcion) {
                    case 1:
                        iniciarSesion();
                        break;
                    case 2:
                        registrarNuevoUsuario();
                        break;
                    case 3:
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

    private static void iniciarSesion() {
        System.out.println("\n--- Inicio de Sesión ---");
        System.out.print("Usuario: ");
        String username = scanner.nextLine().trim();

        System.out.print("Contraseña: ");
        String password = scanner.nextLine();

        UsuarioCredenciales usuario = baseDeDatos.get(username.toLowerCase());
        if (usuario == null) {
            System.out.println("\nError: El usuario '" + username + "' no está registrado.");
            return;
        }

        byte[] salt = usuario.getSalt();
        String hashCalculado = calcularHashConSalt(password, salt);

        boolean coincide = MessageDigest.isEqual(
            hashCalculado.getBytes(StandardCharsets.UTF_8),
            usuario.getHashHex().getBytes(StandardCharsets.UTF_8)
        );

        if (coincide) {
            System.out.println("\nAcceso concedido. Bienvenido/a, " + usuario.getUsername() + ".");
        } else {
            System.out.println("\nError: Contraseña incorrecta.");
        }
    }

    private static void registrarNuevoUsuario() {
        System.out.println("\n--- Registro de Usuario ---");
        System.out.print("Nombre de usuario: ");
        String username = scanner.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("\nEl usuario no puede estar vacío.");
            return;
        }

        if (baseDeDatos.containsKey(username.toLowerCase())) {
            System.out.println("\nError: El usuario ya existe.");
            return;
        }

        System.out.print("Contraseña: ");
        String password = scanner.nextLine();

        registrarEnMemoria(username, password);
        System.out.println("\nUsuario '" + username + "' registrado.");
    }

    private static void listarUsuarios() {
        System.out.println("\n--- Usuarios Almacenados ---");
        if (baseDeDatos.isEmpty()) {
            System.out.println("No hay usuarios.");
            return;
        }

        for (UsuarioCredenciales u : baseDeDatos.values()) {
            System.out.println("Usuario: " + u.getUsername());
            System.out.println("Salt:    " + bytesToHex(u.getSalt()));
            System.out.println("Hash:    " + u.getHashHex());
            System.out.println("--------------------------------------------------");
        }
    }

    private static void cargarUsuariosIniciales() {
        registrarEnMemoria("admin", "admin123");
        registrarEnMemoria("alumno", "psp2026");
    }

    private static void registrarEnMemoria(String username, String password) {
        byte[] salt = new byte[SALT_BYTES];
        secureRandom.nextBytes(salt);

        String hashHex = calcularHashConSalt(password, salt);
        baseDeDatos.put(username.toLowerCase(), new UsuarioCredenciales(username, salt, hashHex));
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
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
