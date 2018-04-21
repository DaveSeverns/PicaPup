package com.pic_a_pup.dev.pic_a_pup.Model;

public class FeedDogSearchResult {
    String breed;
    String dogImageSent;
    String probability;
    public FeedDogSearchResult(){
        this.breed="default_breed";
        this.dogImageSent = "default_image";
        this.probability = ".420";
    }

    public FeedDogSearchResult(String breed, String dogImageSent, String probability) {
        this.breed = breed;
        this.dogImageSent = dogImageSent;
        this.probability = probability;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getDogImageSent() {
        return dogImageSent;
    }

    public void setDogImageSent(String dogImageSent) {
        this.dogImageSent = dogImageSent;
    }

    public String getProbability() {
        return probability;
    }

    public void setProbability(String probability) {
        this.probability = probability;
    }
}
