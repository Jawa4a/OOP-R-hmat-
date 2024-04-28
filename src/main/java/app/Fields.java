package app;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

class Fields {
    private Content content;
    private Author author;
    private Email email;
    private Time time;
    private Likes likes;
    private Comments comments;

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

    public Email getEmail() {
        return email;
    }

    public Comments getComments() {
        return comments;
    }

    public void addLike() {
        this.likes.addLike();
    }

    @Override
    public String toString() {
        return author.getStringValue() + "\n" + time.getStringValue() + "\n" + "--------------------------"
                + "\n" + content.getStringValue() + "\n" + "--------------------------" + "\n" +
                +likes.getIntegerValue() + " Likes";
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

    static class Time implements Comparable<Time> {
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

    static class Comments {
        private ArrayValue arrayValue;

        public void addComment(String comment) {
            arrayValue.addValue(comment);
        }

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

        public void addValue(String value) {
            values.add(new Value(value));
        }

        @JsonProperty("values")
        public List<Value> getValues() {
            return values;
        }

        @JsonProperty("values")
        public void setValues(List<Value> values) {
            this.values = values;
        }

        static class Value {
            private String stringValue;

            @JsonIgnore
            public Value(String comment) {
                this.stringValue = comment;
            }

            public Value() {
                super();
            }

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
}
