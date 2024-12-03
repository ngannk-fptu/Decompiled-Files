/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.CaptchaException
 */
package com.octo.captcha.sound.spellfind;

import com.octo.captcha.CaptchaException;
import com.octo.captcha.CaptchaQuestionHelper;
import com.octo.captcha.component.sound.wordtosound.WordToSound;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.sound.SoundCaptcha;
import com.octo.captcha.sound.SoundCaptchaFactory;
import com.octo.captcha.sound.speller.SpellerSound;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;
import javax.sound.sampled.AudioInputStream;

public class SpellFindCaptchaFactory
extends SoundCaptchaFactory {
    private WordGenerator wordGenerator;
    private WordToSound word2Sound;
    private Random myRandom = new SecureRandom();
    public static final String BUNDLE_QUESTION_KEY = SpellFindCaptchaFactory.class.getName();

    public SpellFindCaptchaFactory(WordGenerator wordGenerator, WordToSound word2Sound) {
        if (wordGenerator == null) {
            throw new CaptchaException("Invalid configuration for a SpellFindCaptchaFactory : WordGenerator can't be null");
        }
        if (word2Sound == null) {
            throw new CaptchaException("Invalid configuration for a SpellFindCaptchaFactory : Word2Sound can't be null");
        }
        this.wordGenerator = wordGenerator;
        this.word2Sound = word2Sound;
    }

    public WordToSound getWordToSound() {
        return this.word2Sound;
    }

    public WordGenerator getWordGenerator() {
        return this.wordGenerator;
    }

    @Override
    public SoundCaptcha getSoundCaptcha() {
        return this.getSoundCaptcha(Locale.getDefault());
    }

    @Override
    public SoundCaptcha getSoundCaptcha(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(this.getClass().getName(), locale);
        int length = this.getRandomLength();
        StringBuffer challenge = new StringBuffer();
        StringBuffer response = new StringBuffer();
        for (int i = 0; i < length; ++i) {
            String word = this.wordGenerator.getWord(new Integer(this.getRandomLength()), locale);
            int position = Math.abs(this.myRandom.nextInt() % word.length());
            challenge.append(bundle.getString("number"));
            challenge.append(" ");
            challenge.append(position + 1);
            challenge.append(" ");
            challenge.append(bundle.getString("word"));
            challenge.append(" ");
            challenge.append(word);
            challenge.append(" ");
            challenge.append(length - 1 == i ? bundle.getString("end") : bundle.getString("transition"));
            response.append(word.charAt(position));
        }
        AudioInputStream sound = this.word2Sound.getSound(challenge.toString(), locale);
        SpellerSound soundCaptcha = new SpellerSound(this.getQuestion(locale), sound, response.toString());
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

