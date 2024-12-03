/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.component.word;

import java.util.Locale;

public interface SizeSortedWordList {
    public Locale getLocale();

    public void addWord(String var1);

    public Integer getMinWord();

    public Integer getMaxWord();

    public String getNextWord(Integer var1);
}

