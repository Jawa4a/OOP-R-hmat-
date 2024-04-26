package app;

import com.fasterxml.jackson.annotation.JsonProperty;

class Fields {
    private Content content;
    private Author author;
    private Email email;
    private Time time;
    private Likes likes;

    public Content getContent() {
        return content;
    }

    public Author getAuthor() {
        return author;
    }

    public Time getTime() {
        return time;
    }

    public Likes getLikes() {
        return likes;
    }

    public Email getEmail(){
        return email;
    }

    public void addLike() {
        this.likes.addLike();
    }

    static class Content {
        @JsonProperty("stringValue")
        private String stringValue;

        public String getStringValue() {
            return stringValue;
        }
    }

    static class Author {
        @JsonProperty("stringValue")
        private String stringValue;

        public String getStringValue() {
            return stringValue;
        }
    }

    static class Time implements Comparable<Time>{
        @JsonProperty("timestampValue")
        private String stringValue;

        public String getStringValue() {
            return stringValue;
        }
        @Override
        public int compareTo(Time o) {
            return o.stringValue.compareTo(this.stringValue);
        }
    }

    static class Likes {
        @JsonProperty("integerValue")
        private int integerValue;

        public int getIntegerValue() {
            return integerValue;
        }

        public void addLike() {
            integerValue += 1;
        }
    }

    static class Email {
        @JsonProperty("stringValue")
        private String stringValue;

        public String getStringValue() {
            return stringValue;
        }
    }


    @Override
    public String toString() {
        return author.getStringValue() + "\n" + time.getStringValue()+ "\n"+ "--------------------------"
                + "\n" + content.getStringValue() + "\n" + "--------------------------" + "\n" +
                + likes.getIntegerValue() + " Likes";
    }
}
