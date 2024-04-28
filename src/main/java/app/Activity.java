package app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


public class Activity {

    public int algus;
    public int lopp;
    public Post[] posts;
    private String autor;
    private LoginSignupResponse userInfo;
    private String projectID;
    private UserProfile userProfile;


    public Activity(LoginSignupResponse userInfo) throws IOException {
        this.algus = 0;
        this.lopp = 1;
        this.projectID = "obje-8d9a1";
        this.userInfo = userInfo;
        this.userProfile = getUserProfileData(userInfo.email);
    }

    public void setautor(String autor) {
        this.autor = autor;
    }

    public String getautor() {
        return this.autor;
    }

    public void CommandHandler(Activity activity, LoginSignupResponse userInfo) throws IOException {
//        this.userInfo=userInfo;
        Scanner scanner = new Scanner(System.in);
        Map<String, Command> commands = new HashMap<>();

        // Register your commands here
        commands.put("help", new HelpCommand());
        commands.put("next", new NextCommand(activity));
        commands.put("prev", new PrevCommand(activity));
        commands.put("like", new LikeCommand(activity, userInfo));
        commands.put("post", new PostCommand(activity));
        commands.put("subscribe", new SubscribeCommand(activity));
        commands.put("followed", new ShowFollowedCommand(activity));
        commands.put("whois", new WhoIs(activity));

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
                addLikedPost(connection, userDbEntry ,curPost.getName());
            } else {
                System.out.println("You have already liked this post");
            }
        } else {
            System.out.println("Post not liked: " + responseCode);
        }

        connection.disconnect();

    }


    public void addLikedPost(HttpURLConnection connection2, UserDbEntry curUser, String postaddress) {
        try {
            curUser.addLikedPost(postaddress);
            ObjectMapper objectMapper = new ObjectMapper();
            String currentUserFields = objectMapper.writeValueAsString(curUser.getFields());
            String requestBody = "{\"fields\": " + currentUserFields + " }";

            System.out.println(curUser.getName());
            HttpURLConnection connection = new ConnectToCloud().connectToDatabaseDocument("users", curUser.getName());
            connection.setRequestMethod("POST");
            connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            os.write(requestBody.getBytes());
            os.flush();
            os.close();
            System.out.println(connection.getResponseCode());
            System.out.println(connection.getResponseMessage());

        } catch (Exception e){
            System.out.println(e);
            System.out.println("problem adding liked post");
        }
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

        String requestBody = "{ \"fields\": {\"author\": { \"stringValue\": \"" + autor + "\" }, \"email\": { \"stringValue\": \"" + this.userInfo.email + "\" },  \"likes\": { \"integerValue\": \"0\" },\"time\": { \"timestampValue\": \"2024-04-07T12:15:05.735Z\" }, \"content\": { \"stringValue\": \"" + postContent + "\",  } } }";

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

    public void subscribeuser(int postId) throws IOException {

        Post curPost = posts[postId-1];
        String urlString = "https://firestore.googleapis.com/v1/projects/" + projectID + "/databases/(default)/documents/users/"+ userInfo.email;
        String sub = curPost.getFields().getEmail().getStringValue();
        UserInformation.Value newSubscription = new UserInformation.Value();
        newSubscription.setStringValue(sub);

        if (userProfile.getUserInformation().getSubscriptions().getArrayValue().getValues()==null||!userProfile.getUserInformation().getSubscriptions().getArrayValue().checkInList(sub)) {
            ObjectMapper mapper = new ObjectMapper();
            List<UserInformation.Value> existingValues = userProfile.getUserInformation().getSubscriptions().getArrayValue().getValues();
            if (existingValues == null){
                existingValues = new ArrayList<>();
            }
            existingValues.add(newSubscription);
            userProfile.getUserInformation().getSubscriptions().getArrayValue().setValues(existingValues);
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
            userProfile = getUserProfileData(userInfo.email);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Kontrollime kas on olemas
                if(!userProfile.getUserInformation().getLikedPosts().getArrayValue().checkInList(newSubscription.getStringValue())){
                    System.out.println("Subscribed");
                } else {
                    System.out.println("Viga");
                }
            } else {
                System.out.println("Cannot subscribe: " + responseCode);
            }
            connection.disconnect();
        } else{
            System.out.println("You have already subscribed this user");
        }
    }

    public UserProfile getUserProfileData(String userEmail) throws IOException {
        HttpURLConnection connection = new ConnectToCloud().connectToDatabaseDocument("users", userEmail);

        connection.setRequestMethod("GET");
        InputStream inputStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        ObjectMapper mapper = new ObjectMapper();

        UserProfile response = mapper.readValue(reader, UserProfile.class);

        return response;
    }

    public void showFollowed() throws IOException {
        int counter = 0;
        List<UserInformation.Value> existingValues = userProfile.getUserInformation().getSubscriptions().getArrayValue().getValues();
        Set<String> setOfValues = new HashSet<>(existingValues.stream().map(UserInformation.Value::getStringValue).collect(Collectors.toSet()));
        for (int j = 0; j < posts.length; j++) {
            Post post = posts[j];
            String postEmail = post.getFields().getEmail().getStringValue();
            if (!postEmail.isEmpty()) {
                if (setOfValues.contains(postEmail)) {
                    System.out.println("");
                    System.out.println("\n" + post.getFields().toString());
                    counter++;
                }
            }
        }
    }

    public void showAboutUser(String userName) throws IOException {
        String userEmail = "";
        for (Post post: posts){
            if (userName.equals(post.getFields().getAuthor().getStringValue())){
                userEmail = post.getFields().getEmail().getStringValue();
                UserProfile searchedUser = getUserProfileData(userEmail);
                System.out.println("About " + userName + ":");
                System.out.println(searchedUser.getUserInformation().getBio().getStringValue());
                return;
            }
        }
    }


}