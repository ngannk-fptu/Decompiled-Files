/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.SegmentReader;

class ReadOnlySegmentReader
extends SegmentReader {
    ReadOnlySegmentReader() {
    }

    static void noWrite() {
        throw new UnsupportedOperationException("This IndexReader cannot make any changes to the index (it was opened with readOnly = true)");
    }

    protected void acquireWriteLock() {
        ReadOnlySegmentReader.noWrite();
    }

    public boolean isDeleted(int n) {
        return this.deletedDocs != null && this.deletedDocs.get(n);
    }
}

