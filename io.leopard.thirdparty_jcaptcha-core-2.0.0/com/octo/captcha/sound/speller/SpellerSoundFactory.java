/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.CaptchaException
 */
package com.octo.captcha.sound.speller;

import com.octo.captcha.CaptchaException;
import com.octo.captcha.CaptchaQuestionHelper;
import com.octo.captcha.component.sound.wordtosound.WordToSound;
import com.octo.captcha.component.word.worddecorator.SpellerWordDecorator;
import com.octo.captcha.component.word.worddecorator.WordDecorator;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.sound.SoundCaptcha;
import com.octo.captcha.sound.SoundCaptchaFactory;
import com.octo.captcha.sound.speller.SpellerSound;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;

public class SpellerSoundFactory
extends SoundCaptchaFactory {
    private WordGenerator wordGenerator;
    private WordToSound word2Sound;
    private WordDecorator wordDecorator;
    private Random myRandom = new SecureRandom();
    public static final String BUNDLE_QUESTION_KEY = SpellerSound.class.getName();

    public SpellerSoundFactory(WordGenerator wordGenerator, WordToSound word2Sound, SpellerWordDecorator wordDecorator) {
        if (wordGenerator == null) {
            throw new CaptchaException("Invalid configuration for a SpellingSoundFactory : WordGenerator can't be null");
        }
        if (word2Sound == null) {
            throw new CaptchaException("Invalid configuration for a SpellingSoundFactory : Word2Sound can't be null");
        }
        if (wordDecorator == null) {
            throw new CaptchaException("Invalid configuration for a SpellingSoundFactory : wordDecorator can't be null");
        }
        this.wordGenerator = wordGenerator;
        this.word2Sound = word2Sound;
        this.wordDecorator = wordDecorator;
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
        AudioInputStream sound = this.word2Sound.getSound(this.wordDecorator.decorateWord(word));
        SpellerSound soundCaptcha = new SpellerSound(this.getQuestion(Locale.getDefault()), sound, word);
        return soundCaptcha;
    }

    @Override
    public SoundCaptcha getSoundCaptcha(Locale locale) {
        String word = this.wordGenerator.getWord(this.getRandomLength(), locale);
        AudioInputStream sound = this.word2Sound.getSound(this.wordDecorator.decorateWord(word), locale);
        SpellerSound soundCaptcha = new SpellerSound(this.getQuestion(locale), sound, word);
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

