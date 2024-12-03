/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.CorruptIndexException;
import com.atlassian.lucene36.index.FieldInfos;
import com.atlassian.lucene36.index.IndexFormatTooNewException;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.index.TermBuffer;
import com.atlassian.lucene36.index.TermEnum;
import com.atlassian.lucene36.index.TermInfo;
import com.atlassian.lucene36.store.IndexInput;
import java.io.IOException;

final class SegmentTermEnum
extends TermEnum
implements Cloneable {
    private IndexInput input;
    FieldInfos fieldInfos;
    long size;
    long position = -1L;
    private boolean first = true;
    private TermBuffer termBuffer = new TermBuffer();
    private TermBuffer prevBuffer = new TermBuffer();
    private TermBuffer scanBuffer = new TermBuffer();
    private TermInfo termInfo = new TermInfo();
    private int format;
    private boolean isIndex = false;
    long indexPointer = 0L;
    int indexInterval;
    int skipInterval;
    int maxSkipLevels;
    private int formatM1SkipInterval;

    SegmentTermEnum(IndexInput i, FieldInfos fis, boolean isi) throws CorruptIndexException, IOException {
        this.input = i;
        this.fieldInfos = fis;
        this.isIndex = isi;
        this.maxSkipLevels = 1;
        int firstInt = this.input.readInt();
        if (firstInt >= 0) {
            this.format = 0;
            this.size = firstInt;
            this.indexInterval = 128;
            this.skipInterval = Integer.MAX_VALUE;
        } else {
            this.format = firstInt;
            if (this.format < -4) {
                throw new IndexFormatTooNewException(this.input, this.format, -1, -4);
            }
            this.size = this.input.readLong();
            if (this.format == -1) {
                if (!this.isIndex) {
                    this.indexInterval = this.input.readInt();
                    this.formatM1SkipInterval = this.input.readInt();
                }
                this.skipInterval = Integer.MAX_VALUE;
            } else {
                this.indexInterval = this.input.readInt();
                this.skipInterval = this.input.readInt();
                if (this.format <= -3) {
                    this.maxSkipLevels = this.input.readInt();
                }
            }
            assert (this.indexInterval > 0) : "indexInterval=" + this.indexInterval + " is negative; must be > 0";
            assert (this.skipInterval > 0) : "skipInterval=" + this.skipInterval + " is negative; must be > 0";
        }
        if (this.format > -4) {
            this.termBuffer.setPreUTF8Strings();
            this.scanBuffer.setPreUTF8Strings();
            this.prevBuffer.setPreUTF8Strings();
        }
    }

    protected Object clone() {
        SegmentTermEnum clone = null;
        try {
            clone = (SegmentTermEnum)super.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            // empty catch block
        }
        clone.input = (IndexInput)this.input.clone();
        clone.termInfo = new TermInfo(this.termInfo);
        clone.termBuffer = (TermBuffer)this.termBuffer.clone();
        clone.prevBuffer = (TermBuffer)this.prevBuffer.clone();
        clone.scanBuffer = new TermBuffer();
        return clone;
    }

    final void seek(long pointer, long p, Term t, TermInfo ti) throws IOException {
        this.input.seek(pointer);
        this.position = p;
        this.termBuffer.set(t);
        this.prevBuffer.reset();
        this.termInfo.set(ti);
        this.first = p == -1L;
    }

    public final boolean next() throws IOException {
        if (this.position++ >= this.size - 1L) {
            this.prevBuffer.set(this.termBuffer);
            this.termBuffer.reset();
            return false;
        }
        this.prevBuffer.set(this.termBuffer);
        this.termBuffer.read(this.input, this.fieldInfos);
        this.termInfo.docFreq = this.input.readVInt();
        this.termInfo.freqPointer += this.input.readVLong();
        this.termInfo.proxPointer += this.input.readVLong();
        if (this.format == -1) {
            if (!this.isIndex && this.termInfo.docFreq > this.formatM1SkipInterval) {
                this.termInfo.skipOffset = this.input.readVInt();
            }
        } else if (this.termInfo.docFreq >= this.skipInterval) {
            this.termInfo.skipOffset = this.input.readVInt();
        }
        if (this.isIndex) {
            this.indexPointer += this.input.readVLong();
        }
        return true;
    }

    final int scanTo(Term term) throws IOException {
        this.scanBuffer.set(term);
        int count = 0;
        if (this.first) {
            this.next();
            this.first = false;
            ++count;
        }
        while (this.scanBuffer.compareTo(this.termBuffer) > 0 && this.next()) {
            ++count;
        }
        return count;
    }

    public final Term term() {
        return this.termBuffer.toTerm();
    }

    final Term prev() {
        return this.prevBuffer.toTerm();
    }

    final TermInfo termInfo() {
        return new TermInfo(this.termInfo);
    }

    final void termInfo(TermInfo ti) {
        ti.set(this.termInfo);
    }

    public final int docFreq() {
        return this.termInfo.docFreq;
    }

    final long freqPointer() {
        return this.termInfo.freqPointer;
    }

    final long proxPointer() {
        return this.termInfo.proxPointer;
    }

    public final void close() throws IOException {
        this.input.close();
    }
}

