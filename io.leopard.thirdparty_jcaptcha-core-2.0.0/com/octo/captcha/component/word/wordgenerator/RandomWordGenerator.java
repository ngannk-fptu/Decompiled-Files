/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.component.word.wordgenerator;

import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;

public class RandomWordGenerator
implements WordGenerator {
    private char[] possiblesChars;
    private Random myRandom = new SecureRandom();

    public RandomWordGenerator(String acceptedChars) {
        this.possiblesChars = acceptedChars.toCharArray();
    }

    @Override
    public String getWord(Integer length) {
        StringBuffer word = new StringBuffer(length);
        for (int i = 0; i < length; ++i) {
            word.append(this.possiblesChars[this.myRandom.nextInt(this.possiblesChars.length)]);
        }
        return word.toString();
    }

    @Override
    public String getWord(Integer length, Locale locale) {
        return this.getWord(length);
    }
}

