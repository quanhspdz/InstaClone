package com.example.instaclone.models;

public class User {
        private String username;
        private String name;
        private String id;
        private String email;
        private String bio;
        private String imageUrl;

    public User(String username, String name, String id, String email, String bio, String imageUrl) {
        this.username = username;
        this.name = name;
        this.id = id;
        this.email = email;
        this.bio = bio;
        this.imageUrl = imageUrl;
    }

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
