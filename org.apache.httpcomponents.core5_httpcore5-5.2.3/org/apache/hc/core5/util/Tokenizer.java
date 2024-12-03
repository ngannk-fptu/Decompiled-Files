/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.util;

import java.util.BitSet;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.util.Args;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public class Tokenizer {
    public static final char DQUOTE = '\"';
    public static final char ESCAPE = '\\';
    public static final int CR = 13;
    public static final int LF = 10;
    public static final int SP = 32;
    public static final int HT = 9;
    public static final Tokenizer INSTANCE = new Tokenizer();

    public static BitSet INIT_BITSET(int ... b) {
        BitSet bitset = new BitSet();
        for (int aB : b) {
            bitset.set(aB);
        }
        return bitset;
    }

    public static boolean isWhitespace(char ch) {
        return ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n';
    }

    public String parseContent(CharSequence buf, Cursor cursor, BitSet delimiters) {
        Args.notNull(buf, "Char sequence");
        Args.notNull(cursor, "Parser cursor");
        StringBuilder dst = new StringBuilder();
        this.copyContent(buf, cursor, delimiters, dst);
        return dst.toString();
    }

    public String parseToken(CharSequence buf, Cursor cursor, BitSet delimiters) {
        Args.notNull(buf, "Char sequence");
        Args.notNull(cursor, "Parser cursor");
        StringBuilder dst = new StringBuilder();
        boolean whitespace = false;
        while (!cursor.atEnd()) {
            char current = buf.charAt(cursor.getPos());
            if (delimiters != null && delimiters.get(current)) break;
            if (Tokenizer.isWhitespace(current)) {
                this.skipWhiteSpace(buf, cursor);
                whitespace = true;
                continue;
            }
            if (whitespace && dst.length() > 0) {
                dst.append(' ');
            }
            this.copyContent(buf, cursor, delimiters, dst);
            whitespace = false;
        }
        return dst.toString();
    }

    public String parseValue(CharSequence buf, Cursor cursor, BitSet delimiters) {
        Args.notNull(buf, "Char sequence");
        Args.notNull(cursor, "Parser cursor");
        StringBuilder dst = new StringBuilder();
        boolean whitespace = false;
        while (!cursor.atEnd()) {
            char current = buf.charAt(cursor.getPos());
            if (delimiters != null && delimiters.get(current)) break;
            if (Tokenizer.isWhitespace(current)) {
                this.skipWhiteSpace(buf, cursor);
                whitespace = true;
                continue;
            }
            if (current == '\"') {
                if (whitespace && dst.length() > 0) {
                    dst.append(' ');
                }
                this.copyQuotedContent(buf, cursor, dst);
                whitespace = false;
                continue;
            }
            if (whitespace && dst.length() > 0) {
                dst.append(' ');
            }
            this.copyUnquotedContent(buf, cursor, delimiters, dst);
            whitespace = false;
        }
        return dst.toString();
    }

    public void skipWhiteSpace(CharSequence buf, Cursor cursor) {
        char current;
        Args.notNull(buf, "Char sequence");
        Args.notNull(cursor, "Parser cursor");
        int pos = cursor.getPos();
        int indexFrom = cursor.getPos();
        int indexTo = cursor.getUpperBound();
        for (int i = indexFrom; i < indexTo && Tokenizer.isWhitespace(current = buf.charAt(i)); ++i) {
            ++pos;
        }
        cursor.updatePos(pos);
    }

    public void copyContent(CharSequence buf, Cursor cursor, BitSet delimiters, StringBuilder dst) {
        Args.notNull(buf, "Char sequence");
        Args.notNull(cursor, "Parser cursor");
        Args.notNull(dst, "String builder");
        int pos = cursor.getPos();
        int indexFrom = cursor.getPos();
        int indexTo = cursor.getUpperBound();
        for (int i = indexFrom; i < indexTo; ++i) {
            char current = buf.charAt(i);
            if (delimiters != null && delimiters.get(current) || Tokenizer.isWhitespace(current)) break;
            ++pos;
            dst.append(current);
        }
        cursor.updatePos(pos);
    }

    public void copyUnquotedContent(CharSequence buf, Cursor cursor, BitSet delimiters, StringBuilder dst) {
        Args.notNull(buf, "Char sequence");
        Args.notNull(cursor, "Parser cursor");
        Args.notNull(dst, "String builder");
        int pos = cursor.getPos();
        int indexFrom = cursor.getPos();
        int indexTo = cursor.getUpperBound();
        for (int i = indexFrom; i < indexTo; ++i) {
            char current = buf.charAt(i);
            if (delimiters != null && delimiters.get(current) || Tokenizer.isWhitespace(current) || current == '\"') break;
            ++pos;
            dst.append(current);
        }
        cursor.updatePos(pos);
    }

    public void copyQuotedContent(CharSequence buf, Cursor cursor, StringBuilder dst) {
        Args.notNull(buf, "Char sequence");
        Args.notNull(cursor, "Parser cursor");
        Args.notNull(dst, "String builder");
        if (cursor.atEnd()) {
            return;
        }
        int pos = cursor.getPos();
        int indexFrom = cursor.getPos();
        int indexTo = cursor.getUpperBound();
        char current = buf.charAt(pos);
        if (current != '\"') {
            return;
        }
        ++pos;
        boolean escaped = false;
        int i = ++indexFrom;
        while (i < indexTo) {
            current = buf.charAt(i);
            if (escaped) {
                if (current != '\"' && current != '\\') {
                    dst.append('\\');
                }
                dst.append(current);
                escaped = false;
            } else {
                if (current == '\"') {
                    ++pos;
                    break;
                }
                if (current == '\\') {
                    escaped = true;
                } else if (current != '\r' && current != '\n') {
                    dst.append(current);
                }
            }
            ++i;
            ++pos;
        }
        cursor.updatePos(pos);
    }

    public static class Cursor {
        private final int lowerBound;
        private final int upperBound;
        private int pos;

        public Cursor(int lowerBound, int upperBound) {
            Args.notNegative(lowerBound, "lowerBound");
            Args.check(lowerBound <= upperBound, "lowerBound cannot be greater than upperBound");
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            this.pos = lowerBound;
        }

        public int getLowerBound() {
            return this.lowerBound;
        }

        public int getUpperBound() {
            return this.upperBound;
        }

        public int getPos() {
            return this.pos;
        }

        public void updatePos(int pos) {
            Args.check(pos >= this.lowerBound, "pos: %s < lowerBound: %s", pos, this.lowerBound);
            Args.check(pos <= this.upperBound, "pos: %s > upperBound: %s", pos, this.upperBound);
            this.pos = pos;
        }

        public boolean atEnd() {
            return this.pos >= this.upperBound;
        }

        public String toString() {
            StringBuilder buffer = new StringBuilder();
            buffer.append('[');
            buffer.append(this.lowerBound);
            buffer.append('>');
            buffer.append(this.pos);
            buffer.append('>');
            buffer.append(this.upperBound);
            buffer.append(']');
            return buffer.toString();
        }
    }
}

