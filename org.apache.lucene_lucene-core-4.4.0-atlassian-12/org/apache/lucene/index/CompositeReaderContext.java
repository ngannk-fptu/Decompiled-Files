/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.CompositeReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexReaderContext;

public final class CompositeReaderContext
extends IndexReaderContext {
    private final List<IndexReaderContext> children;
    private final List<AtomicReaderContext> leaves;
    private final CompositeReader reader;

    static CompositeReaderContext create(CompositeReader reader) {
        return new Builder(reader).build();
    }

    CompositeReaderContext(CompositeReaderContext parent, CompositeReader reader, int ordInParent, int docbaseInParent, List<IndexReaderContext> children) {
        this(parent, reader, ordInParent, docbaseInParent, children, null);
    }

    CompositeReaderContext(CompositeReader reader, List<IndexReaderContext> children, List<AtomicReaderContext> leaves) {
        this(null, reader, 0, 0, children, leaves);
    }

    private CompositeReaderContext(CompositeReaderContext parent, CompositeReader reader, int ordInParent, int docbaseInParent, List<IndexReaderContext> children, List<AtomicReaderContext> leaves) {
        super(parent, ordInParent, docbaseInParent);
        this.children = Collections.unmodifiableList(children);
        this.leaves = leaves == null ? null : Collections.unmodifiableList(leaves);
        this.reader = reader;
    }

    @Override
    public List<AtomicReaderContext> leaves() throws UnsupportedOperationException {
        if (!this.isTopLevel) {
            throw new UnsupportedOperationException("This is not a top-level context.");
        }
        assert (this.leaves != null);
        return this.leaves;
    }

    @Override
    public List<IndexReaderContext> children() {
        return this.children;
    }

    @Override
    public CompositeReader reader() {
        return this.reader;
    }

    private static final class Builder {
        private final CompositeReader reader;
        private final List<AtomicReaderContext> leaves = new ArrayList<AtomicReaderContext>();
        private int leafDocBase = 0;

        public Builder(CompositeReader reader) {
            this.reader = reader;
        }

        public CompositeReaderContext build() {
            return (CompositeReaderContext)this.build(null, this.reader, 0, 0);
        }

        private IndexReaderContext build(CompositeReaderContext parent, IndexReader reader, int ord, int docBase) {
            if (reader instanceof AtomicReader) {
                AtomicReader ar = (AtomicReader)reader;
                AtomicReaderContext atomic = new AtomicReaderContext(parent, ar, ord, docBase, this.leaves.size(), this.leafDocBase);
                this.leaves.add(atomic);
                this.leafDocBase += reader.maxDoc();
                return atomic;
            }
            CompositeReader cr = (CompositeReader)reader;
            List<? extends IndexReader> sequentialSubReaders = cr.getSequentialSubReaders();
            List<IndexReaderContext> children = Arrays.asList(new IndexReaderContext[sequentialSubReaders.size()]);
            CompositeReaderContext newParent = parent == null ? new CompositeReaderContext(cr, children, this.leaves) : new CompositeReaderContext(parent, cr, ord, docBase, children);
            int newDocBase = 0;
            int c = sequentialSubReaders.size();
            for (int i = 0; i < c; ++i) {
                IndexReader r = sequentialSubReaders.get(i);
                children.set(i, this.build(newParent, r, i, newDocBase));
                newDocBase += r.maxDoc();
            }
            assert (newDocBase == cr.maxDoc());
            return newParent;
        }
    }
}

