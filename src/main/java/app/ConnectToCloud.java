package app;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ConnectToCloud {
    private List<Post> posts;
    private Firestore db;
    public void connectToDatabase() throws IOException {
        // Loogika serviceAccount abil ühendamiseks
        // Tõenäoliselt seda ei kasuta, aga praegu las jääda sisse
        /*
        InputStream serviceAccount = new FileInputStream("src/main/resources/key.json");
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .setProjectId(this.projectID)
                .build();
        FirebaseApp.initializeApp(options);
        this.db = FirestoreClient.getFirestore();
        */
        String projectId = "obje-8d9a1";
        String apiFetchUrl = "https://firestore.googleapis.com/v1/projects/" + projectId + "/databases/(default)/documents/posts";
        HttpURLConnection connection = (HttpURLConnection) new URL(apiFetchUrl).openConnection();
        connection.setRequestMethod("GET");

        try {
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            System.out.println(response);

        } catch (Exception e){
            System.out.println("Error fetching posts");
        }






    }
    public void writePost(String title, String message, String author) throws ExecutionException, InterruptedException {
        DocumentReference docRef = this.db.collection("posts").document(title);
        Map<String, Object> data = new HashMap<>();
        data.put("author", author);
        data.put("title", title);
        data.put("message", message);
        data.put("date", Timestamp.now());
        ApiFuture<WriteResult> result = docRef.set(data);
    }
    public void readPost() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> query = this.db.collection("posts").get();
        QuerySnapshot querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        if (!documents.isEmpty())
        {
            for (QueryDocumentSnapshot document: documents){
                if (document.contains("message")) {
                    System.out.println(document.get("author"));
                    System.out.println(document.get("title"));
                    System.out.println(document.get("message"));
                }
            }
        }
    }
}
