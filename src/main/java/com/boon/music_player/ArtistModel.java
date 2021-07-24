package com.boon.music_player;

public class ArtistModel {

    public String title;
    public String image;
    public String preview;

    public ArtistModel(String title, String image, String preview) {
        this.title = title;
        this.image = image;
        this.preview = preview;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }
}
