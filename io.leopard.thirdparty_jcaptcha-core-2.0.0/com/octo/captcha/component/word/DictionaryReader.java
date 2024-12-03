/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.component.word;

import com.octo.captcha.component.word.SizeSortedWordList;
import java.util.Locale;

public interface DictionaryReader {
    public SizeSortedWordList getWordList();

    public SizeSortedWordList getWordList(Locale var1);
}

