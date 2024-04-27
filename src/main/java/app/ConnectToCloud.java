package app;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectToCloud {

    public HttpURLConnection connectToDatabase(String collection) throws IOException {

        String projectId = "obje-8d9a1";
        String apiFetchUrl = "https://firestore.googleapis.com/v1/projects/" + projectId + "/databases/(default)/documents/" + collection;

        return (HttpURLConnection) new URL(apiFetchUrl).openConnection();
    }


    public HttpURLConnection connectToDatabaseDocument(String collection, String document) throws IOException {

        String projectId = "obje-8d9a1";
        String apiFetchUrl = "https://firestore.googleapis.com/v1/projects/" + projectId + "/databases/(default)/documents/" + collection + "/" + document;

        return (HttpURLConnection) new URL(apiFetchUrl).openConnection();
    }

}
