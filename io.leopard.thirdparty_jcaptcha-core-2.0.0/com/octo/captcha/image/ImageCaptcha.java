/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.Captcha
 */
package com.octo.captcha.image;

import com.octo.captcha.Captcha;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

public abstract class ImageCaptcha
implements Captcha {
    private static final long serialVersionUID = 1L;
    private Boolean hasChallengeBeenCalled = Boolean.FALSE;
    protected String question;
    protected String response;
    protected transient BufferedImage challenge;

    protected ImageCaptcha(String question, BufferedImage challenge) {
        this.challenge = challenge;
        this.question = question;
    }

    protected ImageCaptcha(String question, String response, BufferedImage challenge) {
        this.challenge = challenge;
        this.question = question;
        this.response = response;
    }

    public final String getQuestion() {
        return this.question;
    }

    public final String getResponse() {
        return this.response;
    }

    public final String getTextChallenge() {
        return this.response;
    }

    public final Object getChallenge() {
        return this.getImageChallenge();
    }

    public final BufferedImage getImageChallenge() {
        this.hasChallengeBeenCalled = Boolean.TRUE;
        return this.challenge;
    }

    public final void disposeChallenge() {
        this.challenge = null;
    }

    public Boolean hasGetChalengeBeenCalled() {
        return this.hasChallengeBeenCalled;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        if (this.challenge != null) {
            ImageIO.write((RenderedImage)this.challenge, "png", new MemoryCacheImageOutputStream(out));
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        block2: {
            in.defaultReadObject();
            try {
                this.challenge = ImageIO.read(new MemoryCacheImageInputStream(in));
            }
            catch (IOException e) {
                if (this.hasChallengeBeenCalled.booleanValue()) break block2;
                throw e;
            }
        }
    }
}

