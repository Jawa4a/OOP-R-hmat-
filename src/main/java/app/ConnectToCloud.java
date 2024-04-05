package app;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ConnectToCloud {
    private Firestore db;
    private String projectID = "socew-8601a";
    public void connectToDatabase() throws IOException {
        InputStream serviceAccount = new FileInputStream("src/main/resources/key.json");
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .setProjectId(this.projectID)
                .build();
        FirebaseApp.initializeApp(options);
        this.db = FirestoreClient.getFirestore();
    }
    public void writePost(String title, String message, String author) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection("posts").document(title);
        Map<String, Object> data = new HashMap<>();
        data.put("author", author);
        data.put("title", title);
        data.put("message", message);
        data.put("date", Timestamp.now());
        ApiFuture<WriteResult> result = docRef.set(data);
    }
    public void readPost() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> query = db.collection("posts").get();
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
