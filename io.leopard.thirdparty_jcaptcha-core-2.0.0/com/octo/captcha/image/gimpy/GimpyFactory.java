/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.CaptchaException
 */
package com.octo.captcha.image.gimpy;

import com.octo.captcha.CaptchaException;
import com.octo.captcha.CaptchaQuestionHelper;
import com.octo.captcha.component.image.wordtoimage.WordToImage;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.image.ImageCaptcha;
import com.octo.captcha.image.ImageCaptchaFactory;
import com.octo.captcha.image.gimpy.Gimpy;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;

public class GimpyFactory
extends ImageCaptchaFactory {
    private Random myRandom = new SecureRandom();
    private WordToImage wordToImage;
    private WordGenerator wordGenerator;
    private boolean caseSensitive = true;
    public static final String BUNDLE_QUESTION_KEY = Gimpy.class.getName();

    public GimpyFactory(WordGenerator generator, WordToImage word2image) {
        this(generator, word2image, true);
    }

    public GimpyFactory(WordGenerator generator, WordToImage word2image, boolean caseSensitive) {
        if (word2image == null) {
            throw new CaptchaException("Invalid configuration for a GimpyFactory : WordToImage can't be null");
        }
        if (generator == null) {
            throw new CaptchaException("Invalid configuration for a GimpyFactory : WordGenerator can't be null");
        }
        this.wordToImage = word2image;
        this.wordGenerator = generator;
        this.caseSensitive = caseSensitive;
    }

    @Override
    public ImageCaptcha getImageCaptcha() {
        return this.getImageCaptcha(Locale.getDefault());
    }

    public WordToImage getWordToImage() {
        return this.wordToImage;
    }

    public WordGenerator getWordGenerator() {
        return this.wordGenerator;
    }

    @Override
    public ImageCaptcha getImageCaptcha(Locale locale) {
        Integer wordLength = this.getRandomLength();
        String word = this.getWordGenerator().getWord(wordLength, locale);
        BufferedImage image = null;
        try {
            image = this.getWordToImage().getImage(word);
        }
        catch (Throwable e) {
            throw new CaptchaException(e);
        }
        Gimpy captcha = new Gimpy(CaptchaQuestionHelper.getQuestion(locale, BUNDLE_QUESTION_KEY), image, word, this.caseSensitive);
        return captcha;
    }

    protected Integer getRandomLength() {
        int range = this.getWordToImage().getMaxAcceptedWordLength() - this.getWordToImage().getMinAcceptedWordLength();
        int randomRange = range != 0 ? this.myRandom.nextInt(range + 1) : 0;
        Integer wordLength = new Integer(randomRange + this.getWordToImage().getMinAcceptedWordLength());
        return wordLength;
    }
}

