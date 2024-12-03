/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 */
package com.google.template.soy.internal.i18n;

import com.google.common.annotations.VisibleForTesting;
import com.google.template.soy.data.Dir;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.lang.UScript;
import com.ibm.icu.util.ULocale;

public class BidiUtils {
    public static final String RIGHT = "right";
    public static final String LEFT = "left";

    private BidiUtils() {
    }

    public static Dir languageDir(ULocale locale) {
        return BidiUtils.isRtlLanguage(locale) ? Dir.RTL : Dir.LTR;
    }

    public static Dir languageDir(String locale) {
        return BidiUtils.isRtlLanguage(locale) ? Dir.RTL : Dir.LTR;
    }

    public static boolean isRtlLanguage(ULocale locale) {
        return UScript.isRightToLeft(UScript.getCodeFromName(ULocale.addLikelySubtags(locale).getScript()));
    }

    public static boolean isRtlLanguage(String locale) {
        return BidiUtils.isRtlLanguage(new ULocale(locale));
    }

    public static boolean hasAnyLtr(String str, boolean isHtml) {
        return new DirectionalityEstimator(str, isHtml).hasAnyLtr(false);
    }

    public static boolean hasAnyLtr(String str) {
        return BidiUtils.hasAnyLtr(str, false);
    }

    public static boolean hasAnyRtl(String str, boolean isHtml) {
        return new DirectionalityEstimator(str, isHtml).hasAnyRtl(false);
    }

    public static boolean hasAnyRtl(String str) {
        return BidiUtils.hasAnyRtl(str, false);
    }

    public static Dir getUnicodeDir(String str, boolean isHtml) {
        return new DirectionalityEstimator(str, isHtml).getUnicodeDir();
    }

    public static Dir getUnicodeDir(String str) {
        return BidiUtils.getUnicodeDir(str, false);
    }

    public static Dir getEntryDir(String str, boolean isHtml) {
        return new DirectionalityEstimator(str, isHtml).getEntryDir();
    }

    public static Dir getEntryDir(String str) {
        return BidiUtils.getEntryDir(str, false);
    }

    public static Dir getExitDir(String str, boolean isHtml) {
        return new DirectionalityEstimator(str, isHtml).getExitDir();
    }

    public static Dir getExitDir(String str) {
        return BidiUtils.getExitDir(str, false);
    }

    public static Dir estimateDirection(String str) {
        return BidiUtils.estimateDirection(str, false);
    }

    public static Dir estimateDirection(String str, boolean isHtml) {
        return new DirectionalityEstimator(str, isHtml).estimateDirectionByWordCount();
    }

    @VisibleForTesting
    static class DirectionalityEstimator {
        private static final int DIR_TYPE_CACHE_SIZE = 1792;
        private static final byte[] DIR_TYPE_CACHE = new byte[1792];
        private static final double RTL_THRESHOLD = 0.4;
        private final String text;
        private final boolean isHtml;
        private final int length;
        private int charIndex;
        private char lastChar;
        private int ltrWordCount;
        private int rtlWordCount;
        private int urlWordCount;
        private int enWordCount;
        private int signedEnWordCount;
        private int plusAnWordCount;
        private int minusAnWordCount;
        private int wordType;

        DirectionalityEstimator(String text, boolean isHtml) {
            this.text = text;
            this.isHtml = isHtml;
            this.length = text.length();
        }

        boolean hasAnyLtr(boolean countEmbedding) {
            this.charIndex = 0;
            int embeddingLevel = 0;
            while (this.charIndex < this.length) {
                switch (this.dirTypeForward()) {
                    case 0: {
                        if (embeddingLevel != 0) break;
                        return true;
                    }
                    case 11: 
                    case 12: {
                        if (!countEmbedding || embeddingLevel++ != 0) break;
                        return true;
                    }
                    case 14: 
                    case 15: {
                        if (!countEmbedding) break;
                        ++embeddingLevel;
                        break;
                    }
                    case 16: {
                        if (!countEmbedding) break;
                        --embeddingLevel;
                    }
                }
            }
            return false;
        }

        boolean hasAnyRtl(boolean countEmbedding) {
            this.charIndex = 0;
            int embeddingLevel = 0;
            while (this.charIndex < this.length) {
                switch (this.dirTypeForward()) {
                    case 1: 
                    case 13: {
                        if (embeddingLevel != 0) break;
                        return true;
                    }
                    case 14: 
                    case 15: {
                        if (!countEmbedding || embeddingLevel++ != 0) break;
                        return true;
                    }
                    case 11: 
                    case 12: {
                        if (!countEmbedding) break;
                        ++embeddingLevel;
                        break;
                    }
                    case 16: {
                        if (!countEmbedding) break;
                        --embeddingLevel;
                    }
                }
            }
            return false;
        }

