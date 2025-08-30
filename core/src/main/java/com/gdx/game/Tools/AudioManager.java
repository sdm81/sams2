package com.gdx.game.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.gdx.game.Setting.GameResources;


public class AudioManager {

    public boolean isSoundOn;
    public boolean isMusicOn;

    public Music backgroundMusic;
    public Sound shootSound;
    public Sound explosionSound;

    public AudioManager() {
        //backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal(GameResources.BACKGROUND_MUSIC_PATH));

        isMusicOn = true;
        isSoundOn = true;

        backgroundMusic.setVolume(0.2f);
        backgroundMusic.setLooping(true);

        backgroundMusic.play();
    }

    public void updateMusicFlag() {
        if (isMusicOn) backgroundMusic.play();
        else backgroundMusic.stop();
    }

}
