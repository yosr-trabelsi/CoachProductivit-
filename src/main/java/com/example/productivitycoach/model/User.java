package com.example.productivitycoach.model;

public class User {

    private int id;
    private String username;
    private String email;
    private String password;
    private String rythmeTravail;
    private String objectifs;
    private String preferences;


    public User(int id, String username, String email, String password, String rythmeTravail) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.rythmeTravail = rythmeTravail;
    }


    public User(String username, String email, String password, String rythmeTravail) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.rythmeTravail = rythmeTravail;
    }



    public int getId() { return id; }

    public String getUsername() { return username; }

    public String getNom() {
        return username;
    }

    public String getEmail() { return email; }

    public String getPassword() { return password; }

    public String getRythmeTravail() { return rythmeTravail; }

    public String getObjectifs() { return objectifs; }

    public String getPreferences() { return preferences; }



    public void setId(int id) { this.id = id; }

    public void setUsername(String username) { this.username = username; }

    public void setEmail(String email) { this.email = email; }

    public void setPassword(String password) { this.password = password; }

    public void setObjectifs(String objectifs) { this.objectifs = objectifs; }

    public void setPreferences(String preferences) { this.preferences = preferences; }

    public void setRythmeTravail(String rythmeTravail) { this.rythmeTravail = rythmeTravail; }
}