/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.gl;

import java.util.Map;
import org.apache.lucene.analysis.pt.RSLPStemmerBase;

public class GalicianStemmer
extends RSLPStemmerBase {
    private static final RSLPStemmerBase.Step plural;
    private static final RSLPStemmerBase.Step unification;
    private static final RSLPStemmerBase.Step adverb;
    private static final RSLPStemmerBase.Step augmentative;
    private static final RSLPStemmerBase.Step noun;
    private static final RSLPStemmerBase.Step verb;
    private static final RSLPStemmerBase.Step vowel;

    public int stem(char[] s, int len) {
        int oldlen;
        assert (s.length >= len + 1) : "this stemmer requires an oversized array of at least 1";
        len = plural.apply(s, len);
        len = unification.apply(s, len);
        len = adverb.apply(s, len);
        do {
            oldlen = len;
        } while ((len = augmentative.apply(s, len)) != oldlen);
        oldlen = len;
        if ((len = noun.apply(s, len)) == oldlen) {
            len = verb.apply(s, len);
        }
        len = vowel.apply(s, len);
        block8: for (int i = 0; i < len; ++i) {
            switch (s[i]) {
                case '\u00e1': {
                    s[i] = 97;
                    continue block8;
                }
                case '\u00e9': 
                case '\u00ea': {
                    s[i] = 101;
                    continue block8;
                }
                case '\u00ed': {
                    s[i] = 105;
                    continue block8;
                }
                case '\u00f3': {
                    s[i] = 111;
                    continue block8;
                }
                case '\u00fa': {
                    s[i] = 117;
                }
            }
        }
        return len;
    }

    static {
        Map<String, RSLPStemmerBase.Step> steps = GalicianStemmer.parse(GalicianStemmer.class, "galician.rslp");
        plural = steps.get("Plural");
        unification = steps.get("Unification");
        adverb = steps.get("Adverb");
        augmentative = steps.get("Augmentative");
        noun = steps.get("Noun");
        verb = steps.get("Verb");
        vowel = steps.get("Vowel");
    }
}

