/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.store.Directory;

public final class ReaderManager
extends ReferenceManager<DirectoryReader> {
    public ReaderManager(IndexWriter writer, boolean applyAllDeletes) throws IOException {
        this.current = DirectoryReader.open(writer, applyAllDeletes);
    }

    public ReaderManager(Directory dir) throws IOException {
        this.current = DirectoryReader.open(dir);
    }

    @Override
    protected void decRef(DirectoryReader reference) throws IOException {
        reference.decRef();
    }

    @Override
    protected DirectoryReader refreshIfNeeded(DirectoryReader referenceToRefresh) throws IOException {
        return DirectoryReader.openIfChanged(referenceToRefresh);
    }

    @Override
    protected boolean tryIncRef(DirectoryReader reference) {
        return reference.tryIncRef();
    }
}

