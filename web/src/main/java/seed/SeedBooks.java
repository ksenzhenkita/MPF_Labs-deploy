package seed;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SeedBooks {

    private static final String BASE_URL = "https://mpf-labs.onrender.com";
    private static final String EMAIL    = "";//email of moderator from SeedAdmin
    private static final String PASSWORD = "";//password of moderator
    private static final int N           = Integer.parseInt(System.getenv().getOrDefault("N", "5"));

    private static final Pattern CSRF_INPUT = Pattern.compile(
            "name\\s*=\\s*\"_csrf\"[^>]*value\\s*=\\s*\"([^\"]+)\"",
            Pattern.CASE_INSENSITIVE
    );

    private static final Random rnd = new Random();

    public static void main(String[] args) throws Exception {

        var cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        var client = HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .connectTimeout(Duration.ofSeconds(20))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        // 1) GET /login -> отримати cookies + CSRF токен
        String loginHtml = httpGet(client, BASE_URL + "/login");
        String csrf = extractCsrf(loginHtml);
        if (csrf == null) {
            throw new RuntimeException("Cannot find _csrf token on /login page. Either CSRF is off or page markup changed.");
        }

        // 2) POST /login (formLogin)
        doLogin(client, csrf);

        // 3) POST N книг у /api/books
        List<Book> books = generateBooks(N);
        int ok = 0;
        for (int i = 0; i < books.size(); i++) {
            Book b = books.get(i);
            try {
                int code = postBook(client, b, csrf);
                if (code == 401 || code == 403) {
                    // якщо CSRF токен одноразовий/оновився — перетягнемо новий з /login (часто не треба, але нехай)
                    String html2 = httpGet(client, BASE_URL + "/login");
                    String csrf2 = extractCsrf(html2);
                    if (csrf2 != null) csrf = csrf2;

                    code = postBook(client, b, csrf);
                }

                if (code >= 200 && code < 300) {
                    ok++;
                    System.out.printf("[%d/%d] OK: %s%n", ok, books.size(), b.title);
                } else {
                    System.out.printf("[%d/%d] FAIL(%d): %s%n", i + 1, books.size(), code, b.title);
                }
            } catch (Exception e) {
                System.out.printf("[%d/%d] ERROR: %s -> %s%n", i + 1, books.size(), b.title, e.getMessage());
            }
        }

        System.out.println("Done. Created: " + ok + "/" + books.size());
    }

    private static void doLogin(HttpClient client, String csrf) throws IOException, InterruptedException {
        String body = form(
                "_csrf", csrf,
                "email", EMAIL,
                "password", PASSWORD
        );

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/login"))
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

        // Успішний login зазвичай редіректить (302) на "/"
        if (resp.statusCode() >= 400) {
            throw new RuntimeException("Login failed: HTTP " + resp.statusCode() + " body=" + resp.body());
        }

        // маленька перевірка: після логіну /api/books має бути не 401
        HttpRequest check = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/books?page=0&size=1"))
                .timeout(Duration.ofSeconds(20))
                .GET()
                .build();
        HttpResponse<String> checkResp = client.send(check, HttpResponse.BodyHandlers.ofString());
        if (checkResp.statusCode() == 401 || checkResp.statusCode() == 403) {
            throw new RuntimeException("Login seems not effective (still " + checkResp.statusCode() + "). " +
                    "Maybe wrong credentials or role is not MODERATOR/ADMIN.");
        }

        System.out.println("Login OK");
    }

    private static int postBook(HttpClient client, Book b, String csrf) throws IOException, InterruptedException {
        String json = "{"
                + "\"title\":\"" + escapeJson(b.title) + "\","
                + "\"author\":\"" + escapeJson(b.author) + "\","
                + "\"pubYear\":" + b.year
                + "}";

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/books"))
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type", "application/json")
                // Spring Security очікує CSRF або як header, або як параметр. Найчастіше працює header X-CSRF-TOKEN.
                .header("X-CSRF-TOKEN", csrf)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        return resp.statusCode();
    }

    private static String httpGet(HttpClient client, String url) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(20))
                .GET()
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() >= 400) {
            throw new RuntimeException("GET " + url + " failed: HTTP " + resp.statusCode());
        }
        return resp.body();
    }

    private static String extractCsrf(String html) {
        Matcher m = CSRF_INPUT.matcher(html);
        return m.find() ? m.group(1) : null;
    }

    private static String form(String... kv) {
        if (kv.length % 2 != 0) throw new IllegalArgumentException("form expects even number of args");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < kv.length; i += 2) {
            if (sb.length() > 0) sb.append("&");
            sb.append(urlenc(kv[i])).append("=").append(urlenc(kv[i + 1]));
        }
        return sb.toString();
    }

    private static String urlenc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static List<Book> generateBooks(int n) {
        List<Book> base = List.of(
                new Book("The Hobbit", "J.R.R. Tolkien", 1937),
                new Book("Dune", "Frank Herbert", 1965),
                new Book("1984", "George Orwell", 1949),
                new Book("Fahrenheit 451", "Ray Bradbury", 1953),
                new Book("Brave New World", "Aldous Huxley", 1932)
        );

        List<Book> out = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (i < base.size()) out.add(base.get(i));
            else {
                out.add(new Book(
                        "Book " + (i + 1),
                        "Author " + (1 + rnd.nextInt(30)),
                        1900 + rnd.nextInt(126)
                ));
            }
        }
        return out;
    }

    private record Book(String title, String author, int year) {}
}
