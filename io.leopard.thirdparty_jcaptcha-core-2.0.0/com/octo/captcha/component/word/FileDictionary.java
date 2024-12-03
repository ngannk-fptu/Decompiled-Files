/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.component.word;

import com.octo.captcha.component.word.DefaultSizeSortedWordList;
import com.octo.captcha.component.word.DictionaryReader;
import com.octo.captcha.component.word.SizeSortedWordList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

public class FileDictionary
implements DictionaryReader {
    private String myBundle;

    public FileDictionary(String bundle) {
        this.myBundle = bundle;
    }

    @Override
    public SizeSortedWordList getWordList() {
        ResourceBundle bundle = ResourceBundle.getBundle(this.myBundle);
        SizeSortedWordList list = this.generateWordList(Locale.getDefault(), bundle);
        return list;
    }

    @Override
    public SizeSortedWordList getWordList(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(this.myBundle, locale);
        SizeSortedWordList list = this.generateWordList(locale, bundle);
        return list;
    }

    protected SizeSortedWordList generateWordList(Locale locale, ResourceBundle bundle) {
        DefaultSizeSortedWordList list = new DefaultSizeSortedWordList(locale);
        StringTokenizer tokenizer = new StringTokenizer(bundle.getString("words"), ";");
        int count = tokenizer.countTokens();
        for (int i = 0; i < count; ++i) {
            list.addWord(tokenizer.nextToken());
        }
        return list;
    }
}

