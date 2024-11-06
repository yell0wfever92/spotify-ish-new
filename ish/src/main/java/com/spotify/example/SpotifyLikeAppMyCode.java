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
    "C:\\Users\\myxbo\\OneDrive\\Documents\\GitHub\\spotify-ish-new\\ish\\src\\main\\java\\com\\spotify\\example";

  // "main" makes this class a java app that can be executed
  @SuppressWarnings("ConvertToTryWithResources")
  public static void main(final String[] args) {
    SwingUtilities.invokeLater(() -> {
        Song[] library = readAudioLibrary();
        String directoryPath = getDirectoryPath();
        new SpotifyLikeAppGUI(library, directoryPath);
    });
}

  /*
   * displays the menu for the app
   */
  public static void menu() {
    System.out.println("---- SpotifyLikeApp ----");
    System.out.println("[H]ome");
    System.out.println("[S]earch by title");
    System.out.println("[L]ibrary");
    System.out.println("[F]avorites"); // Added new option as Play is now integrated into "S" and "L"
    System.out.println("[Q]uit");
    System.out.println("");
    System.out.print("Enter q to Quit:");
  }

  /*
   * handles the user input for the app
   */
  public static void handleMenu(String userInput, Song[] library, Scanner input) {
    switch (userInput) {
        case "h" -> System.out.println("-->Home<--");
        case "s" -> {
            System.out.println("-->Search by title<--");
            System.out.println("Enter the title of the song:");
            searchByTitle(library, input);
        }
        case "l" -> {
            System.out.println("-->Library<--");
            displayLibrary(library);
            System.out.println("Would you like to play or mark a song as favorite? (play/favorite/n)");
            String response = input.nextLine().toLowerCase();
            if (response.equals("play")) {
                playFromLibrary(library, input);
            } else if (response.equals("favorite")) {
                markFavoriteFromLibrary(library, input);
            }
        }
        case "f" -> {
            System.out.println("-->Favorites<--");
            displayFavorites(library);
            System.out.println("Would you like to play a favorite song? (y/n)");
            String response = input.nextLine().toLowerCase();
            if (response.equals("y")) {
                playFromFavorites(library, input);
            }
        }
        case "q" -> System.out.println("-->Quit<--");
        default -> System.out.println("Invalid option. Please try again.");
    }
}

  @SuppressWarnings({"UseSpecificCatch", "CallToPrintStackTrace"})
  public static void playSong(Song song) {
  if (audioClip != null) {
      audioClip.close();
  }

  try {
      String filename = song.fileName();
      String filePath = directoryPath + "\\wav\\" + filename;
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

public static void displayLibrary(Song[] library) {
  System.out.println("---- Library ----");
  int index = 1;
  for (Song song : library) {
      System.out.println(index + ". " + song);
      index++;
  }
}

public static void displayFavorites(Song[] library) {
  System.out.println("---- Favorites ----");
  List<Song> favorites = new ArrayList<>();
  int index = 1;
  for (Song song : library) {
      if (song.isFavorite()) {
          System.out.println(index + ". " + song);
          favorites.add(song);
          index++;
      }
  }
  if (favorites.isEmpty()) {
      System.out.println("You have no favorite songs.");
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
  @SuppressWarnings("UseSpecificCatch")
  public static Song[] readAudioLibrary() {
    // get the file path
    final String jsonFileName = "audio-library.json";
    final String filePath = directoryPath + "\\" + jsonFileName;

    Song[] library = null;
    try {
      System.out.println("Reading the file " + filePath);
      JsonReader reader = new JsonReader(new FileReader(filePath));
      library = new Gson().fromJson(reader, Song[].class);
    } catch (Exception e) {
      System.out.printf("ERROR: unable to read the file %s\n", filePath);
      System.out.println();
    }

    return library;
  }

    public static String getDirectoryPath() {
        return directoryPath;
    }
}
