/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.document.Document;
import com.atlassian.lucene36.document.FieldSelector;
import com.atlassian.lucene36.index.CorruptIndexException;
import com.atlassian.lucene36.index.DirectoryReader;
import com.atlassian.lucene36.index.FieldInfos;
import com.atlassian.lucene36.index.IndexCommit;
import com.atlassian.lucene36.index.IndexDeletionPolicy;
import com.atlassian.lucene36.index.IndexWriter;
import com.atlassian.lucene36.index.SegmentInfos;
import com.atlassian.lucene36.index.StaleReaderException;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.index.TermDocs;
import com.atlassian.lucene36.index.TermEnum;
import com.atlassian.lucene36.index.TermFreqVector;
import com.atlassian.lucene36.index.TermPositions;
import com.atlassian.lucene36.index.TermVectorMapper;
import com.atlassian.lucene36.search.Similarity;
import com.atlassian.lucene36.store.AlreadyClosedException;
import com.atlassian.lucene36.store.Directory;
import com.atlassian.lucene36.store.LockObtainFailedException;
import com.atlassian.lucene36.util.VirtualMethod;
import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class IndexReader
implements Cloneable,
Closeable {
    private final Set<ReaderClosedListener> readerClosedListeners = Collections.synchronizedSet(new LinkedHashSet());
    private boolean closed = false;
    protected boolean hasChanges;
    private final AtomicInteger refCount = new AtomicInteger();
    static int DEFAULT_TERMS_INDEX_DIVISOR = 1;
    @Deprecated
    private static final VirtualMethod<IndexReader> reopenMethod1 = new VirtualMethod<IndexReader>(IndexReader.class, "reopen", new Class[0]);
    @Deprecated
    private static final VirtualMethod<IndexReader> doOpenIfChangedMethod1 = new VirtualMethod<IndexReader>(IndexReader.class, "doOpenIfChanged", new Class[0]);
    @Deprecated
    private final boolean hasNewReopenAPI1 = VirtualMethod.compareImplementationDistance(this.getClass(), doOpenIfChangedMethod1, reopenMethod1) >= 0;
    @Deprecated
    private static final VirtualMethod<IndexReader> reopenMethod2 = new VirtualMethod<IndexReader>(IndexReader.class, "reopen", Boolean.TYPE);
    @Deprecated
    private static final VirtualMethod<IndexReader> doOpenIfChangedMethod2 = new VirtualMethod<IndexReader>(IndexReader.class, "doOpenIfChanged", Boolean.TYPE);
    @Deprecated
    private final boolean hasNewReopenAPI2 = VirtualMethod.compareImplementationDistance(this.getClass(), doOpenIfChangedMethod2, reopenMethod2) >= 0;
    @Deprecated
    private static final VirtualMethod<IndexReader> reopenMethod3 = new VirtualMethod<IndexReader>(IndexReader.class, "reopen", IndexCommit.class);
    @Deprecated
    private static final VirtualMethod<IndexReader> doOpenIfChangedMethod3 = new VirtualMethod<IndexReader>(IndexReader.class, "doOpenIfChanged", IndexCommit.class);
    @Deprecated
    private final boolean hasNewReopenAPI3 = VirtualMethod.compareImplementationDistance(this.getClass(), doOpenIfChangedMethod3, reopenMethod3) >= 0;
    @Deprecated
    private static final VirtualMethod<IndexReader> reopenMethod4 = new VirtualMethod<IndexReader>(IndexReader.class, "reopen", IndexWriter.class, Boolean.TYPE);
    @Deprecated
    private static final VirtualMethod<IndexReader> doOpenIfChangedMethod4 = new VirtualMethod<IndexReader>(IndexReader.class, "doOpenIfChanged", IndexWriter.class, Boolean.TYPE);
    @Deprecated
    private final boolean hasNewReopenAPI4 = VirtualMethod.compareImplementationDistance(this.getClass(), doOpenIfChangedMethod4, reopenMethod4) >= 0;

    public final void addReaderClosedListener(ReaderClosedListener listener) {
        this.ensureOpen();
        this.readerClosedListeners.add(listener);
    }

    public final void removeReaderClosedListener(ReaderClosedListener listener) {
        this.ensureOpen();
        this.readerClosedListeners.remove(listener);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final void notifyReaderClosedListeners() {
        Set<ReaderClosedListener> set = this.readerClosedListeners;
        synchronized (set) {
            for (ReaderClosedListener listener : this.readerClosedListeners) {
                listener.onClose(this);
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

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        if (this.hasChanges) {
            buffer.append('*');
        }
        buffer.append(this.getClass().getSimpleName());
        buffer.append('(');
        IndexReader[] subReaders = this.getSequentialSubReaders();
        if (subReaders != null && subReaders.length > 0) {
            buffer.append(subReaders[0]);
            for (int i = 1; i < subReaders.length; ++i) {
                buffer.append(" ").append(subReaders[i]);
            }
        }
        buffer.append(')');
        return buffer.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void decRef() throws IOException {
        this.ensureOpen();
        int rc = this.refCount.decrementAndGet();
        if (rc == 0) {
            boolean success = false;
            try {
                this.commit();
                this.doClose();
                success = true;
            }
            finally {
                if (!success) {
                    this.refCount.incrementAndGet();
                }
            }
            this.notifyReaderClosedListeners();
        } else if (rc < 0) {
            throw new IllegalStateException("too many decRef calls: refCount is " + rc + " after decrement");
        }
    }

    protected IndexReader() {
        this.refCount.set(1);
    }

    protected final void ensureOpen() throws AlreadyClosedException {
        if (this.refCount.get() <= 0) {
            throw new AlreadyClosedException("this IndexReader is closed");
        }
    }

    public static IndexReader open(Directory directory) throws CorruptIndexException, IOException {
        return DirectoryReader.open(directory, null, null, true, DEFAULT_TERMS_INDEX_DIVISOR);
    }

    @Deprecated
    public static IndexReader open(Directory directory, boolean readOnly) throws CorruptIndexException, IOException {
        return DirectoryReader.open(directory, null, null, readOnly, DEFAULT_TERMS_INDEX_DIVISOR);
    }

    public static IndexReader open(IndexWriter writer, boolean applyAllDeletes) throws CorruptIndexException, IOException {
        return writer.getReader(applyAllDeletes);
    }

    public static IndexReader open(IndexCommit commit) throws CorruptIndexException, IOException {
        return DirectoryReader.open(commit.getDirectory(), null, commit, true, DEFAULT_TERMS_INDEX_DIVISOR);
    }

    @Deprecated
    public static IndexReader open(IndexCommit commit, boolean readOnly) throws CorruptIndexException, IOException {
        return DirectoryReader.open(commit.getDirectory(), null, commit, readOnly, DEFAULT_TERMS_INDEX_DIVISOR);
    }

    @Deprecated
    public static IndexReader open(Directory directory, IndexDeletionPolicy deletionPolicy, boolean readOnly) throws CorruptIndexException, IOException {
        return DirectoryReader.open(directory, deletionPolicy, null, readOnly, DEFAULT_TERMS_INDEX_DIVISOR);
    }

    @Deprecated
    public static IndexReader open(Directory directory, IndexDeletionPolicy deletionPolicy, boolean readOnly, int termInfosIndexDivisor) throws CorruptIndexException, IOException {
        return DirectoryReader.open(directory, deletionPolicy, null, readOnly, termInfosIndexDivisor);
    }

    @Deprecated
    public static IndexReader open(IndexCommit commit, IndexDeletionPolicy deletionPolicy, boolean readOnly) throws CorruptIndexException, IOException {
        return DirectoryReader.open(commit.getDirectory(), deletionPolicy, commit, readOnly, DEFAULT_TERMS_INDEX_DIVISOR);
    }

    @Deprecated
    public static IndexReader open(IndexCommit commit, IndexDeletionPolicy deletionPolicy, boolean readOnly, int termInfosIndexDivisor) throws CorruptIndexException, IOException {
        return DirectoryReader.open(commit.getDirectory(), deletionPolicy, commit, readOnly, termInfosIndexDivisor);
    }

    public static IndexReader open(Directory directory, int termInfosIndexDivisor) throws CorruptIndexException, IOException {
        return DirectoryReader.open(directory, null, null, true, termInfosIndexDivisor);
    }

    public static IndexReader open(IndexCommit commit, int termInfosIndexDivisor) throws CorruptIndexException, IOException {
        return DirectoryReader.open(commit.getDirectory(), null, commit, true, termInfosIndexDivisor);
    }

    public static IndexReader openIfChanged(IndexReader oldReader) throws IOException {
        if (oldReader.hasNewReopenAPI1) {
            IndexReader newReader = oldReader.doOpenIfChanged();
            assert (newReader != oldReader);
            return newReader;
        }
        IndexReader newReader = oldReader.reopen();
        if (newReader == oldReader) {
            return null;
        }
        return newReader;
    }

    @Deprecated
    public static IndexReader openIfChanged(IndexReader oldReader, boolean readOnly) throws IOException {
        if (oldReader.hasNewReopenAPI2) {
            IndexReader newReader = oldReader.doOpenIfChanged(readOnly);
            assert (newReader != oldReader);
            return newReader;
        }
        IndexReader newReader = oldReader.reopen(readOnly);
        if (newReader == oldReader) {
            return null;
        }
        return newReader;
    }

    public static IndexReader openIfChanged(IndexReader oldReader, IndexCommit commit) throws IOException {
        if (oldReader.hasNewReopenAPI3) {
            IndexReader newReader = oldReader.doOpenIfChanged(commit);
            assert (newReader != oldReader);
            return newReader;
        }
        IndexReader newReader = oldReader.reopen(commit);
        if (newReader == oldReader) {
            return null;
        }
        return newReader;
    }

    public static IndexReader openIfChanged(IndexReader oldReader, IndexWriter writer, boolean applyAllDeletes) throws IOException {
        if (oldReader.hasNewReopenAPI4) {
            IndexReader newReader = oldReader.doOpenIfChanged(writer, applyAllDeletes);
            assert (newReader != oldReader);
            return newReader;
        }
        IndexReader newReader = oldReader.reopen(writer, applyAllDeletes);
        if (newReader == oldReader) {
            return null;
        }
        return newReader;
    }

    @Deprecated
    public IndexReader reopen() throws CorruptIndexException, IOException {
        IndexReader newReader = IndexReader.openIfChanged(this);
        if (newReader == null) {
            return this;
        }
        return newReader;
    }

    @Deprecated
    public IndexReader reopen(boolean openReadOnly) throws CorruptIndexException, IOException {
        IndexReader newReader = IndexReader.openIfChanged(this, openReadOnly);
        if (newReader == null) {
            return this;
        }
        return newReader;
    }

    @Deprecated
    public IndexReader reopen(IndexCommit commit) throws CorruptIndexException, IOException {
        IndexReader newReader = IndexReader.openIfChanged(this, commit);
        if (newReader == null) {
            return this;
        }
        return newReader;
    }

    @Deprecated
    public IndexReader reopen(IndexWriter writer, boolean applyAllDeletes) throws CorruptIndexException, IOException {
        IndexReader newReader = IndexReader.openIfChanged(this, writer, applyAllDeletes);
        if (newReader == null) {
            return this;
        }
        return newReader;
    }

    protected IndexReader doOpenIfChanged() throws CorruptIndexException, IOException {
        throw new UnsupportedOperationException("This reader does not support reopen().");
    }

    @Deprecated
    protected IndexReader doOpenIfChanged(boolean openReadOnly) throws CorruptIndexException, IOException {
        throw new UnsupportedOperationException("This reader does not support reopen().");
    }

    protected IndexReader doOpenIfChanged(IndexCommit commit) throws CorruptIndexException, IOException {
        throw new UnsupportedOperationException("This reader does not support reopen(IndexCommit).");
    }

    protected IndexReader doOpenIfChanged(IndexWriter writer, boolean applyAllDeletes) throws CorruptIndexException, IOException {
        return writer.getReader(applyAllDeletes);
    }

    public synchronized Object clone() {
        throw new UnsupportedOperationException("This reader does not implement clone()");
    }

    @Deprecated
    public synchronized IndexReader clone(boolean openReadOnly) throws CorruptIndexException, IOException {
        throw new UnsupportedOperationException("This reader does not implement clone()");
    }

    public Directory directory() {
        this.ensureOpen();
        throw new UnsupportedOperationException("This reader does not support this method.");
    }

    @Deprecated
    public static long lastModified(final Directory directory2) throws CorruptIndexException, IOException {
        return (Long)new SegmentInfos.FindSegmentsFile(directory2){

            public Object doBody(String segmentFileName) throws IOException {
                return directory2.fileModified(segmentFileName);
            }
        }.run();
    }

    @Deprecated
    public static long getCurrentVersion(Directory directory) throws CorruptIndexException, IOException {
        return SegmentInfos.readCurrentVersion(directory);
    }

    @Deprecated
    public static Map<String, String> getCommitUserData(Directory directory) throws CorruptIndexException, IOException {
        SegmentInfos sis = new SegmentInfos();
        sis.read(directory);
        return sis.getUserData();
    }

    public long getVersion() {
        throw new UnsupportedOperationException("This reader does not support this method.");
    }

    @Deprecated
    public Map<String, String> getCommitUserData() {
        throw new UnsupportedOperationException("This reader does not support this method.");
    }

    public boolean isCurrent() throws CorruptIndexException, IOException {
        throw new UnsupportedOperationException("This reader does not support this method.");
    }

    @Deprecated
    public boolean isOptimized() {
        throw new UnsupportedOperationException("This reader does not support this method.");
    }

    public abstract TermFreqVector[] getTermFreqVectors(int var1) throws IOException;

    public abstract TermFreqVector getTermFreqVector(int var1, String var2) throws IOException;

    public abstract void getTermFreqVector(int var1, String var2, TermVectorMapper var3) throws IOException;

    public abstract void getTermFreqVector(int var1, TermVectorMapper var2) throws IOException;

    public static boolean indexExists(Directory directory) throws IOException {
        try {
            new SegmentInfos().read(directory);
            return true;
        }
        catch (IOException ioe) {
            return false;
        }
    }

    public abstract int numDocs();

    public abstract int maxDoc();

    public final int numDeletedDocs() {
        return this.maxDoc() - this.numDocs();
    }

    public final Document document(int n) throws CorruptIndexException, IOException {
        this.ensureOpen();
        if (n < 0 || n >= this.maxDoc()) {
            throw new IllegalArgumentException("docID must be >= 0 and < maxDoc=" + this.maxDoc() + " (got docID=" + n + ")");
        }
        return this.document(n, null);
    }

    public abstract Document document(int var1, FieldSelector var2) throws CorruptIndexException, IOException;

    public abstract boolean isDeleted(int var1);

    public abstract boolean hasDeletions();

    public boolean hasNorms(String field) throws IOException {
        this.ensureOpen();
        return this.norms(field) != null;
    }

    public abstract byte[] norms(String var1) throws IOException;

    public abstract void norms(String var1, byte[] var2, int var3) throws IOException;

    @Deprecated
    public final synchronized void setNorm(int doc, String field, byte value) throws StaleReaderException, CorruptIndexException, LockObtainFailedException, IOException {
        this.ensureOpen();
        this.acquireWriteLock();
        this.hasChanges = true;
        this.doSetNorm(doc, field, value);
    }

    @Deprecated
    protected abstract void doSetNorm(int var1, String var2, byte var3) throws CorruptIndexException, IOException;

    @Deprecated
    public final void setNorm(int doc, String field, float value) throws StaleReaderException, CorruptIndexException, LockObtainFailedException, IOException {
        this.ensureOpen();
        this.setNorm(doc, field, Similarity.getDefault().encodeNormValue(value));
    }

    public abstract TermEnum terms() throws IOException;

    public abstract TermEnum terms(Term var1) throws IOException;

    public abstract int docFreq(Term var1) throws IOException;

    public TermDocs termDocs(Term term) throws IOException {
        this.ensureOpen();
        TermDocs termDocs = this.termDocs();
        termDocs.seek(term);
        return termDocs;
    }

    public abstract TermDocs termDocs() throws IOException;

    public final TermPositions termPositions(Term term) throws IOException {
        this.ensureOpen();
        TermPositions termPositions = this.termPositions();
        termPositions.seek(term);
        return termPositions;
    }

    public abstract TermPositions termPositions() throws IOException;

    @Deprecated
    public final synchronized void deleteDocument(int docNum) throws StaleReaderException, CorruptIndexException, LockObtainFailedException, IOException {
        this.ensureOpen();
        this.acquireWriteLock();
        this.hasChanges = true;
        this.doDelete(docNum);
    }

    @Deprecated
    protected abstract void doDelete(int var1) throws CorruptIndexException, IOException;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Deprecated
    public final int deleteDocuments(Term term) throws StaleReaderException, CorruptIndexException, LockObtainFailedException, IOException {
        this.ensureOpen();
        TermDocs docs = this.termDocs(term);
        if (docs == null) {
            return 0;
        }
        int n = 0;
        try {
            while (docs.next()) {
                this.deleteDocument(docs.doc());
                ++n;
            }
        }
        finally {
            docs.close();
        }
        return n;
    }

    @Deprecated
    public final synchronized void undeleteAll() throws StaleReaderException, CorruptIndexException, LockObtainFailedException, IOException {
        this.ensureOpen();
        this.acquireWriteLock();
        this.hasChanges = true;
        this.doUndeleteAll();
    }

    @Deprecated
    protected abstract void doUndeleteAll() throws CorruptIndexException, IOException;

    @Deprecated
    protected synchronized void acquireWriteLock() throws IOException {
    }

    @Deprecated
    public final synchronized void flush() throws IOException {
        this.ensureOpen();
        this.commit();
    }

    @Deprecated
    public final synchronized void flush(Map<String, String> commitUserData) throws IOException {
        this.ensureOpen();
        this.commit(commitUserData);
    }

    @Deprecated
    protected final synchronized void commit() throws IOException {
        this.commit(null);
    }

    @Deprecated
    public final synchronized void commit(Map<String, String> commitUserData) throws IOException {
        this.doCommit(commitUserData);
        this.hasChanges = false;
    }

    @Deprecated
    protected abstract void doCommit(Map<String, String> var1) throws IOException;

    @Override
    public final synchronized void close() throws IOException {
        if (!this.closed) {
            this.decRef();
            this.closed = true;
        }
    }

    protected abstract void doClose() throws IOException;

    public abstract FieldInfos getFieldInfos();

    public IndexCommit getIndexCommit() throws IOException {
        throw new UnsupportedOperationException("This reader does not support this method.");
    }

    public static Collection<IndexCommit> listCommits(Directory dir) throws IOException {
        return DirectoryReader.listCommits(dir);
    }

    public IndexReader[] getSequentialSubReaders() {
        this.ensureOpen();
        return null;
    }

    public Object getCoreCacheKey() {
        return this;
    }

    public Object getDeletesCacheKey() {
        return this;
    }

    public long getUniqueTermCount() throws IOException {
        throw new UnsupportedOperationException("this reader does not implement getUniqueTermCount()");
    }

    public int getTermInfosIndexDivisor() {
        throw new UnsupportedOperationException("This reader does not support this method.");
    }

    public static interface ReaderClosedListener {
        public void onClose(IndexReader var1);
    }
}

