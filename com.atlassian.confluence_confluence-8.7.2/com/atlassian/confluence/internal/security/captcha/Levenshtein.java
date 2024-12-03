/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.security.captcha;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

final class Levenshtein {
    private Levenshtein() {
    }

    static Set<String> nearbyWords(String word, CharSequence alphabet) {
        Objects.requireNonNull(word, "Word must not be null");
        Objects.requireNonNull(alphabet, "Alphabet must not be null");
        HashSet<String> nearbySet = new HashSet<String>();
        Levenshtein.oneCharSubstitutions(word, alphabet, nearbySet);
        Levenshtein.oneCharDeletions(word, nearbySet);
        Levenshtein.oneCharAdditions(word, alphabet, nearbySet);
        return nearbySet;
    }

    private static void oneCharSubstitutions(String seedWord, CharSequence alphabet, Set<String> bucket) {
        for (int i = 0; i < seedWord.length(); ++i) {
            String prefix = seedWord.substring(0, i);
            String postfix = seedWord.substring(i + 1);
            Levenshtein.sandwichAlphas(prefix, postfix, alphabet, bucket);
        }
    }

    private static void oneCharDeletions(String seedWord, Set<String> bucket) {
        for (int i = 0; i < seedWord.length(); ++i) {
            String prefix = seedWord.substring(0, i);
            String postfix = seedWord.substring(i + 1);
            bucket.add(prefix + postfix);
        }
    }

    private static void oneCharAdditions(String seedWord, CharSequence alphabet, Set<String> bucket) {
        for (int i = 0; i <= seedWord.length(); ++i) {
            String prefix = seedWord.substring(0, i);
            String postfix = seedWord.substring(i);
            Levenshtein.sandwichAlphas(prefix, postfix, alphabet, bucket);
        }
    }

    private static void sandwichAlphas(String prefix, String postfix, CharSequence alphabet, Set<String> bucket) {
        for (int j = 0; j < alphabet.length(); ++j) {
            bucket.add(prefix + alphabet.charAt(j) + postfix);
        }
    }
}

