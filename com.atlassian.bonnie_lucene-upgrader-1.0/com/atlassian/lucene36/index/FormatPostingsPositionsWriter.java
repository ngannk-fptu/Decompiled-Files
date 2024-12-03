/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.FieldInfo;
import com.atlassian.lucene36.index.FormatPostingsDocsWriter;
import com.atlassian.lucene36.index.FormatPostingsPositionsConsumer;
import com.atlassian.lucene36.index.IndexFileNames;
import com.atlassian.lucene36.index.SegmentWriteState;
import com.atlassian.lucene36.store.IndexOutput;
import com.atlassian.lucene36.util.IOUtils;
import java.io.Closeable;
import java.io.IOException;

final class FormatPostingsPositionsWriter
extends FormatPostingsPositionsConsumer
implements Closeable {
    final FormatPostingsDocsWriter parent;
    final IndexOutput out;
    boolean omitTermFreqAndPositions;
    boolean storePayloads;
    int lastPayloadLength = -1;
    int lastPosition;

    FormatPostingsPositionsWriter(SegmentWriteState state, FormatPostingsDocsWriter parent) throws IOException {
        this.parent = parent;
        this.omitTermFreqAndPositions = parent.omitTermFreqAndPositions;
        if (parent.parent.parent.fieldInfos.hasProx()) {
            this.out = parent.parent.parent.dir.createOutput(IndexFileNames.segmentFileName(parent.parent.parent.segment, "prx"));
            parent.skipListWriter.setProxOutput(this.out);
        } else {
            this.out = null;
        }
    }

    void addPosition(int position, byte[] payload, int payloadOffset, int payloadLength) throws IOException {
        assert (!this.omitTermFreqAndPositions) : "omitTermFreqAndPositions is true";
        assert (this.out != null);
        int delta = position - this.lastPosition;
        this.lastPosition = position;
        if (this.storePayloads) {
            if (payloadLength != this.lastPayloadLength) {
                this.lastPayloadLength = payloadLength;
                this.out.writeVInt(delta << 1 | 1);
                this.out.writeVInt(payloadLength);
            } else {
                this.out.writeVInt(delta << 1);
            }
            if (payloadLength > 0) {
                this.out.writeBytes(payload, payloadLength);
            }
        } else {
            this.out.writeVInt(delta);
        }
    }

    void setField(FieldInfo fieldInfo) {
        this.omitTermFreqAndPositions = fieldInfo.indexOptions == FieldInfo.IndexOptions.DOCS_ONLY;
        this.storePayloads = this.omitTermFreqAndPositions ? false : fieldInfo.storePayloads;
    }

    void finish() {
        this.lastPosition = 0;
        this.lastPayloadLength = -1;
    }

    public void close() throws IOException {
        IOUtils.close(this.out);
    }
}

