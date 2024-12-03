/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.reverse;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

public final class ReverseStringFilter
extends TokenFilter {
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final char marker;
    private final Version matchVersion;
    private static final char NOMARKER = '\uffff';
    public static final char START_OF_HEADING_MARKER = '\u0001';
    public static final char INFORMATION_SEPARATOR_MARKER = '\u001f';
    public static final char PUA_EC00_MARKER = '\uec00';
    public static final char RTL_DIRECTION_MARKER = '\u200f';

    public ReverseStringFilter(Version matchVersion, TokenStream in) {
        this(matchVersion, in, '\uffff');
    }

    public ReverseStringFilter(Version matchVersion, TokenStream in, char marker) {
        super(in);
        this.matchVersion = matchVersion;
        this.marker = marker;
    }

    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            int len = this.termAtt.length();
            if (this.marker != '\uffff') {
                this.termAtt.resizeBuffer(++len);
                this.termAtt.buffer()[len - 1] = this.marker;
            }
            ReverseStringFilter.reverse(this.matchVersion, this.termAtt.buffer(), 0, len);
            this.termAtt.setLength(len);
            return true;
        }
        return false;
    }

    public static String reverse(Version matchVersion, String input) {
        char[] charInput = input.toCharArray();
        ReverseStringFilter.reverse(matchVersion, charInput, 0, charInput.length);
        return new String(charInput);
    }

    public static void reverse(Version matchVersion, char[] buffer) {
        ReverseStringFilter.reverse(matchVersion, buffer, 0, buffer.length);
    }

    public static void reverse(Version matchVersion, char[] buffer, int len) {
        ReverseStringFilter.reverse(matchVersion, buffer, 0, len);
    }

    @Deprecated
    private static void reverseUnicode3(char[] buffer, int start, int len) {
        if (len <= 1) {
            return;
        }
        int num = len >> 1;
        for (int i = start; i < start + num; ++i) {
            char c = buffer[i];
            buffer[i] = buffer[start * 2 + len - i - 1];
            buffer[start * 2 + len - i - 1] = c;
        }
    }

    public static void reverse(Version matchVersion, char[] buffer, int start, int len) {
        if (!matchVersion.onOrAfter(Version.LUCENE_31)) {
            ReverseStringFilter.reverseUnicode3(buffer, start, len);
            return;
        }
        if (len < 2) {
            return;
        }
        int end = start + len - 1;
        char frontHigh = buffer[start];
        char endLow = buffer[end];
        boolean allowFrontSur = true;
        boolean allowEndSur = true;
        int mid = start + (len >> 1);
        int i = start;
        while (i < mid) {
            boolean surAtFront;
            char frontLow = buffer[i + 1];
            char endHigh = buffer[end - 1];
            boolean bl = surAtFront = allowFrontSur && Character.isSurrogatePair(frontHigh, frontLow);
            if (surAtFront && len < 3) {
                return;
            }
            boolean surAtEnd = allowEndSur && Character.isSurrogatePair(endHigh, endLow);
            allowEndSur = true;
            allowFrontSur = true;
            if (surAtFront == surAtEnd) {
                if (surAtFront) {
                    buffer[end] = frontLow;
                    buffer[--end] = frontHigh;
                    buffer[i] = endHigh;
                    buffer[++i] = endLow;
                    frontHigh = buffer[i + 1];
                    endLow = buffer[end - 1];
                } else {
                    buffer[end] = frontHigh;
                    buffer[i] = endLow;
                    frontHigh = frontLow;
                    endLow = endHigh;
                }
            } else if (surAtFront) {
                buffer[end] = frontLow;
                buffer[i] = endLow;
                endLow = endHigh;
                allowFrontSur = false;
            } else {
                buffer[end] = frontHigh;
                buffer[i] = endHigh;
                frontHigh = frontLow;
                allowEndSur = false;
            }
            ++i;
            --end;
        }
        if (!((len & 1) != 1 || allowFrontSur && allowEndSur)) {
            buffer[end] = allowFrontSur ? endLow : frontHigh;
        }
    }
}

