/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.component.word;

import com.octo.captcha.component.word.SizeSortedWordList;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.TreeMap;

public class DefaultSizeSortedWordList
implements SizeSortedWordList {
    private TreeMap sortedWords = new TreeMap();
    private Locale locale;
    private Random myRandom = new SecureRandom();

    public DefaultSizeSortedWordList(Locale locale) {
        this.locale = locale;
    }

    @Override
    public Locale getLocale() {
        return this.locale;
    }

    @Override
    public void addWord(String word) {
        Integer length = new Integer(word.length());
        if (this.sortedWords.containsKey(length)) {
            ArrayList thisLengthWords = (ArrayList)this.sortedWords.get(length);
            thisLengthWords.add(word);
            this.sortedWords.put(length, thisLengthWords);
        } else {
            ArrayList<String> thisLengthWords = new ArrayList<String>();
            thisLengthWords.add(word);
            this.sortedWords.put(length, thisLengthWords);
        }
    }

    @Override
    public Integer getMinWord() {
        return (Integer)this.sortedWords.firstKey();
    }

    @Override
    public Integer getMaxWord() {
        return (Integer)this.sortedWords.lastKey();
    }

    @Override
    public String getNextWord(Integer length) {
        if (this.sortedWords.containsKey(length)) {
            ArrayList thisLengthwords = (ArrayList)this.sortedWords.get(length);
            int pickAWord = this.myRandom.nextInt(thisLengthwords.size());
            return (String)thisLengthwords.get(pickAWord);
        }
        return null;
    }
}

