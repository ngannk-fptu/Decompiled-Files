/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.component.sound.wordtosound;

import com.octo.captcha.component.sound.soundconfigurator.SoundConfigurator;
import com.octo.captcha.component.sound.wordtosound.WordToSound;
import javax.sound.sampled.AudioInputStream;

public abstract class AbstractWordToSound
implements WordToSound {
    protected int maxAcceptedWordLength;
    protected int minAcceptedWordLength;
    protected SoundConfigurator configurator = null;

    public AbstractWordToSound(SoundConfigurator configurator, int minAcceptedWordLength, int maxAcceptedWordLength) {
        this.configurator = configurator;
        this.minAcceptedWordLength = minAcceptedWordLength;
        this.maxAcceptedWordLength = maxAcceptedWordLength;
    }

    @Override
    public int getMaxAcceptedWordLength() {
        return this.maxAcceptedWordLength;
    }

    @Override
    public int getMinAcceptedWordLength() {
        return this.minAcceptedWordLength;
    }

    @Override
    public int getMaxAcceptedWordLenght() {
        return this.maxAcceptedWordLength;
    }

    @Override
    public int getMinAcceptedWordLenght() {
        return this.minAcceptedWordLength;
    }

    protected abstract AudioInputStream addEffects(AudioInputStream var1);
}

