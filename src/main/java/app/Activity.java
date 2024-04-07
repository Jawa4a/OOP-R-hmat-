package app;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
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

    public Activity() throws IOException {
        this.algus = 0;
        this.lopp = 1;
    }
    public void CommandHandler(Activity activity) throws IOException {
        Scanner scanner = new Scanner(System.in);
        Map<String, Command> commands = new HashMap<>();

        // Register your commands here
        commands.put("help", new HelpCommand());
        commands.put("next", new NextCommand(activity));
        commands.put("prev", new PrevCommand(activity));
        commands.put("like", new LikeCommand(activity));
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
        HttpURLConnection connection = new ConnectToCloud().connectToDatabase();;
        connection.setRequestMethod("GET");
        InputStream inputStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        ObjectMapper mapper = new ObjectMapper();

        Response response = mapper.readValue(reader, Response.class);
        this.posts = response.getPosts();
        Arrays.sort(posts, (Post p1, Post p2) -> p1.getFields().getTime().compareTo(p2.getFields().getTime()));

    }
    public void showPosts(int nihe) throws IOException {
        algus += nihe;
        lopp += nihe;
        for (int i = algus; i < lopp; i++) {
            Post post = posts[i];
            System.out.println("\n" + post.getFields().toString());
        }
    }
    public void writePost() throws IOException {
        System.out.println("Adding a new post");
        System.out.println("Post content: ");
        Scanner scanner = new Scanner(System.in);
        String postContent = scanner.nextLine();

        HttpURLConnection connection = new ConnectToCloud().connectToDatabase();;
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");

            String requestBody = "{ \"fields\": {\"author\": { \"stringValue\": \"Ajutine Hardcoded Kasutaja\" },  \"likes\": { \"integerValue\": \"0\" },\"time\": { \"timestampValue\": \"2024-04-07T12:15:05.735Z\" }, \"content\": { \"stringValue\": \"" + postContent + ".\",  } } }";

        try (OutputStream outputStream = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
            outputStream.write(input, 0, input.length);
        }

        // Vajadusel debugimiseks response

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null){
            response.append(line);
        }

        System.out.println(response);

    }

    public void readPost() throws ExecutionException, InterruptedException {
    }
}
