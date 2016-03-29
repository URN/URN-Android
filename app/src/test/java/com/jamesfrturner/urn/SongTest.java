package com.jamesfrturner.urn;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SongTest {
    private Song song;

    @Before
    public void setUp() {
        song = new Song();
        song.setTitle("Pacifier");
        song.setArtist("Catfish and the Bottlemen");
        song.setDurationMillis(237000);
    }

    @Test
    public void progressPercentage_TwentyFivePercent_isCorrect() throws Exception {
        long now = System.currentTimeMillis();

        // Set the start time to 'a quarter of the song ago'
        song.setStartTimeMillis(now - (song.getDurationMillis() / 4));

        int progress = song.getProgress();

        // Won't always be exactly 25% because of the small length of time that passes between 'now'
        // and when the progress is calculated
        assertTrue(progress >= 25 && progress < 30);
    }

    @Test
    public void progressPercentage_FiftyPercent_isCorrect() throws Exception {
        long now = System.currentTimeMillis();

        song.setStartTimeMillis(now - (song.getDurationMillis() / 2));

        int progress = song.getProgress();

        assertTrue(progress >= 50 && progress < 55);
    }

    @Test
    public void progressPercentage_EightyPercent_isCorrect() throws Exception {
        long now = System.currentTimeMillis();

        song.setStartTimeMillis(now - (song.getDurationMillis() / 10) * 8);

        int progress = song.getProgress();

        assertTrue(progress >= 80 && progress < 85);
    }

    @Test
    public void progressPercentage_LessThanZero_isCorrect() throws Exception {
        long now = System.currentTimeMillis();

        // Set start time in the future
        song.setStartTimeMillis(now + song.getDurationMillis() + 1000000);

        int progress = song.getProgress();

        assertTrue(progress == 0);
    }

    @Test
    public void progressPercentage_GreaterThanOneHundred_isCorrect() throws Exception {
        long now = System.currentTimeMillis();

        // Set start time far in past (so that song has ended)
        song.setStartTimeMillis(now - (song.getDurationMillis() + 1000000));

        int progress = song.getProgress();

        assertTrue(progress == 100);
    }
}
