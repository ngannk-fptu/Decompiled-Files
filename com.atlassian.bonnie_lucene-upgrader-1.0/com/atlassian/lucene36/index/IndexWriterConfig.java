/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.analysis.Analyzer;
import com.atlassian.lucene36.index.ConcurrentMergeScheduler;
import com.atlassian.lucene36.index.DocumentsWriter;
import com.atlassian.lucene36.index.IndexCommit;
import com.atlassian.lucene36.index.IndexDeletionPolicy;
import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.IndexWriter;
import com.atlassian.lucene36.index.KeepOnlyLastCommitDeletionPolicy;
import com.atlassian.lucene36.index.LogByteSizeMergePolicy;
import com.atlassian.lucene36.index.MergePolicy;
import com.atlassian.lucene36.index.MergeScheduler;
import com.atlassian.lucene36.index.TieredMergePolicy;
import com.atlassian.lucene36.search.Similarity;
import com.atlassian.lucene36.util.Version;

public final class IndexWriterConfig
implements Cloneable {
    public static final int DEFAULT_TERM_INDEX_INTERVAL = 128;
    public static final int DISABLE_AUTO_FLUSH = -1;
    public static final int DEFAULT_MAX_BUFFERED_DELETE_TERMS = -1;
    public static final int DEFAULT_MAX_BUFFERED_DOCS = -1;
    public static final double DEFAULT_RAM_BUFFER_SIZE_MB = 16.0;
    public static long WRITE_LOCK_TIMEOUT = 1000L;
    public static final int DEFAULT_MAX_THREAD_STATES = 8;
    public static final boolean DEFAULT_READER_POOLING = false;
    public static final int DEFAULT_READER_TERMS_INDEX_DIVISOR = IndexReader.DEFAULT_TERMS_INDEX_DIVISOR;
    private final Analyzer analyzer;
    private volatile IndexDeletionPolicy delPolicy;
    private volatile IndexCommit commit;
    private volatile OpenMode openMode;
    private volatile Similarity similarity;
    private volatile int termIndexInterval;
    private volatile MergeScheduler mergeScheduler;
    private volatile long writeLockTimeout;
    private volatile int maxBufferedDeleteTerms;
    private volatile double ramBufferSizeMB;
    private volatile int maxBufferedDocs;
    private volatile DocumentsWriter.IndexingChain indexingChain;
    private volatile IndexWriter.IndexReaderWarmer mergedSegmentWarmer;
    private volatile MergePolicy mergePolicy;
    private volatile int maxThreadStates;
    private volatile boolean readerPooling;
    private volatile int readerTermsIndexDivisor;
    private Version matchVersion;

    public static void setDefaultWriteLockTimeout(long writeLockTimeout) {
        WRITE_LOCK_TIMEOUT = writeLockTimeout;
    }

    public static long getDefaultWriteLockTimeout() {
        return WRITE_LOCK_TIMEOUT;
    }

    public IndexWriterConfig(Version matchVersion, Analyzer analyzer) {
        this.matchVersion = matchVersion;
        this.analyzer = analyzer;
        this.delPolicy = new KeepOnlyLastCommitDeletionPolicy();
        this.commit = null;
        this.openMode = OpenMode.CREATE_OR_APPEND;
        this.similarity = Similarity.getDefault();
        this.termIndexInterval = 128;
        this.mergeScheduler = new ConcurrentMergeScheduler();
        this.writeLockTimeout = WRITE_LOCK_TIMEOUT;
        this.maxBufferedDeleteTerms = -1;
        this.ramBufferSizeMB = 16.0;
        this.maxBufferedDocs = -1;
        this.indexingChain = DocumentsWriter.defaultIndexingChain;
        this.mergedSegmentWarmer = null;
        this.mergePolicy = matchVersion.onOrAfter(Version.LUCENE_32) ? new TieredMergePolicy() : new LogByteSizeMergePolicy();
        this.maxThreadStates = 8;
        this.readerPooling = false;
        this.readerTermsIndexDivisor = DEFAULT_READER_TERMS_INDEX_DIVISOR;
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public Analyzer getAnalyzer() {
        return this.analyzer;
    }

    public IndexWriterConfig setOpenMode(OpenMode openMode) {
        this.openMode = openMode;
        return this;
    }

    public OpenMode getOpenMode() {
        return this.openMode;
    }

    public IndexWriterConfig setIndexDeletionPolicy(IndexDeletionPolicy delPolicy) {
        this.delPolicy = delPolicy == null ? new KeepOnlyLastCommitDeletionPolicy() : delPolicy;
        return this;
    }

    public IndexDeletionPolicy getIndexDeletionPolicy() {
        return this.delPolicy;
    }

    public IndexWriterConfig setIndexCommit(IndexCommit commit) {
        this.commit = commit;
        return this;
    }

    public IndexCommit getIndexCommit() {
        return this.commit;
    }

    public IndexWriterConfig setSimilarity(Similarity similarity) {
        this.similarity = similarity == null ? Similarity.getDefault() : similarity;
        return this;
    }

    public Similarity getSimilarity() {
        return this.similarity;
    }

    public IndexWriterConfig setTermIndexInterval(int interval) {
        this.termIndexInterval = interval;
        return this;
    }

    public int getTermIndexInterval() {
        return this.termIndexInterval;
    }

    public IndexWriterConfig setMergeScheduler(MergeScheduler mergeScheduler) {
        this.mergeScheduler = mergeScheduler == null ? new ConcurrentMergeScheduler() : mergeScheduler;
        return this;
    }

    public MergeScheduler getMergeScheduler() {
        return this.mergeScheduler;
    }

    public IndexWriterConfig setWriteLockTimeout(long writeLockTimeout) {
        this.writeLockTimeout = writeLockTimeout;
        return this;
    }

    public long getWriteLockTimeout() {
        return this.writeLockTimeout;
    }

    public IndexWriterConfig setMaxBufferedDeleteTerms(int maxBufferedDeleteTerms) {
        if (maxBufferedDeleteTerms != -1 && maxBufferedDeleteTerms < 1) {
            throw new IllegalArgumentException("maxBufferedDeleteTerms must at least be 1 when enabled");
        }
        this.maxBufferedDeleteTerms = maxBufferedDeleteTerms;
        return this;
    }

    public int getMaxBufferedDeleteTerms() {
        return this.maxBufferedDeleteTerms;
    }

    public IndexWriterConfig setRAMBufferSizeMB(double ramBufferSizeMB) {
        if (ramBufferSizeMB > 2048.0) {
            throw new IllegalArgumentException("ramBufferSize " + ramBufferSizeMB + " is too large; should be comfortably less than 2048");
        }
        if (ramBufferSizeMB != -1.0 && ramBufferSizeMB <= 0.0) {
            throw new IllegalArgumentException("ramBufferSize should be > 0.0 MB when enabled");
        }
        if (ramBufferSizeMB == -1.0 && this.maxBufferedDocs == -1) {
            throw new IllegalArgumentException("at least one of ramBufferSize and maxBufferedDocs must be enabled");
        }
        this.ramBufferSizeMB = ramBufferSizeMB;
        return this;
    }

    public double getRAMBufferSizeMB() {
        return this.ramBufferSizeMB;
    }

    public IndexWriterConfig setMaxBufferedDocs(int maxBufferedDocs) {
        if (maxBufferedDocs != -1 && maxBufferedDocs < 2) {
            throw new IllegalArgumentException("maxBufferedDocs must at least be 2 when enabled");
        }
        if (maxBufferedDocs == -1 && this.ramBufferSizeMB == -1.0) {
            throw new IllegalArgumentException("at least one of ramBufferSize and maxBufferedDocs must be enabled");
        }
        this.maxBufferedDocs = maxBufferedDocs;
        return this;
    }

    public int getMaxBufferedDocs() {
        return this.maxBufferedDocs;
    }

    public IndexWriterConfig setMergedSegmentWarmer(IndexWriter.IndexReaderWarmer mergeSegmentWarmer) {
        this.mergedSegmentWarmer = mergeSegmentWarmer;
        return this;
    }

    public IndexWriter.IndexReaderWarmer getMergedSegmentWarmer() {
        return this.mergedSegmentWarmer;
    }

    public IndexWriterConfig setMergePolicy(MergePolicy mergePolicy) {
        this.mergePolicy = mergePolicy == null ? new LogByteSizeMergePolicy() : mergePolicy;
        return this;
    }

    public MergePolicy getMergePolicy() {
        return this.mergePolicy;
    }

    public IndexWriterConfig setMaxThreadStates(int maxThreadStates) {
        this.maxThreadStates = maxThreadStates < 1 ? 8 : maxThreadStates;
        return this;
    }

    public int getMaxThreadStates() {
        return this.maxThreadStates;
    }

    public IndexWriterConfig setReaderPooling(boolean readerPooling) {
        this.readerPooling = readerPooling;
        return this;
    }

    public boolean getReaderPooling() {
        return this.readerPooling;
    }

    IndexWriterConfig setIndexingChain(DocumentsWriter.IndexingChain indexingChain) {
        this.indexingChain = indexingChain == null ? DocumentsWriter.defaultIndexingChain : indexingChain;
        return this;
    }

    DocumentsWriter.IndexingChain getIndexingChain() {
        return this.indexingChain;
    }

    public IndexWriterConfig setReaderTermsIndexDivisor(int divisor) {
        if (divisor <= 0 && divisor != -1) {
            throw new IllegalArgumentException("divisor must be >= 1, or -1 (got " + divisor + ")");
        }
        this.readerTermsIndexDivisor = divisor;
        return this;
    }

    public int getReaderTermsIndexDivisor() {
        return this.readerTermsIndexDivisor;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("matchVersion=").append((Object)this.matchVersion).append("\n");
        sb.append("analyzer=").append(this.analyzer == null ? "null" : this.analyzer.getClass().getName()).append("\n");
        sb.append("delPolicy=").append(this.delPolicy.getClass().getName()).append("\n");
        sb.append("commit=").append(this.commit == null ? "null" : this.commit).append("\n");
        sb.append("openMode=").append((Object)this.openMode).append("\n");
        sb.append("similarity=").append(this.similarity.getClass().getName()).append("\n");
        sb.append("termIndexInterval=").append(this.termIndexInterval).append("\n");
        sb.append("mergeScheduler=").append(this.mergeScheduler.getClass().getName()).append("\n");
        sb.append("default WRITE_LOCK_TIMEOUT=").append(WRITE_LOCK_TIMEOUT).append("\n");
        sb.append("writeLockTimeout=").append(this.writeLockTimeout).append("\n");
        sb.append("maxBufferedDeleteTerms=").append(this.maxBufferedDeleteTerms).append("\n");
        sb.append("ramBufferSizeMB=").append(this.ramBufferSizeMB).append("\n");
        sb.append("maxBufferedDocs=").append(this.maxBufferedDocs).append("\n");
        sb.append("mergedSegmentWarmer=").append(this.mergedSegmentWarmer).append("\n");
        sb.append("mergePolicy=").append(this.mergePolicy).append("\n");
        sb.append("maxThreadStates=").append(this.maxThreadStates).append("\n");
        sb.append("readerPooling=").append(this.readerPooling).append("\n");
        sb.append("readerTermsIndexDivisor=").append(this.readerTermsIndexDivisor).append("\n");
        return sb.toString();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum OpenMode {
        CREATE,
        APPEND,
        CREATE_OR_APPEND;

    }
}

