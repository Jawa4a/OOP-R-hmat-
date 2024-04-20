package app;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


public class LikePostConnection {

    public HttpURLConnection connectToDatabase(String postUrl) throws IOException {
        return (HttpURLConnection) new URL("https://firestore.googleapis.com/v1/" + postUrl).openConnection();
    }
}
