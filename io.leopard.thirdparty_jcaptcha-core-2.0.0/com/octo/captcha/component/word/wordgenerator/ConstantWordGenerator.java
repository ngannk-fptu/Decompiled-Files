/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.CaptchaException
 */
package com.octo.captcha.component.word.wordgenerator;

import com.octo.captcha.CaptchaException;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import java.util.Locale;

public class ConstantWordGenerator
implements WordGenerator {
    String constantString;

    public ConstantWordGenerator(String constantString) {
        this.constantString = constantString;
        if (constantString == null || constantString.isEmpty()) {
            throw new CaptchaException("ConstantWordGenerator must be built with a non empty string");
        }
    }

    @Override
    public String getWord(Integer length) {
        StringBuilder toCut = new StringBuilder(this.constantString);
        while (toCut.length() < length) {
            toCut.append(this.constantString);
        }
        return toCut.substring(0, length);
    }

    @Override
    public String getWord(Integer length, Locale locale) {
        return this.getWord(length);
    }
}

