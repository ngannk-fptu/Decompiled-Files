/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.FieldInfo;
import com.atlassian.lucene36.index.SegmentReader;
import com.atlassian.lucene36.index.SegmentTermDocs;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.index.TermInfo;
import com.atlassian.lucene36.index.TermPositions;
import com.atlassian.lucene36.store.IndexInput;
import java.io.IOException;

final class SegmentTermPositions
extends SegmentTermDocs
implements TermPositions {
    private IndexInput proxStream = null;
    private int proxCount;
    private int position;
    private int payloadLength;
    private boolean needToLoadPayload;
    private long lazySkipPointer = -1L;
    private int lazySkipProxCount = 0;

    SegmentTermPositions(SegmentReader p) {
        super(p);
    }

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
        }
        return delta;
    }

    protected final void skippingDoc() throws IOException {
        this.lazySkipProxCount += this.freq;
    }

    public final boolean next() throws IOException {
        this.lazySkipProxCount += this.proxCount;
        if (super.next()) {
            this.proxCount = this.freq;
            this.position = 0;
            return true;
        }
        return false;
    }

    public final int read(int[] docs, int[] freqs) {
        throw new UnsupportedOperationException("TermPositions does not support processing multiple documents in one call. Use TermDocs instead.");
    }

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
            this.proxStream = (IndexInput)this.parent.core.proxStream.clone();
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

    public byte[] getPayload(byte[] data, int offset) throws IOException {
        int retOffset;
        byte[] retArray;
        if (!this.needToLoadPayload) {
            throw new IOException("Either no payload exists at this term position or an attempt was made to load it more than once.");
        }
        if (data == null || data.length - offset < this.payloadLength) {
            retArray = new byte[this.payloadLength];
            retOffset = 0;
        } else {
            retArray = data;
            retOffset = offset;
        }
        this.proxStream.readBytes(retArray, retOffset, this.payloadLength);
        this.needToLoadPayload = false;
        return retArray;
    }

    public boolean isPayloadAvailable() {
        return this.needToLoadPayload && this.payloadLength > 0;
    }
}

