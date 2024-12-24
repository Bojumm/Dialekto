package com.example.dialekto;

public class CardItem {
    private String language;
    private String phrase;

    public CardItem(String language, String phrase) {
        this.language = language;
        this.phrase = phrase;
    }

    public String getLanguage() {
        return language;
    }

    public String getPhrase() {
        return phrase;
    }
}