        Dir getUnicodeDir() {
            this.charIndex = 0;
            while (this.charIndex < this.length) {
                switch (this.dirTypeForward()) {
                    case 0: {
                        return Dir.LTR;
                    }
                    case 1: 
                    case 13: {
                        return Dir.RTL;
                    }
                }
            }
            return Dir.NEUTRAL;
        }

        Dir getEntryDir() {
            this.charIndex = 0;
            int embeddingLevel = 0;
            Dir embeddingLevelDir = null;
            int firstNonEmptyEmbeddingLevel = 0;
            block13: while (this.charIndex < this.length && firstNonEmptyEmbeddingLevel == 0) {
                switch (this.dirTypeForward()) {
                    case 11: 
                    case 12: {
                        ++embeddingLevel;
                        embeddingLevelDir = Dir.LTR;
                        continue block13;
                    }
                    case 14: 
                    case 15: {
                        ++embeddingLevel;
                        embeddingLevelDir = Dir.RTL;
                        continue block13;
                    }
                    case 16: {
                        --embeddingLevel;
                        embeddingLevelDir = null;
                        continue block13;
                    }
                    case 18: {
                        continue block13;
                    }
                    case 0: {
                        if (embeddingLevel == 0) {
                            return Dir.LTR;
                        }
                        firstNonEmptyEmbeddingLevel = embeddingLevel;
                        continue block13;
                    }
                    case 1: 
                    case 13: {
                        if (embeddingLevel == 0) {
                            return Dir.RTL;
                        }
                        firstNonEmptyEmbeddingLevel = embeddingLevel;
                        continue block13;
                    }
                }
                firstNonEmptyEmbeddingLevel = embeddingLevel;
            }
            if (firstNonEmptyEmbeddingLevel == 0) {
                return Dir.NEUTRAL;
            }
            if (embeddingLevelDir != null) {
                return embeddingLevelDir;
            }
            while (this.charIndex > 0) {
                switch (this.dirTypeBackward()) {
                    case 11: 
                    case 12: {
                        if (firstNonEmptyEmbeddingLevel == embeddingLevel) {
                            return Dir.LTR;
                        }
                        --embeddingLevel;
                        break;
                    }
                    case 14: 
                    case 15: {
                        if (firstNonEmptyEmbeddingLevel == embeddingLevel) {
                            return Dir.RTL;
                        }
                        --embeddingLevel;
                        break;
                    }
                    case 16: {
                        ++embeddingLevel;
                    }
                }
            }
            return Dir.NEUTRAL;
        }

        Dir getExitDir() {
            this.charIndex = this.length;
            int embeddingLevel = 0;
            int lastNonEmptyEmbeddingLevel = 0;
            block8: while (this.charIndex > 0) {
                switch (this.dirTypeBackward()) {
                    case 0: {
                        if (embeddingLevel == 0) {
                            return Dir.LTR;
                        }
                        if (lastNonEmptyEmbeddingLevel != 0) continue block8;
                        lastNonEmptyEmbeddingLevel = embeddingLevel;
                        continue block8;
                    }
                    case 11: 
                    case 12: {
                        if (lastNonEmptyEmbeddingLevel == embeddingLevel) {
                            return Dir.LTR;
                        }
                        --embeddingLevel;
                        continue block8;
                    }
                    case 1: 
                    case 13: {
                        if (embeddingLevel == 0) {
                            return Dir.RTL;
                        }
                        if (lastNonEmptyEmbeddingLevel != 0) continue block8;
                        lastNonEmptyEmbeddingLevel = embeddingLevel;
                        continue block8;
                    }
                    case 14: 
                    case 15: {
                        if (lastNonEmptyEmbeddingLevel == embeddingLevel) {
                            return Dir.RTL;
                        }
                        --embeddingLevel;
                        continue block8;
                    }
                    case 16: {
                        ++embeddingLevel;
                        continue block8;
                    }
                    case 18: {
                        continue block8;
                    }
                }
                if (lastNonEmptyEmbeddingLevel != 0) continue;
                lastNonEmptyEmbeddingLevel = embeddingLevel;
            }
            return Dir.NEUTRAL;
        }

