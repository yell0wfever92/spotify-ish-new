package com.spotify.example;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.SwingUtilities;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

// declares a class for the app
public class SpotifyLikeAppMyCode {

  // the current audio clip
private static Clip audioClip;

@SuppressWarnings("FieldMayBeFinal")
private static String directoryPath =
    "/Users/mccarthydavid92/Documents/GitHub/spotify-ish-new/ish/src/main/java/com/spotify/example/";

  // "main has been updated to start the GUI"
@SuppressWarnings("ConvertToTryWithResources")
public static void main(final String[] args) {
    SwingUtilities.invokeLater(() -> {
        Song[] library = readAudioLibrary();
        String localDirectoryPath = getDirectoryPath();
        new SpotifyLikeAppGUI(library, localDirectoryPath);
    });
}

@SuppressWarnings({"UseSpecificCatch", "CallToPrintStackTrace"})
public static void playSong(Song song) {
if (audioClip != null) {
audioClip.close();
}

try {
String filename = song.fileName();
String filePath = directoryPath + "/wav/" + filename;
File file = new File(filePath);

audioClip = AudioSystem.getClip();
final AudioInputStream in = AudioSystem.getAudioInputStream(file);

audioClip.open(in);
audioClip.setMicrosecondPosition(0);
      audioClip.loop(0); // Play once
} catch (Exception e) {
e.printStackTrace();
}
}

public static void playFromLibrary(Song[] library, Scanner input) {
System.out.println("Enter the number of the song you want to play:");
try {
    int choice = Integer.parseInt(input.nextLine());
    if (choice >= 1 && choice <= library.length) {
        playSong(library[choice - 1]);
    } else {
        System.out.println("Invalid choice.");
    }
} catch (NumberFormatException e) {
System.out.println("Invalid input.");
}
}

public static void markFavoriteFromLibrary(Song[] library, Scanner input) {
System.out.println("Enter the number of the song you want to mark as favorite:");
try {
    int choice = Integer.parseInt(input.nextLine());
    if (choice >= 1 && choice <= library.length) {
        Song song = library[choice - 1];
        song.setFavorite(true);
        System.out.println("Marked as favorite: " + song.name());
    } else {
        System.out.println("Invalid choice.");
    }
} catch (NumberFormatException e) {
    System.out.println("Invalid input.");
}
}

public static void playFromFavorites(Song[] library, Scanner input) {
List<Song> favorites = new ArrayList<>();
for (Song song : library) {
    if (song.isFavorite()) {
        favorites.add(song);
    }
}
if (favorites.isEmpty()) {
    System.out.println("You have no favorite songs to play.");
    return;
}
System.out.println("Enter the number of the favorite song you want to play:");
try {
    int choice = Integer.parseInt(input.nextLine());
    if (choice >= 1 && choice <= favorites.size()) {
        playSong(favorites.get(choice - 1));
    } else {
        System.out.println("Invalid choice.");
    }
} catch (NumberFormatException e) {
    System.out.println("Invalid input.");
}
}

/*
   * searches the library for a song by title
   */
public static void searchByTitle(Song[] library, Scanner input) {
    // get the title from the user
    String title = input.nextLine().toLowerCase();
    @SuppressWarnings("unused")
    boolean found = false;
    List<Song> matchingSongs = new ArrayList<>();
    for (Song song : library) {
        if (song.name().toLowerCase().contains(title)) {
            System.out.println(song);
            found = true;
            System.out.println("Would you like to play this song? (y/n)");
            String response = input.nextLine().toLowerCase();
            if (response.equals("y")) {
                playSong(song);
                break; // Exit after playing the song
            }
        }
    }
    if (matchingSongs.isEmpty()) {
        System.out.println("No songs found with the title: " + title);
    } else {
        System.out.println("Found the following songs:");
        int index = 1;
        for (Song song : matchingSongs) {
            System.out.println(index + ". " + song);
            index++;
        }
        System.out.println("Would you like to play one of these songs? (y/n)");
        String response = input.nextLine().toLowerCase();
        if (response.equals("y")) {
            System.out.println("Enter the number of the song you want to play:");
            try {
                int choice = Integer.parseInt(input.nextLine());
                if (choice >= 1 && choice <= matchingSongs.size()) {
                    playSong(matchingSongs.get(choice - 1));
                } else {
                    System.out.println("Invalid choice.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
    }
}

  // read the audio library of music
@SuppressWarnings({"UseSpecificCatch", "CallToPrintStackTrace"})
public static Song[] readAudioLibrary() {
    // get the file path
    final String jsonFileName = "audio-library.json";
    final String filePath = directoryPath + "/" + jsonFileName;

    Song[] library = null;
    try {
        System.out.println("Reading the file " + filePath);
        JsonReader reader = new JsonReader(new FileReader(filePath));
        library = new Gson().fromJson(reader, Song[].class);
        if (library == null) {
            System.out.println("Library is null after reading JSON.");
        } else {
            System.out.println("Successfully loaded " + library.length + " songs.");
        }
    } catch (Exception e) {
        System.out.printf("ERROR: unable to read the file %s\n", filePath);
        e.printStackTrace();
    }

    return library;
}

    public static String getDirectoryPath() {
        return directoryPath;
    }
}
