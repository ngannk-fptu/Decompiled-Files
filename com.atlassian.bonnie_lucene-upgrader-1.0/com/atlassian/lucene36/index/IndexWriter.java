/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.analysis.Analyzer;
import com.atlassian.lucene36.document.Document;
import com.atlassian.lucene36.index.BufferedDeletesStream;
import com.atlassian.lucene36.index.CompoundFileReader;
import com.atlassian.lucene36.index.CorruptIndexException;
import com.atlassian.lucene36.index.DocumentsWriter;
import com.atlassian.lucene36.index.FieldInfos;
import com.atlassian.lucene36.index.IndexCommit;
import com.atlassian.lucene36.index.IndexDeletionPolicy;
import com.atlassian.lucene36.index.IndexFileDeleter;
import com.atlassian.lucene36.index.IndexFileNames;
import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.IndexWriterConfig;
import com.atlassian.lucene36.index.LogDocMergePolicy;
import com.atlassian.lucene36.index.LogMergePolicy;
import com.atlassian.lucene36.index.MergePolicy;
import com.atlassian.lucene36.index.MergeScheduler;
import com.atlassian.lucene36.index.PayloadProcessorProvider;
import com.atlassian.lucene36.index.ReadOnlyDirectoryReader;
import com.atlassian.lucene36.index.SegmentInfo;
import com.atlassian.lucene36.index.SegmentInfos;
import com.atlassian.lucene36.index.SegmentMerger;
import com.atlassian.lucene36.index.SegmentReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.Query;
import com.atlassian.lucene36.search.Similarity;
import com.atlassian.lucene36.store.AlreadyClosedException;
import com.atlassian.lucene36.store.Directory;
import com.atlassian.lucene36.store.Lock;
import com.atlassian.lucene36.store.LockObtainFailedException;
import com.atlassian.lucene36.util.Constants;
import com.atlassian.lucene36.util.StringHelper;
import com.atlassian.lucene36.util.ThreadInterruptedException;
import com.atlassian.lucene36.util.TwoPhaseCommit;
import com.atlassian.lucene36.util.Version;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class IndexWriter
implements Closeable,
TwoPhaseCommit {
    @Deprecated
    public static long WRITE_LOCK_TIMEOUT = IndexWriterConfig.WRITE_LOCK_TIMEOUT;
    private long writeLockTimeout;
    public static final String WRITE_LOCK_NAME = "write.lock";
    @Deprecated
    public static final int DISABLE_AUTO_FLUSH = -1;
    @Deprecated
    public static final int DEFAULT_MAX_BUFFERED_DOCS = -1;
    @Deprecated
    public static final double DEFAULT_RAM_BUFFER_SIZE_MB = 16.0;
    @Deprecated
    public static final int DEFAULT_MAX_BUFFERED_DELETE_TERMS = -1;
    @Deprecated
    public static final int DEFAULT_MAX_FIELD_LENGTH = MaxFieldLength.UNLIMITED.getLimit();
    @Deprecated
    public static final int DEFAULT_TERM_INDEX_INTERVAL = 128;
    public static final int MAX_TERM_LENGTH = 16383;
    private static final int MERGE_READ_BUFFER_SIZE = 4096;
    private static final AtomicInteger MESSAGE_ID = new AtomicInteger();
    private int messageID;
    private volatile boolean hitOOM;
    private final Directory directory;
    private final Analyzer analyzer;
    private Similarity similarity;
    private volatile long changeCount;
    private long lastCommitChangeCount;
    private List<SegmentInfo> rollbackSegments;
    volatile SegmentInfos pendingCommit;
    volatile long pendingCommitChangeCount;
    final SegmentInfos segmentInfos;
    private Collection<String> filesToCommit;
    private DocumentsWriter docWriter;
    private IndexFileDeleter deleter;
    private Map<SegmentInfo, Boolean> segmentsToMerge;
    private int mergeMaxNumSegments;
    private Lock writeLock;
    private volatile boolean closed;
    private volatile boolean closing;
    private HashSet<SegmentInfo> mergingSegments;
    private MergePolicy mergePolicy;
    private MergeScheduler mergeScheduler;
    private LinkedList<MergePolicy.OneMerge> pendingMerges;
    private Set<MergePolicy.OneMerge> runningMerges;
    private List<MergePolicy.OneMerge> mergeExceptions;
    private long mergeGen;
    private boolean stopMerges;
    private final AtomicInteger flushCount;
    private final AtomicInteger flushDeletesCount;
    final ReaderPool readerPool;
    final BufferedDeletesStream bufferedDeletesStream;
    private volatile boolean poolReaders;
    private final IndexWriterConfig config;
    private PayloadProcessorProvider payloadProcessorProvider;
    boolean anyNonBulkMerges;
    @Deprecated
    private int maxFieldLength;
    private PrintStream infoStream;
    private static PrintStream defaultInfoStream;
    private final Object commitLock;
    private boolean keepFullyDeletedSegments;
    final FlushControl flushControl;

    @Deprecated
    public IndexReader getReader() throws IOException {
        return this.getReader(this.config.getReaderTermsIndexDivisor(), true);
    }

    IndexReader getReader(boolean applyAllDeletes) throws IOException {
        return this.getReader(this.config.getReaderTermsIndexDivisor(), applyAllDeletes);
    }

    @Deprecated
    public IndexReader getReader(int termInfosIndexDivisor) throws IOException {
        return this.getReader(termInfosIndexDivisor, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    IndexReader getReader(int termInfosIndexDivisor, boolean applyAllDeletes) throws IOException {
        ReadOnlyDirectoryReader r;
        this.ensureOpen();
        long tStart = System.currentTimeMillis();
        if (this.infoStream != null) {
            this.message("flush at getReader");
        }
        this.poolReaders = true;
        IndexWriter indexWriter = this;
        synchronized (indexWriter) {
            this.flush(false, applyAllDeletes);
            r = new ReadOnlyDirectoryReader(this, this.segmentInfos, termInfosIndexDivisor, applyAllDeletes);
            if (this.infoStream != null) {
                this.message("return reader version=" + ((IndexReader)r).getVersion() + " reader=" + r);
            }
        }
        this.maybeMerge();
        if (this.infoStream != null) {
            this.message("getReader took " + (System.currentTimeMillis() - tStart) + " msec");
        }
        return r;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int numDeletedDocs(SegmentInfo info) throws IOException {
        int n;
        block5: {
            SegmentReader reader;
            block3: {
                int n2;
                block4: {
                    this.ensureOpen(false);
                    reader = this.readerPool.getIfExists(info);
                    try {
                        if (reader == null) break block3;
                        n2 = reader.numDeletedDocs();
                        Object var5_5 = null;
                        if (reader == null) break block4;
                    }
                    catch (Throwable throwable) {
                        block6: {
                            Object var5_7 = null;
                            if (reader == null) break block6;
                            this.readerPool.release(reader);
                        }
                        throw throwable;
                    }
                    this.readerPool.release(reader);
                }
                return n2;
            }
            n = info.getDelCount();
            Object var5_6 = null;
            if (reader == null) break block5;
            this.readerPool.release(reader);
        }
        return n;
    }

    protected final void ensureOpen(boolean includePendingClose) throws AlreadyClosedException {
        if (this.closed || includePendingClose && this.closing) {
            throw new AlreadyClosedException("this IndexWriter is closed");
        }
    }

    protected final void ensureOpen() throws AlreadyClosedException {
        this.ensureOpen(true);
    }

    public void message(String message) {
        if (this.infoStream != null) {
            this.infoStream.println("IW " + this.messageID + " [" + new Date() + "; " + Thread.currentThread().getName() + "]: " + message);
        }
    }

    private LogMergePolicy getLogMergePolicy() {
        if (this.mergePolicy instanceof LogMergePolicy) {
            return (LogMergePolicy)this.mergePolicy;
        }
        throw new IllegalArgumentException("this method can only be called when the merge policy is the default LogMergePolicy");
    }

    @Deprecated
    public boolean getUseCompoundFile() {
        return this.getLogMergePolicy().getUseCompoundFile();
    }

    @Deprecated
    public void setUseCompoundFile(boolean value) {
        this.getLogMergePolicy().setUseCompoundFile(value);
    }

    @Deprecated
    public void setSimilarity(Similarity similarity) {
        this.ensureOpen();
        this.similarity = similarity;
        this.docWriter.setSimilarity(similarity);
        this.config.setSimilarity(similarity);
    }

    @Deprecated
    public Similarity getSimilarity() {
        this.ensureOpen();
        return this.similarity;
    }

    @Deprecated
    public void setTermIndexInterval(int interval) {
        this.ensureOpen();
        this.config.setTermIndexInterval(interval);
    }

    @Deprecated
    public int getTermIndexInterval() {
        this.ensureOpen(false);
        return this.config.getTermIndexInterval();
    }

    @Deprecated
    public IndexWriter(Directory d, Analyzer a, boolean create, MaxFieldLength mfl) throws CorruptIndexException, LockObtainFailedException, IOException {
        this(d, new IndexWriterConfig(Version.LUCENE_31, a).setOpenMode(create ? IndexWriterConfig.OpenMode.CREATE : IndexWriterConfig.OpenMode.APPEND));
        this.setMaxFieldLength(mfl.getLimit());
    }

    @Deprecated
    public IndexWriter(Directory d, Analyzer a, MaxFieldLength mfl) throws CorruptIndexException, LockObtainFailedException, IOException {
        this(d, new IndexWriterConfig(Version.LUCENE_31, a));
        this.setMaxFieldLength(mfl.getLimit());
    }

    @Deprecated
    public IndexWriter(Directory d, Analyzer a, IndexDeletionPolicy deletionPolicy, MaxFieldLength mfl) throws CorruptIndexException, LockObtainFailedException, IOException {
        this(d, new IndexWriterConfig(Version.LUCENE_31, a).setIndexDeletionPolicy(deletionPolicy));
        this.setMaxFieldLength(mfl.getLimit());
    }

    @Deprecated
    public IndexWriter(Directory d, Analyzer a, boolean create, IndexDeletionPolicy deletionPolicy, MaxFieldLength mfl) throws CorruptIndexException, LockObtainFailedException, IOException {
        this(d, new IndexWriterConfig(Version.LUCENE_31, a).setOpenMode(create ? IndexWriterConfig.OpenMode.CREATE : IndexWriterConfig.OpenMode.APPEND).setIndexDeletionPolicy(deletionPolicy));
        this.setMaxFieldLength(mfl.getLimit());
    }

    @Deprecated
    public IndexWriter(Directory d, Analyzer a, IndexDeletionPolicy deletionPolicy, MaxFieldLength mfl, IndexCommit commit) throws CorruptIndexException, LockObtainFailedException, IOException {
        this(d, new IndexWriterConfig(Version.LUCENE_31, a).setOpenMode(IndexWriterConfig.OpenMode.APPEND).setIndexDeletionPolicy(deletionPolicy).setIndexCommit(commit));
        this.setMaxFieldLength(mfl.getLimit());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public IndexWriter(Directory d, IndexWriterConfig conf) throws CorruptIndexException, LockObtainFailedException, IOException {
        block24: {
            block25: {
                this.messageID = MESSAGE_ID.getAndIncrement();
                this.similarity = Similarity.getDefault();
                this.segmentInfos = new SegmentInfos();
                this.segmentsToMerge = new HashMap<SegmentInfo, Boolean>();
                this.mergingSegments = new HashSet();
                this.pendingMerges = new LinkedList();
                this.runningMerges = new HashSet<MergePolicy.OneMerge>();
                this.mergeExceptions = new ArrayList<MergePolicy.OneMerge>();
                this.flushCount = new AtomicInteger();
                this.flushDeletesCount = new AtomicInteger();
                this.readerPool = new ReaderPool();
                this.maxFieldLength = DEFAULT_MAX_FIELD_LENGTH;
                this.commitLock = new Object();
                this.flushControl = new FlushControl();
                this.config = (IndexWriterConfig)conf.clone();
                this.directory = d;
                this.analyzer = conf.getAnalyzer();
                this.infoStream = defaultInfoStream;
                this.writeLockTimeout = conf.getWriteLockTimeout();
                this.similarity = conf.getSimilarity();
                this.mergePolicy = conf.getMergePolicy();
                this.mergePolicy.setIndexWriter(this);
                this.mergeScheduler = conf.getMergeScheduler();
                this.bufferedDeletesStream = new BufferedDeletesStream(this.messageID);
                this.bufferedDeletesStream.setInfoStream(this.infoStream);
                this.poolReaders = conf.getReaderPooling();
                this.writeLock = this.directory.makeLock(WRITE_LOCK_NAME);
                if (!this.writeLock.obtain(this.writeLockTimeout)) {
                    throw new LockObtainFailedException("Index locked for write: " + this.writeLock);
                }
                boolean success = false;
                try {
                    boolean create;
                    IndexWriterConfig.OpenMode mode = conf.getOpenMode();
                    if (mode == IndexWriterConfig.OpenMode.CREATE) {
                        create = true;
                    } else if (mode == IndexWriterConfig.OpenMode.APPEND) {
                        create = false;
                    } else {
                        boolean bl = create = !IndexReader.indexExists(this.directory);
                    }
                    if (create) {
                        try {
                            this.segmentInfos.read(this.directory);
                            this.segmentInfos.clear();
                        }
                        catch (IOException e) {
                            // empty catch block
                        }
                        ++this.changeCount;
                        this.segmentInfos.changed();
                    } else {
                        this.segmentInfos.read(this.directory);
                        IndexCommit commit = conf.getIndexCommit();
                        if (commit != null) {
                            if (commit.getDirectory() != this.directory) {
                                throw new IllegalArgumentException("IndexCommit's directory doesn't match my directory");
                            }
                            SegmentInfos oldInfos = new SegmentInfos();
                            oldInfos.read(this.directory, commit.getSegmentsFileName());
                            this.segmentInfos.replace(oldInfos);
                            ++this.changeCount;
                            this.segmentInfos.changed();
                            if (this.infoStream != null) {
                                this.message("init: loaded commit \"" + commit.getSegmentsFileName() + "\"");
                            }
                        }
                    }
                    this.rollbackSegments = this.segmentInfos.createBackupSegmentInfos(true);
                    this.docWriter = new DocumentsWriter(this.config, this.directory, this, this.getCurrentFieldInfos(), this.bufferedDeletesStream);
                    this.docWriter.setInfoStream(this.infoStream);
                    this.docWriter.setMaxFieldLength(this.maxFieldLength);
                    IndexWriter indexWriter = this;
                    synchronized (indexWriter) {
                        this.deleter = new IndexFileDeleter(this.directory, conf.getIndexDeletionPolicy(), this.segmentInfos, this.infoStream, this);
                    }
                    if (this.deleter.startingCommitDeleted) {
                        ++this.changeCount;
                        this.segmentInfos.changed();
                    }
                    if (this.infoStream != null) {
                        this.messageState();
                    }
                    success = true;
                    Object var10_10 = null;
                    if (success) break block24;
                    if (this.infoStream == null) break block25;
                    this.message("init: hit exception on init; releasing write lock");
                }
                catch (Throwable throwable) {
                    Object var10_11 = null;
                    if (!success) {
                        if (this.infoStream != null) {
                            this.message("init: hit exception on init; releasing write lock");
                        }
                        try {
                            this.writeLock.release();
                        }
                        catch (Throwable t) {
                            // empty catch block
                        }
                        this.writeLock = null;
                    }
                    throw throwable;
                }
            }
            try {
                this.writeLock.release();
            }
            catch (Throwable t) {
                // empty catch block
            }
            this.writeLock = null;
            {
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private FieldInfos getFieldInfos(SegmentInfo info) throws IOException {
        FieldInfos fieldInfos;
        Directory cfsDir = null;
        try {
            cfsDir = info.getUseCompoundFile() ? new CompoundFileReader(this.directory, IndexFileNames.segmentFileName(info.name, "cfs")) : this.directory;
            fieldInfos = new FieldInfos(cfsDir, IndexFileNames.segmentFileName(info.name, "fnm"));
            Object var5_4 = null;
        }
        catch (Throwable throwable) {
            block3: {
                Object var5_5 = null;
                if (!info.getUseCompoundFile() || cfsDir == null) break block3;
                cfsDir.close();
            }
            throw throwable;
        }
        if (info.getUseCompoundFile() && cfsDir != null) {
            cfsDir.close();
        }
        return fieldInfos;
    }

    private FieldInfos getCurrentFieldInfos() throws IOException {
        FieldInfos fieldInfos;
        if (this.segmentInfos.size() > 0) {
            if (this.segmentInfos.getFormat() > -9) {
                fieldInfos = new FieldInfos();
                for (SegmentInfo info : this.segmentInfos) {
                    FieldInfos segFieldInfos = this.getFieldInfos(info);
                    int fieldCount = segFieldInfos.size();
                    for (int fieldNumber = 0; fieldNumber < fieldCount; ++fieldNumber) {
                        fieldInfos.add(segFieldInfos.fieldInfo(fieldNumber));
                    }
                }
            } else {
                fieldInfos = this.getFieldInfos(this.segmentInfos.info(this.segmentInfos.size() - 1));
            }
        } else {
            fieldInfos = new FieldInfos();
        }
        return fieldInfos;
    }

    public IndexWriterConfig getConfig() {
        this.ensureOpen(false);
        return this.config;
    }

    @Deprecated
    public void setMergePolicy(MergePolicy mp) {
        this.ensureOpen();
        if (mp == null) {
            throw new NullPointerException("MergePolicy must be non-null");
        }
        if (this.mergePolicy != mp) {
            this.mergePolicy.close();
        }
        this.mergePolicy = mp;
        this.mergePolicy.setIndexWriter(this);
        this.pushMaxBufferedDocs();
        if (this.infoStream != null) {
            this.message("setMergePolicy " + mp);
        }
        this.config.setMergePolicy(mp);
    }

    @Deprecated
    public MergePolicy getMergePolicy() {
        this.ensureOpen();
        return this.mergePolicy;
    }

    @Deprecated
    public synchronized void setMergeScheduler(MergeScheduler mergeScheduler) throws CorruptIndexException, IOException {
        this.ensureOpen();
        if (mergeScheduler == null) {
            throw new NullPointerException("MergeScheduler must be non-null");
        }
        if (this.mergeScheduler != mergeScheduler) {
            this.finishMerges(true);
            this.mergeScheduler.close();
        }
        this.mergeScheduler = mergeScheduler;
        if (this.infoStream != null) {
            this.message("setMergeScheduler " + mergeScheduler);
        }
        this.config.setMergeScheduler(mergeScheduler);
    }

    @Deprecated
    public MergeScheduler getMergeScheduler() {
        this.ensureOpen();
        return this.mergeScheduler;
    }

    @Deprecated
    public void setMaxMergeDocs(int maxMergeDocs) {
        this.getLogMergePolicy().setMaxMergeDocs(maxMergeDocs);
    }

    @Deprecated
    public int getMaxMergeDocs() {
        return this.getLogMergePolicy().getMaxMergeDocs();
    }

    @Deprecated
    public void setMaxFieldLength(int maxFieldLength) {
        this.ensureOpen();
        this.maxFieldLength = maxFieldLength;
        this.docWriter.setMaxFieldLength(maxFieldLength);
        if (this.infoStream != null) {
            this.message("setMaxFieldLength " + maxFieldLength);
        }
    }

    @Deprecated
    public int getMaxFieldLength() {
        this.ensureOpen();
        return this.maxFieldLength;
    }

    @Deprecated
    public void setReaderTermsIndexDivisor(int divisor) {
        this.ensureOpen();
        this.config.setReaderTermsIndexDivisor(divisor);
        if (this.infoStream != null) {
            this.message("setReaderTermsIndexDivisor " + divisor);
        }
    }

    @Deprecated
    public int getReaderTermsIndexDivisor() {
        this.ensureOpen();
        return this.config.getReaderTermsIndexDivisor();
    }

    @Deprecated
    public void setMaxBufferedDocs(int maxBufferedDocs) {
        this.ensureOpen();
        this.pushMaxBufferedDocs();
        if (this.infoStream != null) {
            this.message("setMaxBufferedDocs " + maxBufferedDocs);
        }
        this.config.setMaxBufferedDocs(maxBufferedDocs);
    }

    private void pushMaxBufferedDocs() {
        MergePolicy mp;
        if (this.config.getMaxBufferedDocs() != -1 && (mp = this.mergePolicy) instanceof LogDocMergePolicy) {
            LogDocMergePolicy lmp = (LogDocMergePolicy)mp;
            int maxBufferedDocs = this.config.getMaxBufferedDocs();
            if (lmp.getMinMergeDocs() != maxBufferedDocs) {
                if (this.infoStream != null) {
                    this.message("now push maxBufferedDocs " + maxBufferedDocs + " to LogDocMergePolicy");
                }
                lmp.setMinMergeDocs(maxBufferedDocs);
            }
        }
    }

    @Deprecated
    public int getMaxBufferedDocs() {
        this.ensureOpen();
        return this.config.getMaxBufferedDocs();
    }

    @Deprecated
    public void setRAMBufferSizeMB(double mb) {
        if (this.infoStream != null) {
            this.message("setRAMBufferSizeMB " + mb);
        }
        this.config.setRAMBufferSizeMB(mb);
    }

    @Deprecated
    public double getRAMBufferSizeMB() {
        return this.config.getRAMBufferSizeMB();
    }

    @Deprecated
    public void setMaxBufferedDeleteTerms(int maxBufferedDeleteTerms) {
        this.ensureOpen();
        if (this.infoStream != null) {
            this.message("setMaxBufferedDeleteTerms " + maxBufferedDeleteTerms);
        }
        this.config.setMaxBufferedDeleteTerms(maxBufferedDeleteTerms);
    }

    @Deprecated
    public int getMaxBufferedDeleteTerms() {
        this.ensureOpen();
        return this.config.getMaxBufferedDeleteTerms();
    }

    @Deprecated
    public void setMergeFactor(int mergeFactor) {
        this.getLogMergePolicy().setMergeFactor(mergeFactor);
    }

    @Deprecated
    public int getMergeFactor() {
        return this.getLogMergePolicy().getMergeFactor();
    }

    public static void setDefaultInfoStream(PrintStream infoStream) {
        defaultInfoStream = infoStream;
    }

    public static PrintStream getDefaultInfoStream() {
        return defaultInfoStream;
    }

    public void setInfoStream(PrintStream infoStream) throws IOException {
        this.ensureOpen();
        this.infoStream = infoStream;
        this.docWriter.setInfoStream(infoStream);
        this.deleter.setInfoStream(infoStream);
        this.bufferedDeletesStream.setInfoStream(infoStream);
        if (infoStream != null) {
            this.messageState();
        }
    }

    private void messageState() throws IOException {
        this.message("\ndir=" + this.directory + "\n" + "index=" + this.segString() + "\n" + "version=" + Constants.LUCENE_VERSION + "\n" + this.config.toString());
    }

    public PrintStream getInfoStream() {
        this.ensureOpen();
        return this.infoStream;
    }

    public boolean verbose() {
        return this.infoStream != null;
    }

    @Deprecated
    public void setWriteLockTimeout(long writeLockTimeout) {
        this.ensureOpen();
        this.writeLockTimeout = writeLockTimeout;
        this.config.setWriteLockTimeout(writeLockTimeout);
    }

    @Deprecated
    public long getWriteLockTimeout() {
        this.ensureOpen();
        return this.writeLockTimeout;
    }

    @Deprecated
    public static void setDefaultWriteLockTimeout(long writeLockTimeout) {
        IndexWriterConfig.setDefaultWriteLockTimeout(writeLockTimeout);
    }

    @Deprecated
    public static long getDefaultWriteLockTimeout() {
        return IndexWriterConfig.getDefaultWriteLockTimeout();
    }

    @Override
    public void close() throws CorruptIndexException, IOException {
        this.close(true);
    }

    public void close(boolean waitForMerges) throws CorruptIndexException, IOException {
        if (this.shouldClose()) {
            if (this.hitOOM) {
                this.rollbackInternal();
            } else {
                this.closeInternal(waitForMerges);
            }
        }
    }

    private synchronized boolean shouldClose() {
        while (!this.closed) {
            if (!this.closing) {
                this.closing = true;
                return true;
            }
            this.doWait();
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    private void closeInternal(boolean waitForMerges) throws CorruptIndexException, IOException {
        try {
            try {
                if (this.pendingCommit != null) {
                    throw new IllegalStateException("cannot close: prepareCommit was already called with no corresponding call to commit");
                }
                if (this.infoStream != null) {
                    this.message("now flush at close waitForMerges=" + waitForMerges);
                }
                this.docWriter.close();
                if (!this.hitOOM) {
                    this.flush(waitForMerges, true);
                }
                if (waitForMerges) {
                    this.mergeScheduler.merge(this);
                }
                this.mergePolicy.close();
                IndexWriter indexWriter2 = this;
                // MONITORENTER : indexWriter2
                this.finishMerges(waitForMerges);
                this.stopMerges = true;
                // MONITOREXIT : indexWriter2
                this.mergeScheduler.close();
                if (this.infoStream != null) {
                    this.message("now call final commit()");
                }
                if (!this.hitOOM) {
                    this.commitInternal(null);
                }
                if (this.infoStream != null) {
                    this.message("at close: " + this.segString());
                }
                indexWriter2 = this;
                // MONITORENTER : indexWriter2
                this.readerPool.close();
                this.docWriter = null;
                this.deleter.close();
                // MONITOREXIT : indexWriter2
                if (this.writeLock != null) {
                    this.writeLock.release();
                    this.writeLock = null;
                }
                indexWriter2 = this;
                // MONITORENTER : indexWriter2
                this.closed = true;
                // MONITOREXIT : indexWriter2
            }
            catch (OutOfMemoryError oom) {
                this.handleOOM(oom, "closeInternal");
                Object var7_5 = null;
                IndexWriter indexWriter = this;
                // MONITORENTER : indexWriter
                this.closing = false;
                this.notifyAll();
                if (!this.closed && this.infoStream != null) {
                    this.message("hit exception while closing");
                }
                // MONITOREXIT : indexWriter
                return;
            }
            Object var7_4 = null;
            IndexWriter indexWriter = this;
            // MONITORENTER : indexWriter
            this.closing = false;
            this.notifyAll();
            if (!this.closed && this.infoStream != null) {
                this.message("hit exception while closing");
            }
            // MONITOREXIT : indexWriter
            return;
        }
        catch (Throwable throwable) {
            Object var7_6 = null;
            IndexWriter indexWriter = this;
            // MONITORENTER : indexWriter
            this.closing = false;
            this.notifyAll();
            if (!this.closed && this.infoStream != null) {
                this.message("hit exception while closing");
            }
            // MONITOREXIT : indexWriter
            throw throwable;
        }
    }

    public Directory getDirectory() {
        this.ensureOpen(false);
        return this.directory;
    }

    public Analyzer getAnalyzer() {
        this.ensureOpen();
        return this.analyzer;
    }

    public synchronized int maxDoc() {
        this.ensureOpen();
        int count = this.docWriter != null ? this.docWriter.getNumDocs() : 0;
        return count += this.segmentInfos.totalDocCount();
    }

    public synchronized int numDocs() throws IOException {
        this.ensureOpen();
        int count = this.docWriter != null ? this.docWriter.getNumDocs() : 0;
        for (SegmentInfo info : this.segmentInfos) {
            count += info.docCount - this.numDeletedDocs(info);
        }
        return count;
    }

    public synchronized boolean hasDeletions() throws IOException {
        this.ensureOpen();
        if (this.bufferedDeletesStream.any()) {
            return true;
        }
        if (this.docWriter.anyDeletions()) {
            return true;
        }
        for (SegmentInfo info : this.segmentInfos) {
            if (!info.hasDeletions()) continue;
            return true;
        }
        return false;
    }

    public void addDocument(Document doc) throws CorruptIndexException, IOException {
        this.addDocument(doc, this.analyzer);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addDocument(Document doc, Analyzer analyzer) throws CorruptIndexException, IOException {
        this.ensureOpen();
        boolean doFlush = false;
        boolean success = false;
        try {
            try {
                doFlush = this.docWriter.updateDocument(doc, analyzer, null);
                success = true;
                Object var6_5 = null;
                if (!success && this.infoStream != null) {
                    this.message("hit exception adding document");
                }
            }
            catch (Throwable throwable) {
                Object var6_6 = null;
                if (!success && this.infoStream != null) {
                    this.message("hit exception adding document");
                }
                throw throwable;
            }
            if (doFlush) {
                this.flush(true, false);
            }
        }
        catch (OutOfMemoryError oom) {
            this.handleOOM(oom, "addDocument");
        }
    }

    public void addDocuments(Collection<Document> docs) throws CorruptIndexException, IOException {
        this.addDocuments(docs, this.analyzer);
    }

    public void addDocuments(Collection<Document> docs, Analyzer analyzer) throws CorruptIndexException, IOException {
        this.updateDocuments(null, docs, analyzer);
    }

    public void updateDocuments(Term delTerm, Collection<Document> docs) throws CorruptIndexException, IOException {
        this.updateDocuments(delTerm, docs, this.analyzer);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateDocuments(Term delTerm, Collection<Document> docs, Analyzer analyzer) throws CorruptIndexException, IOException {
        this.ensureOpen();
        try {
            boolean success = false;
            boolean doFlush = false;
            try {
                doFlush = this.docWriter.updateDocuments(docs, analyzer, delTerm);
                success = true;
                Object var7_7 = null;
                if (!success && this.infoStream != null) {
                    this.message("hit exception updating document");
                }
            }
            catch (Throwable throwable) {
                Object var7_8 = null;
                if (!success && this.infoStream != null) {
                    this.message("hit exception updating document");
                }
                throw throwable;
            }
            if (doFlush) {
                this.flush(true, false);
            }
        }
        catch (OutOfMemoryError oom) {
            this.handleOOM(oom, "updateDocuments");
        }
    }

    public void deleteDocuments(Term term) throws CorruptIndexException, IOException {
        this.ensureOpen();
        try {
            if (this.docWriter.deleteTerm(term, false)) {
                this.flush(true, false);
            }
        }
        catch (OutOfMemoryError oom) {
            this.handleOOM(oom, "deleteDocuments(Term)");
        }
    }

    public void deleteDocuments(Term ... terms) throws CorruptIndexException, IOException {
        this.ensureOpen();
        try {
            if (this.docWriter.deleteTerms(terms)) {
                this.flush(true, false);
            }
        }
        catch (OutOfMemoryError oom) {
            this.handleOOM(oom, "deleteDocuments(Term..)");
        }
    }

    public void deleteDocuments(Query query) throws CorruptIndexException, IOException {
        this.ensureOpen();
        try {
            if (this.docWriter.deleteQuery(query)) {
                this.flush(true, false);
            }
        }
        catch (OutOfMemoryError oom) {
            this.handleOOM(oom, "deleteDocuments(Query)");
        }
    }

    public void deleteDocuments(Query ... queries) throws CorruptIndexException, IOException {
        this.ensureOpen();
        try {
            if (this.docWriter.deleteQueries(queries)) {
                this.flush(true, false);
            }
        }
        catch (OutOfMemoryError oom) {
            this.handleOOM(oom, "deleteDocuments(Query..)");
        }
    }

    public void updateDocument(Term term, Document doc) throws CorruptIndexException, IOException {
        this.ensureOpen();
        this.updateDocument(term, doc, this.getAnalyzer());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateDocument(Term term, Document doc, Analyzer analyzer) throws CorruptIndexException, IOException {
        this.ensureOpen();
        try {
            boolean doFlush = false;
            boolean success = false;
            try {
                doFlush = this.docWriter.updateDocument(doc, analyzer, term);
                success = true;
                Object var7_7 = null;
                if (!success && this.infoStream != null) {
                    this.message("hit exception updating document");
                }
            }
            catch (Throwable throwable) {
                Object var7_8 = null;
                if (!success && this.infoStream != null) {
                    this.message("hit exception updating document");
                }
                throw throwable;
            }
            if (doFlush) {
                this.flush(true, false);
            }
        }
        catch (OutOfMemoryError oom) {
            this.handleOOM(oom, "updateDocument");
        }
    }

    final synchronized int getSegmentCount() {
        return this.segmentInfos.size();
    }

    final synchronized int getNumBufferedDocuments() {
        return this.docWriter.getNumDocs();
    }

    final synchronized int getDocCount(int i) {
        if (i >= 0 && i < this.segmentInfos.size()) {
            return this.segmentInfos.info((int)i).docCount;
        }
        return -1;
    }

    final int getFlushCount() {
        return this.flushCount.get();
    }

    final int getFlushDeletesCount() {
        return this.flushDeletesCount.get();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final String newSegmentName() {
        SegmentInfos segmentInfos = this.segmentInfos;
        synchronized (segmentInfos) {
            ++this.changeCount;
            this.segmentInfos.changed();
            return "_" + Integer.toString(this.segmentInfos.counter++, 36);
        }
    }

    @Deprecated
    public void optimize() throws CorruptIndexException, IOException {
        this.forceMerge(1, true);
    }

    @Deprecated
    public void optimize(int maxNumSegments) throws CorruptIndexException, IOException {
        this.forceMerge(maxNumSegments, true);
    }

    @Deprecated
    public void optimize(boolean doWait) throws CorruptIndexException, IOException {
        this.forceMerge(1, doWait);
    }

    public void forceMerge(int maxNumSegments) throws CorruptIndexException, IOException {
        this.forceMerge(maxNumSegments, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void forceMerge(int maxNumSegments, boolean doWait) throws CorruptIndexException, IOException {
        this.ensureOpen();
        if (maxNumSegments < 1) {
            throw new IllegalArgumentException("maxNumSegments must be >= 1; got " + maxNumSegments);
        }
        if (this.infoStream != null) {
            this.message("forceMerge: index now " + this.segString());
            this.message("now flush at forceMerge");
        }
        this.flush(true, true);
        IndexWriter indexWriter = this;
        synchronized (indexWriter) {
            this.resetMergeExceptions();
            this.segmentsToMerge.clear();
            for (SegmentInfo info : this.segmentInfos) {
                this.segmentsToMerge.put(info, Boolean.TRUE);
            }
            this.mergeMaxNumSegments = maxNumSegments;
            for (MergePolicy.OneMerge merge : this.pendingMerges) {
                merge.maxNumSegments = maxNumSegments;
                this.segmentsToMerge.put(merge.info, Boolean.TRUE);
            }
            for (MergePolicy.OneMerge merge : this.runningMerges) {
                merge.maxNumSegments = maxNumSegments;
                this.segmentsToMerge.put(merge.info, Boolean.TRUE);
            }
        }
        this.maybeMerge(maxNumSegments);
        if (doWait) {
            indexWriter = this;
            synchronized (indexWriter) {
                while (true) {
                    if (this.hitOOM) {
                        throw new IllegalStateException("this writer hit an OutOfMemoryError; cannot complete forceMerge");
                    }
                    if (this.mergeExceptions.size() > 0) {
                        int size = this.mergeExceptions.size();
                        for (int i = 0; i < size; ++i) {
                            MergePolicy.OneMerge merge = this.mergeExceptions.get(i);
                            if (merge.maxNumSegments == -1) continue;
                            IOException err = new IOException("background merge hit exception: " + merge.segString(this.directory));
                            Throwable t = merge.getException();
                            if (t != null) {
                                err.initCause(t);
                            }
                            throw err;
                        }
                    }
                    if (!this.maxNumSegmentsMergesPending()) break;
                    this.doWait();
                }
            }
            this.ensureOpen();
        }
    }

    private synchronized boolean maxNumSegmentsMergesPending() {
        for (MergePolicy.OneMerge merge : this.pendingMerges) {
            if (merge.maxNumSegments == -1) continue;
            return true;
        }
        for (MergePolicy.OneMerge merge : this.runningMerges) {
            if (merge.maxNumSegments == -1) continue;
            return true;
        }
        return false;
    }

    @Deprecated
    public void expungeDeletes(boolean doWait) throws CorruptIndexException, IOException {
        this.forceMergeDeletes(doWait);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void forceMergeDeletes(boolean doWait) throws CorruptIndexException, IOException {
        MergePolicy.MergeSpecification spec;
        this.ensureOpen();
        this.flush(true, true);
        if (this.infoStream != null) {
            this.message("forceMergeDeletes: index now " + this.segString());
        }
        IndexWriter indexWriter = this;
        synchronized (indexWriter) {
            spec = this.mergePolicy.findForcedDeletesMerges(this.segmentInfos);
            if (spec != null) {
                int numMerges = spec.merges.size();
                for (int i = 0; i < numMerges; ++i) {
                    this.registerMerge(spec.merges.get(i));
                }
            }
        }
        this.mergeScheduler.merge(this);
        if (spec != null && doWait) {
            int numMerges = spec.merges.size();
            IndexWriter indexWriter2 = this;
            synchronized (indexWriter2) {
                boolean running = true;
                while (running) {
                    if (this.hitOOM) {
                        throw new IllegalStateException("this writer hit an OutOfMemoryError; cannot complete forceMergeDeletes");
                    }
                    running = false;
                    for (int i = 0; i < numMerges; ++i) {
                        Throwable t;
                        MergePolicy.OneMerge merge = spec.merges.get(i);
                        if (this.pendingMerges.contains(merge) || this.runningMerges.contains(merge)) {
                            running = true;
                        }
                        if ((t = merge.getException()) == null) continue;
                        IOException ioe = new IOException("background merge hit exception: " + merge.segString(this.directory));
                        ioe.initCause(t);
                        throw ioe;
                    }
                    if (!running) continue;
                    this.doWait();
                }
            }
        }
    }

    @Deprecated
    public void expungeDeletes() throws CorruptIndexException, IOException {
        this.forceMergeDeletes();
    }

    public void forceMergeDeletes() throws CorruptIndexException, IOException {
        this.forceMergeDeletes(true);
    }

    public final void maybeMerge() throws CorruptIndexException, IOException {
        this.maybeMerge(-1);
    }

    private final void maybeMerge(int maxNumSegments) throws CorruptIndexException, IOException {
        this.ensureOpen(false);
        this.updatePendingMerges(maxNumSegments);
        this.mergeScheduler.merge(this);
    }

    private synchronized void updatePendingMerges(int maxNumSegments) throws CorruptIndexException, IOException {
        int i;
        int numMerges;
        MergePolicy.MergeSpecification spec;
        assert (maxNumSegments == -1 || maxNumSegments > 0);
        if (this.stopMerges) {
            return;
        }
        if (this.hitOOM) {
            return;
        }
        if (maxNumSegments != -1) {
            spec = this.mergePolicy.findForcedMerges(this.segmentInfos, maxNumSegments, Collections.unmodifiableMap(this.segmentsToMerge));
            if (spec != null) {
                numMerges = spec.merges.size();
                for (i = 0; i < numMerges; ++i) {
                    MergePolicy.OneMerge merge = spec.merges.get(i);
                    merge.maxNumSegments = maxNumSegments;
                }
            }
        } else {
            spec = this.mergePolicy.findMerges(this.segmentInfos);
        }
        if (spec != null) {
            numMerges = spec.merges.size();
            for (i = 0; i < numMerges; ++i) {
                this.registerMerge(spec.merges.get(i));
            }
        }
    }

    public synchronized Collection<SegmentInfo> getMergingSegments() {
        return this.mergingSegments;
    }

    public synchronized MergePolicy.OneMerge getNextMerge() {
        if (this.pendingMerges.size() == 0) {
            return null;
        }
        MergePolicy.OneMerge merge = this.pendingMerges.removeFirst();
        this.runningMerges.add(merge);
        return merge;
    }

    @Override
    public void rollback() throws IOException {
        this.ensureOpen();
        if (this.shouldClose()) {
            this.rollbackInternal();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    private void rollbackInternal() throws IOException {
        boolean success = false;
        if (this.infoStream != null) {
            this.message("rollback");
        }
        try {
            try {
                IndexWriter indexWriter2 = this;
                // MONITORENTER : indexWriter2
                this.finishMerges(false);
                this.stopMerges = true;
                // MONITOREXIT : indexWriter2
                if (this.infoStream != null) {
                    this.message("rollback: done finish merges");
                }
                this.mergePolicy.close();
                this.mergeScheduler.close();
                this.bufferedDeletesStream.clear();
                indexWriter2 = this;
                // MONITORENTER : indexWriter2
                if (this.pendingCommit != null) {
                    this.pendingCommit.rollbackCommit(this.directory);
                    this.deleter.decRef(this.pendingCommit);
                    this.pendingCommit = null;
                    this.notifyAll();
                }
                this.segmentInfos.rollbackSegmentInfos(this.rollbackSegments);
                if (this.infoStream != null) {
                    this.message("rollback: infos=" + this.segString(this.segmentInfos));
                }
                this.docWriter.abort();
                assert (this.testPoint("rollback before checkpoint"));
                this.deleter.checkpoint(this.segmentInfos, false);
                this.deleter.refresh();
                // MONITOREXIT : indexWriter2
                this.readerPool.clear(null);
                this.lastCommitChangeCount = this.changeCount;
                success = true;
            }
            catch (OutOfMemoryError oom) {
                this.handleOOM(oom, "rollbackInternal");
                Object var6_5 = null;
                IndexWriter indexWriter = this;
                // MONITORENTER : indexWriter
                if (!success) {
                    this.closing = false;
                    this.notifyAll();
                    if (this.infoStream != null) {
                        this.message("hit exception during rollback");
                    }
                }
                // MONITOREXIT : indexWriter
            }
            Object var6_4 = null;
            IndexWriter indexWriter = this;
            // MONITORENTER : indexWriter
            if (!success) {
                this.closing = false;
                this.notifyAll();
                if (this.infoStream != null) {
                    this.message("hit exception during rollback");
                }
            }
            // MONITOREXIT : indexWriter
        }
        catch (Throwable throwable) {
            Object var6_6 = null;
            IndexWriter indexWriter = this;
            // MONITORENTER : indexWriter
            if (!success) {
                this.closing = false;
                this.notifyAll();
                if (this.infoStream != null) {
                    this.message("hit exception during rollback");
                }
            }
            // MONITOREXIT : indexWriter
            throw throwable;
        }
        this.closeInternal(false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void deleteAll() throws IOException {
        this.ensureOpen();
        try {
            try {
                this.finishMerges(false);
                this.docWriter.abort();
                this.segmentInfos.clear();
                this.deleter.checkpoint(this.segmentInfos, false);
                this.deleter.refresh();
                this.readerPool.dropAll();
                ++this.changeCount;
                this.segmentInfos.changed();
            }
            catch (OutOfMemoryError oom) {
                this.handleOOM(oom, "deleteAll");
                Object var3_2 = null;
                if (this.infoStream != null) {
                    this.message("hit exception during deleteAll");
                }
            }
            Object var3_1 = null;
            if (this.infoStream != null) {
                this.message("hit exception during deleteAll");
            }
        }
        catch (Throwable throwable) {
            Object var3_3 = null;
            if (this.infoStream != null) {
                this.message("hit exception during deleteAll");
            }
            throw throwable;
        }
    }

    private synchronized void finishMerges(boolean waitForMerges) throws IOException {
        if (!waitForMerges) {
            this.stopMerges = true;
            for (MergePolicy.OneMerge merge : this.pendingMerges) {
                if (this.infoStream != null) {
                    this.message("now abort pending merge " + merge.segString(this.directory));
                }
                merge.abort();
                this.mergeFinish(merge);
            }
            this.pendingMerges.clear();
            for (MergePolicy.OneMerge merge : this.runningMerges) {
                if (this.infoStream != null) {
                    this.message("now abort running merge " + merge.segString(this.directory));
                }
                merge.abort();
            }
            while (this.runningMerges.size() > 0) {
                if (this.infoStream != null) {
                    this.message("now wait for " + this.runningMerges.size() + " running merge to abort");
                }
                this.doWait();
            }
            this.stopMerges = false;
            this.notifyAll();
            assert (0 == this.mergingSegments.size());
            if (this.infoStream != null) {
                this.message("all running merges have aborted");
            }
        } else {
            this.waitForMerges();
        }
    }

    public synchronized void waitForMerges() {
        this.ensureOpen(false);
        if (this.infoStream != null) {
            this.message("waitForMerges");
        }
        while (this.pendingMerges.size() > 0 || this.runningMerges.size() > 0) {
            this.doWait();
        }
        assert (0 == this.mergingSegments.size());
        if (this.infoStream != null) {
            this.message("waitForMerges done");
        }
    }

    synchronized void checkpoint() throws IOException {
        ++this.changeCount;
        this.segmentInfos.changed();
        this.deleter.checkpoint(this.segmentInfos, false);
    }

    private synchronized void resetMergeExceptions() {
        this.mergeExceptions = new ArrayList<MergePolicy.OneMerge>();
        ++this.mergeGen;
    }

    private void noDupDirs(Directory ... dirs) {
        HashSet<Directory> dups = new HashSet<Directory>();
        for (Directory dir : dirs) {
            if (dups.contains(dir)) {
                throw new IllegalArgumentException("Directory " + dir + " appears more than once");
            }
            if (dir == this.directory) {
                throw new IllegalArgumentException("Cannot add directory to itself");
            }
            dups.add(dir);
        }
    }

    @Deprecated
    public void addIndexesNoOptimize(Directory ... dirs) throws CorruptIndexException, IOException {
        this.addIndexes(dirs);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addIndexes(Directory ... dirs) throws CorruptIndexException, IOException {
        this.ensureOpen();
        this.noDupDirs(dirs);
        try {
            if (this.infoStream != null) {
                this.message("flush at addIndexes(Directory...)");
            }
            this.flush(false, true);
            ArrayList<SegmentInfo> infos = new ArrayList<SegmentInfo>();
            Comparator<String> versionComparator = StringHelper.getVersionComparator();
            for (Directory dir : dirs) {
                if (this.infoStream != null) {
                    this.message("addIndexes: process directory " + dir);
                }
                SegmentInfos sis = new SegmentInfos();
                sis.read(dir);
                HashSet<String> dsFilesCopied = new HashSet<String>();
                HashMap<String, String> dsNames = new HashMap<String, String>();
                for (SegmentInfo info : sis) {
                    assert (!infos.contains(info)) : "dup info dir=" + info.dir + " name=" + info.name;
                    String newSegName = this.newSegmentName();
                    String dsName = info.getDocStoreSegment();
                    if (this.infoStream != null) {
                        this.message("addIndexes: process segment origName=" + info.name + " newName=" + newSegName + " dsName=" + dsName + " info=" + info);
                    }
                    this.copySegmentAsIs(info, newSegName, dsNames, dsFilesCopied);
                    infos.add(info);
                }
            }
            IndexWriter indexWriter = this;
            synchronized (indexWriter) {
                this.ensureOpen();
                this.segmentInfos.addAll(infos);
                this.checkpoint();
            }
        }
        catch (OutOfMemoryError oom) {
            this.handleOOM(oom, "addIndexes(Directory...)");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addIndexes(IndexReader ... readers) throws CorruptIndexException, IOException {
        this.ensureOpen();
        try {
            boolean useCompoundFile;
            if (this.infoStream != null) {
                this.message("flush at addIndexes(IndexReader...)");
            }
            this.flush(false, true);
            String mergedName = this.newSegmentName();
            SegmentMerger merger = new SegmentMerger(this.directory, this.config.getTermIndexInterval(), mergedName, null, this.payloadProcessorProvider, (FieldInfos)this.docWriter.getFieldInfos().clone());
            for (IndexReader reader : readers) {
                merger.add(reader);
            }
            int docCount = merger.merge();
            SegmentInfo info = new SegmentInfo(mergedName, docCount, this.directory, false, true, merger.fieldInfos().hasProx(), merger.fieldInfos().hasVectors());
            this.setDiagnostics(info, "addIndexes(IndexReader...)");
            IndexWriter indexWriter = this;
            synchronized (indexWriter) {
                if (this.stopMerges) {
                    this.deleter.deleteNewFiles(info.files());
                    return;
                }
                this.ensureOpen();
                useCompoundFile = this.mergePolicy.useCompoundFile(this.segmentInfos, info);
            }
            if (useCompoundFile) {
                merger.createCompoundFile(mergedName + ".cfs", info);
                indexWriter = this;
                synchronized (indexWriter) {
                    this.deleter.deleteNewFiles(info.files());
                }
                info.setUseCompoundFile(true);
            }
            indexWriter = this;
            synchronized (indexWriter) {
                if (this.stopMerges) {
                    this.deleter.deleteNewFiles(info.files());
                    return;
                }
                this.ensureOpen();
                this.segmentInfos.add(info);
                this.checkpoint();
            }
        }
        catch (OutOfMemoryError oom) {
            this.handleOOM(oom, "addIndexes(IndexReader...)");
        }
    }

    private void copySegmentAsIs(SegmentInfo info, String segName, Map<String, String> dsNames, Set<String> dsFilesCopied) throws IOException {
        String newDsName;
        String dsName = info.getDocStoreSegment();
        if (dsName != null) {
            if (dsNames.containsKey(dsName)) {
                newDsName = dsNames.get(dsName);
            } else {
                dsNames.put(dsName, segName);
                newDsName = segName;
            }
        } else {
            newDsName = segName;
        }
        for (String file : info.files()) {
            String newFileName;
            if (IndexFileNames.isDocStoreFile(file)) {
                newFileName = newDsName + IndexFileNames.stripSegmentName(file);
                if (dsFilesCopied.contains(newFileName)) continue;
                dsFilesCopied.add(newFileName);
            } else {
                newFileName = segName + IndexFileNames.stripSegmentName(file);
            }
            assert (!this.directory.fileExists(newFileName)) : "file \"" + newFileName + "\" already exists";
            info.dir.copy(this.directory, file, newFileName);
        }
        info.setDocStore(info.getDocStoreOffset(), newDsName, info.getDocStoreIsCompoundFile());
        info.dir = this.directory;
        info.name = segName;
    }

    protected void doAfterFlush() throws IOException {
    }

    protected void doBeforeFlush() throws IOException {
    }

    @Override
    public final void prepareCommit() throws CorruptIndexException, IOException {
        this.ensureOpen();
        this.prepareCommit(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void prepareCommit(Map<String, String> commitUserData) throws CorruptIndexException, IOException {
        SegmentInfos toCommit;
        block22: {
            IndexWriter indexWriter;
            this.ensureOpen(false);
            if (this.hitOOM) {
                throw new IllegalStateException("this writer hit an OutOfMemoryError; cannot commit");
            }
            if (this.pendingCommit != null) {
                throw new IllegalStateException("prepareCommit was already called with no corresponding call to commit");
            }
            if (this.infoStream != null) {
                this.message("prepareCommit: flush");
            }
            this.ensureOpen(false);
            boolean anySegmentsFlushed = false;
            toCommit = null;
            boolean success = false;
            try {
                block20: {
                    try {
                        IndexWriter indexWriter2 = this;
                        synchronized (indexWriter2) {
                            anySegmentsFlushed = this.doFlush(true);
                            this.readerPool.commit(this.segmentInfos);
                            toCommit = (SegmentInfos)this.segmentInfos.clone();
                            this.pendingCommitChangeCount = this.changeCount;
                            this.filesToCommit = toCommit.files(this.directory, false);
                            this.deleter.incRef(this.filesToCommit);
                        }
                        success = true;
                        Object var8_8 = null;
                        if (success || this.infoStream == null) break block20;
                        this.message("hit exception during prepareCommit");
                    }
                    catch (Throwable throwable) {
                        Object var8_9 = null;
                        if (!success && this.infoStream != null) {
                            this.message("hit exception during prepareCommit");
                        }
                        this.doAfterFlush();
                        throw throwable;
                    }
                }
                this.doAfterFlush();
                {
                }
            }
            catch (OutOfMemoryError oom) {
                this.handleOOM(oom, "prepareCommit");
            }
            success = false;
            try {
                if (anySegmentsFlushed) {
                    this.maybeMerge();
                }
                success = true;
                Object var10_11 = null;
                if (success) break block22;
                indexWriter = this;
            }
            catch (Throwable throwable) {
                Object var10_12 = null;
                if (!success) {
                    IndexWriter indexWriter3 = this;
                    synchronized (indexWriter3) {
                        this.deleter.decRef(this.filesToCommit);
                        this.filesToCommit = null;
                    }
                }
                throw throwable;
            }
            synchronized (indexWriter) {
                this.deleter.decRef(this.filesToCommit);
                this.filesToCommit = null;
            }
        }
        this.startCommit(toCommit, commitUserData);
    }

    @Override
    public final void commit() throws CorruptIndexException, IOException {
        this.commit(null);
    }

    @Override
    public final void commit(Map<String, String> commitUserData) throws CorruptIndexException, IOException {
        this.ensureOpen();
        this.commitInternal(commitUserData);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final void commitInternal(Map<String, String> commitUserData) throws CorruptIndexException, IOException {
        if (this.infoStream != null) {
            this.message("commit: start");
        }
        Object object = this.commitLock;
        synchronized (object) {
            if (this.infoStream != null) {
                this.message("commit: enter lock");
            }
            if (this.pendingCommit == null) {
                if (this.infoStream != null) {
                    this.message("commit: now prepare");
                }
                this.prepareCommit(commitUserData);
            } else if (this.infoStream != null) {
                this.message("commit: already prepared");
            }
            this.finishCommit();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final synchronized void finishCommit() throws CorruptIndexException, IOException {
        block7: {
            if (this.pendingCommit != null) {
                try {
                    if (this.infoStream != null) {
                        this.message("commit: pendingCommit != null");
                    }
                    this.pendingCommit.finishCommit(this.directory);
                    if (this.infoStream != null) {
                        this.message("commit: wrote segments file \"" + this.pendingCommit.getSegmentsFileName() + "\"");
                    }
                    this.lastCommitChangeCount = this.pendingCommitChangeCount;
                    this.segmentInfos.updateGeneration(this.pendingCommit);
                    this.segmentInfos.setUserData(this.pendingCommit.getUserData());
                    this.rollbackSegments = this.pendingCommit.createBackupSegmentInfos(true);
                    this.deleter.checkpoint(this.pendingCommit, true);
                    Object var2_1 = null;
                }
                catch (Throwable throwable) {
                    Object var2_2 = null;
                    this.deleter.decRef(this.filesToCommit);
                    this.filesToCommit = null;
                    this.pendingCommit = null;
                    this.notifyAll();
                    throw throwable;
                }
                this.deleter.decRef(this.filesToCommit);
                this.filesToCommit = null;
                this.pendingCommit = null;
                this.notifyAll();
                {
                    break block7;
                }
            }
            if (this.infoStream != null) {
                this.message("commit: pendingCommit == null; skip");
            }
        }
        if (this.infoStream != null) {
            this.message("commit: done");
        }
    }

    protected final void flush(boolean triggerMerge, boolean flushDocStores, boolean flushDeletes) throws CorruptIndexException, IOException {
        this.flush(triggerMerge, flushDeletes);
    }

    protected final void flush(boolean triggerMerge, boolean applyAllDeletes) throws CorruptIndexException, IOException {
        this.ensureOpen(false);
        if (this.doFlush(applyAllDeletes) && triggerMerge) {
            this.maybeMerge();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private synchronized boolean doFlush(boolean applyAllDeletes) throws CorruptIndexException, IOException {
        if (this.hitOOM) {
            throw new IllegalStateException("this writer hit an OutOfMemoryError; cannot flush");
        }
        this.doBeforeFlush();
        assert (this.testPoint("startDoFlush"));
        this.flushControl.setFlushPendingNoWait("explicit flush");
        boolean success = false;
        try {
            SegmentInfo newSegment;
            if (this.infoStream != null) {
                this.message("  start flush: applyAllDeletes=" + applyAllDeletes);
                this.message("  index before flush " + this.segString());
            }
            if ((newSegment = this.docWriter.flush(this, this.deleter, this.mergePolicy, this.segmentInfos)) != null) {
                this.setDiagnostics(newSegment, "flush");
                this.segmentInfos.add(newSegment);
                this.checkpoint();
            }
            if (!applyAllDeletes && (this.flushControl.getFlushDeletes() || this.config.getRAMBufferSizeMB() != -1.0 && (double)this.bufferedDeletesStream.bytesUsed() > 1048576.0 * this.config.getRAMBufferSizeMB() / 2.0)) {
                applyAllDeletes = true;
                if (this.infoStream != null) {
                    this.message("force apply deletes bytesUsed=" + this.bufferedDeletesStream.bytesUsed() + " vs ramBuffer=" + 1048576.0 * this.config.getRAMBufferSizeMB());
                }
            }
            if (applyAllDeletes) {
                if (this.infoStream != null) {
                    this.message("apply all deletes during flush");
                }
                this.flushDeletesCount.incrementAndGet();
                BufferedDeletesStream.ApplyDeletesResult result = this.bufferedDeletesStream.applyDeletes(this.readerPool, this.segmentInfos.asList());
                if (result.anyDeletes) {
                    this.checkpoint();
                }
                if (!this.keepFullyDeletedSegments && result.allDeleted != null) {
                    if (this.infoStream != null) {
                        this.message("drop 100% deleted segments: " + result.allDeleted);
                    }
                    for (SegmentInfo info : result.allDeleted) {
                        if (this.mergingSegments.contains(info)) continue;
                        this.segmentInfos.remove(info);
                        if (this.readerPool == null) continue;
                        this.readerPool.drop(info);
                    }
                    this.checkpoint();
                }
                this.bufferedDeletesStream.prune(this.segmentInfos);
                assert (!this.bufferedDeletesStream.any());
                this.flushControl.clearDeletes();
            } else if (this.infoStream != null) {
                this.message("don't apply deletes now delTermCount=" + this.bufferedDeletesStream.numTerms() + " bytesUsed=" + this.bufferedDeletesStream.bytesUsed());
            }
            this.doAfterFlush();
            this.flushCount.incrementAndGet();
            success = true;
            boolean bl = newSegment != null;
            Object var8_10 = null;
            this.flushControl.clearFlushPending();
            if (!success && this.infoStream != null) {
                this.message("hit exception during flush");
            }
            return bl;
        }
        catch (OutOfMemoryError oom) {
            try {
                this.handleOOM(oom, "doFlush");
                boolean bl = false;
                Object var8_11 = null;
                this.flushControl.clearFlushPending();
                if (!success && this.infoStream != null) {
                    this.message("hit exception during flush");
                }
                return bl;
            }
            catch (Throwable throwable) {
                block21: {
                    Object var8_12 = null;
                    this.flushControl.clearFlushPending();
                    if (success || this.infoStream == null) break block21;
                    this.message("hit exception during flush");
                }
                throw throwable;
            }
        }
    }

    public final long ramSizeInBytes() {
        this.ensureOpen();
        return this.docWriter.bytesUsed() + this.bufferedDeletesStream.bytesUsed();
    }

    public final synchronized int numRamDocs() {
        this.ensureOpen();
        return this.docWriter.getNumDocs();
    }

    private void ensureValidMerge(MergePolicy.OneMerge merge) throws IOException {
        for (SegmentInfo info : merge.segments) {
            if (this.segmentInfos.contains(info)) continue;
            throw new MergePolicy.MergeException("MergePolicy selected a segment (" + info.name + ") that is not in the current index " + this.segString(), this.directory);
        }
    }

    private synchronized void commitMergedDeletes(MergePolicy.OneMerge merge, SegmentReader mergedReader) throws IOException {
        assert (this.testPoint("startCommitMergeDeletes"));
        List<SegmentInfo> sourceSegments = merge.segments;
        if (this.infoStream != null) {
            this.message("commitMergeDeletes " + merge.segString(this.directory));
        }
        int docUpto = 0;
        int delCount = 0;
        long minGen = Long.MAX_VALUE;
        for (int i = 0; i < sourceSegments.size(); ++i) {
            int j;
            SegmentInfo info = sourceSegments.get(i);
            minGen = Math.min(info.getBufferedDeletesGen(), minGen);
            int docCount = info.docCount;
            SegmentReader previousReader = merge.readerClones.get(i);
            if (previousReader == null) continue;
            SegmentReader currentReader = merge.readers.get(i);
            if (previousReader.hasDeletions()) {
                if (currentReader.numDeletedDocs() > previousReader.numDeletedDocs()) {
                    for (j = 0; j < docCount; ++j) {
                        if (previousReader.isDeleted(j)) {
                            assert (currentReader.isDeleted(j));
                            continue;
                        }
                        if (currentReader.isDeleted(j)) {
                            mergedReader.doDelete(docUpto);
                            ++delCount;
                        }
                        ++docUpto;
                    }
                    continue;
                }
                docUpto += docCount - previousReader.numDeletedDocs();
                continue;
            }
            if (currentReader.hasDeletions()) {
                for (j = 0; j < docCount; ++j) {
                    if (currentReader.isDeleted(j)) {
                        mergedReader.doDelete(docUpto);
                        ++delCount;
                    }
                    ++docUpto;
                }
                continue;
            }
            docUpto += info.docCount;
        }
        assert (mergedReader.numDeletedDocs() == delCount);
        boolean bl = mergedReader.hasChanges = delCount > 0;
        assert (!mergedReader.hasChanges || minGen > mergedReader.getSegmentInfo().getBufferedDeletesGen());
        mergedReader.getSegmentInfo().setBufferedDeletesGen(minGen);
    }

    private synchronized boolean commitMerge(MergePolicy.OneMerge merge, SegmentReader mergedReader) throws IOException {
        boolean allDeleted;
        assert (this.testPoint("startCommitMerge"));
        if (this.hitOOM) {
            throw new IllegalStateException("this writer hit an OutOfMemoryError; cannot complete merge");
        }
        if (this.infoStream != null) {
            this.message("commitMerge: " + merge.segString(this.directory) + " index=" + this.segString());
        }
        assert (merge.registerDone);
        if (merge.isAborted()) {
            if (this.infoStream != null) {
                this.message("commitMerge: skipping merge " + merge.segString(this.directory) + ": it was aborted");
            }
            return false;
        }
        if (merge.info.docCount > 0) {
            this.commitMergedDeletes(merge, mergedReader);
        }
        assert (!this.segmentInfos.contains(merge.info));
        boolean bl = allDeleted = mergedReader.numDocs() == 0;
        if (this.infoStream != null && allDeleted) {
            this.message("merged segment " + merge.info + " is 100% deleted" + (this.keepFullyDeletedSegments ? "" : "; skipping insert"));
        }
        boolean dropSegment = allDeleted && !this.keepFullyDeletedSegments;
        this.segmentInfos.applyMergeChanges(merge, dropSegment);
        if (dropSegment) {
            this.readerPool.drop(merge.info);
            this.deleter.deleteNewFiles(merge.info.files());
            assert (!this.segmentInfos.contains(merge.info));
        }
        if (this.infoStream != null) {
            this.message("after commit: " + this.segString());
        }
        this.closeMergeReaders(merge, false);
        this.checkpoint();
        this.readerPool.clear(merge.segments);
        if (merge.maxNumSegments != -1 && !dropSegment && !this.segmentsToMerge.containsKey(merge.info)) {
            this.segmentsToMerge.put(merge.info, Boolean.FALSE);
        }
        return true;
    }

    private final void handleMergeException(Throwable t, MergePolicy.OneMerge merge) throws IOException {
        if (this.infoStream != null) {
            this.message("handleMergeException: merge=" + merge.segString(this.directory) + " exc=" + t);
        }
        merge.setException(t);
        this.addMergeException(merge);
        if (t instanceof MergePolicy.MergeAbortedException) {
            if (merge.isExternal) {
                throw (MergePolicy.MergeAbortedException)t;
            }
        } else {
            if (t instanceof IOException) {
                throw (IOException)t;
            }
            if (t instanceof RuntimeException) {
                throw (RuntimeException)t;
            }
            if (t instanceof Error) {
                throw (Error)t;
            }
            throw new RuntimeException(t);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void merge(MergePolicy.OneMerge merge) throws CorruptIndexException, IOException {
        boolean success = false;
        long t0 = System.currentTimeMillis();
        try {
            IndexWriter indexWriter;
            try {
                try {
                    this.mergeInit(merge);
                    if (this.infoStream != null) {
                        this.message("now merge\n  merge=" + merge.segString(this.directory) + "\n  index=" + this.segString());
                    }
                    this.mergeMiddle(merge);
                    this.mergeSuccess(merge);
                    success = true;
                }
                catch (Throwable t) {
                    this.handleMergeException(t, merge);
                }
                Object var7_6 = null;
                indexWriter = this;
            }
            catch (Throwable throwable) {
                Object var7_7 = null;
                IndexWriter indexWriter2 = this;
                synchronized (indexWriter2) {
                    this.mergeFinish(merge);
                    if (!success) {
                        if (this.infoStream != null) {
                            this.message("hit exception during merge");
                        }
                        if (merge.info != null && !this.segmentInfos.contains(merge.info)) {
                            this.deleter.refresh(merge.info.name);
                        }
                    }
                    if (success && !merge.isAborted() && (merge.maxNumSegments != -1 || !this.closed && !this.closing)) {
                        this.updatePendingMerges(merge.maxNumSegments);
                    }
                }
                throw throwable;
            }
            synchronized (indexWriter) {
                this.mergeFinish(merge);
                if (!success) {
                    if (this.infoStream != null) {
                        this.message("hit exception during merge");
                    }
                    if (merge.info != null && !this.segmentInfos.contains(merge.info)) {
                        this.deleter.refresh(merge.info.name);
                    }
                }
                if (success && !merge.isAborted() && (merge.maxNumSegments != -1 || !this.closed && !this.closing)) {
                    this.updatePendingMerges(merge.maxNumSegments);
                }
            }
        }
        catch (OutOfMemoryError oom) {
            this.handleOOM(oom, "merge");
        }
        if (this.infoStream != null && merge.info != null) {
            this.message("merge time " + (System.currentTimeMillis() - t0) + " msec for " + merge.info.docCount + " docs");
        }
    }

    void mergeSuccess(MergePolicy.OneMerge merge) {
    }

    final synchronized boolean registerMerge(MergePolicy.OneMerge merge) throws MergePolicy.MergeAbortedException, IOException {
        if (merge.registerDone) {
            return true;
        }
        if (this.stopMerges) {
            merge.abort();
            throw new MergePolicy.MergeAbortedException("merge is aborted: " + merge.segString(this.directory));
        }
        boolean isExternal = false;
        for (SegmentInfo info : merge.segments) {
            if (this.mergingSegments.contains(info)) {
                return false;
            }
            if (!this.segmentInfos.contains(info)) {
                return false;
            }
            if (info.dir != this.directory) {
                isExternal = true;
            }
            if (!this.segmentsToMerge.containsKey(info)) continue;
            merge.maxNumSegments = this.mergeMaxNumSegments;
        }
        this.ensureValidMerge(merge);
        this.pendingMerges.add(merge);
        if (this.infoStream != null) {
            this.message("add merge to pendingMerges: " + merge.segString(this.directory) + " [total " + this.pendingMerges.size() + " pending]");
        }
        merge.mergeGen = this.mergeGen;
        merge.isExternal = isExternal;
        this.message("registerMerge merging=" + this.mergingSegments);
        for (SegmentInfo info : merge.segments) {
            this.message("registerMerge info=" + info);
            this.mergingSegments.add(info);
        }
        merge.registerDone = true;
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final synchronized void mergeInit(MergePolicy.OneMerge merge) throws IOException {
        block4: {
            block5: {
                boolean success = false;
                try {
                    this._mergeInit(merge);
                    success = true;
                    Object var4_3 = null;
                    if (success) break block4;
                    if (this.infoStream == null) break block5;
                    this.message("hit exception in mergeInit");
                }
                catch (Throwable throwable) {
                    Object var4_4 = null;
                    if (!success) {
                        if (this.infoStream != null) {
                            this.message("hit exception in mergeInit");
                        }
                        this.mergeFinish(merge);
                    }
                    throw throwable;
                }
            }
            this.mergeFinish(merge);
            {
            }
        }
    }

    private synchronized void _mergeInit(MergePolicy.OneMerge merge) throws IOException {
        assert (this.testPoint("startMergeInit"));
        assert (merge.registerDone);
        assert (merge.maxNumSegments == -1 || merge.maxNumSegments > 0);
        if (this.hitOOM) {
            throw new IllegalStateException("this writer hit an OutOfMemoryError; cannot merge");
        }
        if (merge.info != null) {
            return;
        }
        if (merge.isAborted()) {
            return;
        }
        boolean hasVectors = false;
        for (SegmentInfo sourceSegment : merge.segments) {
            if (!sourceSegment.getHasVectors()) continue;
            hasVectors = true;
        }
        merge.info = new SegmentInfo(this.newSegmentName(), 0, this.directory, false, true, false, hasVectors);
        BufferedDeletesStream.ApplyDeletesResult result = this.bufferedDeletesStream.applyDeletes(this.readerPool, merge.segments);
        if (result.anyDeletes) {
            this.checkpoint();
        }
        if (!this.keepFullyDeletedSegments && result.allDeleted != null) {
            if (this.infoStream != null) {
                this.message("drop 100% deleted segments: " + result.allDeleted);
            }
            for (SegmentInfo info : result.allDeleted) {
                this.segmentInfos.remove(info);
                if (!merge.segments.contains(info)) continue;
                this.mergingSegments.remove(info);
                merge.segments.remove(info);
            }
            if (this.readerPool != null) {
                this.readerPool.drop(result.allDeleted);
            }
            this.checkpoint();
        }
        merge.info.setBufferedDeletesGen(result.gen);
        this.bufferedDeletesStream.prune(this.segmentInfos);
        HashMap<String, String> details = new HashMap<String, String>();
        details.put("mergeMaxNumSegments", "" + merge.maxNumSegments);
        details.put("mergeFactor", Integer.toString(merge.segments.size()));
        this.setDiagnostics(merge.info, "merge", details);
        if (this.infoStream != null) {
            this.message("merge seg=" + merge.info.name);
        }
        assert (merge.estimatedMergeBytes == 0L);
        for (SegmentInfo info : merge.segments) {
            if (info.docCount <= 0) continue;
            int delCount = this.numDeletedDocs(info);
            assert (delCount <= info.docCount);
            double delRatio = (double)delCount / (double)info.docCount;
            merge.estimatedMergeBytes = (long)((double)merge.estimatedMergeBytes + (double)info.sizeInBytes(true) * (1.0 - delRatio));
        }
        this.mergingSegments.add(merge.info);
    }

    private void setDiagnostics(SegmentInfo info, String source) {
        this.setDiagnostics(info, source, null);
    }

    private void setDiagnostics(SegmentInfo info, String source, Map<String, String> details) {
        HashMap<String, String> diagnostics = new HashMap<String, String>();
        diagnostics.put("source", source);
        diagnostics.put("lucene.version", Constants.LUCENE_VERSION);
        diagnostics.put("os", Constants.OS_NAME);
        diagnostics.put("os.arch", Constants.OS_ARCH);
        diagnostics.put("os.version", Constants.OS_VERSION);
        diagnostics.put("java.version", Constants.JAVA_VERSION);
        diagnostics.put("java.vendor", Constants.JAVA_VENDOR);
        if (details != null) {
            diagnostics.putAll(details);
        }
        info.setDiagnostics(diagnostics);
    }

    final synchronized void mergeFinish(MergePolicy.OneMerge merge) throws IOException {
        this.notifyAll();
        if (merge.registerDone) {
            List<SegmentInfo> sourceSegments = merge.segments;
            for (SegmentInfo info : sourceSegments) {
                this.mergingSegments.remove(info);
            }
            this.mergingSegments.remove(merge.info);
            merge.registerDone = false;
        }
        this.runningMerges.remove(merge);
    }

    private final synchronized void closeMergeReaders(MergePolicy.OneMerge merge, boolean suppressExceptions) throws IOException {
        int numSegments = merge.readers.size();
        Throwable th = null;
        boolean anyChanges = false;
        boolean drop = !suppressExceptions;
        for (int i = 0; i < numSegments; ++i) {
            block13: {
                if (merge.readers.get(i) != null) {
                    block12: {
                        try {
                            anyChanges |= this.readerPool.release(merge.readers.get(i), drop);
                        }
                        catch (Throwable t) {
                            if (th != null) break block12;
                            th = t;
                        }
                    }
                    merge.readers.set(i, null);
                }
                if (i >= merge.readerClones.size() || merge.readerClones.get(i) == null) continue;
                try {
                    merge.readerClones.get(i).close();
                }
                catch (Throwable t) {
                    if (th != null) break block13;
                    th = t;
                }
            }
            assert (merge.readerClones.get(i).getRefCount() == 0) : "refCount should be 0 but is " + merge.readerClones.get(i).getRefCount();
            merge.readerClones.set(i, null);
        }
        if (suppressExceptions && anyChanges) {
            this.checkpoint();
        }
        if (!suppressExceptions && th != null) {
            if (th instanceof IOException) {
                throw (IOException)th;
            }
            if (th instanceof RuntimeException) {
                throw (RuntimeException)th;
            }
            if (th instanceof Error) {
                throw (Error)th;
            }
            throw new RuntimeException(th);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    private final int mergeMiddle(MergePolicy.OneMerge merge) throws CorruptIndexException, IOException {
        merge.checkAborted(this.directory);
        String mergedName = merge.info.name;
        int mergedDocCount = 0;
        List<SegmentInfo> sourceSegments = merge.segments;
        SegmentMerger merger = new SegmentMerger(this.directory, this.config.getTermIndexInterval(), mergedName, merge, this.payloadProcessorProvider, (FieldInfos)this.docWriter.getFieldInfos().clone());
        if (this.infoStream != null) {
            this.message("merging " + merge.segString(this.directory) + " mergeVectors=" + merge.info.getHasVectors());
        }
        merge.readers = new ArrayList<SegmentReader>();
        merge.readerClones = new ArrayList<SegmentReader>();
        boolean success = false;
        try {
            SegmentReader mergedReader;
            block52: {
                int n;
                boolean loadDocStores;
                int termsIndexDivisor;
                IndexReaderWarmer mergedSegmentWarmer;
                Closeable reader;
                int totDocCount = 0;
                for (int segUpto = 0; segUpto < sourceSegments.size(); ++segUpto) {
                    SegmentInfo info = sourceSegments.get(segUpto);
                    reader = this.readerPool.get(info, true, 4096, -1);
                    merge.readers.add((SegmentReader)reader);
                    SegmentReader clone = (SegmentReader)reader.clone(true);
                    merge.readerClones.add(clone);
                    if (clone.numDocs() <= 0) continue;
                    merger.add(clone);
                    totDocCount += clone.numDocs();
                }
                if (this.infoStream != null) {
                    this.message("merge: total " + totDocCount + " docs");
                }
                merge.checkAborted(this.directory);
                mergedDocCount = merge.info.docCount = merger.merge();
                merge.info.setHasVectors(merger.fieldInfos().hasVectors());
                assert (mergedDocCount == totDocCount);
                if (this.infoStream != null) {
                    this.message("merge store matchedCount=" + merger.getMatchedSubReaderCount() + " vs " + merge.readers.size());
                }
                this.anyNonBulkMerges |= merger.getAnyNonBulkMerges();
                assert (mergedDocCount == totDocCount) : "mergedDocCount=" + mergedDocCount + " vs " + totDocCount;
                merge.info.setHasProx(merger.fieldInfos().hasProx());
                reader = this;
                // MONITORENTER : reader
                boolean useCompoundFile = this.mergePolicy.useCompoundFile(this.segmentInfos, merge.info);
                // MONITOREXIT : reader
                if (useCompoundFile) {
                    String compoundFileName;
                    block51: {
                        success = false;
                        compoundFileName = IndexFileNames.segmentFileName(mergedName, "cfs");
                        try {
                            try {
                                if (this.infoStream != null) {
                                    this.message("create compound file " + compoundFileName);
                                }
                                merger.createCompoundFile(compoundFileName, merge.info);
                                success = true;
                            }
                            catch (IOException ioe) {
                                IndexWriter indexWriter = this;
                                // MONITORENTER : indexWriter
                                if (!merge.isAborted()) {
                                    this.handleMergeException(ioe, merge);
                                }
                                // MONITOREXIT : indexWriter
                                Object var15_17 = null;
                                if (!success) {
                                    if (this.infoStream != null) {
                                        this.message("hit exception creating compound file during merge");
                                    }
                                    IndexWriter indexWriter2 = this;
                                    // MONITORENTER : indexWriter2
                                    this.deleter.deleteFile(compoundFileName);
                                    this.deleter.deleteNewFiles(merge.info.files());
                                    // MONITOREXIT : indexWriter2
                                }
                                break block51;
                            }
                            catch (Throwable t) {
                                this.handleMergeException(t, merge);
                                Object var15_18 = null;
                                if (!success) {
                                    if (this.infoStream != null) {
                                        this.message("hit exception creating compound file during merge");
                                    }
                                    IndexWriter indexWriter = this;
                                    // MONITORENTER : indexWriter
                                    this.deleter.deleteFile(compoundFileName);
                                    this.deleter.deleteNewFiles(merge.info.files());
                                    // MONITOREXIT : indexWriter
                                }
                            }
                            Object var15_16 = null;
                            if (!success) {
                                if (this.infoStream != null) {
                                    this.message("hit exception creating compound file during merge");
                                }
                                IndexWriter indexWriter = this;
                                // MONITORENTER : indexWriter
                                this.deleter.deleteFile(compoundFileName);
                                this.deleter.deleteNewFiles(merge.info.files());
                                // MONITOREXIT : indexWriter
                            }
                        }
                        catch (Throwable throwable) {
                            Object var15_19 = null;
                            if (success) throw throwable;
                            if (this.infoStream != null) {
                                this.message("hit exception creating compound file during merge");
                            }
                            IndexWriter indexWriter = this;
                            // MONITORENTER : indexWriter
                            this.deleter.deleteFile(compoundFileName);
                            this.deleter.deleteNewFiles(merge.info.files());
                            // MONITOREXIT : indexWriter
                            throw throwable;
                        }
                    }
                    success = false;
                    IndexWriter t = this;
                    // MONITORENTER : t
                    this.deleter.deleteNewFiles(merge.info.files());
                    if (merge.isAborted()) {
                        if (this.infoStream != null) {
                            this.message("abort merge after building CFS");
                        }
                        this.deleter.deleteFile(compoundFileName);
                        int n2 = 0;
                        // MONITOREXIT : t
                        Object var24_29 = null;
                        if (success) return n2;
                        this.closeMergeReaders(merge, true);
                        return n2;
                    }
                    // MONITOREXIT : t
                    merge.info.setUseCompoundFile(true);
                }
                if (this.infoStream != null) {
                    this.message(String.format("merged segment size=%.3f MB vs estimate=%.3f MB", (double)merge.info.sizeInBytes(true) / 1024.0 / 1024.0, (double)(merge.estimatedMergeBytes / 1024L) / 1024.0));
                }
                if ((mergedSegmentWarmer = this.config.getMergedSegmentWarmer()) != null) {
                    termsIndexDivisor = this.config.getReaderTermsIndexDivisor();
                    loadDocStores = true;
                } else {
                    termsIndexDivisor = -1;
                    loadDocStores = false;
                }
                mergedReader = this.readerPool.get(merge.info, loadDocStores, 1024, termsIndexDivisor);
                try {
                    if (this.poolReaders && mergedSegmentWarmer != null) {
                        mergedSegmentWarmer.warm(mergedReader);
                    }
                    if (this.commitMerge(merge, mergedReader)) break block52;
                    n = 0;
                    Object var20_34 = null;
                    IndexWriter indexWriter = this;
                }
                catch (Throwable throwable) {
                    Object var20_36 = null;
                    IndexWriter indexWriter = this;
                    // MONITORENTER : indexWriter
                    if (this.readerPool.release(mergedReader)) {
                        this.checkpoint();
                    }
                    // MONITOREXIT : indexWriter
                    throw throwable;
                }
                if (this.readerPool.release(mergedReader)) {
                    this.checkpoint();
                }
                // MONITOREXIT : indexWriter
                Object var24_30 = null;
                if (success) return n;
                this.closeMergeReaders(merge, true);
                return n;
            }
            Object var20_35 = null;
            IndexWriter indexWriter = this;
            // MONITORENTER : indexWriter
            if (this.readerPool.release(mergedReader)) {
                this.checkpoint();
            }
            // MONITOREXIT : indexWriter
            return mergedDocCount;
        }
        catch (Throwable throwable) {
            Object var24_32 = null;
            if (success) throw throwable;
            this.closeMergeReaders(merge, true);
            throw throwable;
        }
    }

    synchronized void addMergeException(MergePolicy.OneMerge merge) {
        assert (merge.getException() != null);
        if (!this.mergeExceptions.contains(merge) && this.mergeGen == merge.mergeGen) {
            this.mergeExceptions.add(merge);
        }
    }

    final int getBufferedDeleteTermsSize() {
        return this.docWriter.getPendingDeletes().terms.size();
    }

    final int getNumBufferedDeleteTerms() {
        return this.docWriter.getPendingDeletes().numTermDeletes.get();
    }

    synchronized SegmentInfo newestSegment() {
        return this.segmentInfos.size() > 0 ? this.segmentInfos.info(this.segmentInfos.size() - 1) : null;
    }

    public synchronized String segString() throws IOException {
        return this.segString(this.segmentInfos);
    }

    public synchronized String segString(Iterable<SegmentInfo> infos) throws IOException {
        StringBuilder buffer = new StringBuilder();
        for (SegmentInfo s : infos) {
            if (buffer.length() > 0) {
                buffer.append(' ');
            }
            buffer.append(this.segString(s));
        }
        return buffer.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public synchronized String segString(SegmentInfo info) throws IOException {
        StringBuilder buffer = new StringBuilder();
        SegmentReader reader = this.readerPool.getIfExists(info);
        try {
            if (reader != null) {
                buffer.append(reader.toString());
            } else {
                buffer.append(info.toString(this.directory, 0));
                if (info.dir != this.directory) {
                    buffer.append("**");
                }
            }
            Object var5_4 = null;
            if (reader == null) return buffer.toString();
        }
        catch (Throwable throwable) {
            Object var5_5 = null;
            if (reader == null) throw throwable;
            this.readerPool.release(reader);
            throw throwable;
        }
        this.readerPool.release(reader);
        return buffer.toString();
    }

    private synchronized void doWait() {
        try {
            this.wait(1000L);
        }
        catch (InterruptedException ie) {
            throw new ThreadInterruptedException(ie);
        }
    }

    void keepFullyDeletedSegments() {
        this.keepFullyDeletedSegments = true;
    }

    boolean getKeepFullyDeletedSegments() {
        return this.keepFullyDeletedSegments;
    }

    private boolean filesExist(SegmentInfos toSync) throws IOException {
        Collection<String> files = toSync.files(this.directory, false);
        for (String fileName : files) {
            assert (this.directory.fileExists(fileName)) : "file " + fileName + " does not exist";
            assert (this.deleter.exists(fileName)) : "IndexFileDeleter doesn't know about file " + fileName;
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void startCommit(SegmentInfos toSync, Map<String, String> commitUserData) throws IOException {
        assert (this.testPoint("startStartCommit"));
        assert (this.pendingCommit == null);
        if (this.hitOOM) {
            throw new IllegalStateException("this writer hit an OutOfMemoryError; cannot commit");
        }
        try {
            IndexWriter indexWriter;
            if (this.infoStream != null) {
                this.message("startCommit(): start");
            }
            IndexWriter indexWriter2 = this;
            synchronized (indexWriter2) {
                assert (this.lastCommitChangeCount <= this.changeCount);
                if (this.pendingCommitChangeCount == this.lastCommitChangeCount) {
                    if (this.infoStream != null) {
                        this.message("  skip startCommit(): no changes pending");
                    }
                    this.deleter.decRef(this.filesToCommit);
                    this.filesToCommit = null;
                    return;
                }
                if (this.infoStream != null) {
                    this.message("startCommit index=" + this.segString(toSync) + " changeCount=" + this.changeCount);
                }
                assert (this.filesExist(toSync));
                if (commitUserData != null) {
                    toSync.setUserData(commitUserData);
                }
            }
            assert (this.testPoint("midStartCommit"));
            boolean pendingCommitSet = false;
            try {
                this.directory.sync(toSync.files(this.directory, false));
                assert (this.testPoint("midStartCommit2"));
                IndexWriter indexWriter3 = this;
                synchronized (indexWriter3) {
                    assert (this.pendingCommit == null);
                    assert (this.segmentInfos.getGeneration() == toSync.getGeneration());
                    toSync.prepareCommit(this.directory);
                    pendingCommitSet = true;
                    this.pendingCommit = toSync;
                }
                if (this.infoStream != null) {
                    this.message("done all syncs");
                }
                assert (this.testPoint("midStartCommitSuccess"));
                Object var7_9 = null;
                indexWriter = this;
            }
            catch (Throwable throwable) {
                Object var7_10 = null;
                IndexWriter indexWriter4 = this;
                synchronized (indexWriter4) {
                    this.segmentInfos.updateGeneration(toSync);
                    if (!pendingCommitSet) {
                        if (this.infoStream != null) {
                            this.message("hit exception committing segments file");
                        }
                        this.deleter.decRef(this.filesToCommit);
                        this.filesToCommit = null;
                    }
                }
                throw throwable;
            }
            synchronized (indexWriter) {
                this.segmentInfos.updateGeneration(toSync);
                if (!pendingCommitSet) {
                    if (this.infoStream != null) {
                        this.message("hit exception committing segments file");
                    }
                    this.deleter.decRef(this.filesToCommit);
                    this.filesToCommit = null;
                }
            }
        }
        catch (OutOfMemoryError oom) {
            this.handleOOM(oom, "startCommit");
        }
        assert (this.testPoint("finishStartCommit"));
    }

    public static boolean isLocked(Directory directory) throws IOException {
        return directory.makeLock(WRITE_LOCK_NAME).isLocked();
    }

    public static void unlock(Directory directory) throws IOException {
        directory.makeLock(WRITE_LOCK_NAME).release();
    }

    @Deprecated
    public void setMergedSegmentWarmer(IndexReaderWarmer warmer) {
        this.config.setMergedSegmentWarmer(warmer);
    }

    @Deprecated
    public IndexReaderWarmer getMergedSegmentWarmer() {
        return this.config.getMergedSegmentWarmer();
    }

    private void handleOOM(OutOfMemoryError oom, String location) {
        if (this.infoStream != null) {
            this.message("hit OutOfMemoryError inside " + location);
        }
        this.hitOOM = true;
        throw oom;
    }

    boolean testPoint(String name) {
        return true;
    }

    synchronized boolean nrtIsCurrent(SegmentInfos infos) {
        this.ensureOpen();
        return infos.version == this.segmentInfos.version && !this.docWriter.anyChanges() && !this.bufferedDeletesStream.any();
    }

    synchronized boolean isClosed() {
        return this.closed;
    }

    public synchronized void deleteUnusedFiles() throws IOException {
        this.ensureOpen(false);
        this.deleter.deletePendingFiles();
        this.deleter.revisitPolicy();
    }

    synchronized void deletePendingFiles() throws IOException {
        this.deleter.deletePendingFiles();
    }

    public void setPayloadProcessorProvider(PayloadProcessorProvider pcp) {
        this.ensureOpen();
        this.payloadProcessorProvider = pcp;
    }

    public PayloadProcessorProvider getPayloadProcessorProvider() {
        this.ensureOpen();
        return this.payloadProcessorProvider;
    }

    final class FlushControl {
        private boolean flushPending;
        private boolean flushDeletes;
        private int delCount;
        private int docCount;
        private boolean flushing;

        FlushControl() {
        }

        private synchronized boolean setFlushPending(String reason, boolean doWait) {
            if (this.flushPending || this.flushing) {
                if (doWait) {
                    while (this.flushPending || this.flushing) {
                        try {
                            this.wait();
                        }
                        catch (InterruptedException ie) {
                            throw new ThreadInterruptedException(ie);
                        }
                    }
                }
                return false;
            }
            if (IndexWriter.this.infoStream != null) {
                IndexWriter.this.message("now trigger flush reason=" + reason);
            }
            this.flushPending = true;
            return this.flushPending;
        }

        public synchronized void setFlushPendingNoWait(String reason) {
            this.setFlushPending(reason, false);
        }

        public synchronized boolean getFlushPending() {
            return this.flushPending;
        }

        public synchronized boolean getFlushDeletes() {
            return this.flushDeletes;
        }

        public synchronized void clearFlushPending() {
            if (IndexWriter.this.infoStream != null) {
                IndexWriter.this.message("clearFlushPending");
            }
            this.flushPending = false;
            this.flushDeletes = false;
            this.docCount = 0;
            this.notifyAll();
        }

        public synchronized void clearDeletes() {
            this.delCount = 0;
        }

        public synchronized boolean waitUpdate(int docInc, int delInc) {
            return this.waitUpdate(docInc, delInc, false);
        }

        public synchronized boolean waitUpdate(int docInc, int delInc, boolean skipWait) {
            while (this.flushPending) {
                try {
                    this.wait();
                }
                catch (InterruptedException ie) {
                    throw new ThreadInterruptedException(ie);
                }
            }
            this.docCount += docInc;
            this.delCount += delInc;
            if (skipWait) {
                return false;
            }
            int maxBufferedDocs = IndexWriter.this.config.getMaxBufferedDocs();
            if (maxBufferedDocs != -1 && this.docCount >= maxBufferedDocs) {
                return this.setFlushPending("maxBufferedDocs", true);
            }
            int maxBufferedDeleteTerms = IndexWriter.this.config.getMaxBufferedDeleteTerms();
            if (maxBufferedDeleteTerms != -1 && this.delCount >= maxBufferedDeleteTerms) {
                this.flushDeletes = true;
                return this.setFlushPending("maxBufferedDeleteTerms", true);
            }
            return this.flushByRAMUsage("add delete/doc");
        }

        public synchronized boolean flushByRAMUsage(String reason) {
            double ramBufferSizeMB = IndexWriter.this.config.getRAMBufferSizeMB();
            if (ramBufferSizeMB != -1.0) {
                long limit = (long)(ramBufferSizeMB * 1024.0 * 1024.0);
                long used = IndexWriter.this.bufferedDeletesStream.bytesUsed() + IndexWriter.this.docWriter.bytesUsed();
                if (used >= limit) {
                    IndexWriter.this.docWriter.balanceRAM();
                    used = IndexWriter.this.bufferedDeletesStream.bytesUsed() + IndexWriter.this.docWriter.bytesUsed();
                    if (used >= limit) {
                        return this.setFlushPending("ram full: " + reason, false);
                    }
                }
            }
            return false;
        }
    }

    public static abstract class IndexReaderWarmer {
        public abstract void warm(IndexReader var1) throws IOException;
    }

    @Deprecated
    public static final class MaxFieldLength {
        private int limit;
        private String name;
        public static final MaxFieldLength UNLIMITED = new MaxFieldLength("UNLIMITED", Integer.MAX_VALUE);
        public static final MaxFieldLength LIMITED = new MaxFieldLength("LIMITED", 10000);

        private MaxFieldLength(String name, int limit) {
            this.name = name;
            this.limit = limit;
        }

        public MaxFieldLength(int limit) {
            this("User-specified", limit);
        }

        public int getLimit() {
            return this.limit;
        }

        public String toString() {
            return this.name + ":" + this.limit;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    class ReaderPool {
        private final Map<SegmentInfo, SegmentReader> readerMap = new HashMap<SegmentInfo, SegmentReader>();

        ReaderPool() {
        }

        synchronized void clear(List<SegmentInfo> infos) throws IOException {
            if (infos == null) {
                for (Map.Entry<SegmentInfo, SegmentReader> ent : this.readerMap.entrySet()) {
                    ent.getValue().hasChanges = false;
                }
            } else {
                for (SegmentInfo info : infos) {
                    SegmentReader r = this.readerMap.get(info);
                    if (r == null) continue;
                    r.hasChanges = false;
                }
            }
        }

        public synchronized boolean infoIsLive(SegmentInfo info) {
            int idx = IndexWriter.this.segmentInfos.indexOf(info);
            assert (idx != -1) : "info=" + info + " isn't in pool";
            assert (IndexWriter.this.segmentInfos.info(idx) == info) : "info=" + info + " doesn't match live info in segmentInfos";
            return true;
        }

        public synchronized SegmentInfo mapToLive(SegmentInfo info) {
            int idx = IndexWriter.this.segmentInfos.indexOf(info);
            if (idx != -1) {
                info = IndexWriter.this.segmentInfos.info(idx);
            }
            return info;
        }

        public synchronized boolean release(SegmentReader sr) throws IOException {
            return this.release(sr, false);
        }

        public synchronized boolean release(SegmentReader sr, boolean drop) throws IOException {
            boolean pooled = this.readerMap.containsKey(sr.getSegmentInfo());
            assert (!pooled || this.readerMap.get(sr.getSegmentInfo()) == sr);
            sr.decRef();
            if (pooled && (drop || !IndexWriter.this.poolReaders && sr.getRefCount() == 1)) {
                assert (!sr.hasChanges || Thread.holdsLock(IndexWriter.this));
                boolean hasChanges = sr.hasChanges = sr.hasChanges & !drop;
                sr.close();
                this.readerMap.remove(sr.getSegmentInfo());
                return hasChanges;
            }
            return false;
        }

        public synchronized void drop(List<SegmentInfo> infos) throws IOException {
            for (SegmentInfo info : infos) {
                this.drop(info);
            }
        }

        public synchronized void drop(SegmentInfo info) throws IOException {
            SegmentReader sr = this.readerMap.get(info);
            if (sr != null) {
                sr.hasChanges = false;
                this.readerMap.remove(info);
                sr.close();
            }
        }

        public synchronized void dropAll() throws IOException {
            for (SegmentReader reader : this.readerMap.values()) {
                reader.hasChanges = false;
                reader.decRef();
            }
            this.readerMap.clear();
        }

        synchronized void close() throws IOException {
            assert (Thread.holdsLock(IndexWriter.this));
            for (Map.Entry<SegmentInfo, SegmentReader> ent : this.readerMap.entrySet()) {
                SegmentReader sr = ent.getValue();
                if (sr.hasChanges) {
                    assert (this.infoIsLive(sr.getSegmentInfo()));
                    sr.doCommit(null);
                    IndexWriter.this.deleter.checkpoint(IndexWriter.this.segmentInfos, false);
                }
                sr.decRef();
            }
            this.readerMap.clear();
        }

        synchronized void commit(SegmentInfos infos) throws IOException {
            assert (Thread.holdsLock(IndexWriter.this));
            for (SegmentInfo info : infos) {
                SegmentReader sr = this.readerMap.get(info);
                if (sr == null || !sr.hasChanges) continue;
                assert (this.infoIsLive(info));
                sr.doCommit(null);
                IndexWriter.this.deleter.checkpoint(IndexWriter.this.segmentInfos, false);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public synchronized SegmentReader getReadOnlyClone(SegmentInfo info, boolean doOpenStores, int termInfosIndexDivisor) throws IOException {
            SegmentReader segmentReader;
            SegmentReader sr = this.get(info, doOpenStores, 1024, termInfosIndexDivisor);
            try {
                segmentReader = (SegmentReader)sr.clone(true);
                Object var7_6 = null;
            }
            catch (Throwable throwable) {
                Object var7_7 = null;
                sr.decRef();
                throw throwable;
            }
            sr.decRef();
            return segmentReader;
        }

        public synchronized SegmentReader get(SegmentInfo info, boolean doOpenStores) throws IOException {
            return this.get(info, doOpenStores, 1024, IndexWriter.this.config.getReaderTermsIndexDivisor());
        }

        public synchronized SegmentReader get(SegmentInfo info, boolean doOpenStores, int readBufferSize, int termsIndexDivisor) throws IOException {
            SegmentReader sr;
            if (IndexWriter.this.poolReaders) {
                readBufferSize = 1024;
            }
            if ((sr = this.readerMap.get(info)) == null) {
                sr = SegmentReader.get(false, info.dir, info, readBufferSize, doOpenStores, termsIndexDivisor);
                if (info.dir == IndexWriter.this.directory) {
                    this.readerMap.put(info, sr);
                }
            } else {
                if (doOpenStores) {
                    sr.openDocStores();
                }
                if (termsIndexDivisor != -1 && !sr.termsIndexLoaded()) {
                    sr.loadTermsIndex(termsIndexDivisor);
                }
            }
            if (info.dir == IndexWriter.this.directory) {
                sr.incRef();
            }
            return sr;
        }

        public synchronized SegmentReader getIfExists(SegmentInfo info) throws IOException {
            SegmentReader sr = this.readerMap.get(info);
            if (sr != null) {
                sr.incRef();
            }
            return sr;
        }
    }
}

