package seed;

import java.sql.*;
import java.util.Scanner;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class SeedAdmin {

    public static void main(String[] args) throws Exception {
        // DATABASE_URL="postgresql://user:pass@host/db" external url from render
        String externalUrl =  "DATABASE_URL";


        String jdbcUrl = toJdbcUrl(externalUrl);

        DriverManager.setLoginTimeout(10);

        try (Connection c = DriverManager.getConnection(jdbcUrl)) {
            c.setAutoCommit(false);

            Scanner sc = new Scanner(System.in);
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            System.out.println("=== Create/Update ADMIN ===");
            UserInput admin = readUser(sc, "ADMIN");
            upsertUser(c, encoder, admin);

            System.out.println("\n=== Create/Update MODERATOR ===");
            UserInput mod = readUser(sc, "MODERATOR");
            upsertUser(c, encoder, mod);

            c.commit();
            System.out.println("\nDone.  Users seeded/updated.");
        }
    }

    static class UserInput {
        final String role;
        final String email;
        final String nickname;
        final String rawPassword;

        UserInput(String role, String email, String nickname, String rawPassword) {
            this.role = role;
            this.email = email;
            this.nickname = nickname;
            this.rawPassword = rawPassword;
        }
    }

    private static UserInput readUser(Scanner sc, String role) {
        System.out.print("Email: ");
        String email = sc.nextLine().trim();

        System.out.print("Nickname: ");
        String nickname = sc.nextLine().trim();

        System.out.print("Password (will be BCrypt-hashed): ");
        String pw = sc.nextLine(); // простий варіант; без masking

        return new UserInput(role, email, nickname, pw);
    }

    private static void upsertUser(Connection c, BCryptPasswordEncoder encoder, UserInput u) throws SQLException {
        // verified=true, token=NULL
        String sql = """
            INSERT INTO users (email, password, nickname, role, verified, verification_token)
            VALUES (?, ?, ?, ?, TRUE, NULL)
            ON CONFLICT (email) DO UPDATE SET
                password = EXCLUDED.password,
                nickname = EXCLUDED.nickname,
                role = EXCLUDED.role,
                verified = TRUE,
                verification_token = NULL
            """;

        String hash = encoder.encode(u.rawPassword);

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.email);
            ps.setString(2, hash);
            ps.setString(3, u.nickname);
            ps.setString(4, u.role);
            int affected = ps.executeUpdate();
            System.out.println(u.role + ": affected rows = " + affected);
        }
    }

    private static String toJdbcUrl(String externalUrl) {
        // external: postgresql://user:pass@host:5432/db
        // jdbc:     jdbc:postgresql://host:5432/db?user=...&password=...&sslmode=require
        String url = externalUrl.trim();

        if (url.startsWith("jdbc:postgresql://")) {
            // якщо вже jdbc — просто гарантуємо sslmode=require
            return url.contains("sslmode=") ? url : (url + (url.contains("?") ? "&" : "?") + "sslmode=require");
        }

        if (!url.startsWith("postgresql://")) {
            throw new IllegalArgumentException("Expected URL starting with postgresql:// or jdbc:postgresql://");
        }

        // Витягнемо user:pass@host/db
        // postgresql://USER:PASS@HOST/DB
        String withoutScheme = url.substring("postgresql://".length());
        String[] parts = withoutScheme.split("@", 2);
        if (parts.length != 2) throw new IllegalArgumentException("Bad URL format (no @)");

        String userPass = parts[0];
        String hostDb = parts[1];

        String[] up = userPass.split(":", 2);
        if (up.length != 2) throw new IllegalArgumentException("Bad URL format (no user:pass)");

        String user = up[0];
        String pass = up[1];

        // hostDb: host:port/db  OR host/db
        String jdbc = "jdbc:postgresql://" + hostDb;

        // додамо параметри
        String sep = jdbc.contains("?") ? "&" : "?";
        return jdbc + sep + "user=" + encode(user) + "&password=" + encode(pass) + "&sslmode=require";
    }

    private static String encode(String s) {
        // мінімальний енкодинг для спецсимволів у паролі (пробіли, &, =)
        return s.replace("%", "%25")
                .replace(" ", "%20")
                .replace("&", "%26")
                .replace("=", "%3D");
    }
}
