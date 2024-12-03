/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.util.List;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.CompositeReaderContext;
import org.apache.lucene.index.IndexReader;

public abstract class IndexReaderContext {
    public final CompositeReaderContext parent;
    public final boolean isTopLevel;
    public final int docBaseInParent;
    public final int ordInParent;

    IndexReaderContext(CompositeReaderContext parent, int ordInParent, int docBaseInParent) {
        if (!(this instanceof CompositeReaderContext) && !(this instanceof AtomicReaderContext)) {
            throw new Error("This class should never be extended by custom code!");
        }
        this.parent = parent;
        this.docBaseInParent = docBaseInParent;
        this.ordInParent = ordInParent;
        this.isTopLevel = parent == null;
    }

    public abstract IndexReader reader();

    public abstract List<AtomicReaderContext> leaves() throws UnsupportedOperationException;

    public abstract List<IndexReaderContext> children();
}

