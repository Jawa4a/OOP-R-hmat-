package app;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Response {
    @JsonProperty("documents")
    private Post[] posts;

    public Post[] getPosts() {
        return posts;
    }
}