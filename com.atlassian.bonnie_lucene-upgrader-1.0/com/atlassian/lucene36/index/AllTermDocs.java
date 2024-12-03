/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.AbstractAllTermDocs;
import com.atlassian.lucene36.index.SegmentReader;
import com.atlassian.lucene36.util.BitVector;

class AllTermDocs
extends AbstractAllTermDocs {
    protected BitVector deletedDocs;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected AllTermDocs(SegmentReader parent) {
        super(parent.maxDoc());
        SegmentReader segmentReader = parent;
        synchronized (segmentReader) {
            this.deletedDocs = parent.deletedDocs;
        }
    }

    public boolean isDeleted(int doc) {
        return this.deletedDocs != null && this.deletedDocs.get(doc);
    }
}

