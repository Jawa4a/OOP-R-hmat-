package app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.IOUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;


public class Activity {

    int algus;
    int lopp;
    Post[] posts;

    private String autor;


    public Activity() throws IOException {
        this.algus = 0;
        this.lopp = 1;
    }

    public void setautor(String autor) {
        this.autor = autor;
    }

    public String getautor() {
        return this.autor;
    }

    public void CommandHandler(Activity activity, LoginSignupResponse userInfo) throws IOException {
        Scanner scanner = new Scanner(System.in);
        Map<String, Command> commands = new HashMap<>();

        // Register your commands here
        commands.put("help", new HelpCommand());
        commands.put("next", new NextCommand(activity));
        commands.put("prev", new PrevCommand(activity));
        commands.put("like", new LikeCommand(activity, userInfo));
        commands.put("post", new PostCommand(activity));


        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            String[] inputParts = input.split(" ");

            String commandName = inputParts[0];

            Command command = commands.get(commandName);

            if (command != null) {
                command.execute(inputParts);
            } else {
                System.out.println("Unknown command");
            }
        }
    }

    public void loadPosts() throws IOException {
        HttpURLConnection connection = new ConnectToCloud().connectToDatabase("posts");
        connection.setRequestMethod("GET");
        InputStream inputStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        ObjectMapper mapper = new ObjectMapper();

        Response response = mapper.readValue(reader, Response.class);
        this.posts = response.getPosts();
        Arrays.sort(posts, (Post p1, Post p2) -> p2.getCreateTime().compareTo(p1.getCreateTime()));

    }

    public void showPosts(int nihe) throws IOException {
        algus += nihe;
        lopp += nihe;
        for (int i = algus; i < lopp; i++) {
            Post post = posts[i];
            System.out.println("");
            System.out.println("\n" + post.getFields().toString());
        }
    }

    public int getPostNumber(){
        return algus;
    }

    public void likePost(int postNumber, LoginSignupResponse userInfo) throws IOException {
        Post curPost = posts[postNumber];
        String postPath = curPost.getName();
        curPost.addLike();
        ObjectMapper objectMapper = new ObjectMapper();
        String currentPostFields = objectMapper.writeValueAsString(curPost.getFields());
        String requestBody = "{\"fields\": "+ currentPostFields +" }";
        HttpURLConnection connection = new LikePostConnection().connectToDatabase(postPath);

        connection.setRequestMethod("POST");
        connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        OutputStream os = connection.getOutputStream();
        os.write(requestBody.getBytes());
        os.flush();
        os.close();

        int responseCode = connection.getResponseCode();
        UserDbEntry userDbEntry = getUserData(userInfo.email);
        if (responseCode == HttpURLConnection.HTTP_OK) {
            if(!userDbEntry.getFields().getLikedPosts().getArrayValue().checkIfPostIsLiked(curPost.getName())){
                System.out.println("Post liked");
            } else {
                System.out.println("You have already liked this post");
            }
        } else {
            System.out.println("Post not liked: " + responseCode);
        }

        connection.disconnect();

    }



    public void writePost(String autor) throws IOException {
        System.out.println("Adding a new post");
        System.out.println("Post content: ");
        Scanner scanner = new Scanner(System.in);
        String postContent = scanner.nextLine();

        HttpURLConnection connection = new ConnectToCloud().connectToDatabase("posts");
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");

        String requestBody = "{ \"fields\": {\"author\": { \"stringValue\": \"" + autor + "\" },  \"likes\": { \"integerValue\": \"0\" },\"time\": { \"timestampValue\": \"2024-04-07T12:15:05.735Z\" }, \"content\": { \"stringValue\": \"" + postContent + "\",  } } }";

        try (OutputStream outputStream = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
            outputStream.write(input, 0, input.length);
        }

        // Debugging response
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }


    }

    public UserDbEntry getUserData(String userEmail) throws IOException {
        HttpURLConnection connection = new ConnectToCloud().connectToDatabaseDocument("users", userEmail);

        connection.setRequestMethod("GET");
        InputStream inputStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        ObjectMapper mapper = new ObjectMapper();

        UserDbEntry response = mapper.readValue(reader, UserDbEntry.class);

        return response ;
    }

    public void readPost() throws ExecutionException, InterruptedException {
    }
}
