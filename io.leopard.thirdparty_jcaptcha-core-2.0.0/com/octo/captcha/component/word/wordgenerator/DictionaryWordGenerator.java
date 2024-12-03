/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.CaptchaException
 */
package com.octo.captcha.component.word.wordgenerator;

import com.octo.captcha.CaptchaException;
import com.octo.captcha.component.word.DefaultSizeSortedWordList;
import com.octo.captcha.component.word.DictionaryReader;
import com.octo.captcha.component.word.SizeSortedWordList;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import java.util.HashMap;
import java.util.Locale;

public class DictionaryWordGenerator
implements WordGenerator {
    private Locale defaultLocale;
    private DictionaryReader factory;
    private HashMap localizedwords = new HashMap();

    public DictionaryWordGenerator(DictionaryReader reader) {
        this.factory = reader;
        this.defaultLocale = this.factory.getWordList().getLocale();
        this.localizedwords.put(this.defaultLocale, this.factory.getWordList());
    }

    @Override
    public final String getWord(Integer length) {
        return this.getWord(length, this.defaultLocale);
    }

    @Override
    public String getWord(Integer length, Locale locale) {
        SizeSortedWordList words = this.getWordList(locale);
        String word = words.getNextWord(length);
        if (word == null) {
            throw new CaptchaException("No word of length : " + length + " exists in dictionnary! please " + "update your dictionary or your range!");
        }
        return word;
    }

    final SizeSortedWordList getWordList(Locale locale) {
        SizeSortedWordList words;
        if (this.localizedwords.containsKey(locale)) {
            words = (DefaultSizeSortedWordList)this.localizedwords.get(locale);
        } else {
            words = this.factory.getWordList(locale);
            this.localizedwords.put(locale, words);
        }
        return words;
    }
}

