package app;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

public class LoginOrSignup {
    // avalik "key", võib committida
    private final String apiKey = "AIzaSyD73mvB5ln64_naLcGEX1G-gevoIwRLDZ0";
    private final Scanner scanner = new Scanner(System.in);

    public LoginSignupResponse logIn() throws IOException {

        // https://firebase.google.com/docs/reference/rest/auth#section-sign-in-email-password
        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + apiKey;

        // vaja teha try-w-resources äkki?

        System.out.println("Sisesta e-maili aadress");
        String email = scanner.nextLine().toLowerCase();
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

            System.out.println("Tere,");
            System.out.println(responseJSON.email);
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

            if (createUserDbEntry(email)){
                System.out.println("Kasutaja loodud!");
                System.out.println("Sisse logitud kui:");
                System.out.println(responseJSON.email);
            }
            else {
                System.out.println("Viga");
            }
            connection.disconnect();
            return responseJSON;

        } catch (IOException e) {
            System.out.println("Kasutaja loomine ebaõnnestus");
            connection.disconnect();
            return null;
        }

    }

    public boolean createUserDbEntry(String email) throws IOException {
        // Loome andmebaasi dokumendi, mis viitab uuele kasutajale.
        String urlString = "https://firestore.googleapis.com/v1/projects/obje-8d9a1/databases/(default)/documents/users/" + email;
        HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        OutputStream os = connection.getOutputStream();
        os.flush();
        os.close();
        int responseCode = connection.getResponseCode();
        connection.disconnect();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println("Kasutaja lisatud andmebaasi");
            return createProfile(email);
        }
        else return false;
    }
    public boolean createProfile(String email) throws IOException {
        // Uuendame loodud kasutaja välju, et ei oleks tühi (loome baas väljad)
        String urlString = "https://firestore.googleapis.com/v1/projects/obje-8d9a1/databases/(default)/documents/users/" + email;
        UserProfile userProfile = new UserProfile();
        userProfile.setName(urlString);
        // Lisame uude profiili väljad.
        userProfile.setUserInformation(new UserInformation());
        userProfile.getUserInformation().setBio(new UserInformation.Bio());
        userProfile.getUserInformation().getBio().setStringValue("");

        userProfile.getUserInformation().setSubscriptions(new UserInformation.Subscriptions());
        userProfile.getUserInformation().getSubscriptions().setArrayValue(new UserInformation.ArrayValue());
        userProfile.getUserInformation().getSubscriptions().getArrayValue().setValues(new ArrayList<>());

        userProfile.getUserInformation().setLikedPosts(new UserInformation.LikedPosts());
        userProfile.getUserInformation().getLikedPosts().setArrayValue(new UserInformation.ArrayValue());
        userProfile.getUserInformation().getLikedPosts().getArrayValue().setValues(new ArrayList<>());

        userProfile.getUserInformation().setFriends(new UserInformation.Friends());
        userProfile.getUserInformation().getFriends().setArrayValue(new UserInformation.ArrayValue());
        userProfile.getUserInformation().getFriends().getArrayValue().setValues(new ArrayList<>());

        userProfile.getUserInformation().setFriendrequests(new UserInformation.FriendRequests());
        userProfile.getUserInformation().getFriendrequests().setArrayValue(new UserInformation.ArrayValue());
        userProfile.getUserInformation().getFriendrequests().getArrayValue().setValues(new ArrayList<>());


        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(userProfile);

        HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        OutputStream os = connection.getOutputStream();
        os.write(json.getBytes());
        os.flush();
        os.close();
        int responseCode = connection.getResponseCode();
        connection.disconnect();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println("Profiil loodud");
            return true;
        }
        else return false;
    }
}
