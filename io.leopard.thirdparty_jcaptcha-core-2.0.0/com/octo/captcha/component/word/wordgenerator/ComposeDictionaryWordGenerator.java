/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.CaptchaException
 */
package com.octo.captcha.component.word.wordgenerator;

import com.octo.captcha.CaptchaException;
import com.octo.captcha.component.word.DictionaryReader;
import com.octo.captcha.component.word.SizeSortedWordList;
import com.octo.captcha.component.word.wordgenerator.DictionaryWordGenerator;
import java.util.Locale;

public class ComposeDictionaryWordGenerator
extends DictionaryWordGenerator {
    public ComposeDictionaryWordGenerator(DictionaryReader reader) {
        super(reader);
    }

    @Override
    public String getWord(Integer length, Locale locale) {
        SizeSortedWordList words = this.getWordList(locale);
        int firstLength = length / 2;
        String firstWord = null;
        for (int i = firstLength; i < 50; ++i) {
            firstWord = words.getNextWord(new Integer(firstLength + i));
            if (firstWord == null) continue;
            firstWord = firstWord.substring(0, firstLength);
            break;
        }
        String secondWord = null;
        for (int i = firstLength; i < 50; ++i) {
            secondWord = words.getNextWord(new Integer(length - firstLength + i));
            if (secondWord == null) continue;
            secondWord = secondWord.substring(secondWord.length() - length + firstLength, secondWord.length());
            break;
        }
        firstWord = this.checkAndFindSmaller(firstWord, firstLength, locale);
        secondWord = this.checkAndFindSmaller(secondWord, length - firstLength, locale);
        return firstWord + secondWord;
    }

    private String checkAndFindSmaller(String firstWord, int length, Locale locale) {
        if (firstWord == null) {
            if (length > 1) {
                firstWord = this.getWord(new Integer(length), locale);
            } else {
                throw new CaptchaException("No word of length : " + length + " exists in dictionnary! please " + "update your dictionary or your range!");
            }
        }
        return firstWord;
    }
}

