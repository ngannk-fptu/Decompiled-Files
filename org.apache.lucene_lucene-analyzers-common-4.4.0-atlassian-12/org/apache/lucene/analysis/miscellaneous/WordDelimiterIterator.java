/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.miscellaneous;

import org.apache.lucene.analysis.miscellaneous.WordDelimiterFilter;

public final class WordDelimiterIterator {
    public static final int DONE = -1;
    public static final byte[] DEFAULT_WORD_DELIM_TABLE;
    char[] text;
    int length;
    int startBounds;
    int endBounds;
    int current;
    int end;
    private boolean hasFinalPossessive = false;
    final boolean splitOnCaseChange;
    final boolean splitOnNumerics;
    final boolean stemEnglishPossessive;
    private final byte[] charTypeTable;
    private boolean skipPossessive = false;

    WordDelimiterIterator(byte[] charTypeTable, boolean splitOnCaseChange, boolean splitOnNumerics, boolean stemEnglishPossessive) {
        this.charTypeTable = charTypeTable;
        this.splitOnCaseChange = splitOnCaseChange;
        this.splitOnNumerics = splitOnNumerics;
        this.stemEnglishPossessive = stemEnglishPossessive;
    }

    int next() {
        int type;
        this.current = this.end;
        if (this.current == -1) {
            return -1;
        }
        if (this.skipPossessive) {
            this.current += 2;
            this.skipPossessive = false;
        }
        int lastType = 0;
        while (this.current < this.endBounds && WordDelimiterFilter.isSubwordDelim(lastType = this.charType(this.text[this.current]))) {
            ++this.current;
        }
        if (this.current >= this.endBounds) {
            this.end = -1;
            return -1;
        }
        this.end = this.current + 1;
        while (this.end < this.endBounds && !this.isBreak(lastType, type = this.charType(this.text[this.end]))) {
            lastType = type;
            ++this.end;
        }
        if (this.end < this.endBounds - 1 && this.endsWithPossessive(this.end + 2)) {
            this.skipPossessive = true;
        }
        return this.end;
    }

    int type() {
        if (this.end == -1) {
            return 0;
        }
        int type = this.charType(this.text[this.current]);
        switch (type) {
            case 1: 
            case 2: {
                return 3;
            }
        }
        return type;
    }

    void setText(char[] text, int length) {
        this.text = text;
        this.length = this.endBounds = length;
        this.end = 0;
        this.startBounds = 0;
        this.current = 0;
        this.hasFinalPossessive = false;
        this.skipPossessive = false;
        this.setBounds();
    }

    private boolean isBreak(int lastType, int type) {
        if ((type & lastType) != 0) {
            return false;
        }
        if (!this.splitOnCaseChange && WordDelimiterFilter.isAlpha(lastType) && WordDelimiterFilter.isAlpha(type)) {
            return false;
        }
        if (WordDelimiterFilter.isUpper(lastType) && WordDelimiterFilter.isAlpha(type)) {
            return false;
        }
        return this.splitOnNumerics || (!WordDelimiterFilter.isAlpha(lastType) || !WordDelimiterFilter.isDigit(type)) && (!WordDelimiterFilter.isDigit(lastType) || !WordDelimiterFilter.isAlpha(type));
    }

    boolean isSingleWord() {
        if (this.hasFinalPossessive) {
            return this.current == this.startBounds && this.end == this.endBounds - 2;
        }
        return this.current == this.startBounds && this.end == this.endBounds;
    }

    private void setBounds() {
        while (this.startBounds < this.length && WordDelimiterFilter.isSubwordDelim(this.charType(this.text[this.startBounds]))) {
            ++this.startBounds;
        }
        while (this.endBounds > this.startBounds && WordDelimiterFilter.isSubwordDelim(this.charType(this.text[this.endBounds - 1]))) {
            --this.endBounds;
        }
        if (this.endsWithPossessive(this.endBounds)) {
            this.hasFinalPossessive = true;
        }
        this.current = this.startBounds;
    }

    private boolean endsWithPossessive(int pos) {
        return !(!this.stemEnglishPossessive || pos <= 2 || this.text[pos - 2] != '\'' || this.text[pos - 1] != 's' && this.text[pos - 1] != 'S' || !WordDelimiterFilter.isAlpha(this.charType(this.text[pos - 3])) || pos != this.endBounds && !WordDelimiterFilter.isSubwordDelim(this.charType(this.text[pos])));
    }

    private int charType(int ch) {
        if (ch < this.charTypeTable.length) {
            return this.charTypeTable[ch];
        }
        return WordDelimiterIterator.getType(ch);
    }

    public static byte getType(int ch) {
        switch (Character.getType(ch)) {
            case 1: {
                return 2;
            }
            case 2: {
                return 1;
            }
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: {
                return 3;
            }
            case 9: 
            case 10: 
            case 11: {
                return 4;
            }
            case 19: {
                return 7;
            }
        }
        return 8;
    }

    static {
        byte[] tab = new byte[256];
        for (int i = 0; i < 256; ++i) {
            byte code = 0;
            if (Character.isLowerCase(i)) {
                code = (byte)(code | 1);
            } else if (Character.isUpperCase(i)) {
                code = (byte)(code | 2);
            } else if (Character.isDigit(i)) {
                code = (byte)(code | 4);
            }
            if (code == 0) {
                code = (byte)8;
            }
            tab[i] = code;
        }
        DEFAULT_WORD_DELIM_TABLE = tab;
    }
}

