/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene3x;

import java.io.IOException;
import org.apache.lucene.codecs.lucene3x.Lucene3xSkipListReader;
import org.apache.lucene.codecs.lucene3x.SegmentTermEnum;
import org.apache.lucene.codecs.lucene3x.TermInfo;
import org.apache.lucene.codecs.lucene3x.TermInfosReader;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.Bits;

@Deprecated
class SegmentTermDocs {
    private final FieldInfos fieldInfos;
    private final TermInfosReader tis;
    protected Bits liveDocs;
    protected IndexInput freqStream;
    protected int count;
    protected int df;
    int doc = 0;
    int freq;
    private int skipInterval;
    private int maxSkipLevels;
    private Lucene3xSkipListReader skipListReader;
    private long freqBasePointer;
    private long proxBasePointer;
    private long skipPointer;
    private boolean haveSkipped;
    protected boolean currentFieldStoresPayloads;
    protected FieldInfo.IndexOptions indexOptions;

    public SegmentTermDocs(IndexInput freqStream, TermInfosReader tis, FieldInfos fieldInfos) {
        this.freqStream = freqStream.clone();
        this.tis = tis;
        this.fieldInfos = fieldInfos;
        this.skipInterval = tis.getSkipInterval();
        this.maxSkipLevels = tis.getMaxSkipLevels();
    }

    public void seek(Term term) throws IOException {
        TermInfo ti = this.tis.get(term);
        this.seek(ti, term);
    }

    public void setLiveDocs(Bits liveDocs) {
        this.liveDocs = liveDocs;
    }

    public void seek(SegmentTermEnum segmentTermEnum) throws IOException {
        TermInfo ti;
        Term term;
        if (segmentTermEnum.fieldInfos == this.fieldInfos) {
            term = segmentTermEnum.term();
            ti = segmentTermEnum.termInfo();
        } else {
            term = segmentTermEnum.term();
            ti = this.tis.get(term);
        }
        this.seek(ti, term);
    }

    void seek(TermInfo ti, Term term) throws IOException {
        this.count = 0;
        FieldInfo fi = this.fieldInfos.fieldInfo(term.field());
        this.indexOptions = fi != null ? fi.getIndexOptions() : FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS;
        boolean bl = this.currentFieldStoresPayloads = fi != null ? fi.hasPayloads() : false;
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
            } else {
                this.doc += docCode >>> 1;
                if ((docCode & 1) != 0) {
                    this.freq = 1;
                } else {
                    this.freq = this.freqStream.readVInt();
                    assert (this.freq != 1);
                }
            }
            ++this.count;
            if (this.liveDocs == null || this.liveDocs.get(this.doc)) break;
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
            if (this.liveDocs != null && !this.liveDocs.get(this.doc)) continue;
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
            if (this.liveDocs != null && !this.liveDocs.get(this.doc)) continue;
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
                this.skipListReader = new Lucene3xSkipListReader(this.freqStream.clone(), this.maxSkipLevels, this.skipInterval);
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

