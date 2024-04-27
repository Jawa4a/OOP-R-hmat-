package app;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class UserInformation {
    @JsonProperty("bio")
    private Bio bio;

    @JsonProperty("likedPosts")
    private LikedPosts likedPosts;

    @JsonProperty("subscriptions")
    private Subscriptions subscriptions;


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

    public Subscriptions getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Subscriptions sub) {
        this.subscriptions = sub;
    }

    // toString method to display object as string
    @Override
    public String toString() {
        return "UserInformation{" +
                "bio=" + bio +
                ", likedPosts=" + likedPosts +
                ", subscriptions=" + subscriptions +
                '}';
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

    static class Subscriptions {
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

        public boolean checkInList(String str) {
            for (Value v : values) {
                if (str.equals(v.stringValue)) {
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
