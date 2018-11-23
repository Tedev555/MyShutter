package me.thanongsine.myshutter.models;

public class Photo {
    private String url;
    private String title;

    public Photo() { }

    public Photo(String url, String title) {
        this.url = url;
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }
}
