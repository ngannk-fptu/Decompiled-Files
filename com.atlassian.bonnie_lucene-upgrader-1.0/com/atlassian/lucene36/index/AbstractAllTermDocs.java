/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.index.TermDocs;
import com.atlassian.lucene36.index.TermEnum;
import java.io.IOException;

public abstract class AbstractAllTermDocs
implements TermDocs {
    protected int maxDoc;
    protected int doc = -1;

    protected AbstractAllTermDocs(int maxDoc) {
        this.maxDoc = maxDoc;
    }

    public void seek(Term term) throws IOException {
        if (term != null) {
            throw new UnsupportedOperationException();
        }
        this.doc = -1;
    }

    public void seek(TermEnum termEnum) throws IOException {
        throw new UnsupportedOperationException();
    }

    public int doc() {
        return this.doc;
    }

    public int freq() {
        return 1;
    }

    public boolean next() throws IOException {
        return this.skipTo(this.doc + 1);
    }

    public int read(int[] docs, int[] freqs) throws IOException {
        int length = docs.length;
        int i = 0;
        while (i < length && this.doc < this.maxDoc) {
            if (!this.isDeleted(this.doc)) {
                docs[i] = this.doc;
                freqs[i] = 1;
                ++i;
            }
            ++this.doc;
        }
        return i;
    }

    public boolean skipTo(int target) throws IOException {
        this.doc = target;
        while (this.doc < this.maxDoc) {
            if (!this.isDeleted(this.doc)) {
                return true;
            }
            ++this.doc;
        }
        return false;
    }

    public void close() throws IOException {
    }

    public abstract boolean isDeleted(int var1);
}

