/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene3x;

import java.io.Closeable;
import java.io.IOException;
import org.apache.lucene.codecs.lucene3x.TermBuffer;
import org.apache.lucene.codecs.lucene3x.TermInfo;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexFormatTooNewException;
import org.apache.lucene.index.IndexFormatTooOldException;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.IndexInput;

@Deprecated
final class SegmentTermEnum
implements Cloneable,
Closeable {
    private IndexInput input;
    FieldInfos fieldInfos;
    long size;
    long position = -1L;
    public static final int FORMAT_VERSION_UTF8_LENGTH_IN_BYTES = -4;
    public static final int FORMAT_CURRENT = -4;
    public static final int FORMAT_MINIMUM = -4;
    private TermBuffer termBuffer = new TermBuffer();
    private TermBuffer prevBuffer = new TermBuffer();
    private TermBuffer scanBuffer = new TermBuffer();
    TermInfo termInfo = new TermInfo();
    private int format;
    private boolean isIndex = false;
    long indexPointer = 0L;
    int indexInterval;
    int skipInterval;
    int newSuffixStart;
    int maxSkipLevels;
    private boolean first = true;

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
            if (this.format > -4) {
                throw new IndexFormatTooOldException(this.input, this.format, -4, -4);
            }
            if (this.format < -4) {
                throw new IndexFormatTooNewException(this.input, this.format, -4, -4);
            }
            this.size = this.input.readLong();
            this.indexInterval = this.input.readInt();
            this.skipInterval = this.input.readInt();
            this.maxSkipLevels = this.input.readInt();
            assert (this.indexInterval > 0) : "indexInterval=" + this.indexInterval + " is negative; must be > 0";
            assert (this.skipInterval > 0) : "skipInterval=" + this.skipInterval + " is negative; must be > 0";
        }
    }

    protected SegmentTermEnum clone() {
        SegmentTermEnum clone = null;
        try {
            clone = (SegmentTermEnum)super.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            // empty catch block
        }
        clone.input = this.input.clone();
        clone.termInfo = new TermInfo(this.termInfo);
        clone.termBuffer = this.termBuffer.clone();
        clone.prevBuffer = this.prevBuffer.clone();
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
        this.prevBuffer.set(this.termBuffer);
        if (this.position++ >= this.size - 1L) {
            this.termBuffer.reset();
            return false;
        }
        this.termBuffer.read(this.input, this.fieldInfos);
        this.newSuffixStart = this.termBuffer.newSuffixStart;
        this.termInfo.docFreq = this.input.readVInt();
        this.termInfo.freqPointer += this.input.readVLong();
        this.termInfo.proxPointer += this.input.readVLong();
        if (this.termInfo.docFreq >= this.skipInterval) {
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

    @Override
    public final void close() throws IOException {
        this.input.close();
    }
}

