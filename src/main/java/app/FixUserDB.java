package app;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class FixUserDB {
    private Scanner scanner = new Scanner(System.in);

    public void checkUserDB(String email) {
        String urlString = "https://firestore.googleapis.com/v1/projects/obje-8d9a1/databases/(default)/documents/users/" + email;
        UserProfile userProfile = null;
        // Reading profile information.
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
            connection.setRequestMethod("GET");
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            ObjectMapper mapper = new ObjectMapper();

            userProfile = mapper.readValue(reader, UserProfile.class);
            connection.disconnect();
        } catch (IOException e){
            System.out.println("Ei saanud luua ühendust.");
        }
        if (userProfile==null){
            System.out.println("Andmete lugemisel tekkis viga.");
            return;
        }
        // Updating profile values
        try {
            // checking: userInformation
            try {
                UserInformation userInformation = userProfile.getUserInformation();
                if (userInformation==null) throw new NullPointerException("");
            } catch (NullPointerException e){
                userProfile.setUserInformation(new UserInformation());
            }
            // checking: username
            try {
                String username = userProfile.getUserInformation().getUsername().getStringValue();
                if (username.isEmpty()) {
                    throw new NullPointerException("Tühi nimi.");
                }
            } catch (NullPointerException e) {
                System.out.println("Teil pole kasutaja nime!");
                String newUserName = checkUniqueUsername();
                userProfile.getUserInformation().setUsername(new UserInformation.Username());
                userProfile.getUserInformation().getUsername().setStringValue(newUserName);
            }
            // checking: bio
            try {
                String bio = userProfile.getUserInformation().getBio().getStringValue();
            } catch (NullPointerException e) {
                userProfile.getUserInformation().setBio(new UserInformation.Bio());
                userProfile.getUserInformation().getBio().setStringValue("");
            }
            // checking: friendrequests
            try {
                List<UserInformation.Value> friendrequests = userProfile.getUserInformation().getFriendrequests().getArrayValue().getValues();
            } catch (NullPointerException e) {
                userProfile.getUserInformation().setFriendrequests(new UserInformation.FriendRequests());
                userProfile.getUserInformation().getFriendrequests().setArrayValue(new UserInformation.ArrayValue());
                userProfile.getUserInformation().getFriendrequests().getArrayValue().setValues(new ArrayList<>());
            }
            // checking: friends
            try {
                List<UserInformation.Value> friends = userProfile.getUserInformation().getFriends().getArrayValue().getValues();
            } catch (NullPointerException e) {
                userProfile.getUserInformation().setFriends(new UserInformation.Friends());
                userProfile.getUserInformation().getFriends().setArrayValue(new UserInformation.ArrayValue());
                userProfile.getUserInformation().getFriends().getArrayValue().setValues(new ArrayList<>());
            }
            // checking: likedPosts
            try {
                List<UserInformation.Value> likedPosts = userProfile.getUserInformation().getLikedPosts().getArrayValue().getValues();
            } catch (NullPointerException e) {
                userProfile.getUserInformation().setLikedPosts(new UserInformation.LikedPosts());
                userProfile.getUserInformation().getLikedPosts().setArrayValue(new UserInformation.ArrayValue());
                userProfile.getUserInformation().getLikedPosts().getArrayValue().setValues(new ArrayList<>());
            }
            // checking: subscriptions
            try {
                List<UserInformation.Value> subscriptions = userProfile.getUserInformation().getSubscriptions().getArrayValue().getValues();
            } catch (NullPointerException e) {
                userProfile.getUserInformation().setSubscriptions(new UserInformation.Subscriptions());
                userProfile.getUserInformation().getSubscriptions().setArrayValue(new UserInformation.ArrayValue());
                userProfile.getUserInformation().getSubscriptions().getArrayValue().setValues(new ArrayList<>());
            }
            // checking: sentrequests
            try {
                List<UserInformation.Value> sentrequests = userProfile.getUserInformation().getSentrequests().getArrayValue().getValues();
            } catch (NullPointerException e) {
                userProfile.getUserInformation().setSentrequests(new UserInformation.Sentrequests());
                userProfile.getUserInformation().getSentrequests().setArrayValue(new UserInformation.ArrayValue());
                userProfile.getUserInformation().getSentrequests().getArrayValue().setValues(new ArrayList<>());
            }
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(userProfile);
            System.out.println(json);
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
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Profiil salvestatud");
            }
        } catch (IOException e){
            System.out.println("Ei saanud andmeid salvestada.");
        }

    }
    private String checkUniqueUsername() throws IOException {
        String dbLink = "https://firestore.googleapis.com/v1/projects/obje-8d9a1/databases/(default)/documents/users";
        HttpURLConnection connection = (HttpURLConnection) new URL(dbLink).openConnection();
        connection.setRequestMethod("GET");
        InputStream inputStream = connection.getInputStream();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            ObjectMapper mapper = new ObjectMapper();
            Users response = mapper.readValue(reader, Users.class);
            Set<String> existingNames = new HashSet<>();
            for (UserProfile user : response.getUsers()){
                try {
                    existingNames.add(user.getUserInformation().getUsername().getStringValue());
                } catch (NullPointerException e){}
            }
//                    Arrays.stream(response.getUsers()).map(UserProfile::getUserInformation).map(UserInformation::getUsername).map(UserInformation.Username::getStringValue).map(String::toLowerCase).collect(Collectors.toSet());
            System.out.println("Sisestage kasutajanimi: ");
            String username = scanner.nextLine();
            while (existingNames.contains(username.toLowerCase())) {
                System.out.println("Kahjuks ei olnud kasutajanimi unikaalne.\nSisestage muu kasutaja nimi: ");
                username = scanner.nextLine();
            }
            return username;
        } catch (IOException e) {
            return "";
        }
    }
}


