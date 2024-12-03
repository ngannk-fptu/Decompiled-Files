/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.CharFilter
 *  org.apache.lucene.analysis.util.RollingCharBuffer
 */
package org.apache.lucene.analysis.ja;

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.CharFilter;
import org.apache.lucene.analysis.util.RollingCharBuffer;

public class JapaneseIterationMarkCharFilter
extends CharFilter {
    public static final boolean NORMALIZE_KANJI_DEFAULT = true;
    public static final boolean NORMALIZE_KANA_DEFAULT = true;
    private static final char KANJI_ITERATION_MARK = '\u3005';
    private static final char HIRAGANA_ITERATION_MARK = '\u309d';
    private static final char HIRAGANA_VOICED_ITERATION_MARK = '\u309e';
    private static final char KATAKANA_ITERATION_MARK = '\u30fd';
    private static final char KATAKANA_VOICED_ITERATION_MARK = '\u30fe';
    private static final char FULL_STOP_PUNCTUATION = '\u3002';
    private static char[] h2d = new char[50];
    private static char[] k2d = new char[50];
    private final RollingCharBuffer buffer = new RollingCharBuffer();
    private int bufferPosition = 0;
    private int iterationMarksSpanSize = 0;
    private int iterationMarkSpanEndPosition = 0;
    private boolean normalizeKanji;
    private boolean normalizeKana;

    public JapaneseIterationMarkCharFilter(Reader input) {
        this(input, true, true);
    }

    public JapaneseIterationMarkCharFilter(Reader input, boolean normalizeKanji, boolean normalizeKana) {
        super(input);
        this.normalizeKanji = normalizeKanji;
        this.normalizeKana = normalizeKana;
        this.buffer.reset(input);
    }

    public int read(char[] buffer, int offset, int length) throws IOException {
        int c;
        int read = 0;
        for (int i = offset; i < offset + length && (c = this.read()) != -1; ++i) {
            buffer[i] = (char)c;
            ++read;
        }
        return read == 0 ? -1 : read;
    }

    public int read() throws IOException {
        int ic = this.buffer.get(this.bufferPosition);
        if (ic == -1) {
            this.buffer.freeBefore(this.bufferPosition);
            return ic;
        }
        char c = (char)ic;
        if (Character.isHighSurrogate(c) || Character.isLowSurrogate(c)) {
            this.iterationMarkSpanEndPosition = this.bufferPosition + 1;
        }
        if (c == '\u3002') {
            this.buffer.freeBefore(this.bufferPosition);
            this.iterationMarkSpanEndPosition = this.bufferPosition + 1;
        }
        if (this.isIterationMark(c)) {
            c = this.normalizeIterationMark(c);
        }
        ++this.bufferPosition;
        return c;
    }

    private char normalizeIterationMark(char c) throws IOException {
        if (this.bufferPosition < this.iterationMarkSpanEndPosition) {
            return this.normalize(this.sourceCharacter(this.bufferPosition, this.iterationMarksSpanSize), c);
        }
        if (this.bufferPosition == this.iterationMarkSpanEndPosition) {
            ++this.iterationMarkSpanEndPosition;
            return c;
        }
        this.iterationMarksSpanSize = this.nextIterationMarkSpanSize();
        this.iterationMarkSpanEndPosition = this.bufferPosition + this.iterationMarksSpanSize;
        return this.normalize(this.sourceCharacter(this.bufferPosition, this.iterationMarksSpanSize), c);
    }

    private int nextIterationMarkSpanSize() throws IOException {
        int spanSize = 0;
        int i = this.bufferPosition;
        while (this.buffer.get(i) != -1 && this.isIterationMark((char)this.buffer.get(i))) {
            ++spanSize;
            ++i;
        }
        if (this.bufferPosition - spanSize < this.iterationMarkSpanEndPosition) {
            spanSize = this.bufferPosition - this.iterationMarkSpanEndPosition;
        }
        return spanSize;
    }

    private char sourceCharacter(int position, int spanSize) throws IOException {
        return (char)this.buffer.get(position - spanSize);
    }

    private char normalize(char c, char m) {
        if (this.isHiraganaIterationMark(m)) {
            return this.normalizedHiragana(c, m);
        }
        if (this.isKatakanaIterationMark(m)) {
            return this.normalizedKatakana(c, m);
        }
        return c;
    }

    private char normalizedHiragana(char c, char m) {
        switch (m) {
            case '\u309d': {
                return this.isHiraganaDakuten(c) ? (char)(c - '\u0001') : c;
            }
            case '\u309e': {
                return this.lookupHiraganaDakuten(c);
            }
        }
        return c;
    }

    private char normalizedKatakana(char c, char m) {
        switch (m) {
            case '\u30fd': {
                return this.isKatakanaDakuten(c) ? (char)(c - '\u0001') : c;
            }
            case '\u30fe': {
                return this.lookupKatakanaDakuten(c);
            }
        }
        return c;
    }

    private boolean isIterationMark(char c) {
        return this.isKanjiIterationMark(c) || this.isHiraganaIterationMark(c) || this.isKatakanaIterationMark(c);
    }

    private boolean isHiraganaIterationMark(char c) {
        if (this.normalizeKana) {
            return c == '\u309d' || c == '\u309e';
        }
        return false;
    }

    private boolean isKatakanaIterationMark(char c) {
        if (this.normalizeKana) {
            return c == '\u30fd' || c == '\u30fe';
        }
        return false;
    }

    private boolean isKanjiIterationMark(char c) {
        if (this.normalizeKanji) {
            return c == '\u3005';
        }
        return false;
    }

    private char lookupHiraganaDakuten(char c) {
        return this.lookup(c, h2d, '\u304b');
    }

    private char lookupKatakanaDakuten(char c) {
        return this.lookup(c, k2d, '\u30ab');
    }

    private boolean isHiraganaDakuten(char c) {
        return this.inside(c, h2d, '\u304b') && c == this.lookupHiraganaDakuten(c);
    }

    private boolean isKatakanaDakuten(char c) {
        return this.inside(c, k2d, '\u30ab') && c == this.lookupKatakanaDakuten(c);
    }

    private char lookup(char c, char[] map, char offset) {
        if (!this.inside(c, map, offset)) {
            return c;
        }
        return map[c - offset];
    }

    private boolean inside(char c, char[] map, char offset) {
        return c >= offset && c < offset + map.length;
    }

    protected int correct(int currentOff) {
        return currentOff;
    }

    static {
        JapaneseIterationMarkCharFilter.h2d[0] = 12364;
        JapaneseIterationMarkCharFilter.h2d[1] = 12364;
        JapaneseIterationMarkCharFilter.h2d[2] = 12366;
        JapaneseIterationMarkCharFilter.h2d[3] = 12366;
        JapaneseIterationMarkCharFilter.h2d[4] = 12368;
        JapaneseIterationMarkCharFilter.h2d[5] = 12368;
        JapaneseIterationMarkCharFilter.h2d[6] = 12370;
        JapaneseIterationMarkCharFilter.h2d[7] = 12370;
        JapaneseIterationMarkCharFilter.h2d[8] = 12372;
        JapaneseIterationMarkCharFilter.h2d[9] = 12372;
        JapaneseIterationMarkCharFilter.h2d[10] = 12374;
        JapaneseIterationMarkCharFilter.h2d[11] = 12374;
        JapaneseIterationMarkCharFilter.h2d[12] = 12376;
        JapaneseIterationMarkCharFilter.h2d[13] = 12376;
        JapaneseIterationMarkCharFilter.h2d[14] = 12378;
        JapaneseIterationMarkCharFilter.h2d[15] = 12378;
        JapaneseIterationMarkCharFilter.h2d[16] = 12380;
        JapaneseIterationMarkCharFilter.h2d[17] = 12380;
        JapaneseIterationMarkCharFilter.h2d[18] = 12382;
        JapaneseIterationMarkCharFilter.h2d[19] = 12382;
        JapaneseIterationMarkCharFilter.h2d[20] = 12384;
        JapaneseIterationMarkCharFilter.h2d[21] = 12384;
        JapaneseIterationMarkCharFilter.h2d[22] = 12386;
        JapaneseIterationMarkCharFilter.h2d[23] = 12386;
        JapaneseIterationMarkCharFilter.h2d[24] = 12387;
        JapaneseIterationMarkCharFilter.h2d[25] = 12389;
        JapaneseIterationMarkCharFilter.h2d[26] = 12389;
        JapaneseIterationMarkCharFilter.h2d[27] = 12391;
        JapaneseIterationMarkCharFilter.h2d[28] = 12391;
        JapaneseIterationMarkCharFilter.h2d[29] = 12393;
        JapaneseIterationMarkCharFilter.h2d[30] = 12393;
        JapaneseIterationMarkCharFilter.h2d[31] = 12394;
        JapaneseIterationMarkCharFilter.h2d[32] = 12395;
        JapaneseIterationMarkCharFilter.h2d[33] = 12396;
        JapaneseIterationMarkCharFilter.h2d[34] = 12397;
        JapaneseIterationMarkCharFilter.h2d[35] = 12398;
        JapaneseIterationMarkCharFilter.h2d[36] = 12400;
        JapaneseIterationMarkCharFilter.h2d[37] = 12400;
        JapaneseIterationMarkCharFilter.h2d[38] = 12401;
        JapaneseIterationMarkCharFilter.h2d[39] = 12403;
        JapaneseIterationMarkCharFilter.h2d[40] = 12403;
        JapaneseIterationMarkCharFilter.h2d[41] = 12404;
        JapaneseIterationMarkCharFilter.h2d[42] = 12406;
        JapaneseIterationMarkCharFilter.h2d[43] = 12406;
        JapaneseIterationMarkCharFilter.h2d[44] = 12407;
        JapaneseIterationMarkCharFilter.h2d[45] = 12409;
        JapaneseIterationMarkCharFilter.h2d[46] = 12409;
        JapaneseIterationMarkCharFilter.h2d[47] = 12410;
        JapaneseIterationMarkCharFilter.h2d[48] = 12412;
        JapaneseIterationMarkCharFilter.h2d[49] = 12412;
        int codePointDifference = 96;
        assert (h2d.length == k2d.length);
        for (int i = 0; i < k2d.length; ++i) {
            JapaneseIterationMarkCharFilter.k2d[i] = (char)(h2d[i] + codePointDifference);
        }
    }
}