        Dir estimateDirectionByWordCount() {
            this.charIndex = 0;
            this.ltrWordCount = 0;
            this.rtlWordCount = 0;
            this.urlWordCount = 0;
            this.enWordCount = 0;
            this.signedEnWordCount = 0;
            this.plusAnWordCount = 0;
            this.minusAnWordCount = 0;
            int embedLevel = 0;
            this.wordType = 0;
            while (this.charIndex < this.length) {
                byte dirType = this.dirTypeForward();
                if (dirType == 0) {
                    this.processStrong(false);
                    continue;
                }
                block0 : switch (dirType) {
                    case 1: 
                    case 13: {
                        this.processStrong(true);
                        break;
                    }
                    case 2: {
                        this.processEuropeanDigit();
                        break;
                    }
                    case 5: {
                        this.processArabicDigit();
                        break;
                    }
                    case 3: {
                        if (this.wordType >= 8) break;
                        if (this.wordType <= 2) {
                            switch (this.lastChar) {
                                case '+': 
                                case '\u207a': 
                                case '\u208a': 
                                case '\ufb29': 
                                case '\ufe62': 
                                case '\uff0b': {
                                    this.wordType = 1;
                                    break block0;
                                }
                            }
                            this.wordType = 2;
                            break;
                        }
                        this.wordType = 0;
                        break;
                    }
                    case 6: {
                        if (this.wordType >= 8 || this.wordType > 2 && this.lastChar != '/') break;
                        this.wordType = 0;
                        break;
                    }
                    case 4: 
                    case 10: {
                        if (this.wordType >= 8) break;
                        this.wordType = 0;
                        break;
                    }
                    case 8: 
                    case 9: {
                        if (this.wordType >= 10) break;
                        this.wordType = 0;
                        break;
                    }
                    case 7: {
                        embedLevel = 0;
                        this.wordType = 0;
                        break;
                    }
                    case 12: {
                        this.processStrong(false);
                    }
                    case 11: {
                        if (embedLevel++ != 0) break;
                        this.wordType = 10;
                        break;
                    }
                    case 15: {
                        this.processStrong(true);
                    }
                    case 14: {
                        if (embedLevel++ != 0) break;
                        this.wordType = 10;
                        break;
                    }
                    case 16: {
                        if (--embedLevel != 0) break;
                        this.wordType = 0;
                        break;
                    }
                }
            }
            return this.compareCounts();
        }

        Dir compareCounts() {
            if ((double)this.rtlWordCount > (double)(this.ltrWordCount + this.rtlWordCount) * 0.4) {
                return Dir.RTL;
            }
            if (this.ltrWordCount + this.urlWordCount + this.signedEnWordCount > 0 || this.enWordCount > 1) {
                return Dir.LTR;
            }
            if (this.minusAnWordCount > 0) {
                return Dir.RTL;
            }
            if (this.plusAnWordCount > 0) {
                return Dir.LTR;
            }
            return Dir.NEUTRAL;
        }

        private void processStrong(boolean isRtl) {
            if (this.wordType >= 8) {
                return;
            }
            switch (this.wordType) {
                case 0: {
                    if (isRtl || this.lastChar != 'h' || !this.matchForward("ttp://", true) && !this.matchForward("ttps://", true)) break;
                    this.wordType = 9;
                    ++this.urlWordCount;
                    return;
                }
                case 5: {
                    --this.signedEnWordCount;
                    break;
                }
                case 6: {
                    --this.plusAnWordCount;
                    break;
                }
                case 7: {
                    --this.minusAnWordCount;
                    break;
                }
                case 3: {
                    --this.enWordCount;
                    break;
                }
            }
            this.wordType = 8;
            if (isRtl) {
                ++this.rtlWordCount;
            } else {
                ++this.ltrWordCount;
            }
        }

        private void processEuropeanDigit() {
            switch (this.wordType) {
                case 0: {
                    ++this.enWordCount;
                    this.wordType = 3;
                    break;
                }
                case 1: 
                case 2: {
                    ++this.signedEnWordCount;
                    this.wordType = 5;
                    break;
                }
            }
        }

        private void processArabicDigit() {
            switch (this.wordType) {
                case 0: {
                    this.wordType = 4;
                    break;
                }
                case 1: {
                    ++this.plusAnWordCount;
                    this.wordType = 6;
                    break;
                }
                case 2: {
                    ++this.minusAnWordCount;
                    this.wordType = 7;
                    break;
                }
            }
        }

        @VisibleForTesting
        boolean matchForward(String match, boolean advance) {
            int matchLength = match.length();
            if (matchLength > this.length - this.charIndex) {
                return false;
            }
            for (int checkIndex = 0; checkIndex < matchLength; ++checkIndex) {
                if (this.text.charAt(this.charIndex + checkIndex) == match.charAt(checkIndex)) continue;
                return false;
            }
            if (advance) {
                this.charIndex += matchLength;
            }
            return true;
        }

