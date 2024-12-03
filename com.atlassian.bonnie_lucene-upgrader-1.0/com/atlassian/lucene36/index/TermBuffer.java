/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.FieldInfos;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.store.IndexInput;
import com.atlassian.lucene36.util.UnicodeUtil;
import java.io.IOException;

final class TermBuffer
implements Cloneable {
    private String field;
    private Term term;
    private boolean preUTF8Strings;
    private boolean dirty;
    private UnicodeUtil.UTF16Result text = new UnicodeUtil.UTF16Result();
    private UnicodeUtil.UTF8Result bytes = new UnicodeUtil.UTF8Result();

    TermBuffer() {
    }

    public final int compareTo(TermBuffer other) {
        if (this.field == other.field) {
            return TermBuffer.compareChars(this.text.result, this.text.length, other.text.result, other.text.length);
        }
        return this.field.compareTo(other.field);
    }

    private static final int compareChars(char[] chars1, int len1, char[] chars2, int len2) {
        int end = len1 < len2 ? len1 : len2;
        for (int k = 0; k < end; ++k) {
            char c1 = chars1[k];
            char c2 = chars2[k];
            if (c1 == c2) continue;
            return c1 - c2;
        }
        return len1 - len2;
    }

    void setPreUTF8Strings() {
        this.preUTF8Strings = true;
    }

    public final void read(IndexInput input, FieldInfos fieldInfos) throws IOException {
        this.term = null;
        int start = input.readVInt();
        int length = input.readVInt();
        int totalLength = start + length;
        if (this.preUTF8Strings) {
            this.text.setLength(totalLength);
            input.readChars(this.text.result, start, length);
        } else if (this.dirty) {
            UnicodeUtil.UTF16toUTF8(this.text.result, 0, this.text.length, this.bytes);
            this.bytes.setLength(totalLength);
            input.readBytes(this.bytes.result, start, length);
            UnicodeUtil.UTF8toUTF16(this.bytes.result, 0, totalLength, this.text);
            this.dirty = false;
        } else {
            this.bytes.setLength(totalLength);
            input.readBytes(this.bytes.result, start, length);
            UnicodeUtil.UTF8toUTF16(this.bytes.result, start, length, this.text);
        }
        this.field = fieldInfos.fieldName(input.readVInt());
    }

    public final void set(Term term) {
        if (term == null) {
            this.reset();
            return;
        }
        String termText = term.text();
        int termLen = termText.length();
        this.text.setLength(termLen);
        termText.getChars(0, termLen, this.text.result, 0);
        this.dirty = true;
        this.field = term.field();
        this.term = term;
    }

    public final void set(TermBuffer other) {
        this.text.copyText(other.text);
        this.dirty = true;
        this.field = other.field;
        this.term = other.term;
    }

    public void reset() {
        this.field = null;
        this.text.setLength(0);
        this.term = null;
        this.dirty = true;
    }

    public Term toTerm() {
        if (this.field == null) {
            return null;
        }
        if (this.term == null) {
            this.term = new Term(this.field, new String(this.text.result, 0, this.text.length), false);
        }
        return this.term;
    }

    protected Object clone() {
        TermBuffer clone = null;
        try {
            clone = (TermBuffer)super.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            // empty catch block
        }
        clone.dirty = true;
        clone.bytes = new UnicodeUtil.UTF8Result();
        clone.text = new UnicodeUtil.UTF16Result();
        clone.text.copyText(this.text);
        return clone;
    }
}

