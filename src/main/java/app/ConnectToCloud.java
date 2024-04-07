package app;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.Firestore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ConnectToCloud {
    private Firestore db;

    public void connectToDatabase() throws IOException {

        String projectId = "obje-8d9a1";
        String apiFetchUrl = "https://firestore.googleapis.com/v1/projects/" + projectId + "/databases/(default)/documents/posts";

        HttpURLConnection connection = (HttpURLConnection) new URL(apiFetchUrl).openConnection();
        connection.setRequestMethod("GET");


        InputStream inputStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        ObjectMapper mapper = new ObjectMapper();

        Response response = mapper.readValue(reader, Response.class);

        for(Post post: response.getPosts()){
            System.out.println(post);
        }

    }

    public void writePost(String title, String message, String author) {
    }

    public void readPost() throws ExecutionException, InterruptedException {
    }








}
