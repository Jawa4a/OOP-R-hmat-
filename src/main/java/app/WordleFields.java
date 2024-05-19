package app;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WordleFields {
    @JsonProperty("word")
    private Word word;


    public Word getWord() {
        return word;
    }

    static class Word {
        @JsonProperty("stringValue")
        private String stringValue;

        public String getStringValue() {
            return stringValue;
        }

        public void setStringValue(String stringValue) {
            this.stringValue = stringValue;
        }
    }



    @Override
    public String toString() {
        return "WordleFields{" +
                "word=" + word +
                '}';
    }
}
