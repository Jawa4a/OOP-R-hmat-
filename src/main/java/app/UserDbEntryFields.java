package app;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class UserDbEntryFields {
    @JsonProperty("bio")
    private String bio;

    @JsonProperty("likedPosts")
    private Map<String, Object> likedPosts;

    @JsonProperty("comments")
    private Map<String, Object> comments;

    // Getters and Setters
    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Map<String, Object> getLikedPosts() {
        return likedPosts;
    }

    public void setLikedPosts(Map<String, Object> likedPosts) {
        this.likedPosts = likedPosts;
    }

    public Map<String, Object> getComments() {
        return comments;
    }

    public void setComments(Map<String, Object> comments) {
        this.comments = comments;
    }

    // toString method to display object as string
    @Override
    public String toString() {
        return "Fields{" +
                "bio='" + bio + '\'' +
                ", likedPosts=" + likedPosts +
                ", comments=" + comments +
                '}';
    }
}
