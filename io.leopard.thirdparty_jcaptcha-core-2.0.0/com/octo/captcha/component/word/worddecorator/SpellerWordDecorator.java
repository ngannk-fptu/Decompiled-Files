/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.component.word.worddecorator;

import com.octo.captcha.component.word.worddecorator.WordDecorator;

public class SpellerWordDecorator
implements WordDecorator {
    private String separtor;

    public SpellerWordDecorator(String seprator) {
        this.separtor = seprator;
    }

    @Override
    public String decorateWord(String original) {
        StringBuffer chars = new StringBuffer();
        for (int i = 0; i < original.length(); ++i) {
            chars.append(" ");
            chars.append(original.charAt(i));
            if (i >= original.length() - 1) continue;
            chars.append(this.separtor);
        }
        return chars.toString();
    }
}

