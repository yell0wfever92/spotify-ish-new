package com.spotify.example;

import java.util.Objects;

public class Song {

    private String name;
    private String artist;
    private String fileName;
    private boolean isFavorite = false;
    private String year;      // New field
    private String genre;     // New field
    private String filePath;  // New field

    @Override
    public String toString() {
        return "Title: " + name + ", Artist: " + artist + ", Year: " + year + ", Genre: " + genre + (isFavorite ? " [Favorite]" : "");
    }

    public String name() { return this.name; }
    public String artist() { return this.artist; }
    public String fileName() { return this.fileName; }
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    public String year() { return this.year; }
    public String genre() { return this.genre; }
    public String filePath() { return this.filePath; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Song song = (Song) obj;
        return Objects.equals(name, song.name) &&
        Objects.equals(artist, song.artist) &&
        Objects.equals(fileName, song.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, artist, fileName);
    }

}