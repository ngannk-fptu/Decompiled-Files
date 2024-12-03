/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.CaptchaException
 */
package com.octo.captcha.sound.gimpy;

import com.octo.captcha.CaptchaException;
import com.octo.captcha.CaptchaQuestionHelper;
import com.octo.captcha.component.sound.wordtosound.WordToSound;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.sound.SoundCaptcha;
import com.octo.captcha.sound.SoundCaptchaFactory;
import com.octo.captcha.sound.gimpy.GimpySound;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;

public class GimpySoundFactory
extends SoundCaptchaFactory {
    private WordGenerator wordGenerator;
    private WordToSound word2Sound;
    private Random myRandom = new SecureRandom();
    public static final String BUNDLE_QUESTION_KEY = GimpySound.class.getName();

    public GimpySoundFactory(WordGenerator thewordGenerator, WordToSound theword2Sound) {
        if (thewordGenerator == null) {
            throw new CaptchaException("Invalid configuration for a GimpySoundFactory : WordGenerator can't be null");
        }
        if (theword2Sound == null) {
            throw new CaptchaException("Invalid configuration for a GimpySoundFactory : Word2Sound can't be null");
        }
        this.wordGenerator = thewordGenerator;
        this.word2Sound = theword2Sound;
    }

    public WordToSound getWordToSound() {
        return this.word2Sound;
    }

    public WordGenerator getWordGenerator() {
        return this.wordGenerator;
    }

    @Override
    public SoundCaptcha getSoundCaptcha() {
        String word = this.wordGenerator.getWord(this.getRandomLength(), Locale.getDefault());
        AudioInputStream sound = this.word2Sound.getSound(word);
        GimpySound soundCaptcha = new GimpySound(this.getQuestion(Locale.getDefault()), sound, word);
        return soundCaptcha;
    }

    @Override
    public SoundCaptcha getSoundCaptcha(Locale locale) {
        String word = this.wordGenerator.getWord(this.getRandomLength(), locale);
        AudioInputStream sound = this.word2Sound.getSound(word, locale);
        GimpySound soundCaptcha = new GimpySound(this.getQuestion(locale), sound, word);
        return soundCaptcha;
    }

    protected String getQuestion(Locale locale) {
        return CaptchaQuestionHelper.getQuestion(locale, BUNDLE_QUESTION_KEY);
    }

    protected Integer getRandomLength() {
        int range = this.getWordToSound().getMaxAcceptedWordLength() - this.getWordToSound().getMinAcceptedWordLength();
        int randomRange = range != 0 ? this.myRandom.nextInt(range + 1) : 0;
        Integer wordLength = new Integer(randomRange + this.getWordToSound().getMinAcceptedWordLength());
        return wordLength;
    }
}

