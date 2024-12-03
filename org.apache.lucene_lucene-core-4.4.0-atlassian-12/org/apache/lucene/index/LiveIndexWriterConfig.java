/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.index.ConcurrentMergeScheduler;
import org.apache.lucene.index.DocumentsWriterPerThread;
import org.apache.lucene.index.DocumentsWriterPerThreadPool;
import org.apache.lucene.index.FlushByRamOrCountsPolicy;
import org.apache.lucene.index.FlushPolicy;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexDeletionPolicy;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.KeepOnlyLastCommitDeletionPolicy;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.index.MergeScheduler;
import org.apache.lucene.index.ThreadAffinityDocumentsWriterThreadPool;
import org.apache.lucene.index.TieredMergePolicy;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.InfoStream;
import org.apache.lucene.util.Version;

public class LiveIndexWriterConfig {
    private final Analyzer analyzer;
    private volatile int maxBufferedDocs;
    private volatile double ramBufferSizeMB;
    private volatile int maxBufferedDeleteTerms;
    private volatile int readerTermsIndexDivisor;
    private volatile IndexWriter.IndexReaderWarmer mergedSegmentWarmer;
    private volatile int termIndexInterval;
    protected volatile IndexDeletionPolicy delPolicy;
    protected volatile IndexCommit commit;
    protected volatile IndexWriterConfig.OpenMode openMode;
    protected volatile Similarity similarity;
    protected volatile MergeScheduler mergeScheduler;
    protected volatile long writeLockTimeout;
    protected volatile DocumentsWriterPerThread.IndexingChain indexingChain;
    protected volatile Codec codec;
    protected volatile InfoStream infoStream;
    protected volatile MergePolicy mergePolicy;
    protected volatile DocumentsWriterPerThreadPool indexerThreadPool;
    protected volatile boolean readerPooling;
    protected volatile FlushPolicy flushPolicy;
    protected volatile int perThreadHardLimitMB;
    protected final Version matchVersion;
    protected volatile boolean useCompoundFile = true;

    LiveIndexWriterConfig(Analyzer analyzer, Version matchVersion) {
        this.analyzer = analyzer;
        this.matchVersion = matchVersion;
        this.ramBufferSizeMB = 16.0;
        this.maxBufferedDocs = -1;
        this.maxBufferedDeleteTerms = -1;
        this.readerTermsIndexDivisor = 1;
        this.mergedSegmentWarmer = null;
        this.termIndexInterval = 32;
        this.delPolicy = new KeepOnlyLastCommitDeletionPolicy();
        this.commit = null;
        this.useCompoundFile = true;
        this.openMode = IndexWriterConfig.OpenMode.CREATE_OR_APPEND;
        this.similarity = IndexSearcher.getDefaultSimilarity();
        this.mergeScheduler = new ConcurrentMergeScheduler();
        this.writeLockTimeout = IndexWriterConfig.WRITE_LOCK_TIMEOUT;
        this.indexingChain = DocumentsWriterPerThread.defaultIndexingChain;
        this.codec = Codec.getDefault();
        if (this.codec == null) {
            throw new NullPointerException();
        }
        this.infoStream = InfoStream.getDefault();
        this.mergePolicy = new TieredMergePolicy();
        this.flushPolicy = new FlushByRamOrCountsPolicy();
        this.readerPooling = false;
        this.indexerThreadPool = new ThreadAffinityDocumentsWriterThreadPool(8);
        this.perThreadHardLimitMB = 1945;
    }

    LiveIndexWriterConfig(IndexWriterConfig config) {
        this.maxBufferedDeleteTerms = config.getMaxBufferedDeleteTerms();
        this.maxBufferedDocs = config.getMaxBufferedDocs();
        this.mergedSegmentWarmer = config.getMergedSegmentWarmer();
        this.ramBufferSizeMB = config.getRAMBufferSizeMB();
        this.readerTermsIndexDivisor = config.getReaderTermsIndexDivisor();
        this.termIndexInterval = config.getTermIndexInterval();
        this.matchVersion = config.matchVersion;
        this.analyzer = config.getAnalyzer();
        this.delPolicy = config.getIndexDeletionPolicy();
        this.commit = config.getIndexCommit();
        this.openMode = config.getOpenMode();
        this.similarity = config.getSimilarity();
        this.mergeScheduler = config.getMergeScheduler();
        this.writeLockTimeout = config.getWriteLockTimeout();
        this.indexingChain = config.getIndexingChain();
        this.codec = config.getCodec();
        this.infoStream = config.getInfoStream();
        this.mergePolicy = config.getMergePolicy();
        this.indexerThreadPool = config.getIndexerThreadPool();
        this.readerPooling = config.getReaderPooling();
        this.flushPolicy = config.getFlushPolicy();
        this.perThreadHardLimitMB = config.getRAMPerThreadHardLimitMB();
        this.useCompoundFile = config.getUseCompoundFile();
    }

    public Analyzer getAnalyzer() {
        return this.analyzer;
    }

    public LiveIndexWriterConfig setTermIndexInterval(int interval) {
        this.termIndexInterval = interval;
        return this;
    }

    public int getTermIndexInterval() {
        return this.termIndexInterval;
    }

