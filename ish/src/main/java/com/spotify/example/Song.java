package com.spotify.example;

public class Song {

  private String name;
  private String artist;
  private String fileName;
  private boolean isFavorite = false; // New attribute
  
  // serializes attributes into a string
  @Override
  public String toString() {
    return "Name: " + name + ", Artist: " + artist + (isFavorite ? " [Favorite]" : "");
  }

  // getters
  public String name() {
    return this.name;
  }

  public String artist() {
    return this.artist;
  }

  public String fileName() {
    return this.fileName;
  }

  public boolean isFavorite() {
    return isFavorite;
  }

  public void setFavorite(boolean favorite) {
    isFavorite = favorite;
  }
}
