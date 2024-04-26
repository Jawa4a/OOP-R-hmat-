package app;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class UserDbEntryFields {
    @JsonProperty("bio")
    private Bio bio;

    @JsonProperty("likedPosts")
    private LikedPosts likedPosts;


    // Getters and Setters
    public Bio getBio() {
        return bio;
    }

    public void setBio(Bio bio) {
        this.bio = bio;
    }

    public LikedPosts getLikedPosts() {
        return likedPosts;
    }

    public void setLikedPosts(LikedPosts likedPosts) {
        this.likedPosts = likedPosts;
    }

    // toString method to display object as string
    @Override
    public String toString() {
        return "Fields{" + "bio='" + bio + '\'' + ", likedPosts=" + likedPosts + '}';
    }

    static class Bio {
        @JsonProperty("stringValue")
        private String stringValue;

        public String getStringValue() {
            return stringValue;
        }
    }

    static class LikedPosts {
        private ArrayValue arrayValue;

        @JsonProperty("arrayValue")
        public ArrayValue getArrayValue() {
            return arrayValue;
        }

        @JsonProperty("arrayValue")
        public void setArrayValue(ArrayValue arrayValue) {
            this.arrayValue = arrayValue;
        }
    }

    static class ArrayValue {
        private List<Value> values;

        public boolean checkIfPostIsLiked(String postName) {

            for (Value v : values) {
                if (postName.equals(v.stringValue)) {
                    return true;
                }
            }
            return false;
        }

        @JsonProperty("values")
        public List<Value> getValues() {
            return values;
        }

        @JsonProperty("values")
        public void setValues(List<Value> values) {
            this.values = values;
        }

    }

    static class Value {
        private String stringValue;

        @JsonProperty("stringValue")
        public String getStringValue() {
            return stringValue;
        }

        @JsonProperty("stringValue")
        public void setStringValue(String stringValue) {
            this.stringValue = stringValue;
        }
    }
}
