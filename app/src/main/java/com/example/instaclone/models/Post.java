package com.example.instaclone.models;

public class Post {

    private String descriptions;
    private String imageUrl;
    private String postId;
    private String publisher;

    public Post(String descriptions, String imageUrl, String postId, String publisher) {
        this.descriptions = descriptions;
        this.imageUrl = imageUrl;
        this.postId = postId;
        this.publisher = publisher;
    }

    public Post() {
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
