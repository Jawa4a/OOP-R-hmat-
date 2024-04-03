package app;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) throws IOException {
        // avalik "key", võib committida
        String apiKey = "AIzaSyD73mvB5ln64_naLcGEX1G-gevoIwRLDZ0";


        // https://firebase.google.com/docs/reference/rest/auth#section-sign-in-email-password
        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + apiKey;

        // vaja teha try-w-resources äkki?
        Scanner scanner = new Scanner(System.in);
        System.out.println("Sisesta e-maili aadress");
        String email = scanner.nextLine();
        System.out.println("Sisesta salasõna");
        String password = scanner.nextLine();
        String requestBody = String.format("{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}", email, password);


        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);


        try (OutputStream outputstream = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes("utf-8");
            outputstream.write(input, 0, input.length);
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            ObjectMapper mapper = new ObjectMapper();

            // API vastus JSON-i
            LoginResponse responseJSON = mapper.readValue(response.toString(), LoginResponse.class);


            System.out.println("Sisse logitud kui:");
            System.out.println(responseJSON.email);


        } catch (IOException e) {
            System.out.println("Sisse logimine ebaõnnestus");
        }

        connection.disconnect();

    }
}