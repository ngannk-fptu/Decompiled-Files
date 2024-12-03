/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.Captcha
 *  com.octo.captcha.CaptchaException
 */
package com.octo.captcha.sound;

import com.octo.captcha.Captcha;
import com.octo.captcha.CaptchaException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public abstract class SoundCaptcha
implements Captcha {
    private static final long serialVersionUID = 1L;
    protected Boolean hasChallengeBeenCalled = Boolean.FALSE;
    protected String question;
    protected String response;
    protected byte[] challenge;

    protected SoundCaptcha(String thequestion, String response, AudioInputStream thechallenge) {
        this.question = thequestion;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            AudioSystem.write(thechallenge, AudioFileFormat.Type.WAVE, out);
            this.challenge = out.toByteArray();
        }
        catch (IOException ioe) {
            throw new CaptchaException("unable to serialize input stream", (Throwable)ioe);
        }
    }

    public final String getQuestion() {
        return this.question;
    }

    public final String getResponse() {
        return this.response;
    }

    public final Object getChallenge() {
        return this.getSoundChallenge();
    }

    public final AudioInputStream getSoundChallenge() {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(this.challenge));
            this.hasChallengeBeenCalled = Boolean.TRUE;
            return audioStream;
        }
        catch (UnsupportedAudioFileException e) {
            throw new CaptchaException("unable to deserialize input stream", (Throwable)e);
        }
        catch (IOException e) {
            throw new CaptchaException("unable to deserialize input stream", (Throwable)e);
        }
    }

    public void disposeChallenge() {
        this.challenge = null;
    }

    public Boolean hasGetChalengeBeenCalled() {
        return this.hasChallengeBeenCalled;
    }
}

