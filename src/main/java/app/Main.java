package app;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {
    // avalik "key", võib committida
    static String apiKey = "AIzaSyD73mvB5ln64_naLcGEX1G-gevoIwRLDZ0";
    static Scanner scanner = new Scanner(System.in);

    public static LoginSignupResponse logIn() throws IOException {

        // https://firebase.google.com/docs/reference/rest/auth#section-sign-in-email-password
        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + apiKey;

        // vaja teha try-w-resources äkki?

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
            System.out.println(responseJSON.email);
            connection.disconnect();

            return responseJSON;

        } catch (IOException e) {
            System.out.println("Sisse logimine ebaõnnestus");
            connection.disconnect();
            return null;
        }
    }

    public static LoginSignupResponse signup() throws IOException{

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

            System.out.println("Kasutaja loodud!");
            System.out.println("Sisse logitud kui:");
            System.out.println(responseJSON.email);
            connection.disconnect();

            return responseJSON;

        } catch (IOException e) {
            System.out.println("Kasutaja loomine ebaõnnestus");
            connection.disconnect();
            return null;
        }

    }


    public static void main(String[] args) throws IOException {
        // tuleb teha loogika, mis tervitab kasutajat, annab valiku teha uus konto või logida sisse olemasolevaga, kutsub vastava meetodi jne jne
       // LoginSignupResponse userInfo = signup();
       // LoginSignupResponse userInfo = logIn();
    }
}