package com.spotify.example;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class SpotifyLikeAppGUI {

    private JFrame frame;
    private JButton homeButton, searchButton, libraryButton, favoritesButton, quitButton;
    private JButton playButton, pauseButton, stopButton, rewindButton, forwardButton, favoriteButton;
    private JList<String> songList;
    private DefaultListModel<String> listModel;
    private final Song[] library;
    @SuppressWarnings("FieldMayBeFinal")
    private List<Song> favorites;
    private Clip audioClip;
    private Song currentlyPlayingSong;
    private List<Song> currentDisplaySongs;

    public SpotifyLikeAppGUI(Song[] library) {
        this.library = library;
        this.favorites = new ArrayList<>();
        initialize();
    }

    private void initialize() {
        frame = new JFrame("David McCarthy's Spotify Clone");
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

        // Song list area for displaying songs
        listModel = new DefaultListModel<>();
        songList = new JList<>(listModel);
        frame.add(new JScrollPane(songList), BorderLayout.CENTER);

        // Playback control panel
        JPanel controlPanel = new JPanel();
        playButton = new JButton("Play");
        pauseButton = new JButton("Pause");
        stopButton = new JButton("Stop");
        rewindButton = new JButton("Rewind");
        forwardButton = new JButton("Forward");
        favoriteButton = new JButton("Favorite");

        controlPanel.add(playButton);
        controlPanel.add(pauseButton);
        controlPanel.add(stopButton);
        controlPanel.add(rewindButton);
        controlPanel.add(forwardButton);
        controlPanel.add(favoriteButton);

        frame.add(controlPanel, BorderLayout.SOUTH);

        loadFavorites();

        // Add action listeners for buttons
        addActionListeners();

        // Add mouse listener for song list
        addSongListMouseListener();

        // Display the home screen on startup
        displayHome();

        frame.setVisible(true);
    }

    private void addActionListeners() {
        homeButton.addActionListener(e -> displayHome());
        searchButton.addActionListener(e -> searchSongs());
        libraryButton.addActionListener(e -> displayLibrary());
        favoritesButton.addActionListener(e -> displayFavorites());
        quitButton.addActionListener(e -> quitApplication());

        playButton.addActionListener(e -> playSelectedSong());
        pauseButton.addActionListener(e -> pauseOrResumeSong());
        stopButton.addActionListener(e -> stopSong());
        rewindButton.addActionListener(e -> rewindSong());
        forwardButton.addActionListener(e -> forwardSong());
        favoriteButton.addActionListener(e -> favoriteCurrentSong());
    }

    private void addSongListMouseListener() {
        songList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int index = songList.locationToIndex(evt.getPoint());
                    if (index >= 0 && index < currentDisplaySongs.size()) {
                        Song selectedSong = currentDisplaySongs.get(index);
                        int action = JOptionPane.showConfirmDialog(
                                frame,
                                "Would you like to favorite/unfavorite this song?",
                                "Favorite",
                                JOptionPane.YES_NO_OPTION);
                        if (action == JOptionPane.YES_OPTION) {
                            toggleFavorite(selectedSong);
                        }
                        playSong(selectedSong);
                    }
                }
            }
        });        
    }

    private void displayHome() {
        listModel.clear();
        listModel.addElement("Welcome to the prime competitor to Spotify in the making!");
        listModel.addElement("Use the buttons above to navigate.");
        currentDisplaySongs = new ArrayList<>();
    }

    private void displayLibrary() {
        listModel.clear();
        currentDisplaySongs = new ArrayList<>();
        for (Song song : library) {
            listModel.addElement(song.toString());
            currentDisplaySongs.add(song);
        }
    }

    private void displayFavorites() {
        if (favorites.isEmpty()) {
            listModel.clear();
            listModel.addElement("You have no favorite songs.");
            currentDisplaySongs = new ArrayList<>();
            return;
        }
        listModel.clear();
        currentDisplaySongs = new ArrayList<>();
        for (Song song : favorites) {
            listModel.addElement(song.toString());
            currentDisplaySongs.add(song);
        }
    }

    private void toggleFavorite(Song song) {
        if (favorites.contains(song)) {
            favorites.remove(song);
            song.setFavorite(false);
            JOptionPane.showMessageDialog(frame, song.name() + " has been removed from your favorites.");
        } else {
            favorites.add(song);
            song.setFavorite(true);
            JOptionPane.showMessageDialog(frame, song.name() + " has been added to your favorites.");
        }
    }

    @SuppressWarnings({"UseSpecificCatch", "CallToPrintStackTrace"})
    public void playSong(Song song) {
    if (audioClip != null) {
        audioClip.close();
    }

    try {
        String resourcePath = "wav/" + song.fileName();
        System.out.println("Attempting to load resource: " + resourcePath);

        InputStream audioSrc = DavidMcCarthySpotifyClone.class.getClassLoader().getResourceAsStream(resourcePath);
        if (audioSrc == null) {
            System.out.println("ERROR: Audio file not found: " + resourcePath);
            JOptionPane.showMessageDialog(frame, "Audio file not found: " + resourcePath);
            return;
        }
        System.out.println("Audio resource found.");

        InputStream bufferedIn = new BufferedInputStream(audioSrc);
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

        audioClip = AudioSystem.getClip();
        audioClip.open(audioStream);
        audioClip.setMicrosecondPosition(0);
        audioClip.start();

        currentlyPlayingSong = song;
        // Display song information
        String songInfo = String.format(
            "Now playing:\nTitle: %s\nArtist: %s\nYear: %s\nGenre: %s",
            song.name(), song.artist(), song.year(), song.genre()
        );
        JOptionPane.showMessageDialog(frame, songInfo);

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(frame, "Unable to play the selected song.");
    }
}


    private void playSelectedSong() {
        int index = songList.getSelectedIndex();
        if (index >= 0 && index < currentDisplaySongs.size()) {
            Song selectedSong = currentDisplaySongs.get(index);
            playSong(selectedSong);
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a song from the list.");
        }
    }

    private void pauseOrResumeSong() {
        if (audioClip != null) {
            if (audioClip.isRunning()) {
                audioClip.stop();
                JOptionPane.showMessageDialog(frame, "Playback paused.");
            } else {
                audioClip.start();
                JOptionPane.showMessageDialog(frame, "Playback resumed.");
            }
        }
    }

    private void stopSong() {
        if (audioClip != null) {
            audioClip.stop();
            audioClip.setMicrosecondPosition(0);
            JOptionPane.showMessageDialog(frame, "Playback stopped and reset.");
        }
    }

    private void rewindSong() {
        if (audioClip != null) {
            long currentPosition = audioClip.getMicrosecondPosition();
            long newPosition = currentPosition - 5_000_000; // rewind 5 seconds
            if (newPosition < 0) {
                newPosition = 0;
            }
            audioClip.setMicrosecondPosition(newPosition);
            JOptionPane.showMessageDialog(frame, "Song rewound 5 seconds.");
        }
    }

    private void forwardSong() {
        if (audioClip != null) {
            long currentPosition = audioClip.getMicrosecondPosition();
            long clipLength = audioClip.getMicrosecondLength();
            long newPosition = currentPosition + 5_000_000; // forward 5 seconds
            if (newPosition > clipLength) {
                newPosition = clipLength;
            }
            audioClip.setMicrosecondPosition(newPosition);
            JOptionPane.showMessageDialog(frame, "Song forwarded 5 seconds.");
        }
    }

    private void favoriteCurrentSong() {
        if (currentlyPlayingSong != null) {
            toggleFavorite(currentlyPlayingSong);
        } else {
            JOptionPane.showMessageDialog(frame, "No song is currently playing to favorite.");
        }
    }
    
    @SuppressWarnings("CallToPrintStackTrace")
    private void saveFavorites() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("favorites.txt"))) {
            for (Song song : favorites) {
                writer.write(song.fileName());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private void loadFavorites() {
        favorites.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader("favorites.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                for (Song song : library) {
                    if (song.fileName().equals(line)) {
                        favorites.add(song);
                        song.setFavorite(true);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            // If the file doesn't exist, it's the first run; ignore the exception.
            if (!(e instanceof java.io.FileNotFoundException)) {
                e.printStackTrace();
            }
        }
    }

    private void quitApplication() {
        if (audioClip != null && audioClip.isRunning()) {
            audioClip.stop();
            audioClip.close();
        }

        saveFavorites(); // Save the list of favorite songs
        DavidMcCarthySpotifyClone.saveAudioLibrary(library);
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
                listModel.clear();
                listModel.addElement("No songs found with the title: " + searchTerm);
                currentDisplaySongs = new ArrayList<>();
            } else {
                listModel.clear();
                currentDisplaySongs = new ArrayList<>();
                for (Song song : matchingSongs) {
                    listModel.addElement(song.toString());
                    currentDisplaySongs.add(song);
                }
            }
        }
    }
}