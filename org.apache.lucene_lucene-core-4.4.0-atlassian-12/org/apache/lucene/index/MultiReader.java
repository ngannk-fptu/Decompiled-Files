/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import org.apache.lucene.index.BaseCompositeReader;
import org.apache.lucene.index.IndexReader;

public class MultiReader
extends BaseCompositeReader<IndexReader> {
    private final boolean closeSubReaders;

    public MultiReader(IndexReader ... subReaders) {
        this(subReaders, true);
    }

    public MultiReader(IndexReader[] subReaders, boolean closeSubReaders) {
        super((IndexReader[])subReaders.clone());
        this.closeSubReaders = closeSubReaders;
        if (!closeSubReaders) {
            for (int i = 0; i < subReaders.length; ++i) {
                subReaders[i].incRef();
            }
        }
    }

    @Override
    protected synchronized void doClose() throws IOException {
        IOException ioe = null;
        for (IndexReader r : this.getSequentialSubReaders()) {
            try {
                if (this.closeSubReaders) {
                    r.close();
                    continue;
                }
                r.decRef();
            }
            catch (IOException e) {
                if (ioe != null) continue;
                ioe = e;
            }
        }
        if (ioe != null) {
            throw ioe;
        }
    }
}

