package com.spotify.example;

import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

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
    // reading audio library from json file
    Song[] library = readAudioLibrary();

    // create a scanner for user input
    Scanner input = new Scanner(System.in);

    String userInput = "";
    while (!userInput.equals("q")) {
      menu();

      // get input
      userInput = input.nextLine();

      // accept upper or lower case commands
      userInput = userInput.toLowerCase();

      // do something
      handleMenu(userInput, library, input);
    }

    // close the scanner
    input.close();
  }

  /*
   * displays the menu for the app
   */
  public static void menu() {
    System.out.println("---- SpotifyLikeApp ----");
    System.out.println("[H]ome");
    System.out.println("[S]earch by title");
    System.out.println("[L]ibrary");
    System.out.println("[P]lay");
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
        }
        case "p" -> {
          System.out.println("-->Play<--");
          displayLibrary(library);
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
        case "q" -> System.out.println("-->Quit<--");
        default -> {
        }
    }
  }
  /*
  plays a song
  */
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
        audioClip.loop(Clip.LOOP_CONTINUOUSLY);
    } catch (Exception e) {
        e.printStackTrace();
    }
}
  /*
   * plays an audio file
   */
  @SuppressWarnings({"CallToPrintStackTrace", "UseSpecificCatch"})
  public static void play(Song[] library) {
    // open the audio file

    // get the filePath and open a audio file
    final Integer i = 3;
    final String filename = library[i].fileName();
    final String filePath = "C:\\Users\\myxbo\\OneDrive\\Documents\\GitHub\\spotify-ish\\ish\\src\\main\\java\\com\\spotify\\example\\wav" + "\\" + filename;
    final File file = new File(filePath);

    // stop the current song from playing, before playing the next one
    if (audioClip != null) {
      audioClip.close();
    }

    try {
      // create clip
      audioClip = AudioSystem.getClip();

      // get input stream
      final AudioInputStream in = AudioSystem.getAudioInputStream(file);

      audioClip.open(in);
      audioClip.setMicrosecondPosition(0);
      audioClip.loop(Clip.LOOP_CONTINUOUSLY);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /*
   * displays the list of songs in the library
   */
  public static void displayLibrary(Song[] library) {
    System.out.println("---- Library ----");
    int index = 1;
    for (Song song : library) {
        System.out.println(index + ". " + song);
        index++;
    }
}


  /*
   * searches the library for a song by title
   */
  public static void searchByTitle(Song[] library, Scanner input) {
    // get the title from the user
    String title = input.nextLine().toLowerCase(); // Convert user input to lowercase
    boolean found = false;
    for (Song song : library) {
        if (song.name().toLowerCase().contains(title)) { // Convert song name to lowercase
            System.out.println(song);
            found = true;
        }
    }
    if (!found) {
        System.out.println("No songs found with the title: " + title);
    }
  }

  // read the audio library of music
  @SuppressWarnings("UseSpecificCatch")
  public static Song[] readAudioLibrary() {
    // get the file path
    final String jsonFileName = "audio-library.json";
    final String filePath = "C:\\Users\\myxbo\\OneDrive\\Documents\\GitHub\\spotify-ish\\ish\\src\\main\\java\\com\\spotify\\example" + "\\" + jsonFileName;

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
