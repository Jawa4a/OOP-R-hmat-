package app;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class Activity {
    HttpURLConnection connection;
    int algus;
    int lopp;
    Post[] posts;

    public Activity() throws IOException {
        this.connection = new ConnectToCloud().connectToDatabase();
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
    public void writePost(String message) {
    }

    public void readPost() throws ExecutionException, InterruptedException {
    }
}
