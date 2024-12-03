/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.PrintStream;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.index.DocumentsWriterPerThread;
import org.apache.lucene.index.DocumentsWriterPerThreadPool;
import org.apache.lucene.index.FlushPolicy;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexDeletionPolicy;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.LiveIndexWriterConfig;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.index.MergeScheduler;
import org.apache.lucene.index.ThreadAffinityDocumentsWriterThreadPool;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.InfoStream;
import org.apache.lucene.util.PrintStreamInfoStream;
import org.apache.lucene.util.Version;

public final class IndexWriterConfig
extends LiveIndexWriterConfig
implements Cloneable {
    public static final int DEFAULT_TERM_INDEX_INTERVAL = 32;
    public static final int DISABLE_AUTO_FLUSH = -1;
    public static final int DEFAULT_MAX_BUFFERED_DELETE_TERMS = -1;
    public static final int DEFAULT_MAX_BUFFERED_DOCS = -1;
    public static final double DEFAULT_RAM_BUFFER_SIZE_MB = 16.0;
    public static long WRITE_LOCK_TIMEOUT = 1000L;
    public static final boolean DEFAULT_READER_POOLING = false;
    public static final int DEFAULT_READER_TERMS_INDEX_DIVISOR = 1;
    public static final int DEFAULT_RAM_PER_THREAD_HARD_LIMIT_MB = 1945;
    public static final int DEFAULT_MAX_THREAD_STATES = 8;
    public static final boolean DEFAULT_USE_COMPOUND_FILE_SYSTEM = true;

    public static void setDefaultWriteLockTimeout(long writeLockTimeout) {
        WRITE_LOCK_TIMEOUT = writeLockTimeout;
    }

    public static long getDefaultWriteLockTimeout() {
        return WRITE_LOCK_TIMEOUT;
    }

    public IndexWriterConfig(Version matchVersion, Analyzer analyzer) {
        super(analyzer, matchVersion);
    }

    public IndexWriterConfig clone() {
        try {
            IndexWriterConfig clone = (IndexWriterConfig)super.clone();
            clone.delPolicy = this.delPolicy.clone();
            clone.flushPolicy = this.flushPolicy.clone();
            clone.indexerThreadPool = this.indexerThreadPool.clone();
            clone.infoStream = this.infoStream.clone();
            clone.mergePolicy = this.mergePolicy.clone();
            clone.mergeScheduler = this.mergeScheduler.clone();
            return clone;
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public IndexWriterConfig setOpenMode(OpenMode openMode) {
        if (openMode == null) {
            throw new IllegalArgumentException("openMode must not be null");
        }
        this.openMode = openMode;
        return this;
    }

    @Override
    public OpenMode getOpenMode() {
        return this.openMode;
    }

    public IndexWriterConfig setIndexDeletionPolicy(IndexDeletionPolicy delPolicy) {
        if (delPolicy == null) {
            throw new IllegalArgumentException("indexDeletionPolicy must not be null");
        }
        this.delPolicy = delPolicy;
        return this;
    }

    @Override
    public IndexDeletionPolicy getIndexDeletionPolicy() {
        return this.delPolicy;
    }

    public IndexWriterConfig setIndexCommit(IndexCommit commit) {
        this.commit = commit;
        return this;
    }

    @Override
    public IndexCommit getIndexCommit() {
        return this.commit;
    }

    public IndexWriterConfig setSimilarity(Similarity similarity) {
        if (similarity == null) {
            throw new IllegalArgumentException("similarity must not be null");
        }
        this.similarity = similarity;
        return this;
    }

    @Override
    public Similarity getSimilarity() {
        return this.similarity;
    }

    public IndexWriterConfig setMergeScheduler(MergeScheduler mergeScheduler) {
        if (mergeScheduler == null) {
            throw new IllegalArgumentException("mergeScheduler must not be null");
        }
        this.mergeScheduler = mergeScheduler;
        return this;
    }

    @Override
    public MergeScheduler getMergeScheduler() {
        return this.mergeScheduler;
    }

    public IndexWriterConfig setWriteLockTimeout(long writeLockTimeout) {
        this.writeLockTimeout = writeLockTimeout;
        return this;
    }

    @Override
    public long getWriteLockTimeout() {
        return this.writeLockTimeout;
    }

    public IndexWriterConfig setMergePolicy(MergePolicy mergePolicy) {
        if (mergePolicy == null) {
            throw new IllegalArgumentException("mergePolicy must not be null");
        }
        this.mergePolicy = mergePolicy;
        return this;
    }

    public IndexWriterConfig setCodec(Codec codec) {
        if (codec == null) {
            throw new IllegalArgumentException("codec must not be null");
        }
        this.codec = codec;
        return this;
    }

    @Override
    public Codec getCodec() {
        return this.codec;
    }

    @Override
    public MergePolicy getMergePolicy() {
        return this.mergePolicy;
    }

    IndexWriterConfig setIndexerThreadPool(DocumentsWriterPerThreadPool threadPool) {
        if (threadPool == null) {
            throw new IllegalArgumentException("threadPool must not be null");
        }
        this.indexerThreadPool = threadPool;
        return this;
    }

    @Override
    DocumentsWriterPerThreadPool getIndexerThreadPool() {
        return this.indexerThreadPool;
    }

    public IndexWriterConfig setMaxThreadStates(int maxThreadStates) {
        this.indexerThreadPool = new ThreadAffinityDocumentsWriterThreadPool(maxThreadStates);
        return this;
    }

    @Override
    public int getMaxThreadStates() {
        try {
            return ((ThreadAffinityDocumentsWriterThreadPool)this.indexerThreadPool).getMaxThreadStates();
        }
        catch (ClassCastException cce) {
            throw new IllegalStateException(cce);
        }
    }

    public IndexWriterConfig setReaderPooling(boolean readerPooling) {
        this.readerPooling = readerPooling;
        return this;
    }

    @Override
    public boolean getReaderPooling() {
        return this.readerPooling;
    }

    IndexWriterConfig setIndexingChain(DocumentsWriterPerThread.IndexingChain indexingChain) {
        if (indexingChain == null) {
            throw new IllegalArgumentException("indexingChain must not be null");
        }
        this.indexingChain = indexingChain;
        return this;
    }

    @Override
    DocumentsWriterPerThread.IndexingChain getIndexingChain() {
        return this.indexingChain;
    }

    IndexWriterConfig setFlushPolicy(FlushPolicy flushPolicy) {
        if (flushPolicy == null) {
            throw new IllegalArgumentException("flushPolicy must not be null");
        }
        this.flushPolicy = flushPolicy;
        return this;
    }

    public IndexWriterConfig setRAMPerThreadHardLimitMB(int perThreadHardLimitMB) {
        if (perThreadHardLimitMB <= 0 || perThreadHardLimitMB >= 2048) {
            throw new IllegalArgumentException("PerThreadHardLimit must be greater than 0 and less than 2048MB");
        }
        this.perThreadHardLimitMB = perThreadHardLimitMB;
        return this;
    }

    @Override
    public int getRAMPerThreadHardLimitMB() {
        return this.perThreadHardLimitMB;
    }

    @Override
    FlushPolicy getFlushPolicy() {
        return this.flushPolicy;
    }

    @Override
    public InfoStream getInfoStream() {
        return this.infoStream;
    }

    @Override
    public Analyzer getAnalyzer() {
        return super.getAnalyzer();
    }

    @Override
    public int getMaxBufferedDeleteTerms() {
        return super.getMaxBufferedDeleteTerms();
    }

    @Override
    public int getMaxBufferedDocs() {
        return super.getMaxBufferedDocs();
    }

    @Override
    public IndexWriter.IndexReaderWarmer getMergedSegmentWarmer() {
        return super.getMergedSegmentWarmer();
    }

    @Override
    public double getRAMBufferSizeMB() {
        return super.getRAMBufferSizeMB();
    }

    @Override
    public int getReaderTermsIndexDivisor() {
        return super.getReaderTermsIndexDivisor();
    }

    @Override
    public int getTermIndexInterval() {
        return super.getTermIndexInterval();
    }

    public IndexWriterConfig setInfoStream(InfoStream infoStream) {
        if (infoStream == null) {
            throw new IllegalArgumentException("Cannot set InfoStream implementation to null. To disable logging use InfoStream.NO_OUTPUT");
        }
        this.infoStream = infoStream;
        return this;
    }

    public IndexWriterConfig setInfoStream(PrintStream printStream) {
        if (printStream == null) {
            throw new IllegalArgumentException("printStream must not be null");
        }
        return this.setInfoStream(new PrintStreamInfoStream(printStream));
    }

    @Override
    public IndexWriterConfig setMaxBufferedDeleteTerms(int maxBufferedDeleteTerms) {
        return (IndexWriterConfig)super.setMaxBufferedDeleteTerms(maxBufferedDeleteTerms);
    }

    @Override
    public IndexWriterConfig setMaxBufferedDocs(int maxBufferedDocs) {
        return (IndexWriterConfig)super.setMaxBufferedDocs(maxBufferedDocs);
    }

    @Override
    public IndexWriterConfig setMergedSegmentWarmer(IndexWriter.IndexReaderWarmer mergeSegmentWarmer) {
        return (IndexWriterConfig)super.setMergedSegmentWarmer(mergeSegmentWarmer);
    }

    @Override
    public IndexWriterConfig setRAMBufferSizeMB(double ramBufferSizeMB) {
        return (IndexWriterConfig)super.setRAMBufferSizeMB(ramBufferSizeMB);
    }

    @Override
    public IndexWriterConfig setReaderTermsIndexDivisor(int divisor) {
        return (IndexWriterConfig)super.setReaderTermsIndexDivisor(divisor);
    }

    @Override
    public IndexWriterConfig setTermIndexInterval(int interval) {
        return (IndexWriterConfig)super.setTermIndexInterval(interval);
    }

    @Override
    public IndexWriterConfig setUseCompoundFile(boolean useCompoundFile) {
        return (IndexWriterConfig)super.setUseCompoundFile(useCompoundFile);
    }

    public static enum OpenMode {
        CREATE,
        APPEND,
        CREATE_OR_APPEND;

    }
}

