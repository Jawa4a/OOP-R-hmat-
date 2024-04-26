
import app.LoginSignupResponse;
import app.ConnectToCloud;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class LoginOrSignuptest {
    // avalik "key", võib committida
    private String apiKey = "AIzaSyD73mvB5ln64_naLcGEX1G-gevoIwRLDZ0";
    private Scanner scanner = new Scanner(System.in);

    public LoginSignupResponse logIn() throws IOException {

        // https://firebase.google.com/docs/reference/rest/auth#section-sign-in-email-password
        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + apiKey;

        // vaja teha try-w-resources äkki?

        System.out.println("Sisesta e-maili aadress");
        String email = "test@mail.com";
        System.out.println("Sisesta salasõna");
        String password = "parool";
        String requestBody = String.format("{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}", email, password);


        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream outputstream = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
            outputstream.write(input, 0, input.length);
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            ObjectMapper mapper = new ObjectMapper();

            // API vastus JSON-i
            LoginSignupResponse responseJSON = mapper.readValue(response.toString(), LoginSignupResponse.class);

            System.out.println("Sisse logitud kui:");
            System.out.println(responseJSON.getEmail());
//            connection.disconnect();

            return responseJSON;

        } catch (IOException e) {
            System.out.println("Sisse logimine ebaõnnestus");
            connection.disconnect();
            return null;
        }
    }

    public LoginSignupResponse signup() throws IOException{

        // https://firebase.google.com/docs/reference/rest/auth#section-create-email-password
        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + apiKey;
        System.out.println("Uue kasutaja loomine");
        System.out.println("Sisesta uue kasutaja e-maili aadress");
        String email = scanner.nextLine();
        System.out.println("Sisesta uue kasutaja salasõna");
        String password = scanner.nextLine();
        String requestBody = String.format("{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}", email, password);

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream outputstream = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
            outputstream.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            ObjectMapper mapper = new ObjectMapper();



            // API vastus JSON-i
            LoginSignupResponse responseJSON = mapper.readValue(response.toString(), LoginSignupResponse.class);

            createUserDbEntry(email);

            System.out.println("Kasutaja loodud!");
            System.out.println("Sisse logitud kui:");
            System.out.println(responseJSON.getEmail());
            connection.disconnect();

            return responseJSON;

        } catch (IOException e) {
            System.out.println("Kasutaja loomine ebaõnnestus");
            connection.disconnect();
            return null;
        }

    }

    public void createUserDbEntry(String email) throws IOException {
        HttpURLConnection connection = new ConnectToCloud().connectToDatabaseDocument("users", email);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");

        String requestBody = "{ \\\"fields\\\": {\\\"bio\\\": {\\\"stringValue\\\": \\\"\uD83D\uDE0A\\\"}, \\\"likedPosts\\\": {\\\"arrayValue\\\": {\\\"values\\\": []}}} }";

        try (OutputStream outputStream = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
            outputStream.write(input, 0, input.length);
        }
    }
}
