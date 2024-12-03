/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.engine.sound.utils;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class SoundToFile {
    public static void serialize(AudioInputStream pAudioInputStream, File pFile) throws IOException {
        pFile.createNewFile();
        AudioSystem.write(pAudioInputStream, AudioFileFormat.Type.WAVE, pFile);
    }
}