    public LiveIndexWriterConfig setMaxBufferedDeleteTerms(int maxBufferedDeleteTerms) {
        if (maxBufferedDeleteTerms != -1 && maxBufferedDeleteTerms < 1) {
            throw new IllegalArgumentException("maxBufferedDeleteTerms must at least be 1 when enabled");
        }
        this.maxBufferedDeleteTerms = maxBufferedDeleteTerms;
        return this;
    }

    public int getMaxBufferedDeleteTerms() {
        return this.maxBufferedDeleteTerms;
    }

    public LiveIndexWriterConfig setRAMBufferSizeMB(double ramBufferSizeMB) {
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

    public LiveIndexWriterConfig setMaxBufferedDocs(int maxBufferedDocs) {
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

    public LiveIndexWriterConfig setMergedSegmentWarmer(IndexWriter.IndexReaderWarmer mergeSegmentWarmer) {
        this.mergedSegmentWarmer = mergeSegmentWarmer;
        return this;
    }

    public IndexWriter.IndexReaderWarmer getMergedSegmentWarmer() {
        return this.mergedSegmentWarmer;
    }

    public LiveIndexWriterConfig setReaderTermsIndexDivisor(int divisor) {
        if (divisor <= 0 && divisor != -1) {
            throw new IllegalArgumentException("divisor must be >= 1, or -1 (got " + divisor + ")");
        }
        this.readerTermsIndexDivisor = divisor;
        return this;
    }

    public int getReaderTermsIndexDivisor() {
        return this.readerTermsIndexDivisor;
    }

    public IndexWriterConfig.OpenMode getOpenMode() {
        return this.openMode;
    }

    public IndexDeletionPolicy getIndexDeletionPolicy() {
        return this.delPolicy;
    }

    public IndexCommit getIndexCommit() {
        return this.commit;
    }

    public Similarity getSimilarity() {
        return this.similarity;
    }

    public MergeScheduler getMergeScheduler() {
        return this.mergeScheduler;
    }

    public long getWriteLockTimeout() {
        return this.writeLockTimeout;
    }

    public Codec getCodec() {
        return this.codec;
    }

    public MergePolicy getMergePolicy() {
        return this.mergePolicy;
    }

    DocumentsWriterPerThreadPool getIndexerThreadPool() {
        return this.indexerThreadPool;
    }

    public int getMaxThreadStates() {
        try {
            return ((ThreadAffinityDocumentsWriterThreadPool)this.indexerThreadPool).getMaxThreadStates();
        }
        catch (ClassCastException cce) {
            throw new IllegalStateException(cce);
        }
    }

    public boolean getReaderPooling() {
        return this.readerPooling;
    }

    DocumentsWriterPerThread.IndexingChain getIndexingChain() {
        return this.indexingChain;
    }

    public int getRAMPerThreadHardLimitMB() {
        return this.perThreadHardLimitMB;
    }

    FlushPolicy getFlushPolicy() {
        return this.flushPolicy;
    }

    public InfoStream getInfoStream() {
        return this.infoStream;
    }

    public LiveIndexWriterConfig setUseCompoundFile(boolean useCompoundFile) {
        this.useCompoundFile = useCompoundFile;
        return this;
    }

    public boolean getUseCompoundFile() {
        return this.useCompoundFile;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("matchVersion=").append((Object)this.matchVersion).append("\n");
        sb.append("analyzer=").append(this.analyzer == null ? "null" : this.analyzer.getClass().getName()).append("\n");
        sb.append("ramBufferSizeMB=").append(this.getRAMBufferSizeMB()).append("\n");
        sb.append("maxBufferedDocs=").append(this.getMaxBufferedDocs()).append("\n");
        sb.append("maxBufferedDeleteTerms=").append(this.getMaxBufferedDeleteTerms()).append("\n");
        sb.append("mergedSegmentWarmer=").append(this.getMergedSegmentWarmer()).append("\n");
        sb.append("readerTermsIndexDivisor=").append(this.getReaderTermsIndexDivisor()).append("\n");
        sb.append("termIndexInterval=").append(this.getTermIndexInterval()).append("\n");
        sb.append("delPolicy=").append(this.getIndexDeletionPolicy().getClass().getName()).append("\n");
        IndexCommit commit = this.getIndexCommit();
        sb.append("commit=").append(commit == null ? "null" : commit).append("\n");
        sb.append("openMode=").append((Object)this.getOpenMode()).append("\n");
        sb.append("similarity=").append(this.getSimilarity().getClass().getName()).append("\n");
        sb.append("mergeScheduler=").append(this.getMergeScheduler()).append("\n");
        sb.append("default WRITE_LOCK_TIMEOUT=").append(IndexWriterConfig.WRITE_LOCK_TIMEOUT).append("\n");
        sb.append("writeLockTimeout=").append(this.getWriteLockTimeout()).append("\n");
        sb.append("codec=").append(this.getCodec()).append("\n");
        sb.append("infoStream=").append(this.getInfoStream().getClass().getName()).append("\n");
        sb.append("mergePolicy=").append(this.getMergePolicy()).append("\n");
        sb.append("indexerThreadPool=").append(this.getIndexerThreadPool()).append("\n");
        sb.append("readerPooling=").append(this.getReaderPooling()).append("\n");
        sb.append("perThreadHardLimitMB=").append(this.getRAMPerThreadHardLimitMB()).append("\n");
        sb.append("useCompoundFile=").append(this.getUseCompoundFile()).append("\n");
        return sb.toString();
    }
}

