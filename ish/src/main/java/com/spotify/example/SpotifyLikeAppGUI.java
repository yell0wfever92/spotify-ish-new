package com.spotify.example;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
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
    private final List<Song> favorites;
    private final String directoryPath;
    private Clip audioClip;
    private Song currentlyPlayingSong;
    private List<Song> currentDisplaySongs;

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

        // Add action listeners for buttons
        addActionListeners();

        // Add mouse listener for song list
        addSongListMouseListener();

        // Display the home screen on startup
        displayHome();

        frame.setVisible(true);
    }

    @SuppressWarnings("unused")
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
                                "Would you like to mark this song as favorite?",
                                "Favorite",
                                JOptionPane.YES_NO_OPTION);
                        if (action == JOptionPane.YES_OPTION) {
                            addToFavorites(selectedSong);
                        }
                        playSong(selectedSong);
                    }
                }
            }
        });
    }

    private void displayHome() {
        listModel.clear();
        listModel.addElement("Welcome to the Spotify-Like App!");
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

    private void addToFavorites(Song song) {
        if (!favorites.contains(song)) {
            favorites.add(song);
            song.setFavorite(true);
            JOptionPane.showMessageDialog(frame, song.name() + " has been added to your favorites.");
        } else {
            JOptionPane.showMessageDialog(frame, song.name() + " is already in your favorites.");
        }
    }

    @SuppressWarnings({"UseSpecificCatch", "CallToPrintStackTrace"})
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
            currentlyPlayingSong = song;
            JOptionPane.showMessageDialog(frame, "Now playing: " + song.name());
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
            addToFavorites(currentlyPlayingSong);
        } else {
            JOptionPane.showMessageDialog(frame, "No song is currently playing to favorite.");
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