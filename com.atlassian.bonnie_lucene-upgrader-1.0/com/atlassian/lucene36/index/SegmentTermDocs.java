/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.DefaultSkipListReader;
import com.atlassian.lucene36.index.FieldInfo;
import com.atlassian.lucene36.index.SegmentReader;
import com.atlassian.lucene36.index.SegmentTermEnum;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.index.TermDocs;
import com.atlassian.lucene36.index.TermEnum;
import com.atlassian.lucene36.index.TermInfo;
import com.atlassian.lucene36.store.IndexInput;
import com.atlassian.lucene36.util.BitVector;
import java.io.IOException;

class SegmentTermDocs
implements TermDocs {
    protected SegmentReader parent;
    protected IndexInput freqStream;
    protected int count;
    protected int df;
    protected BitVector deletedDocs;
    int doc = 0;
    int freq;
    private int skipInterval;
    private int maxSkipLevels;
    private DefaultSkipListReader skipListReader;
    private long freqBasePointer;
    private long proxBasePointer;
    private long skipPointer;
    private boolean haveSkipped;
    protected boolean currentFieldStoresPayloads;
    protected FieldInfo.IndexOptions indexOptions;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected SegmentTermDocs(SegmentReader parent, boolean raw) {
        this.parent = parent;
        this.freqStream = (IndexInput)parent.core.freqStream.clone();
        if (!raw) {
            SegmentReader segmentReader = parent;
            synchronized (segmentReader) {
                this.deletedDocs = parent.deletedDocs;
            }
        } else {
            this.deletedDocs = null;
        }
        this.skipInterval = parent.core.getTermsReader().getSkipInterval();
        this.maxSkipLevels = parent.core.getTermsReader().getMaxSkipLevels();
    }

    protected SegmentTermDocs(SegmentReader parent) {
        this(parent, false);
    }

    public void seek(Term term) throws IOException {
        TermInfo ti = this.parent.core.getTermsReader().get(term);
        this.seek(ti, term);
    }

    public void seek(TermEnum termEnum) throws IOException {
        TermInfo ti;
        Term term;
        if (termEnum instanceof SegmentTermEnum && ((SegmentTermEnum)termEnum).fieldInfos == this.parent.core.fieldInfos) {
            SegmentTermEnum segmentTermEnum = (SegmentTermEnum)termEnum;
            term = segmentTermEnum.term();
            ti = segmentTermEnum.termInfo();
        } else {
            term = termEnum.term();
            ti = this.parent.core.getTermsReader().get(term);
        }
        this.seek(ti, term);
    }

    void seek(TermInfo ti, Term term) throws IOException {
        this.count = 0;
        FieldInfo fi = this.parent.core.fieldInfos.fieldInfo(term.field);
        this.indexOptions = fi != null ? fi.indexOptions : FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS;
        boolean bl = this.currentFieldStoresPayloads = fi != null ? fi.storePayloads : false;
        if (ti == null) {
            this.df = 0;
        } else {
            this.df = ti.docFreq;
            this.doc = 0;
            this.freqBasePointer = ti.freqPointer;
            this.proxBasePointer = ti.proxPointer;
            this.skipPointer = this.freqBasePointer + (long)ti.skipOffset;
            this.freqStream.seek(this.freqBasePointer);
            this.haveSkipped = false;
        }
    }

    public void close() throws IOException {
        this.freqStream.close();
        if (this.skipListReader != null) {
            this.skipListReader.close();
        }
    }

    public final int doc() {
        return this.doc;
    }

    public final int freq() {
        return this.freq;
    }

    protected void skippingDoc() throws IOException {
    }

    public boolean next() throws IOException {
        while (true) {
            if (this.count == this.df) {
                return false;
            }
            int docCode = this.freqStream.readVInt();
            if (this.indexOptions == FieldInfo.IndexOptions.DOCS_ONLY) {
                this.doc += docCode;
                this.freq = 1;
            } else {
                this.doc += docCode >>> 1;
                this.freq = (docCode & 1) != 0 ? 1 : this.freqStream.readVInt();
            }
            ++this.count;
            if (this.deletedDocs == null || !this.deletedDocs.get(this.doc)) break;
            this.skippingDoc();
        }
        return true;
    }

    public int read(int[] docs, int[] freqs) throws IOException {
        int length = docs.length;
        if (this.indexOptions == FieldInfo.IndexOptions.DOCS_ONLY) {
            return this.readNoTf(docs, freqs, length);
        }
        int i = 0;
        while (i < length && this.count < this.df) {
            int docCode = this.freqStream.readVInt();
            this.doc += docCode >>> 1;
            this.freq = (docCode & 1) != 0 ? 1 : this.freqStream.readVInt();
            ++this.count;
            if (this.deletedDocs != null && this.deletedDocs.get(this.doc)) continue;
            docs[i] = this.doc;
            freqs[i] = this.freq;
            ++i;
        }
        return i;
    }

    private final int readNoTf(int[] docs, int[] freqs, int length) throws IOException {
        int i = 0;
        while (i < length && this.count < this.df) {
            this.doc += this.freqStream.readVInt();
            ++this.count;
            if (this.deletedDocs != null && this.deletedDocs.get(this.doc)) continue;
            docs[i] = this.doc;
            freqs[i] = 1;
            ++i;
        }
        return i;
    }

    protected void skipProx(long proxPointer, int payloadLength) throws IOException {
    }

    public boolean skipTo(int target) throws IOException {
        if (target - this.skipInterval >= this.doc && this.df >= this.skipInterval) {
            int newCount;
            if (this.skipListReader == null) {
                this.skipListReader = new DefaultSkipListReader((IndexInput)this.freqStream.clone(), this.maxSkipLevels, this.skipInterval);
            }
            if (!this.haveSkipped) {
                this.skipListReader.init(this.skipPointer, this.freqBasePointer, this.proxBasePointer, this.df, this.currentFieldStoresPayloads);
                this.haveSkipped = true;
            }
            if ((newCount = this.skipListReader.skipTo(target)) > this.count) {
                this.freqStream.seek(this.skipListReader.getFreqPointer());
                this.skipProx(this.skipListReader.getProxPointer(), this.skipListReader.getPayloadLength());
                this.doc = this.skipListReader.getDoc();
                this.count = newCount;
            }
        }
        do {
            if (this.next()) continue;
            return false;
        } while (target > this.doc);
        return true;
    }
}

