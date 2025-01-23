package com.example.dialekto;

import java.util.Objects;

public class CardItem {

    private String inputText;
    private String translationText;
    private String inputLanguage;
    private String translationLanguage;

    public CardItem(String inputText, String translationText, String inputLanguage, String translationLanguage) {
        this.inputText = inputText;
        this.translationText = translationText;
        this.inputLanguage = inputLanguage;
        this.translationLanguage = translationLanguage;
    }

    public String getInputText() {
        return inputText;
    }

    public String getTranslationText() {
        return translationText;
    }

    public String getInputLanguage() {
        return inputLanguage;
    }

    public String getTranslationLanguage() {
        return translationLanguage;
    }
}

