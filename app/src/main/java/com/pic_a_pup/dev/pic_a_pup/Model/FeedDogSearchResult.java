package com.pic_a_pup.dev.pic_a_pup.Model;

public class FeedDogSearchResult {
    String breed;
    String dogImageSent;
    public FeedDogSearchResult(){
        this.breed="default_breed";
        this.dogImageSent = "default_image";
    }

    public FeedDogSearchResult(String breed, String dogImageSent) {
        this.breed = breed;
        this.dogImageSent = dogImageSent;
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
}
