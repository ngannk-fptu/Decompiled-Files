/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.ja;

import org.apache.lucene.analysis.ja.JapaneseTokenizer;
import org.apache.lucene.analysis.ja.dict.Dictionary;

public class Token {
    private final Dictionary dictionary;
    private final int wordId;
    private final char[] surfaceForm;
    private final int offset;
    private final int length;
    private final int position;
    private int positionLength;
    private final JapaneseTokenizer.Type type;

    public Token(int wordId, char[] surfaceForm, int offset, int length, JapaneseTokenizer.Type type, int position, Dictionary dictionary) {
        this.wordId = wordId;
        this.surfaceForm = surfaceForm;
        this.offset = offset;
        this.length = length;
        this.type = type;
        this.position = position;
        this.dictionary = dictionary;
    }

    public String toString() {
        return "Token(\"" + new String(this.surfaceForm, this.offset, this.length) + "\" pos=" + this.position + " length=" + this.length + " posLen=" + this.positionLength + " type=" + (Object)((Object)this.type) + " wordId=" + this.wordId + " leftID=" + this.dictionary.getLeftId(this.wordId) + ")";
    }

    public char[] getSurfaceForm() {
        return this.surfaceForm;
    }

    public int getOffset() {
        return this.offset;
    }

    public int getLength() {
        return this.length;
    }

    public String getSurfaceFormString() {
        return new String(this.surfaceForm, this.offset, this.length);
    }

    public String getReading() {
        return this.dictionary.getReading(this.wordId, this.surfaceForm, this.offset, this.length);
    }

    public String getPronunciation() {
        return this.dictionary.getPronunciation(this.wordId, this.surfaceForm, this.offset, this.length);
    }

    public String getPartOfSpeech() {
        return this.dictionary.getPartOfSpeech(this.wordId);
    }

    public String getInflectionType() {
        return this.dictionary.getInflectionType(this.wordId);
    }

    public String getInflectionForm() {
        return this.dictionary.getInflectionForm(this.wordId);
    }

    public String getBaseForm() {
        return this.dictionary.getBaseForm(this.wordId, this.surfaceForm, this.offset, this.length);
    }

    public boolean isKnown() {
        return this.type == JapaneseTokenizer.Type.KNOWN;
    }

    public boolean isUnknown() {
        return this.type == JapaneseTokenizer.Type.UNKNOWN;
    }

    public boolean isUser() {
        return this.type == JapaneseTokenizer.Type.USER;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPositionLength(int positionLength) {
        this.positionLength = positionLength;
    }

    public int getPositionLength() {
        return this.positionLength;
    }
}

