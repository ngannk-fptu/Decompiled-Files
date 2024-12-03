/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene3x;

import java.io.IOException;
import org.apache.lucene.codecs.lucene3x.SegmentTermDocs;
import org.apache.lucene.codecs.lucene3x.TermInfo;
import org.apache.lucene.codecs.lucene3x.TermInfosReader;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.BytesRef;

@Deprecated
final class SegmentTermPositions
extends SegmentTermDocs {
    private IndexInput proxStream;
    private IndexInput proxStreamOrig;
    private int proxCount;
    private int position;
    private BytesRef payload;
    private int payloadLength;
    private boolean needToLoadPayload;
    private long lazySkipPointer = -1L;
    private int lazySkipProxCount = 0;

    public SegmentTermPositions(IndexInput freqStream, IndexInput proxStream, TermInfosReader tis, FieldInfos fieldInfos) {
        super(freqStream, tis, fieldInfos);
        this.proxStreamOrig = proxStream;
    }

    @Override
    final void seek(TermInfo ti, Term term) throws IOException {
        super.seek(ti, term);
        if (ti != null) {
            this.lazySkipPointer = ti.proxPointer;
        }
        this.lazySkipProxCount = 0;
        this.proxCount = 0;
        this.payloadLength = 0;
        this.needToLoadPayload = false;
    }

    @Override
    public final void close() throws IOException {
        super.close();
        if (this.proxStream != null) {
            this.proxStream.close();
        }
    }

    public final int nextPosition() throws IOException {
        if (this.indexOptions != FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) {
            return 0;
        }
        this.lazySkip();
        --this.proxCount;
        return this.position += this.readDeltaPosition();
    }

    private final int readDeltaPosition() throws IOException {
        int delta = this.proxStream.readVInt();
        if (this.currentFieldStoresPayloads) {
            if ((delta & 1) != 0) {
                this.payloadLength = this.proxStream.readVInt();
            }
            delta >>>= 1;
            this.needToLoadPayload = true;
        } else if (delta == -1) {
            delta = 0;
        }
        return delta;
    }

    @Override
    protected final void skippingDoc() throws IOException {
        this.lazySkipProxCount += this.freq;
    }

    @Override
    public final boolean next() throws IOException {
        this.lazySkipProxCount += this.proxCount;
        if (super.next()) {
            this.proxCount = this.freq;
            this.position = 0;
            return true;
        }
        return false;
    }

    @Override
    public final int read(int[] docs, int[] freqs) {
        throw new UnsupportedOperationException("TermPositions does not support processing multiple documents in one call. Use TermDocs instead.");
    }

    @Override
    protected void skipProx(long proxPointer, int payloadLength) throws IOException {
        this.lazySkipPointer = proxPointer;
        this.lazySkipProxCount = 0;
        this.proxCount = 0;
        this.payloadLength = payloadLength;
        this.needToLoadPayload = false;
    }

    private void skipPositions(int n) throws IOException {
        assert (this.indexOptions == FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
        for (int f = n; f > 0; --f) {
            this.readDeltaPosition();
            this.skipPayload();
        }
    }

    private void skipPayload() throws IOException {
        if (this.needToLoadPayload && this.payloadLength > 0) {
            this.proxStream.seek(this.proxStream.getFilePointer() + (long)this.payloadLength);
        }
        this.needToLoadPayload = false;
    }

    private void lazySkip() throws IOException {
        if (this.proxStream == null) {
            this.proxStream = this.proxStreamOrig.clone();
        }
        this.skipPayload();
        if (this.lazySkipPointer != -1L) {
            this.proxStream.seek(this.lazySkipPointer);
            this.lazySkipPointer = -1L;
        }
        if (this.lazySkipProxCount != 0) {
            this.skipPositions(this.lazySkipProxCount);
            this.lazySkipProxCount = 0;
        }
    }

    public int getPayloadLength() {
        return this.payloadLength;
    }

    public BytesRef getPayload() throws IOException {
        if (this.payloadLength <= 0) {
            return null;
        }
        if (this.needToLoadPayload) {
            if (this.payload == null) {
                this.payload = new BytesRef(this.payloadLength);
            } else {
                this.payload.grow(this.payloadLength);
            }
            this.proxStream.readBytes(this.payload.bytes, this.payload.offset, this.payloadLength);
            this.payload.length = this.payloadLength;
            this.needToLoadPayload = false;
        }
        return this.payload;
    }

    public boolean isPayloadAvailable() {
        return this.needToLoadPayload && this.payloadLength > 0;
    }
}

