/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.util.List;
import org.apache.lucene.index.CompositeReaderContext;
import org.apache.lucene.index.IndexReader;

public abstract class CompositeReader
extends IndexReader {
    private volatile CompositeReaderContext readerContext = null;

    protected CompositeReader() {
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        for (Class<?> clazz = this.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            if (clazz.isAnonymousClass()) continue;
            buffer.append(clazz.getSimpleName());
            break;
        }
        buffer.append('(');
        List<? extends IndexReader> subReaders = this.getSequentialSubReaders();
        assert (subReaders != null);
        if (!subReaders.isEmpty()) {
            buffer.append(subReaders.get(0));
            int c = subReaders.size();
            for (int i = 1; i < c; ++i) {
                buffer.append(" ").append(subReaders.get(i));
            }
        }
        buffer.append(')');
        return buffer.toString();
    }

    protected abstract List<? extends IndexReader> getSequentialSubReaders();

    @Override
    public final CompositeReaderContext getContext() {
        this.ensureOpen();
        if (this.readerContext == null) {
            assert (this.getSequentialSubReaders() != null);
            this.readerContext = CompositeReaderContext.create(this);
        }
        return this.readerContext;
    }
}