        private static byte getCachedDirectionality(char c) {
            return c < '\u0700' ? DIR_TYPE_CACHE[c] : UCharacter.getDirectionality(c);
        }

        @VisibleForTesting
        byte dirTypeForward() {
            this.lastChar = this.text.charAt(this.charIndex);
            if (UCharacter.isHighSurrogate((char)this.lastChar)) {
                int codePoint = UCharacter.codePointAt(this.text, this.charIndex);
                this.charIndex += UCharacter.charCount(codePoint);
                return UCharacter.getDirectionality(codePoint);
            }
            ++this.charIndex;
            byte dirType = DirectionalityEstimator.getCachedDirectionality(this.lastChar);
            if (this.isHtml) {
                if (this.lastChar == '<') {
                    dirType = this.skipTagForward();
                } else if (this.lastChar == '&') {
                    dirType = this.skipEntityForward();
                }
            }
            return dirType;
        }

        @VisibleForTesting
        byte dirTypeBackward() {
            this.lastChar = this.text.charAt(this.charIndex - 1);
            if (UCharacter.isLowSurrogate((char)this.lastChar)) {
                int codePoint = UCharacter.codePointBefore(this.text, this.charIndex);
                this.charIndex -= UCharacter.charCount(codePoint);
                return UCharacter.getDirectionality(codePoint);
            }
            --this.charIndex;
            byte dirType = DirectionalityEstimator.getCachedDirectionality(this.lastChar);
            if (this.isHtml) {
                if (this.lastChar == '>') {
                    dirType = this.skipTagBackward();
                } else if (this.lastChar == ';') {
                    dirType = this.skipEntityBackward();
                }
            }
            return dirType;
        }

        private byte skipTagForward() {
            int initialCharIndex = this.charIndex;
            while (this.charIndex < this.length) {
                this.lastChar = this.text.charAt(this.charIndex++);
                if (this.lastChar == '>') {
                    return 18;
                }
                if (this.lastChar != '\"' && this.lastChar != '\'') continue;
                char quote = this.lastChar;
                while (this.charIndex < this.length && (this.lastChar = this.text.charAt(this.charIndex++)) != quote) {
                }
            }
            this.charIndex = initialCharIndex;
            this.lastChar = (char)60;
            return 10;
        }

        private byte skipTagBackward() {
            int initialCharIndex = this.charIndex;
            while (this.charIndex > 0) {
                this.lastChar = this.text.charAt(--this.charIndex);
                if (this.lastChar == '<') {
                    return 18;
                }
                if (this.lastChar == '>') break;
                if (this.lastChar != '\"' && this.lastChar != '\'') continue;
                char quote = this.lastChar;
                while (this.charIndex > 0 && (this.lastChar = this.text.charAt(--this.charIndex)) != quote) {
                }
            }
            this.charIndex = initialCharIndex;
            this.lastChar = (char)62;
            return 10;
        }

        private byte skipEntityForward() {
            while (this.charIndex < this.length && (this.lastChar = this.text.charAt(this.charIndex++)) != ';') {
            }
            return 9;
        }

        private byte skipEntityBackward() {
            int initialCharIndex = this.charIndex;
            while (this.charIndex > 0) {
                this.lastChar = this.text.charAt(--this.charIndex);
                if (this.lastChar == '&') {
                    return 9;
                }
                if (this.lastChar != ';') continue;
            }
            this.charIndex = initialCharIndex;
            this.lastChar = (char)59;
            return 10;
        }

        static {
            for (int i = 0; i < 1792; ++i) {
                DirectionalityEstimator.DIR_TYPE_CACHE[i] = UCharacter.getDirectionality(i);
            }
        }

        private static class WordType {
            public static final int NEUTRAL = 0;
            public static final int PLUS = 1;
            public static final int MINUS = 2;
            public static final int EN = 3;
            public static final int AN = 4;
            public static final int SIGNED_EN = 5;
            public static final int PLUS_AN = 6;
            public static final int MINUS_AN = 7;
            public static final int STRONG = 8;
            public static final int URL = 9;
            public static final int EMBEDDED = 10;

            private WordType() {
            }
        }
    }

    public static final class Format {
        public static final char LRE = '\u202a';
        public static final char RLE = '\u202b';
        public static final char PDF = '\u202c';
        public static final char LRM = '\u200e';
        public static final char RLM = '\u200f';
        public static final String LRM_STRING = Character.toString('\u200e');
        public static final String RLM_STRING = Character.toString('\u200f');

        private Format() {
        }
    }
}

