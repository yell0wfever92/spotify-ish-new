package com.spotify.example;

import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class SpotifyLikeAppGUI {

    private JFrame frame;
    private JButton homeButton, searchButton, libraryButton, favoritesButton, quitButton;
    private JButton playButton, stopButton, rewindButton;
    private JTextArea displayArea;
    private Song[] library;
    private List<Song> favorites;
    private String directoryPath;
    private Clip audioClip;

    public SpotifyLikeAppGUI(Song[] library, String directoryPath) {
        this.library = library;
        this.directoryPath = directoryPath;
        this.favorites = new ArrayList<>();
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Spotify-Like App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        // Main menu panel with buttons
        JPanel menuPanel = new JPanel();
        homeButton = new JButton("Home");
        searchButton = new JButton("Search");
        libraryButton = new JButton("Library");
        favoritesButton = new JButton("Favorites");
        quitButton = new JButton("Quit");

        menuPanel.add(homeButton);
        menuPanel.add(searchButton);
        menuPanel.add(libraryButton);
        menuPanel.add(favoritesButton);
        menuPanel.add(quitButton);

        frame.add(menuPanel, BorderLayout.NORTH);

        // Display area for showing song info
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        frame.add(new JScrollPane(displayArea), BorderLayout.CENTER);

        // Playback control panel
        JPanel controlPanel = new JPanel();
        playButton = new JButton("Play");
        stopButton = new JButton("Stop");
        rewindButton = new JButton("Rewind");

        controlPanel.add(playButton);
        controlPanel.add(stopButton);
        controlPanel.add(rewindButton);

        frame.add(controlPanel, BorderLayout.SOUTH);

        // Add action listeners for buttons
        addActionListeners();

        frame.setVisible(true);
    }

    private void addActionListeners() {
        homeButton.addActionListener(e -> displayHome());
        searchButton.addActionListener(e -> searchSongs());
        libraryButton.addActionListener(e -> displayLibrary());
        favoritesButton.addActionListener(e -> displayFavorites());
        quitButton.addActionListener(e -> quitApplication());

        playButton.addActionListener(e -> playSelectedSong());
        stopButton.addActionListener(e -> stopSong());
        rewindButton.addActionListener(e -> rewindSong());
    }

    private void displayHome() {
        displayArea.setText("Welcome to the Spotify-Like App!\nUse the buttons above to navigate.");
    }

    private void displayLibrary() {
        displayArea.setText("Library:\n");
        int index = 1;
        for (Song song : library) {
            displayArea.append(index + ". " + song + "\n");
            index++;
        }
        String[] options = new String[library.length];
        for (int i = 0; i < library.length; i++) {
            options[i] = library[i].name();
        }
        String selectedSong = (String) JOptionPane.showInputDialog(
                frame,
                "Select a song to play or mark as favorite:",
                "Library",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);
        if (selectedSong != null) {
            for (Song song : library) {
                if (song.name().equals(selectedSong)) {
                    int action = JOptionPane.showConfirmDialog(
                            frame,
                            "Would you like to mark this song as favorite?",
                            "Favorite",
                            JOptionPane.YES_NO_OPTION);
                    if (action == JOptionPane.YES_OPTION) {
                        addToFavorites(song);
                    }
                    playSong(song);
                    break;
                }
            }
        }
    }

    private void displayFavorites() {
        if (favorites.isEmpty()) {
            displayArea.setText("You have no favorite songs.");
            return;
        }
        displayArea.setText("Favorites:\n");
        int index = 1;
        for (Song song : favorites) {
            displayArea.append(index + ". " + song + "\n");
            index++;
        }
        String[] options = new String[favorites.size()];
        for (int i = 0; i < favorites.size(); i++) {
            options[i] = favorites.get(i).name();
        }
        String selectedSong = (String) JOptionPane.showInputDialog(
                frame,
                "Select a favorite song to play:",
                "Favorites",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);
        if (selectedSong != null) {
            for (Song song : favorites) {
                if (song.name().equals(selectedSong)) {
                    playSong(song);
                    break;
                }
            }
        }
    }

    private void addToFavorites(Song song) {
        if (!favorites.contains(song)) {
            favorites.add(song);
            displayArea.append("\nMarked as favorite: " + song.name());
        } else {
            displayArea.append("\n" + song.name() + " is already in your favorites.");
        }
    }

    private void playSong(Song song) {
        stopSong(); // Stop any currently playing song
        try {
            String filename = song.fileName();
            String filePath = directoryPath + File.separator + "wav" + File.separator + filename;
            System.out.println("Attempting to play file: " + filePath); // Debug statement
            File file = new File(filePath);
            audioClip = AudioSystem.getClip();
            AudioInputStream in = AudioSystem.getAudioInputStream(file);
            audioClip.open(in);
            audioClip.start();
            displayArea.append("\nNow playing: " + song.name());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Unable to play the selected song.");
        }
    }

    private void playSelectedSong() {
        JOptionPane.showMessageDialog(frame, "Please select a song from Library, Search, or Favorites.");
    }

    private void stopSong() {
        if (audioClip != null && audioClip.isRunning()) {
            audioClip.stop();
            displayArea.append("\nPlayback stopped.");
        }
    }

    private void rewindSong() {
        if (audioClip != null) {
            audioClip.setMicrosecondPosition(0);
            audioClip.start();
            displayArea.append("\nSong rewound to the beginning.");
        }
    }

    private void quitApplication() {
        if (audioClip != null && audioClip.isRunning()) {
            audioClip.stop();
            audioClip.close();
        }
        frame.dispose();
    }

    private void searchSongs() {
        String searchTerm = JOptionPane.showInputDialog(frame, "Enter the title of the song:");
        if (searchTerm != null && !searchTerm.isEmpty()) {
            List<Song> matchingSongs = new ArrayList<>();
            for (Song song : library) {
                if (song.name().toLowerCase().contains(searchTerm.toLowerCase())) {
                    matchingSongs.add(song);
                }
            }
            if (matchingSongs.isEmpty()) {
                displayArea.setText("No songs found with the title: " + searchTerm);
            } else {
                displayArea.setText("Search Results:\n");
                String[] options = new String[matchingSongs.size()];
                for (int i = 0; i < matchingSongs.size(); i++) {
                    options[i] = matchingSongs.get(i).name();
                }
                String selectedSong = (String) JOptionPane.showInputDialog(
                        frame,
                        "Select a song to play:",
                        "Search Results",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        options,
                        options[0]);
                if (selectedSong != null) {
                    for (Song song : matchingSongs) {
                        if (song.name().equals(selectedSong)) {
                            playSong(song);
                            break;
                        }
                    }
                }
            }
        }
    }
}