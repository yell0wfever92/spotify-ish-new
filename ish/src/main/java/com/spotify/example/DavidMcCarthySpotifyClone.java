package com.spotify.example;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.SwingUtilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

// declares a class for the app
public class DavidMcCarthySpotifyClone {

  // the current audio clip
private static Clip audioClip;

  // "main has been updated to start the GUI"
@SuppressWarnings("ConvertToTryWithResources")
public static void main(final String[] args) {
    SwingUtilities.invokeLater(() -> {
        Song[] library = readAudioLibrary();
        String directoryPath = getDirectoryPath();
        new SpotifyLikeAppGUI(library);
    });

}

@SuppressWarnings({"UseSpecificCatch", "CallToPrintStackTrace"})
public static void playSong(Song song) {
    if (audioClip != null) {
        audioClip.close();
    }

    try {
        String resourcePath = "wav/" + song.fileName();
        URL audioUrl = DavidMcCarthySpotifyClone.class.getClassLoader().getResource(resourcePath);
        if (audioUrl == null) {
            System.out.println("ERROR: Audio file not found: " + resourcePath);
            return;
        }
        audioClip = AudioSystem.getClip();
        AudioInputStream in = AudioSystem.getAudioInputStream(audioUrl);

        audioClip.open(in);
        audioClip.setMicrosecondPosition(0);
        audioClip.start();
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

@SuppressWarnings("CallToPrintStackTrace")
public static void saveAudioLibrary(Song[] library) {
    String jsonFileName = "audio-library.json";
    String filePath = directoryPath + "/" + jsonFileName;
    try (FileWriter writer = new FileWriter(filePath)) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        gson.toJson(library, writer);
        System.out.println("Library saved successfully.");
    } catch (IOException e) {
        System.out.println("Failed to save library.");
        e.printStackTrace();
    }
}

  // read the audio library of music
@SuppressWarnings({"UseSpecificCatch", "CallToPrintStackTrace"})
public static Song[] readAudioLibrary() {
    Song[] library = null;
    try (InputStream inputStream = DavidMcCarthySpotifyClone.class.getClassLoader().getResourceAsStream("audio-library.json");
         InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
        if (inputStream == null) {
            System.out.println("ERROR: audio-library.json not found in classpath.");
            return new Song[0]; // or handle as appropriate
        }
        library = new Gson().fromJson(reader, Song[].class);
        System.out.println("Successfully loaded " + library.length + " songs.");
    } catch (Exception e) {
        System.out.println("ERROR: Unable to read the audio-library.json file from resources.");
        e.printStackTrace();
    }
    return library;
}

    public static String getDirectoryPath() {
        return directoryPath;
    }
}
