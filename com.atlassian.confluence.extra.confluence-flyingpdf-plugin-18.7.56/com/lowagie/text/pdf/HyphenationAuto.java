/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.HyphenationEvent;
import com.lowagie.text.pdf.hyphenation.Hyphenation;
import com.lowagie.text.pdf.hyphenation.Hyphenator;

public class HyphenationAuto
implements HyphenationEvent {
    protected Hyphenator hyphenator;
    protected String post;

    public HyphenationAuto(String lang, String country, int leftMin, int rightMin) {
        this.hyphenator = new Hyphenator(lang, country, leftMin, rightMin);
    }

    @Override
    public String getHyphenSymbol() {
        return "-";
    }

    @Override
    public String getHyphenatedWordPre(String word, BaseFont font, float fontSize, float remainingWidth) {
        int k;
        this.post = word;
        String hyphen = this.getHyphenSymbol();
        float hyphenWidth = font.getWidthPoint(hyphen, fontSize);
        if (hyphenWidth > remainingWidth) {
            return "";
        }
        Hyphenation hyphenation = this.hyphenator.hyphenate(word);
        if (hyphenation == null) {
            return "";
        }
        int len = hyphenation.length();
        for (k = 0; k < len && !(font.getWidthPoint(hyphenation.getPreHyphenText(k), fontSize) + hyphenWidth > remainingWidth); ++k) {
        }
        if (--k < 0) {
            return "";
        }
        this.post = hyphenation.getPostHyphenText(k);
        return hyphenation.getPreHyphenText(k) + hyphen;
    }

    @Override
    public String getHyphenatedWordPost() {
        return this.post;
    }
}

