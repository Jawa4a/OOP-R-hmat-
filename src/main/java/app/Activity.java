package app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class Activity {

    private final LoginSignupResponse userInfo;
    private final String projectID;
    private final String baseLink;
    public int algus;
    public int lopp;
    public Post[] posts;
    private String autor;
    private UserProfile userProfile;

    public Activity(LoginSignupResponse userInfo) throws IOException {
        this.algus = 0;
        this.lopp = 1;
        this.projectID = "obje-8d9a1";
        this.userInfo = userInfo;
        this.userProfile = getUserProfileData(userInfo.email);
        this.autor = userInfo.getDisplayName();
        this.baseLink = "https://firestore.googleapis.com/v1/projects/" + this.projectID + "/databases/(default)/documents/";
    }

    public static boolean contains(char c, char[] s) {
        for (Character character : s) {
            if (character == c) {
                return true;
            }
        }
        return false;
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
        commands.put("comment", new CommentCommand(activity, userInfo));
        commands.put("commentshow", new ShowCommentsCommand(activity));
        commands.put("top", new Top(activity));
        commands.put("reloadposts", new Reload(activity));
        commands.put("addfriend", new AddFriend(activity));
        commands.put("friends", new ShowFriends(activity));
        commands.put("wordle", new Wordle(activity));

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
            if (i >= posts.length) {
                System.out.println("No more posts to show. Use  \"top\" to return to first post.");
                return;
            }
            Post post = posts[i];
            System.out.println("");
            System.out.println("\n" + post.getFields().toString());
        }
    }

    public int getPostNumber() {
        return algus;
    }

    public void likePost(int postNumber, LoginSignupResponse userInfo) throws IOException {
        Post curPost = posts[postNumber];
        String postPath = curPost.getName();
        curPost.addLike();
        ObjectMapper mapper = new ObjectMapper();
        String currentPostFields = mapper.writeValueAsString(curPost.getFields());
        String requestBody = "{\"fields\": " + currentPostFields + " }";
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
            if (!userDbEntry.getFields().getLikedPosts().getArrayValue().checkIfPostIsLiked(curPost.getName())) {
                System.out.println("Post liked");
                addLikedPost(connection, userDbEntry, curPost.getName());
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
            ObjectMapper mapper = new ObjectMapper();
            String currentUserFields = mapper.writeValueAsString(curUser.getFields());
            String requestBody = "{\"fields\": " + currentUserFields + " }";
            HttpURLConnection connection = (HttpURLConnection) new URL("https://firestore.googleapis.com/v1/" + curUser.getName()).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            os.write(requestBody.getBytes());
            os.flush();
            os.close();
            int res = connection.getResponseCode();


        } catch (Exception e) {
            System.out.println(e);
            System.out.println("problem adding liked post");
        }
    }

    public void writePost() throws IOException {
        String nickname = getUserNickname();
        if (nickname.isEmpty()) {
            System.out.println("Teil pole kasutajanime, sisselogimisel sisestage 3, et parandada konto andmed.");
            return;
        }
        System.out.println("Adding a new post");
        System.out.println("Post content: ");
        Scanner scanner = new Scanner(System.in);
        String postContent = scanner.nextLine();
        HttpURLConnection connection = new ConnectToCloud().connectToDatabase("posts");
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");

        String requestBody = "{ \"fields\": {\"author\": { \"stringValue\": \"" + nickname + "\" }, \"email\": { \"stringValue\": \"" + this.userInfo.email + "\" },  \"likes\": { \"integerValue\": \"0\" },\"time\": { \"timestampValue\": \"2024-04-07T12:15:05.735Z\" }, \"content\": { \"stringValue\": \"" + postContent + "\", }, \"comments\": {\"arrayValue\":{},  } } }";

        try (OutputStream outputStream = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
            outputStream.write(input, 0, input.length);
        }

        /*
        // Debugging response
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        */
    }

    public UserDbEntry getUserData(String userEmail) throws IOException {
        HttpURLConnection connection = new ConnectToCloud().connectToDatabaseDocument("users", userEmail);

        connection.setRequestMethod("GET");
        InputStream inputStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(reader, UserDbEntry.class);
    }

    public void subscribeuser(int postId) throws IOException {

        Post curPost = posts[postId - 1];
        String urlString = "https://firestore.googleapis.com/v1/projects/" + projectID + "/databases/(default)/documents/users/" + userInfo.email;
        String sub = curPost.getFields().getEmail().getStringValue();
        UserInformation.Value newSubscription = new UserInformation.Value();
        newSubscription.setStringValue(sub);

        if (userProfile.getUserInformation().getSubscriptions().getArrayValue().getValues() == null || !userProfile.getUserInformation().getSubscriptions().getArrayValue().checkInList(sub)) {
            List<UserInformation.Value> existingValues = userProfile.getUserInformation().getSubscriptions().getArrayValue().getValues();
            if (existingValues == null) {
                existingValues = new ArrayList<>();
            }
            existingValues.add(newSubscription);
            userProfile.getUserInformation().getSubscriptions().getArrayValue().setValues(existingValues);
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
            userProfile = getUserProfileData(userInfo.email);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Kontrollime kas on olemas
                if (!userProfile.getUserInformation().getLikedPosts().getArrayValue().checkInList(newSubscription.getStringValue())) {
                    System.out.println("Subscribed");
                } else {
                    System.out.println("Viga");
                }
            } else {
                System.out.println("Cannot subscribe: " + responseCode);
            }
            connection.disconnect();
        } else {
            System.out.println("You have already subscribed this user");
        }
    }

    public UserProfile getUserProfileData(String userEmail) throws IOException {
        HttpURLConnection connection = new ConnectToCloud().connectToDatabaseDocument("users", userEmail);
        connection.setRequestMethod("GET");
        InputStream inputStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(reader, UserProfile.class);
    }

    public void showFollowed() {
        List<UserInformation.Value> existingValues = userProfile.getUserInformation().getSubscriptions().getArrayValue().getValues();
        Set<String> setOfValues = new HashSet<>(existingValues.stream().map(UserInformation.Value::getStringValue).collect(Collectors.toSet()));
        for (int j = 0; j < posts.length; j++) {
            Post post = posts[j];
            String postEmail = post.getFields().getEmail().getStringValue();
            if (!postEmail.isEmpty()) {
                if (setOfValues.contains(postEmail)) {
                    System.out.println();
                    System.out.println("\n" + post.getFields().toString());
                }
            }
        }
    }

    public void showAboutUser(String userName) throws IOException {
        String userEmail = "";
        for (Post post : posts) {
            if (userName.equals(post.getFields().getAuthor().getStringValue())) {
                userEmail = post.getFields().getEmail().getStringValue();
                UserProfile searchedUser = getUserProfileData(userEmail);
                System.out.println("About " + userName + ":");
                System.out.println(searchedUser.getUserInformation().getBio().getStringValue());
                return;
            }
        }
    }

    public void showComments(int postNumber) {
        Post post = posts[postNumber];
        List<Fields.ArrayValue.Value> comments = post.getFields().getComments().getArrayValue().getValues();
        if (comments == null || comments.isEmpty()) {
            System.out.println("Puuduvad komentaarid");
        } else {
            System.out.println("Comments:");
            for (Fields.ArrayValue.Value comment : comments) {
                String[] commentString = comment.getStringValue().split("×");
                System.out.println("---------------------------");
                System.out.println(commentString[0]);
                System.out.println(commentString[2]);
                System.out.println();
                System.out.println(commentString[1]);
                System.out.println();
                System.out.println("---------------------------");
            }
        }
    }

    public void commentOnPost(int postNumber, String comment, LoginSignupResponse userInfo) throws IOException {

        Post post = posts[postNumber];
        String nickname = getUserNickname();
        if (nickname.isEmpty()) {
            System.out.println("Teil pole kasutajanime, sisselogimisel sisestage 3, et parandada konto andmed.");
            return;
        }
        post.addComment(nickname, comment);
        System.out.println("Kommentaar on lisatud");
    }

    public void top() throws IOException {
        algus = 0;
        lopp = 1;
        showPosts(0);
    }

    public String getUserNickname() {
        try {
            return userProfile.getUserInformation().getUsername().getStringValue();
        } catch (NullPointerException e) {
            return "";
        }
    }

    public void sendfriendrequest(String reciever) {
        try {
            this.userProfile = getUserProfileData(userInfo.email);
        } catch (IOException e) {
            System.out.println("Ei suutnud teie andmeid uuendada.");
            return;
        }
        UserInformation.Value recieverValue = new UserInformation.Value();
        recieverValue.setStringValue(reciever);
        List<UserInformation.Value> currentFriends;
        try {
            currentFriends = userProfile.getUserInformation().getFriends().getArrayValue().getValues();
        } catch (NullPointerException e) {
            currentFriends = new ArrayList<>();
        }

        if (currentFriends != null && isinlist(currentFriends, reciever)) {
            System.out.println("Kasutaja on juba teie sõber!");
            return;
        }

        String recieverProfile = findUser(reciever);
        UserProfile recProfile;
        if (recieverProfile.isEmpty()) {
            System.out.println("Kasutajat ei leitud;");
            return;
        } else {
            try {
                ObjectMapper mapper = new ObjectMapper();
                recProfile = mapper.readValue(recieverProfile, UserProfile.class);
            } catch (JsonProcessingException e) {
                System.out.println("Ei saanud kasutaja andmeid kätte.");
                return;
            }
        }

        UserInformation.Value currentNickname = new UserInformation.Value();
        currentNickname.setStringValue(userProfile.getUserInformation().getUsername().getStringValue());

        List<UserInformation.Value> currentFriendRequests;
        List<UserInformation.Value> currentSentRequests;
        try {
            currentSentRequests = userProfile.getUserInformation().getSentrequests().getArrayValue().getValues();
        } catch (NullPointerException e) {
            currentSentRequests = new ArrayList<>();
        }
        if (currentSentRequests != null && isinlist(currentSentRequests, reciever)) {
            System.out.println("Olete juba saatnud sõbrakutse kasutajale " + reciever);
            return;
        }

        List<UserInformation.Value> recieverFriends;
        List<UserInformation.Value> recieverFriendRequests;
        List<UserInformation.Value> recieverSentRequests;

        try {
            currentFriendRequests = userProfile.getUserInformation().getFriendrequests().getArrayValue().getValues();
        } catch (NullPointerException e) {
            currentFriendRequests = new ArrayList<>();
        }

        try {
            recieverFriends = recProfile.getUserInformation().getFriends().getArrayValue().getValues();
        } catch (NullPointerException e) {
            recieverFriends = new ArrayList<>();
        }
        try {
            recieverSentRequests = recProfile.getUserInformation().getSentrequests().getArrayValue().getValues();
        } catch (NullPointerException e) {
            recieverSentRequests = new ArrayList<>();
        }
        try {
            recieverFriendRequests = recProfile.getUserInformation().getFriendrequests().getArrayValue().getValues();
        } catch (NullPointerException e) {
            recieverFriendRequests = new ArrayList<>();
        }

        if (currentFriendRequests != null && isinlist(currentFriendRequests, reciever)) {
            System.out.println("2");
            int indexInCurrent = indexinlist(currentFriendRequests, reciever);
            currentFriendRequests.remove(indexInCurrent);
            int indexInReciever = indexinlist(recieverSentRequests, currentNickname.getStringValue());
            recieverSentRequests.remove(indexInReciever);
            try {
                currentFriends.add(recieverValue);
            } catch (NullPointerException e) {
                currentFriends = Arrays.asList(recieverValue);
            }
            try {
                recieverFriends.add(currentNickname);
            } catch (NullPointerException e) {
                recieverFriends = Arrays.asList(currentNickname);
            }
            System.out.println("Nüüd on " + reciever + " teie sõber.");
        } else {
            try {
                currentSentRequests.add(recieverValue);
            } catch (NullPointerException e) {
                currentSentRequests = Arrays.asList(recieverValue);
            }
            try {
                recieverFriendRequests.add(currentNickname);
            } catch (NullPointerException e) {
                recieverFriendRequests = Arrays.asList(currentNickname);
            }
        }

        userProfile.getUserInformation().getFriends().getArrayValue().setValues(currentFriends);
        userProfile.getUserInformation().getFriendrequests().getArrayValue().setValues(currentFriendRequests);
        userProfile.getUserInformation().getSentrequests().getArrayValue().setValues(currentSentRequests);

        recProfile.getUserInformation().getFriends().getArrayValue().setValues(recieverFriends);
        recProfile.getUserInformation().getFriendrequests().getArrayValue().setValues(recieverFriendRequests);
        recProfile.getUserInformation().getSentrequests().getArrayValue().setValues(recieverSentRequests);

        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(userProfile);
//            System.out.println(json);
            String userDatabase = baseLink + "users/" + userInfo.email;
            writetodatabase(json, userDatabase);
        } catch (JsonProcessingException e) {
            System.out.println("Ei saanud teie andmeid salvestada");
            return;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(recProfile);
//            System.out.println(json);
            String[] recieverLinkdata = recProfile.getName().split("/");
            String recieverDatabase = baseLink + "users/" + recieverLinkdata[recieverLinkdata.length - 1];
            writetodatabase(json, recieverDatabase);
        } catch (JsonProcessingException e) {
            System.out.println("Ei saanud " + reciever + " andmeid salvestada.");
        }
    }

    public void showfriends() {
        try {
            for (UserInformation.Value friendNickname : userProfile.getUserInformation().getFriends().getArrayValue().getValues()) {
                System.out.println(friendNickname.getStringValue());
            }
        } catch (NullPointerException e) {
            System.out.println("Teil ei ole sõpru(");
        }
    }

    public String findUser(String userNickname) {
        String allUsers = readfromdatabase(baseLink + "users");
        try {
            ObjectMapper mapper = new ObjectMapper();
            Users users = mapper.readValue(allUsers, Users.class);
            for (UserProfile userProf : users.getUsers()) {
                if (userProf.getUserInformation().getUsername().getStringValue().equals(userNickname)) {
                    return mapper.writeValueAsString(userProf);
                }
            }
            return "";
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    private void writetodatabase(String text, String urlString) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            os.write(text.getBytes());
            os.flush();
            os.close();
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                //System.out.println("Andmed salvestatud.");
                throw new IOException("Server ei anna õiget tagasisidet.");
            }
            connection.disconnect();
        } catch (IOException e) {
            System.out.println("Ei saanud andmeid salvestada.");
        }
    }

    private String readfromdatabase(String urlString) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
            connection.setRequestMethod("GET");
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
                content.append(System.lineSeparator());
            }
            connection.disconnect();
            return content.toString();
        } catch (IOException e) {
            return "";
        }
    }

    private boolean isinlist(List<UserInformation.Value> list, String value) {
        for (UserInformation.Value item : list) {
            if (item.getStringValue().equals(value)) {
                return true;
            }
        }
        return false;
    }

    private int indexinlist(List<UserInformation.Value> list, String value) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getStringValue().equals(value)) {
                return i;
            }
        }
        return -1;
    }

    private String getWordleWord() throws IOException {
        HttpURLConnection connection = new ConnectToCloud().connectToDatabaseDocument("wordle", "curWord");
        connection.setRequestMethod("GET");
        InputStream inputStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        ObjectMapper mapper = new ObjectMapper();
        WordleInfo wordleInfo = mapper.readValue(reader, WordleInfo.class);
        if(LocalDateTime.parse(wordleInfo.getUpdateTime().substring(0,21)).toLocalDate().isBefore(LocalDateTime.now().toLocalDate())){
            updateWordleWord(wordleInfo);
        }
        return wordleInfo.getFields().getWord().getStringValue();
    }

    private LeaderboardDb getWordleLeaderboard() throws IOException {
        HttpURLConnection connection = new ConnectToCloud().connectToDatabaseDocument("wordle", "leaderboard");
        connection.setRequestMethod("GET");
        InputStream inputStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(reader, LeaderboardDb.class);
    }

    private void updateLeaderboardDb(LeaderboardDb leaderboard) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String currentUserFields = mapper.writeValueAsString(leaderboard.getFields());
            String requestBody = "{\"fields\": " + currentUserFields + " }";
            HttpURLConnection connection = (HttpURLConnection) new URL("https://firestore.googleapis.com/v1/" + leaderboard.getName()).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            os.write(requestBody.getBytes());
            os.flush();
            os.close();
            int res = connection.getResponseCode();


        } catch (Exception e) {
            System.out.println(e);
            System.out.println("problem adding you to leaderboard");
        }

    }

    private void updateWordleWord(WordleInfo wordleInfo) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Random random = new Random();
            String[] commonFiveLetterWords = {
                    "apple", "bread", "chair", "dance", "eagle",
                    "faith", "globe", "house", "image", "jelly",
                    "knife", "lemon", "mouse", "nurse", "ocean",
                    "paint", "queen", "river", "stone", "table",
                    "uncle", "vivid", "wheat", "xenon", "youth",
                    "zebra", "brave", "cloud", "drink", "earth",
                    "flame", "grape", "heart", "inbox", "joker",
                    "kneel", "light", "magic", "north", "organ",
                    "peace", "quick", "rider", "smile", "torch",
                    "unify", "voice", "whale", "x-ray", "yield"
            };
            wordleInfo.getFields().getWord().setStringValue(commonFiveLetterWords[random.nextInt(commonFiveLetterWords.length)]);
            String wordleFields = mapper.writeValueAsString(wordleInfo.getFields());
            String requestBody = "{\"fields\": " + wordleFields + " }";
            HttpURLConnection connection = (HttpURLConnection) new URL("https://firestore.googleapis.com/v1/" + wordleInfo.getName()).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            os.write(requestBody.getBytes());
            os.flush();
            os.close();
            int res = connection.getResponseCode();
            System.out.println(res);


        } catch (Exception e) {
            System.out.println(e);
            System.out.println("problem adding you to leaderboard");
        }
    }


    public void wordle() throws IOException {
        String ANSI_RESET = "\u001B[0m";
        String ANSI_GREEN_BACKGROUND = "\u001B[42m";
        String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
        String ANSI_RED_BACKGROUND = "\u001B[41m";
        Scanner scanner = new Scanner(System.in);

        System.out.println("Kas näete ekraanil kolme värvi (jah/ei):");

        System.out.print(ANSI_GREEN_BACKGROUND + " ROHELINE " + ANSI_RESET);
        System.out.print(ANSI_YELLOW_BACKGROUND + " KOLLANE " + ANSI_RESET);
        System.out.print(ANSI_RED_BACKGROUND + " PUNANE " + ANSI_RESET);
        System.out.println();
        String jahei = scanner.nextLine();
        if(!jahei.equals("jah")){
            System.out.println("Kahjuks kasutate konsooli, mis ei toeta värve, ehk mängida ei ole võimalik");
            return;
        }



        String answer = getWordleWord();
        char[] vastus = answer.toUpperCase().toCharArray();

        String seni = "";
        String rida = "";
        int tulemus = -1;

        System.out.println("Wordle on nuputamismäng, kus tuleb ära arvata ette määratud sõna. Selleks tuleb teha pakkumine ");
        System.out.println("ja ekraanile ilmuvad värvid näitavad, millised tähed on õiges kohas (roheline), millised on vale koha peal (kollane) ja milliseid sõnas üldse ei ole (punane).");
        System.out.println("Kokku on 5 katset ja arvatav sõna vahetub iga päev.");
        LeaderboardDb leaderboard = getWordleLeaderboard();

        for (int j = 0; j < 5; j++) {
            System.out.println("Pakkumine " + (j + 1) + ":");
            String pakkumine = scanner.nextLine().toUpperCase();
            if (pakkumine.length() > 5) {
                System.out.println("Sõna liiga pikk!");
                j--;
            } else if (pakkumine.length() < 5) {
                System.out.println("Sõna liiga lühike!");
                j--;
            } else {
                for (int i = 0; i < pakkumine.length(); i++) {
                    char taht = pakkumine.toUpperCase().toCharArray()[i];
                    if (taht == vastus[i]) {
                        rida = ANSI_GREEN_BACKGROUND + " " + taht + " " + ANSI_RESET;
                    } else if (contains(taht, vastus)) {
                        rida = ANSI_YELLOW_BACKGROUND + " " + taht + " " + ANSI_RESET;
                    } else {
                        rida = ANSI_RED_BACKGROUND + " " + taht + " " + ANSI_RESET;
                    }
                    seni += rida;
                }
                seni += "\n";
                System.out.println(seni);
                if (pakkumine.equalsIgnoreCase(answer)) {
                    tulemus = j;
                    break;
                }
            }
            System.out.println();
        }

        String kasutajaTulemus = (getUserNickname() + "                      ").substring(0, 22) + " " + (tulemus == -1 ? " " : tulemus + 1);
        leaderboard.getFields().getLeaderboard().getArrayValue().addLeaderboardEntry(kasutajaTulemus);
        System.out.println("Sõna oli " + answer.toUpperCase() + "!");
        System.out.println("Vaata oma tulemust edetabelist:");
        System.out.println();
        System.out.println(leaderboard);


    }
}
