/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.util.Collections;
import java.util.List;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.CompositeReaderContext;
import org.apache.lucene.index.IndexReaderContext;

public final class AtomicReaderContext
extends IndexReaderContext {
    public final int ord;
    public final int docBase;
    private final AtomicReader reader;
    private final List<AtomicReaderContext> leaves;

    AtomicReaderContext(CompositeReaderContext parent, AtomicReader reader, int ord, int docBase, int leafOrd, int leafDocBase) {
        super(parent, ord, docBase);
        this.ord = leafOrd;
        this.docBase = leafDocBase;
        this.reader = reader;
        this.leaves = this.isTopLevel ? Collections.singletonList(this) : null;
    }

    AtomicReaderContext(AtomicReader atomicReader) {
        this(null, atomicReader, 0, 0, 0, 0);
    }

    @Override
    public List<AtomicReaderContext> leaves() {
        if (!this.isTopLevel) {
            throw new UnsupportedOperationException("This is not a top-level context.");
        }
        assert (this.leaves != null);
        return this.leaves;
    }

    @Override
    public List<IndexReaderContext> children() {
        return null;
    }

    @Override
    public AtomicReader reader() {
        return this.reader;
    }
}

