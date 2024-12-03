/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DocumentStoredFieldVisitor;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.CompositeReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.StoredFieldVisitor;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.Directory;

public abstract class IndexReader
implements Closeable {
    private boolean closed = false;
    private boolean closedByChild = false;
    private final AtomicInteger refCount = new AtomicInteger(1);
    private final Set<ReaderClosedListener> readerClosedListeners = Collections.synchronizedSet(new LinkedHashSet());
    private final Set<IndexReader> parentReaders = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));

    IndexReader() {
        if (!(this instanceof CompositeReader) && !(this instanceof AtomicReader)) {
            throw new Error("IndexReader should never be directly extended, subclass AtomicReader or CompositeReader instead.");
        }
    }

    public final void addReaderClosedListener(ReaderClosedListener listener) {
        this.ensureOpen();
        this.readerClosedListeners.add(listener);
    }

    public final void removeReaderClosedListener(ReaderClosedListener listener) {
        this.ensureOpen();
        this.readerClosedListeners.remove(listener);
    }

    public final void registerParentReader(IndexReader reader) {
        this.ensureOpen();
        this.parentReaders.add(reader);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void notifyReaderClosedListeners() {
        Set<ReaderClosedListener> set = this.readerClosedListeners;
        synchronized (set) {
            for (ReaderClosedListener listener : this.readerClosedListeners) {
                listener.onClose(this);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void reportCloseToParentReaders() {
        Set<IndexReader> set = this.parentReaders;
        synchronized (set) {
            for (IndexReader parent : this.parentReaders) {
                parent.closedByChild = true;
                parent.refCount.addAndGet(0);
                parent.reportCloseToParentReaders();
            }
        }
    }

    public final int getRefCount() {
        return this.refCount.get();
    }

    public final void incRef() {
        this.ensureOpen();
        this.refCount.incrementAndGet();
    }

    public final boolean tryIncRef() {
        int count;
        while ((count = this.refCount.get()) > 0) {
            if (!this.refCount.compareAndSet(count, count + 1)) continue;
            return true;
        }
        return false;
    }

    public final void decRef() throws IOException {
        if (this.refCount.get() <= 0) {
            throw new AlreadyClosedException("this IndexReader is closed");
        }
        int rc = this.refCount.decrementAndGet();
        if (rc == 0) {
            boolean success = false;
            try {
                this.doClose();
                success = true;
            }
            finally {
                if (!success) {
                    this.refCount.incrementAndGet();
                }
            }
            this.reportCloseToParentReaders();
            this.notifyReaderClosedListeners();
        } else if (rc < 0) {
            throw new IllegalStateException("too many decRef calls: refCount is " + rc + " after decrement");
        }
    }

    protected final void ensureOpen() throws AlreadyClosedException {
        if (this.refCount.get() <= 0) {
            throw new AlreadyClosedException("this IndexReader is closed");
        }
        if (this.closedByChild) {
            throw new AlreadyClosedException("this IndexReader cannot be used anymore as one of its child readers was closed");
        }
    }

    public final boolean equals(Object obj) {
        return this == obj;
    }

    public final int hashCode() {
        return System.identityHashCode(this);
    }

    @Deprecated
    public static DirectoryReader open(Directory directory) throws IOException {
        return DirectoryReader.open(directory);
    }

    @Deprecated
    public static DirectoryReader open(Directory directory, int termInfosIndexDivisor) throws IOException {
        return DirectoryReader.open(directory, termInfosIndexDivisor);
    }

    @Deprecated
    public static DirectoryReader open(IndexWriter writer, boolean applyAllDeletes) throws IOException {
        return DirectoryReader.open(writer, applyAllDeletes);
    }

    @Deprecated
    public static DirectoryReader open(IndexCommit commit) throws IOException {
        return DirectoryReader.open(commit);
    }

    @Deprecated
    public static DirectoryReader open(IndexCommit commit, int termInfosIndexDivisor) throws IOException {
        return DirectoryReader.open(commit, termInfosIndexDivisor);
    }

    public abstract Fields getTermVectors(int var1) throws IOException;

    public final Terms getTermVector(int docID, String field) throws IOException {
        Fields vectors = this.getTermVectors(docID);
        if (vectors == null) {
            return null;
        }
        return vectors.terms(field);
    }

    public abstract int numDocs();

    public abstract int maxDoc();

    public final int numDeletedDocs() {
        return this.maxDoc() - this.numDocs();
    }

    public abstract void document(int var1, StoredFieldVisitor var2) throws IOException;

    public final Document document(int docID) throws IOException {
        DocumentStoredFieldVisitor visitor = new DocumentStoredFieldVisitor();
        this.document(docID, visitor);
        return visitor.getDocument();
    }

    public final Document document(int docID, Set<String> fieldsToLoad) throws IOException {
        DocumentStoredFieldVisitor visitor = new DocumentStoredFieldVisitor(fieldsToLoad);
        this.document(docID, visitor);
        return visitor.getDocument();
    }

    public boolean hasDeletions() {
        return this.numDeletedDocs() > 0;
    }

    @Override
    public final synchronized void close() throws IOException {
        if (!this.closed) {
            this.decRef();
            this.closed = true;
        }
    }

    protected abstract void doClose() throws IOException;

    public abstract IndexReaderContext getContext();

    public final List<AtomicReaderContext> leaves() {
        return this.getContext().leaves();
    }

    public Object getCoreCacheKey() {
        return this;
    }

    public Object getCombinedCoreAndDeletesKey() {
        return this;
    }

    public abstract int docFreq(Term var1) throws IOException;

    public abstract long totalTermFreq(Term var1) throws IOException;

    public abstract long getSumDocFreq(String var1) throws IOException;

    public abstract int getDocCount(String var1) throws IOException;

    public abstract long getSumTotalTermFreq(String var1) throws IOException;

    public static interface ReaderClosedListener {
        public void onClose(IndexReader var1);
    }
}

