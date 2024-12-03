/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.pt;

import java.util.Map;
import org.apache.lucene.analysis.pt.RSLPStemmerBase;

public class PortugueseStemmer
extends RSLPStemmerBase {
    private static final RSLPStemmerBase.Step plural;
    private static final RSLPStemmerBase.Step feminine;
    private static final RSLPStemmerBase.Step adverb;
    private static final RSLPStemmerBase.Step augmentative;
    private static final RSLPStemmerBase.Step noun;
    private static final RSLPStemmerBase.Step verb;
    private static final RSLPStemmerBase.Step vowel;

    public int stem(char[] s, int len) {
        assert (s.length >= len + 1) : "this stemmer requires an oversized array of at least 1";
        len = plural.apply(s, len);
        len = adverb.apply(s, len);
        len = feminine.apply(s, len);
        int oldlen = len = augmentative.apply(s, len);
        if ((len = noun.apply(s, len)) == oldlen) {
            oldlen = len;
            if ((len = verb.apply(s, len)) == oldlen) {
                len = vowel.apply(s, len);
            }
        }
        block10: for (int i = 0; i < len; ++i) {
            switch (s[i]) {
                case '\u00e0': 
                case '\u00e1': 
                case '\u00e2': 
                case '\u00e3': 
                case '\u00e4': 
                case '\u00e5': {
                    s[i] = 97;
                    continue block10;
                }
                case '\u00e7': {
                    s[i] = 99;
                    continue block10;
                }
                case '\u00e8': 
                case '\u00e9': 
                case '\u00ea': 
                case '\u00eb': {
                    s[i] = 101;
                    continue block10;
                }
                case '\u00ec': 
                case '\u00ed': 
                case '\u00ee': 
                case '\u00ef': {
                    s[i] = 105;
                    continue block10;
                }
                case '\u00f1': {
                    s[i] = 110;
                    continue block10;
                }
                case '\u00f2': 
                case '\u00f3': 
                case '\u00f4': 
                case '\u00f5': 
                case '\u00f6': {
                    s[i] = 111;
                    continue block10;
                }
                case '\u00f9': 
                case '\u00fa': 
                case '\u00fb': 
                case '\u00fc': {
                    s[i] = 117;
                    continue block10;
                }
                case '\u00fd': 
                case '\u00ff': {
                    s[i] = 121;
                }
            }
        }
        return len;
    }

    static {
        Map<String, RSLPStemmerBase.Step> steps = PortugueseStemmer.parse(PortugueseStemmer.class, "portuguese.rslp");
        plural = steps.get("Plural");
        feminine = steps.get("Feminine");
        adverb = steps.get("Adverb");
        augmentative = steps.get("Augmentative");
        noun = steps.get("Noun");
        verb = steps.get("Verb");
        vowel = steps.get("Vowel");
    }
}

