/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.component.word.wordgenerator.WordGenerator
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.security.captcha;

import com.atlassian.confluence.internal.security.captcha.Language;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import java.util.Locale;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CensoringWordGenerator
implements WordGenerator {
    private static final Logger log = LoggerFactory.getLogger(CensoringWordGenerator.class);
    private final WordGenerator censoredWordGenerator;

    public CensoringWordGenerator(WordGenerator censoredWordGenerator) {
        Objects.requireNonNull(censoredWordGenerator, "Censored word generator must not be null");
        this.censoredWordGenerator = censoredWordGenerator;
        log.debug("Using CensoringWordGenerator to censor {}", (Object)censoredWordGenerator.getClass().getName());
    }

    public String getWord(Integer length) {
        Objects.requireNonNull(length);
        return this.getWord(length, Locale.getDefault());
    }

    public String getWord(Integer length, Locale locale) {
        Objects.requireNonNull(length);
        Objects.requireNonNull(locale);
        Language language = Language.forLocale(locale);
        String word = this.censoredWordGenerator.getWord(length, locale);
        for (int ii = 0; ii < 100 && language.isOffensive(word); ++ii) {
            word = this.censoredWordGenerator.getWord(length, locale);
        }
        return word;
    }
}

