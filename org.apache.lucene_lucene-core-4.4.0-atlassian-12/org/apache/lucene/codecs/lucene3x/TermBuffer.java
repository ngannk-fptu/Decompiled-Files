/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene3x;

import java.io.IOException;
import java.util.Comparator;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.BytesRef;

@Deprecated
final class TermBuffer
implements Cloneable {
    private String field;
    private Term term;
    private BytesRef bytes = new BytesRef(10);
    private int currentFieldNumber = -2;
    private static final Comparator<BytesRef> utf8AsUTF16Comparator = BytesRef.getUTF8SortedAsUTF16Comparator();
    int newSuffixStart;

    TermBuffer() {
    }

    public int compareTo(TermBuffer other) {
        if (this.field == other.field) {
            return utf8AsUTF16Comparator.compare(this.bytes, other.bytes);
        }
        return this.field.compareTo(other.field);
    }

    public void read(IndexInput input, FieldInfos fieldInfos) throws IOException {
        this.term = null;
        this.newSuffixStart = input.readVInt();
        int length = input.readVInt();
        int totalLength = this.newSuffixStart + length;
        assert (totalLength <= 32766) : "termLength=" + totalLength + ",resource=" + input;
        if (this.bytes.bytes.length < totalLength) {
            this.bytes.grow(totalLength);
        }
        this.bytes.length = totalLength;
        input.readBytes(this.bytes.bytes, this.newSuffixStart, length);
        int fieldNumber = input.readVInt();
        if (fieldNumber != this.currentFieldNumber) {
            this.currentFieldNumber = fieldNumber;
            if (this.currentFieldNumber == -1) {
                this.field = "";
            } else {
                assert (fieldInfos.fieldInfo(this.currentFieldNumber) != null) : this.currentFieldNumber;
                this.field = fieldInfos.fieldInfo((int)this.currentFieldNumber).name.intern();
            }
        } else assert (this.field.equals(fieldInfos.fieldInfo((int)fieldNumber).name)) : "currentFieldNumber=" + this.currentFieldNumber + " field=" + this.field + " vs " + fieldInfos.fieldInfo(fieldNumber) == null ? "null" : fieldInfos.fieldInfo((int)fieldNumber).name;
    }

    public void set(Term term) {
        if (term == null) {
            this.reset();
            return;
        }
        this.bytes.copyBytes(term.bytes());
        this.field = term.field().intern();
        this.currentFieldNumber = -1;
        this.term = term;
    }

    public void set(TermBuffer other) {
        this.field = other.field;
        this.currentFieldNumber = other.currentFieldNumber;
        this.term = null;
        this.bytes.copyBytes(other.bytes);
    }

    public void reset() {
        this.field = null;
        this.term = null;
        this.currentFieldNumber = -1;
    }

    public Term toTerm() {
        if (this.field == null) {
            return null;
        }
        if (this.term == null) {
            this.term = new Term(this.field, BytesRef.deepCopyOf(this.bytes));
        }
        return this.term;
    }

    protected TermBuffer clone() {
        TermBuffer clone = null;
        try {
            clone = (TermBuffer)super.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            // empty catch block
        }
        clone.bytes = BytesRef.deepCopyOf(this.bytes);
        return clone;
    }
}

