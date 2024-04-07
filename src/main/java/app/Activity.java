package app;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

public class Activity {
    HttpURLConnection connection;
    int algus;
    int lopp;

    public Activity() throws IOException {
        this.connection = new ConnectToCloud().connectToDatabase();
        this.algus = 0;
        this.lopp = 2;
    }

    public void showPosts(int nihe) throws IOException {
        connection.setRequestMethod("GET");
        InputStream inputStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        ObjectMapper mapper = new ObjectMapper();

        Response response = mapper.readValue(reader, Response.class);
        Post post;
        for (int i = algus+nihe; i < lopp+nihe; i++) {
             post = response.getPosts()[i];
            System.out.println(post);
        }
//        for(Post post: response.getPosts()){
//            System.out.println(post);
//        }
    }
    public void writePost(String title, String message, String author) {
    }

    public void readPost() throws ExecutionException, InterruptedException {
    }
}
