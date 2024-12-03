/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.NotThreadSafe
 */
package com.atlassian.confluence.internal.security.captcha;

import com.atlassian.confluence.internal.security.captcha.Levenshtein;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
final class Language {
    private static final String BASE_NAME = "com.atlassian.confluence.internal.security.captcha.Language";
    private static final Map<Locale, Language> cache = new HashMap<Locale, Language>();
    private final CharSequence alphabet;
    private final Set<String> offensiveWords;

    private Language(CharSequence alphabet, Set<String> offensiveWords) {
        this.alphabet = alphabet;
        this.offensiveWords = offensiveWords;
    }

    static Language forLocale(Locale locale) {
        if (!cache.containsKey(locale)) {
            ResourceBundle.Control propOnlyControl = ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_PROPERTIES);
            ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale, propOnlyControl);
            String alphabet = bundle.getString("alphabet");
            String encodedOffensiveWords = bundle.getString("offensive");
            List offensiveWords = Arrays.stream(encodedOffensiveWords.split(";")).map(Base64.getDecoder()::decode).map(bytes -> new String((byte[])bytes, StandardCharsets.UTF_8)).collect(Collectors.toList());
            cache.put(locale, new Language(alphabet, new HashSet<String>(offensiveWords)));
        }
        return cache.get(locale);
    }

    boolean isOffensive(String word) {
        Objects.requireNonNull(word);
        String lowerCaseWord = word.trim().toLowerCase();
        if (this.offensiveWords.stream().anyMatch(badWord -> lowerCaseWord.startsWith((String)badWord) || lowerCaseWord.endsWith((String)badWord))) {
            return true;
        }
        return Levenshtein.nearbyWords(lowerCaseWord, this.alphabet).stream().anyMatch(this.offensiveWords::contains);
    }
}

