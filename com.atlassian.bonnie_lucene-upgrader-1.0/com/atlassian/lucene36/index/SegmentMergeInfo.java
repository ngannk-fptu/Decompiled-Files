/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.PayloadProcessorProvider;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.index.TermEnum;
import com.atlassian.lucene36.index.TermPositions;
import java.io.IOException;

final class SegmentMergeInfo {
    Term term;
    int base;
    int ord;
    TermEnum termEnum;
    IndexReader reader;
    int delCount;
    private TermPositions postings;
    private int[] docMap;
    PayloadProcessorProvider.ReaderPayloadProcessor readerPayloadProcessor;

    SegmentMergeInfo(int b, TermEnum te, IndexReader r) throws IOException {
        this.base = b;
        this.reader = r;
        this.termEnum = te;
        this.term = te.term();
    }

    int[] getDocMap() {
        if (this.docMap == null) {
            this.delCount = 0;
            if (this.reader.hasDeletions()) {
                int maxDoc = this.reader.maxDoc();
                this.docMap = new int[maxDoc];
                int j = 0;
                for (int i = 0; i < maxDoc; ++i) {
                    if (this.reader.isDeleted(i)) {
                        ++this.delCount;
                        this.docMap[i] = -1;
                        continue;
                    }
                    this.docMap[i] = j++;
                }
            }
        }
        return this.docMap;
    }

    TermPositions getPositions() throws IOException {
        if (this.postings == null) {
            this.postings = this.reader.termPositions();
        }
        return this.postings;
    }

    final boolean next() throws IOException {
        if (this.termEnum.next()) {
            this.term = this.termEnum.term();
            return true;
        }
        this.term = null;
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    final void close() throws IOException {
        try {
            this.termEnum.close();
            Object var2_1 = null;
            if (this.postings == null) return;
        }
        catch (Throwable throwable) {
            Object var2_2 = null;
            if (this.postings == null) throw throwable;
            this.postings.close();
            throw throwable;
        }
        this.postings.close();
    }
}

