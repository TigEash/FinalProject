package com.gamecodeschool.snake;


import static android.os.Build.VERSION.*;
import static android.os.Build.VERSION_CODES.*;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

import java.io.IOException;

public class AudioSystem {
    private SoundPool mSP;

    public AudioSystem(Context context, int i) {
        // Initialize the SoundPool
        if (i == 1) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            mSP = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();

        }

        else {
            mSP = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }/*
        try {
        AssetManager assetManager = context.getAssets();
        AssetFileDescriptor descriptor;

        // Prepare the sounds in memory
        descriptor = assetManager.openFd("get_apple.ogg");
        mEat_ID = mSP.load(descriptor, 0);

        descriptor = assetManager.openFd("snake_death.ogg");
        mCrashID = mSP.load(descriptor, 0);

    } catch (
    IOException e) {
        // Error
    }*/
    }
}
