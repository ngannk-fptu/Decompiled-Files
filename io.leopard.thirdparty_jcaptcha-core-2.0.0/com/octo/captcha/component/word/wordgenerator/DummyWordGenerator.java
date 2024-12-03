/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.component.word.wordgenerator;

import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import java.util.Locale;

public class DummyWordGenerator
implements WordGenerator {
    private String word = "JCAPTCHA";

    public DummyWordGenerator(String word) {
        this.word = word == null || "".equals(word) ? this.word : word;
    }

    @Override
    public String getWord(Integer length) {
        int mod = length % this.word.length();
        String cut = "";
        int mul = (length - mod) / this.word.length();
        if (mod > 0) {
            cut = this.word.substring(0, mod);
        }
        StringBuffer returned = new StringBuffer();
        for (int i = 0; i < mul; ++i) {
            returned.append(this.word);
        }
        returned.append(cut);
        return returned.toString();
    }

    @Override
    public String getWord(Integer length, Locale locale) {
        return this.getWord(length);
    }
}

