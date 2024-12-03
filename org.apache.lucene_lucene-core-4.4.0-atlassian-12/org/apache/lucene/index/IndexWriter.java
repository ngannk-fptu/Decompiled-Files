/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.codecs.lucene3x.Lucene3xCodec;
import org.apache.lucene.codecs.lucene3x.Lucene3xSegmentInfoFormat;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.BufferedDeletesStream;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocumentsWriter;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.FrozenBufferedDeletes;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexFileDeleter;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.LiveIndexWriterConfig;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.index.MergeScheduler;
import org.apache.lucene.index.MergeState;
import org.apache.lucene.index.ReaderUtil;
import org.apache.lucene.index.ReadersAndLiveDocs;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.index.SegmentInfoPerCommit;
import org.apache.lucene.index.SegmentInfos;
import org.apache.lucene.index.SegmentMerger;
import org.apache.lucene.index.SegmentReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TwoPhaseCommit;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.CompoundFileDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.Lock;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.MergeInfo;
import org.apache.lucene.store.TrackingDirectoryWrapper;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.Constants;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.InfoStream;
import org.apache.lucene.util.ThreadInterruptedException;

public class IndexWriter
implements Closeable,
TwoPhaseCommit {
    private static final int UNBOUNDED_MAX_MERGE_SEGMENTS = -1;
    public static final String WRITE_LOCK_NAME = "write.lock";
    public static final String SOURCE = "source";
    public static final String SOURCE_MERGE = "merge";
    public static final String SOURCE_FLUSH = "flush";
    public static final String SOURCE_ADDINDEXES_READERS = "addIndexes(IndexReader...)";
    public static final int MAX_TERM_LENGTH = 32766;
    private volatile boolean hitOOM;
    private final Directory directory;
    private final Analyzer analyzer;
    private volatile long changeCount;
    private volatile long lastCommitChangeCount;
    private List<SegmentInfoPerCommit> rollbackSegments;
    volatile SegmentInfos pendingCommit;
    volatile long pendingCommitChangeCount;
    private Collection<String> filesToCommit;
    final SegmentInfos segmentInfos;
    final FieldInfos.FieldNumbers globalFieldNumberMap;
    private DocumentsWriter docWriter;
    final IndexFileDeleter deleter;
    private Map<SegmentInfoPerCommit, Boolean> segmentsToMerge = new HashMap<SegmentInfoPerCommit, Boolean>();
    private int mergeMaxNumSegments;
    private Lock writeLock;
    private volatile boolean closed;
    private volatile boolean closing;
    private HashSet<SegmentInfoPerCommit> mergingSegments = new HashSet();
    private MergePolicy mergePolicy;
    private final MergeScheduler mergeScheduler;
    private LinkedList<MergePolicy.OneMerge> pendingMerges = new LinkedList();
    private Set<MergePolicy.OneMerge> runningMerges = new HashSet<MergePolicy.OneMerge>();
    private List<MergePolicy.OneMerge> mergeExceptions = new ArrayList<MergePolicy.OneMerge>();
    private long mergeGen;
    private boolean stopMerges;
    final AtomicInteger flushCount = new AtomicInteger();
    final AtomicInteger flushDeletesCount = new AtomicInteger();
    final ReaderPool readerPool = new ReaderPool();
    final BufferedDeletesStream bufferedDeletesStream;
    private volatile boolean poolReaders;
    private final LiveIndexWriterConfig config;
    final Codec codec;
    final InfoStream infoStream;
    private final Object commitLock = new Object();
    private final Object fullFlushLock = new Object();
    private boolean keepFullyDeletedSegments;

    DirectoryReader getReader() throws IOException {
        return this.getReader(true);
    }

    /*
     * Exception decompiling
     */
    DirectoryReader getReader(boolean applyAllDeletes) throws IOException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[TRYBLOCK]], but top level block is 5[TRYBLOCK]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    public int numDeletedDocs(SegmentInfoPerCommit info) {
        this.ensureOpen(false);
        int delCount = info.getDelCount();
        ReadersAndLiveDocs rld = this.readerPool.get(info, false);
        if (rld != null) {
            delCount += rld.getPendingDeleteCount();
        }
        return delCount;
    }

    protected final void ensureOpen(boolean failIfClosing) throws AlreadyClosedException {
        if (this.closed || failIfClosing && this.closing) {
            throw new AlreadyClosedException("this IndexWriter is closed");
        }
    }

    protected final void ensureOpen() throws AlreadyClosedException {
        this.ensureOpen(true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public IndexWriter(Directory d, IndexWriterConfig conf) throws IOException {
        this.config = new LiveIndexWriterConfig(conf.clone());
        this.directory = d;
        this.analyzer = this.config.getAnalyzer();
        this.infoStream = this.config.getInfoStream();
        this.mergePolicy = this.config.getMergePolicy();
        this.mergePolicy.setIndexWriter(this);
        this.mergeScheduler = this.config.getMergeScheduler();
        this.codec = this.config.getCodec();
        this.bufferedDeletesStream = new BufferedDeletesStream(this.infoStream);
        this.poolReaders = this.config.getReaderPooling();
        this.writeLock = this.directory.makeLock(WRITE_LOCK_NAME);
        if (!this.writeLock.obtain(this.config.getWriteLockTimeout())) {
            throw new LockObtainFailedException("Index locked for write: " + this.writeLock);
        }
        boolean success = false;
        try {
            IndexWriterConfig.OpenMode mode = this.config.getOpenMode();
            boolean create = mode == IndexWriterConfig.OpenMode.CREATE ? true : (mode == IndexWriterConfig.OpenMode.APPEND ? false : !DirectoryReader.indexExists(this.directory));
            this.segmentInfos = new SegmentInfos();
            boolean initialIndexExists = true;
            if (create) {
                try {
                    this.segmentInfos.read(this.directory);
                    this.segmentInfos.clear();
                }
                catch (IOException e) {
                    initialIndexExists = false;
                }
                this.changed();
            } else {
                this.segmentInfos.read(this.directory);
                IndexCommit commit = this.config.getIndexCommit();
                if (commit != null) {
                    if (commit.getDirectory() != this.directory) {
                        throw new IllegalArgumentException("IndexCommit's directory doesn't match my directory");
                    }
                    SegmentInfos oldInfos = new SegmentInfos();
                    oldInfos.read(this.directory, commit.getSegmentsFileName());
                    this.segmentInfos.replace(oldInfos);
                    this.changed();
                    if (this.infoStream.isEnabled("IW")) {
                        this.infoStream.message("IW", "init: loaded commit \"" + commit.getSegmentsFileName() + "\"");
                    }
                }
            }
            this.rollbackSegments = this.segmentInfos.createBackupSegmentInfos();
            this.globalFieldNumberMap = this.getFieldNumberMap();
            this.docWriter = new DocumentsWriter(this.codec, this.config, this.directory, this, this.globalFieldNumberMap, this.bufferedDeletesStream);
            IndexWriter indexWriter = this;
            synchronized (indexWriter) {
                this.deleter = new IndexFileDeleter(this.directory, this.config.getIndexDeletionPolicy(), this.segmentInfos, this.infoStream, this, initialIndexExists);
            }
            if (this.deleter.startingCommitDeleted) {
                this.changed();
            }
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "init: create=" + create);
                this.messageState();
            }
            success = true;
        }
        finally {
            if (!success) {
                if (this.infoStream.isEnabled("IW")) {
                    this.infoStream.message("IW", "init: hit exception on init; releasing write lock");
                }
                try {
                    this.writeLock.release();
                }
                catch (Throwable throwable) {}
                this.writeLock = null;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private FieldInfos getFieldInfos(SegmentInfo info) throws IOException {
        Directory cfsDir = null;
        try {
            cfsDir = info.getUseCompoundFile() ? new CompoundFileDirectory(info.dir, IndexFileNames.segmentFileName(info.name, "", "cfs"), IOContext.READONCE, false) : info.dir;
            FieldInfos fieldInfos = info.getCodec().fieldInfosFormat().getFieldInfosReader().read(cfsDir, info.name, IOContext.READONCE);
            return fieldInfos;
        }
        finally {
            if (info.getUseCompoundFile() && cfsDir != null) {
                cfsDir.close();
            }
        }
    }

    private FieldInfos.FieldNumbers getFieldNumberMap() throws IOException {
        FieldInfos.FieldNumbers map = new FieldInfos.FieldNumbers();
        for (SegmentInfoPerCommit info : this.segmentInfos) {
            for (FieldInfo fi : this.getFieldInfos(info.info)) {
                map.addOrGet(fi.name, fi.number, fi.getDocValuesType());
            }
        }
        return map;
    }

    public LiveIndexWriterConfig getConfig() {
        this.ensureOpen(false);
        return this.config;
    }

    private void messageState() {
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "\ndir=" + this.directory + "\nindex=" + this.segString() + "\nversion=" + Constants.LUCENE_VERSION + "\n" + this.config.toString());
        }
    }

    @Override
    public void close() throws IOException {
        this.close(true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void close(boolean waitForMerges) throws IOException {
        Object object = this.commitLock;
        synchronized (object) {
            if (this.shouldClose()) {
                if (this.hitOOM) {
                    this.rollbackInternal();
                } else {
                    this.closeInternal(waitForMerges, true);
                }
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
     * Loose catch block
     */
    private void closeInternal(boolean waitForMerges, boolean doFlush) throws IOException {
        boolean interrupted = false;
        try {
            block56: {
                block54: {
                    if (this.pendingCommit != null) {
                        throw new IllegalStateException("cannot close: prepareCommit was already called with no corresponding call to commit");
                    }
                    if (this.infoStream.isEnabled("IW")) {
                        this.infoStream.message("IW", "now flush at close waitForMerges=" + waitForMerges);
                    }
                    this.docWriter.close();
                    if (doFlush) {
                        this.flush(waitForMerges, true);
                        break block54;
                    }
                    this.docWriter.abort();
                }
                try {
                    block55: {
                        interrupted = Thread.interrupted();
                        if (waitForMerges) {
                            try {
                                this.mergeScheduler.merge(this);
                            }
                            catch (ThreadInterruptedException tie) {
                                interrupted = true;
                                if (!this.infoStream.isEnabled("IW")) break block55;
                                this.infoStream.message("IW", "interrupted while waiting for final merges");
                            }
                        }
                    }
                    IndexWriter tie = this;
                    synchronized (tie) {
                        while (true) {
                            try {
                                this.finishMerges(waitForMerges && !interrupted);
                            }
                            catch (ThreadInterruptedException tie2) {
                                interrupted = true;
                                if (!this.infoStream.isEnabled("IW")) continue;
                                this.infoStream.message("IW", "interrupted while waiting for merges to finish");
                                continue;
                            }
                            break;
                        }
                        this.stopMerges = true;
                        break block56;
                    }
                }
                catch (Throwable throwable) {
                    IOUtils.closeWhileHandlingException(this.mergePolicy, this.mergeScheduler);
                    throw throwable;
                }
                catch (Throwable throwable) {
                    try {
                        block57: {
                            interrupted = Thread.interrupted();
                            if (waitForMerges) {
                                try {
                                    this.mergeScheduler.merge(this);
                                }
                                catch (ThreadInterruptedException tie) {
                                    interrupted = true;
                                    if (!this.infoStream.isEnabled("IW")) break block57;
                                    this.infoStream.message("IW", "interrupted while waiting for final merges");
                                }
                            }
                        }
                        IndexWriter indexWriter = this;
                        synchronized (indexWriter) {
                            while (true) {
                                try {
                                    this.finishMerges(waitForMerges && !interrupted);
                                }
                                catch (ThreadInterruptedException tie) {
                                    interrupted = true;
                                    if (!this.infoStream.isEnabled("IW")) continue;
                                    this.infoStream.message("IW", "interrupted while waiting for merges to finish");
                                    continue;
                                }
                                break;
                            }
                            this.stopMerges = true;
                        }
                    }
                    catch (Throwable throwable2) {
                        IOUtils.closeWhileHandlingException(this.mergePolicy, this.mergeScheduler);
                        throw throwable2;
                    }
                    IOUtils.closeWhileHandlingException(this.mergePolicy, this.mergeScheduler);
                    throw throwable;
                }
            }
            IOUtils.closeWhileHandlingException(this.mergePolicy, this.mergeScheduler);
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "now call final commit()");
            }
            if (doFlush) {
                this.commitInternal();
            }
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "at close: " + this.segString());
            }
            DocumentsWriter oldWriter = this.docWriter;
            IndexWriter indexWriter = this;
            synchronized (indexWriter) {
                this.readerPool.dropAll(true);
                this.docWriter = null;
                this.deleter.close();
            }
            if (this.writeLock != null) {
                this.writeLock.release();
                this.writeLock = null;
            }
            indexWriter = this;
            synchronized (indexWriter) {
                this.closed = true;
            }
            assert (oldWriter.perThreadPool.numDeactivatedThreadStates() == oldWriter.perThreadPool.getMaxThreadStates());
        }
        catch (OutOfMemoryError oom) {
            this.handleOOM(oom, "closeInternal");
        }
        finally {
            IndexWriter indexWriter = this;
            synchronized (indexWriter) {
                this.closing = false;
                this.notifyAll();
                if (!this.closed && this.infoStream.isEnabled("IW")) {
                    this.infoStream.message("IW", "hit exception while closing");
                }
            }
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public Directory getDirectory() {
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

    public synchronized int numDocs() {
        this.ensureOpen();
        int count = this.docWriter != null ? this.docWriter.getNumDocs() : 0;
        for (SegmentInfoPerCommit info : this.segmentInfos) {
            count += info.info.getDocCount() - this.numDeletedDocs(info);
        }
        return count;
    }

    public synchronized boolean hasDeletions() {
        this.ensureOpen();
        if (this.bufferedDeletesStream.any()) {
            return true;
        }
        if (this.docWriter.anyDeletions()) {
            return true;
        }
        if (this.readerPool.anyPendingDeletes()) {
            return true;
        }
        for (SegmentInfoPerCommit info : this.segmentInfos) {
            if (!info.hasDeletions()) continue;
            return true;
        }
        return false;
    }

    public void addDocument(Iterable<? extends IndexableField> doc) throws IOException {
        this.addDocument(doc, this.analyzer);
    }

    public void addDocument(Iterable<? extends IndexableField> doc, Analyzer analyzer) throws IOException {
        this.updateDocument(null, doc, analyzer);
    }

    public void addDocuments(Iterable<? extends Iterable<? extends IndexableField>> docs) throws IOException {
        this.addDocuments(docs, this.analyzer);
    }

    public void addDocuments(Iterable<? extends Iterable<? extends IndexableField>> docs, Analyzer analyzer) throws IOException {
        this.updateDocuments(null, docs, analyzer);
    }

    public void updateDocuments(Term delTerm, Iterable<? extends Iterable<? extends IndexableField>> docs) throws IOException {
        this.updateDocuments(delTerm, docs, this.analyzer);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateDocuments(Term delTerm, Iterable<? extends Iterable<? extends IndexableField>> docs, Analyzer analyzer) throws IOException {
        this.ensureOpen();
        try {
            boolean success = false;
            boolean anySegmentFlushed = false;
            try {
                anySegmentFlushed = this.docWriter.updateDocuments(docs, analyzer, delTerm);
                success = true;
            }
            finally {
                if (!success && this.infoStream.isEnabled("IW")) {
                    this.infoStream.message("IW", "hit exception updating document");
                }
            }
            if (anySegmentFlushed) {
                this.maybeMerge(MergePolicy.MergeTrigger.SEGMENT_FLUSH, -1);
            }
        }
        catch (OutOfMemoryError oom) {
            this.handleOOM(oom, "updateDocuments");
        }
    }

    public void deleteDocuments(Term term) throws IOException {
        this.ensureOpen();
        try {
            this.docWriter.deleteTerms(term);
        }
        catch (OutOfMemoryError oom) {
            this.handleOOM(oom, "deleteDocuments(Term)");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized boolean tryDeleteDocument(IndexReader readerIn, int docID) throws IOException {
        ReadersAndLiveDocs rld;
        AtomicReader reader;
        if (readerIn instanceof AtomicReader) {
            reader = (AtomicReader)readerIn;
        } else {
            List<AtomicReaderContext> leaves = readerIn.leaves();
            int subIndex = ReaderUtil.subIndex(docID, leaves);
            reader = leaves.get(subIndex).reader();
            assert ((docID -= leaves.get((int)subIndex).docBase) >= 0);
            assert (docID < reader.maxDoc());
        }
        if (!(reader instanceof SegmentReader)) {
            throw new IllegalArgumentException("the reader must be a SegmentReader or composite reader containing only SegmentReaders");
        }
        SegmentInfoPerCommit info = ((SegmentReader)reader).getSegmentInfo();
        if (this.segmentInfos.indexOf(info) != -1 && (rld = this.readerPool.get(info, false)) != null) {
            BufferedDeletesStream bufferedDeletesStream = this.bufferedDeletesStream;
            synchronized (bufferedDeletesStream) {
                rld.initWritableLiveDocs();
                if (rld.delete(docID)) {
                    int fullDelCount = rld.info.getDelCount() + rld.getPendingDeleteCount();
                    if (fullDelCount == rld.info.info.getDocCount() && !this.mergingSegments.contains(rld.info)) {
                        this.segmentInfos.remove(rld.info);
                        this.readerPool.drop(rld.info);
                        this.checkpoint();
                    }
                    this.changed();
                }
                return true;
            }
        }
        return false;
    }

    public void deleteDocuments(Term ... terms) throws IOException {
        this.ensureOpen();
        try {
            this.docWriter.deleteTerms(terms);
        }
        catch (OutOfMemoryError oom) {
            this.handleOOM(oom, "deleteDocuments(Term..)");
        }
    }

    public void deleteDocuments(Query query) throws IOException {
        this.ensureOpen();
        try {
            this.docWriter.deleteQueries(query);
        }
        catch (OutOfMemoryError oom) {
            this.handleOOM(oom, "deleteDocuments(Query)");
        }
    }

    public void deleteDocuments(Query ... queries) throws IOException {
        this.ensureOpen();
        try {
            this.docWriter.deleteQueries(queries);
        }
        catch (OutOfMemoryError oom) {
            this.handleOOM(oom, "deleteDocuments(Query..)");
        }
    }

    public void updateDocument(Term term, Iterable<? extends IndexableField> doc) throws IOException {
        this.ensureOpen();
        this.updateDocument(term, doc, this.analyzer);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateDocument(Term term, Iterable<? extends IndexableField> doc, Analyzer analyzer) throws IOException {
        this.ensureOpen();
        try {
            boolean success = false;
            boolean anySegmentFlushed = false;
            try {
                anySegmentFlushed = this.docWriter.updateDocument(doc, analyzer, term);
                success = true;
            }
            finally {
                if (!success && this.infoStream.isEnabled("IW")) {
                    this.infoStream.message("IW", "hit exception updating document");
                }
            }
            if (anySegmentFlushed) {
                this.maybeMerge(MergePolicy.MergeTrigger.SEGMENT_FLUSH, -1);
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

    final synchronized Collection<String> getIndexFileNames() throws IOException {
        return this.segmentInfos.files(this.directory, true);
    }

    final synchronized int getDocCount(int i) {
        if (i >= 0 && i < this.segmentInfos.size()) {
            return this.segmentInfos.info((int)i).info.getDocCount();
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

    public void forceMerge(int maxNumSegments) throws IOException {
        this.forceMerge(maxNumSegments, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void forceMerge(int maxNumSegments, boolean doWait) throws IOException {
        this.ensureOpen();
        if (maxNumSegments < 1) {
            throw new IllegalArgumentException("maxNumSegments must be >= 1; got " + maxNumSegments);
        }
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "forceMerge: index now " + this.segString());
            this.infoStream.message("IW", "now flush at forceMerge");
        }
        this.flush(true, true);
        IndexWriter indexWriter = this;
        synchronized (indexWriter) {
            this.resetMergeExceptions();
            this.segmentsToMerge.clear();
            for (SegmentInfoPerCommit info : this.segmentInfos) {
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
        this.maybeMerge(MergePolicy.MergeTrigger.EXPLICIT, maxNumSegments);
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void forceMergeDeletes(boolean doWait) throws IOException {
        MergePolicy.MergeSpecification spec;
        this.ensureOpen();
        this.flush(true, true);
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "forceMergeDeletes: index now " + this.segString());
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

    public void forceMergeDeletes() throws IOException {
        this.forceMergeDeletes(true);
    }

    public final void maybeMerge() throws IOException {
        this.maybeMerge(MergePolicy.MergeTrigger.EXPLICIT, -1);
    }

    private final void maybeMerge(MergePolicy.MergeTrigger trigger, int maxNumSegments) throws IOException {
        this.ensureOpen(false);
        this.updatePendingMerges(trigger, maxNumSegments);
        this.mergeScheduler.merge(this);
    }

    private synchronized void updatePendingMerges(MergePolicy.MergeTrigger trigger, int maxNumSegments) throws IOException {
        int i;
        int numMerges;
        MergePolicy.MergeSpecification spec;
        assert (maxNumSegments == -1 || maxNumSegments > 0);
        assert (trigger != null);
        if (this.stopMerges) {
            return;
        }
        if (this.hitOOM) {
            return;
        }
        if (maxNumSegments != -1) {
            assert (trigger == MergePolicy.MergeTrigger.EXPLICIT || trigger == MergePolicy.MergeTrigger.MERGE_FINISHED) : "Expected EXPLICT or MERGE_FINISHED as trigger even with maxNumSegments set but was: " + trigger.name();
            spec = this.mergePolicy.findForcedMerges(this.segmentInfos, maxNumSegments, Collections.unmodifiableMap(this.segmentsToMerge));
            if (spec != null) {
                numMerges = spec.merges.size();
                for (i = 0; i < numMerges; ++i) {
                    MergePolicy.OneMerge merge = spec.merges.get(i);
                    merge.maxNumSegments = maxNumSegments;
                }
            }
        } else {
            spec = this.mergePolicy.findMerges(trigger, this.segmentInfos);
        }
        if (spec != null) {
            numMerges = spec.merges.size();
            for (i = 0; i < numMerges; ++i) {
                this.registerMerge(spec.merges.get(i));
            }
        }
    }

    public synchronized Collection<SegmentInfoPerCommit> getMergingSegments() {
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

    public synchronized boolean hasPendingMerges() {
        return this.pendingMerges.size() != 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void rollback() throws IOException {
        this.ensureOpen();
        Object object = this.commitLock;
        synchronized (object) {
            if (this.shouldClose()) {
                this.rollbackInternal();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void rollbackInternal() throws IOException {
        boolean success = false;
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "rollback");
        }
        try {
            IndexWriter indexWriter = this;
            synchronized (indexWriter) {
                this.finishMerges(false);
                this.stopMerges = true;
            }
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "rollback: done finish merges");
            }
            this.mergePolicy.close();
            this.mergeScheduler.close();
            this.bufferedDeletesStream.clear();
            this.docWriter.close();
            this.docWriter.abort();
            indexWriter = this;
            synchronized (indexWriter) {
                if (this.pendingCommit != null) {
                    this.pendingCommit.rollbackCommit(this.directory);
                    this.deleter.decRef(this.pendingCommit);
                    this.pendingCommit = null;
                    this.notifyAll();
                }
                this.readerPool.dropAll(false);
                this.segmentInfos.rollbackSegmentInfos(this.rollbackSegments);
                if (this.infoStream.isEnabled("IW")) {
                    this.infoStream.message("IW", "rollback: infos=" + this.segString(this.segmentInfos));
                }
                assert (this.testPoint("rollback before checkpoint"));
                this.deleter.checkpoint(this.segmentInfos, false);
                this.deleter.refresh();
                this.lastCommitChangeCount = this.changeCount;
            }
            success = true;
        }
        catch (OutOfMemoryError oom) {
            this.handleOOM(oom, "rollbackInternal");
        }
        finally {
            IndexWriter indexWriter = this;
            synchronized (indexWriter) {
                if (!success) {
                    this.closing = false;
                    this.notifyAll();
                    if (this.infoStream.isEnabled("IW")) {
                        this.infoStream.message("IW", "hit exception during rollback");
                    }
                }
            }
        }
        this.closeInternal(false, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void deleteAll() throws IOException {
        this.ensureOpen();
        boolean success = false;
        Object object = this.fullFlushLock;
        synchronized (object) {
            try {
                this.docWriter.lockAndAbortAll();
                IndexWriter indexWriter = this;
                synchronized (indexWriter) {
                    try {
                        this.finishMerges(false);
                        this.segmentInfos.clear();
                        this.deleter.checkpoint(this.segmentInfos, false);
                        this.readerPool.dropAll(false);
                        ++this.changeCount;
                        this.segmentInfos.changed();
                        this.globalFieldNumberMap.clear();
                        success = true;
                    }
                    catch (OutOfMemoryError oom) {
                        this.handleOOM(oom, "deleteAll");
                    }
                    finally {
                        if (!success && this.infoStream.isEnabled("IW")) {
                            this.infoStream.message("IW", "hit exception during deleteAll");
                        }
                    }
                }
            }
            finally {
                this.docWriter.unlockAllAfterAbortAll();
            }
        }
    }

    private synchronized void finishMerges(boolean waitForMerges) {
        if (!waitForMerges) {
            this.stopMerges = true;
            for (MergePolicy.OneMerge merge : this.pendingMerges) {
                if (this.infoStream.isEnabled("IW")) {
                    this.infoStream.message("IW", "now abort pending merge " + this.segString(merge.segments));
                }
                merge.abort();
                this.mergeFinish(merge);
            }
            this.pendingMerges.clear();
            for (MergePolicy.OneMerge merge : this.runningMerges) {
                if (this.infoStream.isEnabled("IW")) {
                    this.infoStream.message("IW", "now abort running merge " + this.segString(merge.segments));
                }
                merge.abort();
            }
            while (this.runningMerges.size() > 0) {
                if (this.infoStream.isEnabled("IW")) {
                    this.infoStream.message("IW", "now wait for " + this.runningMerges.size() + " running merge/s to abort");
                }
                this.doWait();
            }
            this.stopMerges = false;
            this.notifyAll();
            assert (0 == this.mergingSegments.size());
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "all running merges have aborted");
            }
        } else {
            this.waitForMerges();
        }
    }

    public synchronized void waitForMerges() {
        this.ensureOpen(false);
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "waitForMerges");
        }
        while (this.pendingMerges.size() > 0 || this.runningMerges.size() > 0) {
            this.doWait();
        }
        assert (0 == this.mergingSegments.size());
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "waitForMerges done");
        }
    }

    synchronized void checkpoint() throws IOException {
        this.changed();
        this.deleter.checkpoint(this.segmentInfos, false);
    }

    synchronized void changed() {
        ++this.changeCount;
        this.segmentInfos.changed();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    synchronized void publishFrozenDeletes(FrozenBufferedDeletes packet) {
        assert (packet != null && packet.any());
        BufferedDeletesStream bufferedDeletesStream = this.bufferedDeletesStream;
        synchronized (bufferedDeletesStream) {
            this.bufferedDeletesStream.push(packet);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    synchronized void publishFlushedSegment(SegmentInfoPerCommit newSegment, FrozenBufferedDeletes packet, FrozenBufferedDeletes globalPacket) throws IOException {
        BufferedDeletesStream bufferedDeletesStream = this.bufferedDeletesStream;
        synchronized (bufferedDeletesStream) {
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "publishFlushedSegment");
            }
            if (globalPacket != null && globalPacket.any()) {
                this.bufferedDeletesStream.push(globalPacket);
            }
            long nextGen = packet != null && packet.any() ? this.bufferedDeletesStream.push(packet) : this.bufferedDeletesStream.getNextGen();
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "publish sets newSegment delGen=" + nextGen + " seg=" + this.segString(newSegment));
            }
            newSegment.setBufferedDeletesGen(nextGen);
            this.segmentInfos.add(newSegment);
            this.checkpoint();
        }
    }

    private synchronized void resetMergeExceptions() {
        this.mergeExceptions = new ArrayList<MergePolicy.OneMerge>();
        ++this.mergeGen;
    }

    private void noDupDirs(Directory ... dirs) {
        HashSet<Directory> dups = new HashSet<Directory>();
        for (int i = 0; i < dirs.length; ++i) {
            if (dups.contains(dirs[i])) {
                throw new IllegalArgumentException("Directory " + dirs[i] + " appears more than once");
            }
            if (dirs[i] == this.directory) {
                throw new IllegalArgumentException("Cannot add directory to itself");
            }
            dups.add(dirs[i]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addIndexes(Directory ... dirs) throws IOException {
        this.ensureOpen();
        this.noDupDirs(dirs);
        try {
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "flush at addIndexes(Directory...)");
            }
            this.flush(false, true);
            ArrayList<SegmentInfoPerCommit> infos = new ArrayList<SegmentInfoPerCommit>();
            boolean success = false;
            try {
                for (Directory dir : dirs) {
                    if (this.infoStream.isEnabled("IW")) {
                        this.infoStream.message("IW", "addIndexes: process directory " + dir);
                    }
                    SegmentInfos sis = new SegmentInfos();
                    sis.read(dir);
                    HashSet<String> dsFilesCopied = new HashSet<String>();
                    HashMap<String, String> dsNames = new HashMap<String, String>();
                    HashSet<String> copiedFiles = new HashSet<String>();
                    for (SegmentInfoPerCommit info : sis) {
                        assert (!infos.contains(info)) : "dup info dir=" + info.info.dir + " name=" + info.info.name;
                        String newSegName = this.newSegmentName();
                        if (this.infoStream.isEnabled("IW")) {
                            this.infoStream.message("IW", "addIndexes: process segment origName=" + info.info.name + " newName=" + newSegName + " info=" + info);
                        }
                        IOContext context = new IOContext(new MergeInfo(info.info.getDocCount(), info.sizeInBytes(), true, -1));
                        for (FieldInfo fi : this.getFieldInfos(info.info)) {
                            this.globalFieldNumberMap.addOrGet(fi.name, fi.number, fi.getDocValuesType());
                        }
                        infos.add(this.copySegmentAsIs(info, newSegName, dsNames, dsFilesCopied, context, copiedFiles));
                    }
                }
                success = true;
            }
            finally {
                if (!success) {
                    for (SegmentInfoPerCommit sipc : infos) {
                        for (String file : sipc.files()) {
                            try {
                                this.directory.deleteFile(file);
                            }
                            catch (Throwable sis) {}
                        }
                    }
                }
            }
            IndexWriter indexWriter = this;
            synchronized (indexWriter) {
                success = false;
                try {
                    this.ensureOpen();
                    success = true;
                }
                finally {
                    if (!success) {
                        for (SegmentInfoPerCommit sipc : infos) {
                            for (String file : sipc.files()) {
                                try {
                                    this.directory.deleteFile(file);
                                }
                                catch (Throwable throwable) {}
                            }
                        }
                    }
                }
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
    public void addIndexes(IndexReader ... readers) throws IOException {
        this.ensureOpen();
        int numDocs = 0;
        try {
            boolean useCompoundFile;
            MergeState mergeState;
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "flush at addIndexes(IndexReader...)");
            }
            this.flush(false, true);
            String mergedName = this.newSegmentName();
            ArrayList<AtomicReader> mergeReaders = new ArrayList<AtomicReader>();
            for (IndexReader indexReader : readers) {
                numDocs += indexReader.numDocs();
                for (AtomicReaderContext ctx : indexReader.leaves()) {
                    mergeReaders.add(ctx.reader());
                }
            }
            IOContext context = new IOContext(new MergeInfo(numDocs, -1L, true, -1));
            TrackingDirectoryWrapper trackingDir = new TrackingDirectoryWrapper(this.directory);
            SegmentInfo info = new SegmentInfo(this.directory, Constants.LUCENE_MAIN_VERSION, mergedName, -1, false, this.codec, null, null);
            SegmentMerger merger = new SegmentMerger(mergeReaders, info, this.infoStream, trackingDir, this.config.getTermIndexInterval(), MergeState.CheckAbort.NONE, this.globalFieldNumberMap, context);
            boolean success = false;
            try {
                mergeState = merger.merge();
                success = true;
            }
            finally {
                if (!success) {
                    IndexWriter indexWriter = this;
                    synchronized (indexWriter) {
                        this.deleter.refresh(info.name);
                    }
                }
            }
            SegmentInfoPerCommit infoPerCommit = new SegmentInfoPerCommit(info, 0, -1L);
            info.setFiles(new HashSet<String>(trackingDir.getCreatedFiles()));
            trackingDir.getCreatedFiles().clear();
            IndexWriter.setDiagnostics(info, SOURCE_ADDINDEXES_READERS);
            IndexWriter indexWriter = this;
            synchronized (indexWriter) {
                if (this.stopMerges) {
                    this.deleter.deleteNewFiles(infoPerCommit.files());
                    return;
                }
                this.ensureOpen();
                useCompoundFile = this.mergePolicy.useCompoundFile(this.segmentInfos, infoPerCommit);
            }
            if (useCompoundFile) {
                Collection<String> filesToDelete = infoPerCommit.files();
                try {
                    IndexWriter.createCompoundFile(this.infoStream, this.directory, MergeState.CheckAbort.NONE, info, context);
                }
                finally {
                    IndexWriter indexWriter2 = this;
                    synchronized (indexWriter2) {
                        this.deleter.deleteNewFiles(filesToDelete);
                    }
                }
                info.setUseCompoundFile(true);
            }
            success = false;
            try {
                this.codec.segmentInfoFormat().getSegmentInfoWriter().write(trackingDir, info, mergeState.fieldInfos, context);
                success = true;
            }
            finally {
                if (!success) {
                    indexWriter = this;
                    synchronized (indexWriter) {
                        this.deleter.refresh(info.name);
                    }
                }
            }
            info.addFiles(trackingDir.getCreatedFiles());
            indexWriter = this;
            synchronized (indexWriter) {
                if (this.stopMerges) {
                    this.deleter.deleteNewFiles(info.files());
                    return;
                }
                this.ensureOpen();
                this.segmentInfos.add(infoPerCommit);
                this.checkpoint();
            }
        }
        catch (OutOfMemoryError oom) {
            this.handleOOM(oom, SOURCE_ADDINDEXES_READERS);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private SegmentInfoPerCommit copySegmentAsIs(SegmentInfoPerCommit info, String segName, Map<String, String> dsNames, Set<String> dsFilesCopied, IOContext context, Set<String> copiedFiles) throws IOException {
        String newDsName;
        String dsName = Lucene3xSegmentInfoFormat.getDocStoreSegment(info.info);
        assert (dsName != null);
        if (dsNames.containsKey(dsName)) {
            newDsName = dsNames.get(dsName);
        } else {
            dsNames.put(dsName, segName);
            newDsName = segName;
        }
        FieldInfos fis = this.getFieldInfos(info.info);
        Set<String> docStoreFiles3xOnly = Lucene3xCodec.getDocStoreFiles(info.info);
        HashMap<Object, Object> attributes = info.info.attributes() == null ? new HashMap() : new HashMap<String, String>(info.info.attributes());
        if (docStoreFiles3xOnly != null) {
            attributes.put(Lucene3xSegmentInfoFormat.DS_NAME_KEY, newDsName);
        }
        SegmentInfo newInfo = new SegmentInfo(this.directory, info.info.getVersion(), segName, info.info.getDocCount(), info.info.getUseCompoundFile(), info.info.getCodec(), info.info.getDiagnostics(), attributes);
        SegmentInfoPerCommit newInfoPerCommit = new SegmentInfoPerCommit(newInfo, info.getDelCount(), info.getDelGen());
        HashSet<String> segFiles = new HashSet<String>();
        for (String file : info.files()) {
            String newFileName = docStoreFiles3xOnly != null && docStoreFiles3xOnly.contains(file) ? newDsName + IndexFileNames.stripSegmentName(file) : segName + IndexFileNames.stripSegmentName(file);
            segFiles.add(newFileName);
        }
        newInfo.setFiles(segFiles);
        TrackingDirectoryWrapper trackingDir = new TrackingDirectoryWrapper(this.directory);
        try {
            newInfo.getCodec().segmentInfoFormat().getSegmentInfoWriter().write(trackingDir, newInfo, fis, context);
        }
        catch (UnsupportedOperationException file) {
            // empty catch block
        }
        Set<String> siFiles = trackingDir.getCreatedFiles();
        boolean success = false;
        try {
            for (String file : info.files()) {
                String newFileName;
                if (docStoreFiles3xOnly != null && docStoreFiles3xOnly.contains(file)) {
                    newFileName = newDsName + IndexFileNames.stripSegmentName(file);
                    if (dsFilesCopied.contains(newFileName)) continue;
                    dsFilesCopied.add(newFileName);
                } else {
                    newFileName = segName + IndexFileNames.stripSegmentName(file);
                }
                if (siFiles.contains(newFileName)) continue;
                assert (!this.directory.fileExists(newFileName)) : "file \"" + newFileName + "\" already exists; siFiles=" + siFiles;
                assert (!copiedFiles.contains(file)) : "file \"" + file + "\" is being copied more than once";
                copiedFiles.add(file);
                info.info.dir.copy(this.directory, file, newFileName, context);
            }
            success = true;
        }
        finally {
            if (!success) {
                for (String file : newInfo.files()) {
                    try {
                        this.directory.deleteFile(file);
                    }
                    catch (Throwable throwable) {}
                }
            }
        }
        return newInfoPerCommit;
    }

    protected void doAfterFlush() throws IOException {
    }

    protected void doBeforeFlush() throws IOException {
    }

    @Override
    public final void prepareCommit() throws IOException {
        this.ensureOpen();
        this.prepareCommitInternal();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void prepareCommitInternal() throws IOException {
        Object object = this.commitLock;
        synchronized (object) {
            this.ensureOpen(false);
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "prepareCommit: flush");
                this.infoStream.message("IW", "  index before flush " + this.segString());
            }
            if (this.hitOOM) {
                throw new IllegalStateException("this writer hit an OutOfMemoryError; cannot commit");
            }
            if (this.pendingCommit != null) {
                throw new IllegalStateException("prepareCommit was already called with no corresponding call to commit");
            }
            this.doBeforeFlush();
            assert (this.testPoint("startDoFlush"));
            SegmentInfos toCommit = null;
            boolean anySegmentsFlushed = false;
            try {
                Object object2 = this.fullFlushLock;
                synchronized (object2) {
                    boolean flushSuccess = false;
                    boolean success = false;
                    try {
                        anySegmentsFlushed = this.docWriter.flushAllThreads();
                        if (!anySegmentsFlushed) {
                            this.flushCount.incrementAndGet();
                        }
                        flushSuccess = true;
                        IndexWriter indexWriter = this;
                        synchronized (indexWriter) {
                            this.maybeApplyDeletes(true);
                            this.readerPool.commit(this.segmentInfos);
                            toCommit = this.segmentInfos.clone();
                            this.pendingCommitChangeCount = this.changeCount;
                            this.filesToCommit = toCommit.files(this.directory, false);
                            this.deleter.incRef(this.filesToCommit);
                        }
                        success = true;
                    }
                    finally {
                        if (!success && this.infoStream.isEnabled("IW")) {
                            this.infoStream.message("IW", "hit exception during prepareCommit");
                        }
                        this.docWriter.finishFullFlush(flushSuccess);
                        this.doAfterFlush();
                    }
                }
            }
            catch (OutOfMemoryError oom) {
                this.handleOOM(oom, "prepareCommit");
            }
            boolean success = false;
            try {
                if (anySegmentsFlushed) {
                    this.maybeMerge(MergePolicy.MergeTrigger.FULL_FLUSH, -1);
                }
                success = true;
            }
            finally {
                if (!success) {
                    IndexWriter indexWriter = this;
                    synchronized (indexWriter) {
                        this.deleter.decRef(this.filesToCommit);
                        this.filesToCommit = null;
                    }
                }
            }
            this.startCommit(toCommit);
        }
    }

    public final synchronized void setCommitData(Map<String, String> commitUserData) {
        this.segmentInfos.setUserData(new HashMap<String, String>(commitUserData));
        ++this.changeCount;
    }

    public final synchronized Map<String, String> getCommitData() {
        return this.segmentInfos.getUserData();
    }

    @Override
    public final void commit() throws IOException {
        this.ensureOpen();
        this.commitInternal();
    }

    public final boolean hasUncommittedChanges() {
        return this.changeCount != this.lastCommitChangeCount;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final void commitInternal() throws IOException {
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "commit: start");
        }
        Object object = this.commitLock;
        synchronized (object) {
            this.ensureOpen(false);
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "commit: enter lock");
            }
            if (this.pendingCommit == null) {
                if (this.infoStream.isEnabled("IW")) {
                    this.infoStream.message("IW", "commit: now prepare");
                }
                this.prepareCommitInternal();
            } else if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "commit: already prepared");
            }
            this.finishCommit();
        }
    }

    private final synchronized void finishCommit() throws IOException {
        if (this.pendingCommit != null) {
            try {
                if (this.infoStream.isEnabled("IW")) {
                    this.infoStream.message("IW", "commit: pendingCommit != null");
                }
                this.pendingCommit.finishCommit(this.directory);
                if (this.infoStream.isEnabled("IW")) {
                    this.infoStream.message("IW", "commit: wrote segments file \"" + this.pendingCommit.getSegmentsFileName() + "\"");
                }
                this.segmentInfos.updateGeneration(this.pendingCommit);
                this.lastCommitChangeCount = this.pendingCommitChangeCount;
                this.rollbackSegments = this.pendingCommit.createBackupSegmentInfos();
                this.deleter.checkpoint(this.pendingCommit, true);
            }
            finally {
                this.deleter.decRef(this.filesToCommit);
                this.filesToCommit = null;
                this.pendingCommit = null;
                this.notifyAll();
            }
        } else if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "commit: pendingCommit == null; skip");
        }
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "commit: done");
        }
    }

    boolean holdsFullFlushLock() {
        return Thread.holdsLock(this.fullFlushLock);
    }

    protected final void flush(boolean triggerMerge, boolean applyAllDeletes) throws IOException {
        this.ensureOpen(false);
        if (this.doFlush(applyAllDeletes) && triggerMerge) {
            this.maybeMerge(MergePolicy.MergeTrigger.FULL_FLUSH, -1);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean doFlush(boolean applyAllDeletes) throws IOException {
        if (this.hitOOM) {
            throw new IllegalStateException("this writer hit an OutOfMemoryError; cannot flush");
        }
        this.doBeforeFlush();
        assert (this.testPoint("startDoFlush"));
        boolean success = false;
        try {
            boolean anySegmentFlushed;
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "  start flush: applyAllDeletes=" + applyAllDeletes);
                this.infoStream.message("IW", "  index before flush " + this.segString());
            }
            Object object = this.fullFlushLock;
            synchronized (object) {
                boolean flushSuccess = false;
                try {
                    anySegmentFlushed = this.docWriter.flushAllThreads();
                    flushSuccess = true;
                }
                finally {
                    this.docWriter.finishFullFlush(flushSuccess);
                }
            }
            object = this;
            synchronized (object) {
                try {
                    this.maybeApplyDeletes(applyAllDeletes);
                    this.doAfterFlush();
                    if (!anySegmentFlushed) {
                        this.flushCount.incrementAndGet();
                    }
                    success = true;
                    boolean bl = anySegmentFlushed;
                    return bl;
                }
                catch (Throwable throwable) {
                    try {
                        throw throwable;
                    }
                    catch (OutOfMemoryError oom) {
                        this.handleOOM(oom, "doFlush");
                        boolean bl = false;
                        return bl;
                    }
                }
            }
        }
        finally {
            if (!success && this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "hit exception during flush");
            }
        }
    }

    final synchronized void maybeApplyDeletes(boolean applyAllDeletes) throws IOException {
        if (applyAllDeletes) {
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "apply all deletes during flush");
            }
            this.applyAllDeletes();
        } else if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "don't apply deletes now delTermCount=" + this.bufferedDeletesStream.numTerms() + " bytesUsed=" + this.bufferedDeletesStream.bytesUsed());
        }
    }

    final synchronized void applyAllDeletes() throws IOException {
        this.flushDeletesCount.incrementAndGet();
        BufferedDeletesStream.ApplyDeletesResult result = this.bufferedDeletesStream.applyDeletes(this.readerPool, this.segmentInfos.asList());
        if (result.anyDeletes) {
            this.checkpoint();
        }
        if (!this.keepFullyDeletedSegments && result.allDeleted != null) {
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "drop 100% deleted segments: " + this.segString(result.allDeleted));
            }
            for (SegmentInfoPerCommit info : result.allDeleted) {
                if (this.mergingSegments.contains(info)) continue;
                this.segmentInfos.remove(info);
                this.readerPool.drop(info);
            }
            this.checkpoint();
        }
        this.bufferedDeletesStream.prune(this.segmentInfos);
    }

    public final long ramSizeInBytes() {
        this.ensureOpen();
        return this.docWriter.flushControl.netBytes() + this.bufferedDeletesStream.bytesUsed();
    }

    DocumentsWriter getDocsWriter() {
        boolean test = false;
        if (!$assertionsDisabled) {
            test = true;
            if (!true) {
                throw new AssertionError();
            }
        }
        return test ? this.docWriter : null;
    }

    public final synchronized int numRamDocs() {
        this.ensureOpen();
        return this.docWriter.getNumDocs();
    }

    private synchronized void ensureValidMerge(MergePolicy.OneMerge merge) {
        for (SegmentInfoPerCommit info : merge.segments) {
            if (this.segmentInfos.contains(info)) continue;
            throw new MergePolicy.MergeException("MergePolicy selected a segment (" + info.info.name + ") that is not in the current index " + this.segString(), this.directory);
        }
    }

    private synchronized ReadersAndLiveDocs commitMergedDeletes(MergePolicy.OneMerge merge, MergeState mergeState) throws IOException {
        assert (this.testPoint("startCommitMergeDeletes"));
        List<SegmentInfoPerCommit> sourceSegments = merge.segments;
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "commitMergeDeletes " + this.segString(merge.segments));
        }
        int docUpto = 0;
        long minGen = Long.MAX_VALUE;
        ReadersAndLiveDocs mergedDeletes = null;
        MergePolicy.DocMap docMap = null;
        for (int i = 0; i < sourceSegments.size(); ++i) {
            int j;
            SegmentInfoPerCommit info = sourceSegments.get(i);
            minGen = Math.min(info.getBufferedDeletesGen(), minGen);
            int docCount = info.info.getDocCount();
            Bits prevLiveDocs = merge.readers.get(i).getLiveDocs();
            ReadersAndLiveDocs rld = this.readerPool.get(info, false);
            assert (rld != null) : "seg=" + info.info.name;
            Bits currentLiveDocs = rld.getLiveDocs();
            if (prevLiveDocs != null) {
                assert (currentLiveDocs != null);
                assert (prevLiveDocs.length() == docCount);
                assert (currentLiveDocs.length() == docCount);
                if (currentLiveDocs != prevLiveDocs) {
                    for (j = 0; j < docCount; ++j) {
                        if (!prevLiveDocs.get(j)) {
                            assert (!currentLiveDocs.get(j));
                            continue;
                        }
                        if (!currentLiveDocs.get(j)) {
                            if (mergedDeletes == null) {
                                mergedDeletes = this.readerPool.get(merge.info, true);
                                mergedDeletes.initWritableLiveDocs();
                                docMap = merge.getDocMap(mergeState);
                                assert (docMap.isConsistent(merge.info.info.getDocCount()));
                            }
                            mergedDeletes.delete(docMap.map(docUpto));
                        }
                        ++docUpto;
                    }
                    continue;
                }
                docUpto += info.info.getDocCount() - info.getDelCount() - rld.getPendingDeleteCount();
                continue;
            }
            if (currentLiveDocs != null) {
                assert (currentLiveDocs.length() == docCount);
                for (j = 0; j < docCount; ++j) {
                    if (!currentLiveDocs.get(j)) {
                        if (mergedDeletes == null) {
                            mergedDeletes = this.readerPool.get(merge.info, true);
                            mergedDeletes.initWritableLiveDocs();
                            docMap = merge.getDocMap(mergeState);
                            assert (docMap.isConsistent(merge.info.info.getDocCount()));
                        }
                        mergedDeletes.delete(docMap.map(docUpto));
                    }
                    ++docUpto;
                }
                continue;
            }
            docUpto += info.info.getDocCount();
        }
        assert (docUpto == merge.info.info.getDocCount());
        if (this.infoStream.isEnabled("IW")) {
            if (mergedDeletes == null) {
                this.infoStream.message("IW", "no new deletes since merge started");
            } else {
                this.infoStream.message("IW", mergedDeletes.getPendingDeleteCount() + " new deletes since merge started");
            }
        }
        merge.info.setBufferedDeletesGen(minGen);
        return mergedDeletes;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private synchronized boolean commitMerge(MergePolicy.OneMerge merge, MergeState mergeState) throws IOException {
        boolean dropSegment;
        boolean allDeleted;
        ReadersAndLiveDocs mergedDeletes;
        assert (this.testPoint("startCommitMerge"));
        if (this.hitOOM) {
            throw new IllegalStateException("this writer hit an OutOfMemoryError; cannot complete merge");
        }
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "commitMerge: " + this.segString(merge.segments) + " index=" + this.segString());
        }
        assert (merge.registerDone);
        if (merge.isAborted()) {
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "commitMerge: skip: it was aborted");
            }
            this.deleter.deleteNewFiles(merge.info.files());
            return false;
        }
        ReadersAndLiveDocs readersAndLiveDocs = mergedDeletes = merge.info.info.getDocCount() == 0 ? null : this.commitMergedDeletes(merge, mergeState);
        assert (mergedDeletes == null || mergedDeletes.getPendingDeleteCount() != 0);
        assert (!this.segmentInfos.contains(merge.info));
        boolean bl = allDeleted = merge.segments.size() == 0 || merge.info.info.getDocCount() == 0 || mergedDeletes != null && mergedDeletes.getPendingDeleteCount() == merge.info.info.getDocCount();
        if (this.infoStream.isEnabled("IW") && allDeleted) {
            this.infoStream.message("IW", "merged segment " + merge.info + " is 100% deleted" + (this.keepFullyDeletedSegments ? "" : "; skipping insert"));
        }
        boolean bl2 = dropSegment = allDeleted && !this.keepFullyDeletedSegments;
        assert (merge.segments.size() > 0 || dropSegment);
        assert (merge.info.info.getDocCount() != 0 || this.keepFullyDeletedSegments || dropSegment);
        this.segmentInfos.applyMergeChanges(merge, dropSegment);
        if (mergedDeletes != null) {
            if (dropSegment) {
                mergedDeletes.dropChanges();
            }
            this.readerPool.release(mergedDeletes);
        }
        if (dropSegment) {
            assert (!this.segmentInfos.contains(merge.info));
            this.readerPool.drop(merge.info);
            this.deleter.deleteNewFiles(merge.info.files());
        }
        boolean success = false;
        try {
            this.closeMergeReaders(merge, false);
            success = true;
        }
        finally {
            if (success) {
                this.checkpoint();
            } else {
                try {
                    this.checkpoint();
                }
                catch (Throwable throwable) {}
            }
        }
        this.deleter.deletePendingFiles();
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "after commitMerge: " + this.segString());
        }
        if (merge.maxNumSegments != -1 && !dropSegment && !this.segmentsToMerge.containsKey(merge.info)) {
            this.segmentsToMerge.put(merge.info, Boolean.FALSE);
        }
        return true;
    }

    private final void handleMergeException(Throwable t, MergePolicy.OneMerge merge) throws IOException {
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "handleMergeException: merge=" + this.segString(merge.segments) + " exc=" + t);
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
    public void merge(MergePolicy.OneMerge merge) throws IOException {
        boolean success = false;
        long t0 = System.currentTimeMillis();
        try {
            try {
                try {
                    this.mergeInit(merge);
                    if (this.infoStream.isEnabled("IW")) {
                        this.infoStream.message("IW", "now merge\n  merge=" + this.segString(merge.segments) + "\n  index=" + this.segString());
                    }
                    this.mergeMiddle(merge);
                    this.mergeSuccess(merge);
                    success = true;
                }
                catch (Throwable t) {
                    this.handleMergeException(t, merge);
                }
            }
            finally {
                IndexWriter t = this;
                synchronized (t) {
                    this.mergeFinish(merge);
                    if (!success) {
                        if (this.infoStream.isEnabled("IW")) {
                            this.infoStream.message("IW", "hit exception during merge");
                        }
                        if (merge.info != null && !this.segmentInfos.contains(merge.info)) {
                            this.deleter.refresh(merge.info.info.name);
                        }
                    }
                    if (success && !merge.isAborted() && (merge.maxNumSegments != -1 || !this.closed && !this.closing)) {
                        this.updatePendingMerges(MergePolicy.MergeTrigger.MERGE_FINISHED, merge.maxNumSegments);
                    }
                }
            }
        }
        catch (OutOfMemoryError oom) {
            this.handleOOM(oom, SOURCE_MERGE);
        }
        if (merge.info != null && !merge.isAborted() && this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "merge time " + (System.currentTimeMillis() - t0) + " msec for " + merge.info.info.getDocCount() + " docs");
        }
    }

    void mergeSuccess(MergePolicy.OneMerge merge) {
    }

    final synchronized boolean registerMerge(MergePolicy.OneMerge merge) throws IOException {
        if (merge.registerDone) {
            return true;
        }
        assert (merge.segments.size() > 0);
        if (this.stopMerges) {
            merge.abort();
            throw new MergePolicy.MergeAbortedException("merge is aborted: " + this.segString(merge.segments));
        }
        boolean isExternal = false;
        for (SegmentInfoPerCommit segmentInfoPerCommit : merge.segments) {
            if (this.mergingSegments.contains(segmentInfoPerCommit)) {
                if (this.infoStream.isEnabled("IW")) {
                    this.infoStream.message("IW", "reject merge " + this.segString(merge.segments) + ": segment " + this.segString(segmentInfoPerCommit) + " is already marked for merge");
                }
                return false;
            }
            if (!this.segmentInfos.contains(segmentInfoPerCommit)) {
                if (this.infoStream.isEnabled("IW")) {
                    this.infoStream.message("IW", "reject merge " + this.segString(merge.segments) + ": segment " + this.segString(segmentInfoPerCommit) + " does not exist in live infos");
                }
                return false;
            }
            if (segmentInfoPerCommit.info.dir != this.directory) {
                isExternal = true;
            }
            if (!this.segmentsToMerge.containsKey(segmentInfoPerCommit)) continue;
            merge.maxNumSegments = this.mergeMaxNumSegments;
        }
        this.ensureValidMerge(merge);
        this.pendingMerges.add(merge);
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "add merge to pendingMerges: " + this.segString(merge.segments) + " [total " + this.pendingMerges.size() + " pending]");
        }
        merge.mergeGen = this.mergeGen;
        merge.isExternal = isExternal;
        if (this.infoStream.isEnabled("IW")) {
            StringBuilder builder = new StringBuilder("registerMerge merging= [");
            for (SegmentInfoPerCommit info : this.mergingSegments) {
                builder.append(info.info.name).append(", ");
            }
            builder.append("]");
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", builder.toString());
            }
        }
        for (SegmentInfoPerCommit segmentInfoPerCommit : merge.segments) {
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "registerMerge info=" + this.segString(segmentInfoPerCommit));
            }
            this.mergingSegments.add(segmentInfoPerCommit);
        }
        assert (merge.estimatedMergeBytes == 0L);
        assert (merge.totalMergeBytes == 0L);
        for (SegmentInfoPerCommit segmentInfoPerCommit : merge.segments) {
            if (segmentInfoPerCommit.info.getDocCount() <= 0) continue;
            int delCount = this.numDeletedDocs(segmentInfoPerCommit);
            assert (delCount <= segmentInfoPerCommit.info.getDocCount());
            double delRatio = (double)delCount / (double)segmentInfoPerCommit.info.getDocCount();
            merge.estimatedMergeBytes = (long)((double)merge.estimatedMergeBytes + (double)segmentInfoPerCommit.sizeInBytes() * (1.0 - delRatio));
            merge.totalMergeBytes += segmentInfoPerCommit.sizeInBytes();
        }
        merge.registerDone = true;
        return true;
    }

    final synchronized void mergeInit(MergePolicy.OneMerge merge) throws IOException {
        boolean success = false;
        try {
            this._mergeInit(merge);
            success = true;
        }
        finally {
            if (!success) {
                if (this.infoStream.isEnabled("IW")) {
                    this.infoStream.message("IW", "hit exception in mergeInit");
                }
                this.mergeFinish(merge);
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
        BufferedDeletesStream.ApplyDeletesResult result = this.bufferedDeletesStream.applyDeletes(this.readerPool, merge.segments);
        if (result.anyDeletes) {
            this.checkpoint();
        }
        if (!this.keepFullyDeletedSegments && result.allDeleted != null) {
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "drop 100% deleted segments: " + result.allDeleted);
            }
            for (SegmentInfoPerCommit info : result.allDeleted) {
                this.segmentInfos.remove(info);
                if (merge.segments.contains(info)) {
                    this.mergingSegments.remove(info);
                    merge.segments.remove(info);
                }
                this.readerPool.drop(info);
            }
            this.checkpoint();
        }
        String mergeSegmentName = this.newSegmentName();
        SegmentInfo si = new SegmentInfo(this.directory, Constants.LUCENE_MAIN_VERSION, mergeSegmentName, -1, false, this.codec, null, null);
        HashMap<String, String> details = new HashMap<String, String>();
        details.put("mergeMaxNumSegments", "" + merge.maxNumSegments);
        details.put("mergeFactor", Integer.toString(merge.segments.size()));
        IndexWriter.setDiagnostics(si, SOURCE_MERGE, details);
        merge.setInfo(new SegmentInfoPerCommit(si, 0, -1L));
        this.bufferedDeletesStream.prune(this.segmentInfos);
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "merge seg=" + merge.info.info.name + " " + this.segString(merge.segments));
        }
    }

    static void setDiagnostics(SegmentInfo info, String source) {
        IndexWriter.setDiagnostics(info, source, null);
    }

    private static void setDiagnostics(SegmentInfo info, String source, Map<String, String> details) {
        HashMap<String, String> diagnostics = new HashMap<String, String>();
        diagnostics.put(SOURCE, source);
        diagnostics.put("lucene.version", Constants.LUCENE_VERSION);
        diagnostics.put("os", Constants.OS_NAME);
        diagnostics.put("os.arch", Constants.OS_ARCH);
        diagnostics.put("os.version", Constants.OS_VERSION);
        diagnostics.put("java.version", Constants.JAVA_VERSION);
        diagnostics.put("java.vendor", Constants.JAVA_VENDOR);
        diagnostics.put("timestamp", Long.toString(new Date().getTime()));
        if (details != null) {
            diagnostics.putAll(details);
        }
        info.setDiagnostics(diagnostics);
    }

    final synchronized void mergeFinish(MergePolicy.OneMerge merge) {
        this.notifyAll();
        if (merge.registerDone) {
            List<SegmentInfoPerCommit> sourceSegments = merge.segments;
            for (SegmentInfoPerCommit info : sourceSegments) {
                this.mergingSegments.remove(info);
            }
            merge.registerDone = false;
        }
        this.runningMerges.remove(merge);
    }

    private final synchronized void closeMergeReaders(MergePolicy.OneMerge merge, boolean suppressExceptions) throws IOException {
        int numSegments = merge.readers.size();
        Throwable th = null;
        boolean drop = !suppressExceptions;
        for (int i = 0; i < numSegments; ++i) {
            block10: {
                SegmentReader sr = merge.readers.get(i);
                if (sr == null) continue;
                try {
                    ReadersAndLiveDocs rld = this.readerPool.get(sr.getSegmentInfo(), false);
                    assert (rld != null);
                    if (drop) {
                        rld.dropChanges();
                    }
                    rld.release(sr);
                    this.readerPool.release(rld);
                    if (drop) {
                        this.readerPool.drop(rld.info);
                    }
                }
                catch (Throwable t) {
                    if (th != null) break block10;
                    th = t;
                }
            }
            merge.readers.set(i, null);
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
    private int mergeMiddle(MergePolicy.OneMerge merge) throws IOException {
        merge.checkAborted(this.directory);
        String mergedName = merge.info.info.name;
        List<SegmentInfoPerCommit> sourceSegments = merge.segments;
        IOContext context = new IOContext(merge.getMergeInfo());
        MergeState.CheckAbort checkAbort = new MergeState.CheckAbort(merge, this.directory);
        TrackingDirectoryWrapper dirWrapper = new TrackingDirectoryWrapper(this.directory);
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "merging " + this.segString(merge.segments));
        }
        merge.readers = new ArrayList<SegmentReader>();
        boolean success = false;
        try {
            MergeState mergeState;
            Object liveDocs;
            for (int segUpto = 0; segUpto < sourceSegments.size(); ++segUpto) {
                SegmentInfoPerCommit info = sourceSegments.get(segUpto);
                ReadersAndLiveDocs rld = this.readerPool.get(info, true);
                SegmentReader reader = rld.getMergeReader(context);
                assert (reader != null);
                IndexWriter indexWriter = this;
                // MONITORENTER : indexWriter
                liveDocs = rld.getReadOnlyLiveDocs();
                int delCount = rld.getPendingDeleteCount() + info.getDelCount();
                assert (rld.verifyDocCounts());
                if (this.infoStream.isEnabled("IW")) {
                    if (rld.getPendingDeleteCount() != 0) {
                        this.infoStream.message("IW", "seg=" + this.segString(info) + " delCount=" + info.getDelCount() + " pendingDelCount=" + rld.getPendingDeleteCount());
                    } else if (info.getDelCount() != 0) {
                        this.infoStream.message("IW", "seg=" + this.segString(info) + " delCount=" + info.getDelCount());
                    } else {
                        this.infoStream.message("IW", "seg=" + this.segString(info) + " no deletes");
                    }
                }
                // MONITOREXIT : indexWriter
                if (reader.numDeletedDocs() != delCount) {
                    assert (delCount > reader.numDeletedDocs());
                    SegmentReader segmentReader = new SegmentReader(info, reader.core, (Bits)liveDocs, info.info.getDocCount() - delCount);
                    boolean released = false;
                    try {
                        rld.release(reader);
                        released = true;
                    }
                    finally {
                        if (!released) {
                            segmentReader.decRef();
                        }
                    }
                    reader = segmentReader;
                }
                merge.readers.add(reader);
                assert (delCount <= info.info.getDocCount()) : "delCount=" + delCount + " info.docCount=" + info.info.getDocCount() + " rld.pendingDeleteCount=" + rld.getPendingDeleteCount() + " info.getDelCount()=" + info.getDelCount();
            }
            SegmentMerger merger = new SegmentMerger(merge.getMergeReaders(), merge.info.info, this.infoStream, dirWrapper, this.config.getTermIndexInterval(), checkAbort, this.globalFieldNumberMap, context);
            merge.checkAborted(this.directory);
            boolean success3 = false;
            try {
                mergeState = merger.merge();
                success3 = true;
            }
            finally {
                if (!success3) {
                    liveDocs = this;
                }
            }
            assert (mergeState.segmentInfo == merge.info.info);
            merge.info.info.setFiles(new HashSet<String>(dirWrapper.getCreatedFiles()));
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "merge codec=" + this.codec + " docCount=" + merge.info.info.getDocCount() + "; merged segment has " + (mergeState.fieldInfos.hasVectors() ? "vectors" : "no vectors") + "; " + (mergeState.fieldInfos.hasNorms() ? "norms" : "no norms") + "; " + (mergeState.fieldInfos.hasDocValues() ? "docValues" : "no docValues") + "; " + (mergeState.fieldInfos.hasProx() ? "prox" : "no prox") + "; " + (mergeState.fieldInfos.hasProx() ? "freqs" : "no freqs"));
            }
            IndexWriter delCount = this;
            // MONITORENTER : delCount
            boolean useCompoundFile = this.mergePolicy.useCompoundFile(this.segmentInfos, merge.info);
            // MONITOREXIT : delCount
            if (useCompoundFile) {
                Collection<String> filesToRemove;
                block83: {
                    success = false;
                    filesToRemove = merge.info.files();
                    try {
                        filesToRemove = IndexWriter.createCompoundFile(this.infoStream, this.directory, checkAbort, merge.info.info, context);
                        success = true;
                    }
                    catch (IOException iOException) {
                        IndexWriter released = this;
                        // MONITORENTER : released
                        if (!merge.isAborted()) {
                            this.handleMergeException(iOException, merge);
                        }
                        // MONITOREXIT : released
                    }
                    catch (Throwable throwable) {
                        this.handleMergeException(throwable, merge);
                        if (success) break block83;
                        if (this.infoStream.isEnabled("IW")) {
                            this.infoStream.message("IW", "hit exception creating compound file during merge");
                        }
                        IndexWriter indexWriter = this;
                        this.deleter.deleteFile(IndexFileNames.segmentFileName(mergedName, "", "cfs"));
                        this.deleter.deleteFile(IndexFileNames.segmentFileName(mergedName, "", "cfe"));
                        this.deleter.deleteNewFiles(merge.info.files());
                        // MONITOREXIT : indexWriter
                    }
                    finally {
                        if (success) {
                        }
                        if (this.infoStream.isEnabled("IW")) {
                            this.infoStream.message("IW", "hit exception creating compound file during merge");
                        }
                        IndexWriter indexWriter = this;
                    }
                }
                success = false;
                IndexWriter indexWriter = this;
                // MONITORENTER : indexWriter
                this.deleter.deleteNewFiles(filesToRemove);
                if (merge.isAborted()) {
                    if (this.infoStream.isEnabled("IW")) {
                        this.infoStream.message("IW", "abort merge after building CFS");
                    }
                    this.deleter.deleteFile(IndexFileNames.segmentFileName(mergedName, "", "cfs"));
                    this.deleter.deleteFile(IndexFileNames.segmentFileName(mergedName, "", "cfe"));
                    int released = 0;
                    // MONITOREXIT : indexWriter
                    return released;
                }
                // MONITOREXIT : indexWriter
                merge.info.info.setUseCompoundFile(true);
            } else {
                success = false;
            }
            boolean success2 = false;
            try {
                this.codec.segmentInfoFormat().getSegmentInfoWriter().write(this.directory, merge.info.info, mergeState.fieldInfos, context);
                success2 = true;
            }
            finally {
                if (!success2) {
                    IndexWriter indexWriter = this;
                }
            }
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", String.format(Locale.ROOT, "merged segment size=%.3f MB vs estimate=%.3f MB", (double)merge.info.sizeInBytes() / 1024.0 / 1024.0, (double)(merge.estimatedMergeBytes / 1024L) / 1024.0));
            }
            IndexReaderWarmer indexReaderWarmer = this.config.getMergedSegmentWarmer();
            if (this.poolReaders && indexReaderWarmer != null && merge.info.info.getDocCount() != 0) {
                ReadersAndLiveDocs rld = this.readerPool.get(merge.info, true);
                SegmentReader sr = rld.getReader(IOContext.READ);
                try {
                    indexReaderWarmer.warm(sr);
                    IndexWriter indexWriter = this;
                }
                catch (Throwable throwable) {
                    IndexWriter indexWriter = this;
                    // MONITORENTER : indexWriter
                    rld.release(sr);
                    this.readerPool.release(rld);
                    // MONITOREXIT : indexWriter
                    throw throwable;
                }
                rld.release(sr);
                this.readerPool.release(rld);
                // MONITOREXIT : indexWriter
            }
            if (!this.commitMerge(merge, mergeState)) {
                int n = 0;
                return n;
            }
            success = true;
            return merge.info.info.getDocCount();
        }
        finally {
            if (!success) {
                this.closeMergeReaders(merge, true);
            }
        }
    }

    synchronized void addMergeException(MergePolicy.OneMerge merge) {
        assert (merge.getException() != null);
        if (!this.mergeExceptions.contains(merge) && this.mergeGen == merge.mergeGen) {
            this.mergeExceptions.add(merge);
        }
    }

    final int getBufferedDeleteTermsSize() {
        return this.docWriter.getBufferedDeleteTermsSize();
    }

    final int getNumBufferedDeleteTerms() {
        return this.docWriter.getNumBufferedDeleteTerms();
    }

    synchronized SegmentInfoPerCommit newestSegment() {
        return this.segmentInfos.size() > 0 ? this.segmentInfos.info(this.segmentInfos.size() - 1) : null;
    }

    public synchronized String segString() {
        return this.segString(this.segmentInfos);
    }

    public synchronized String segString(Iterable<SegmentInfoPerCommit> infos) {
        StringBuilder buffer = new StringBuilder();
        for (SegmentInfoPerCommit info : infos) {
            if (buffer.length() > 0) {
                buffer.append(' ');
            }
            buffer.append(this.segString(info));
        }
        return buffer.toString();
    }

    public synchronized String segString(SegmentInfoPerCommit info) {
        return info.toString(info.info.dir, this.numDeletedDocs(info) - info.getDelCount());
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

    synchronized SegmentInfos toLiveInfos(SegmentInfos sis) {
        SegmentInfos newSIS = new SegmentInfos();
        HashMap<SegmentInfoPerCommit, SegmentInfoPerCommit> liveSIS = new HashMap<SegmentInfoPerCommit, SegmentInfoPerCommit>();
        for (SegmentInfoPerCommit info : this.segmentInfos) {
            liveSIS.put(info, info);
        }
        for (SegmentInfoPerCommit info : sis) {
            SegmentInfoPerCommit liveInfo = (SegmentInfoPerCommit)liveSIS.get(info);
            if (liveInfo != null) {
                info = liveInfo;
            }
            newSIS.add(info);
        }
        return newSIS;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void startCommit(SegmentInfos toSync) throws IOException {
        assert (this.testPoint("startStartCommit"));
        assert (this.pendingCommit == null);
        if (this.hitOOM) {
            throw new IllegalStateException("this writer hit an OutOfMemoryError; cannot commit");
        }
        try {
            if (this.infoStream.isEnabled("IW")) {
                this.infoStream.message("IW", "startCommit(): start");
            }
            IndexWriter indexWriter = this;
            synchronized (indexWriter) {
                assert (this.lastCommitChangeCount <= this.changeCount) : "lastCommitChangeCount=" + this.lastCommitChangeCount + " changeCount=" + this.changeCount;
                if (this.pendingCommitChangeCount == this.lastCommitChangeCount) {
                    if (this.infoStream.isEnabled("IW")) {
                        this.infoStream.message("IW", "  skip startCommit(): no changes pending");
                    }
                    this.deleter.decRef(this.filesToCommit);
                    this.filesToCommit = null;
                    return;
                }
                if (this.infoStream.isEnabled("IW")) {
                    this.infoStream.message("IW", "startCommit index=" + this.segString(this.toLiveInfos(toSync)) + " changeCount=" + this.changeCount);
                }
                assert (this.filesExist(toSync));
            }
            assert (this.testPoint("midStartCommit"));
            boolean pendingCommitSet = false;
            try {
                Collection<String> filesToSync;
                assert (this.testPoint("midStartCommit2"));
                IndexWriter indexWriter2 = this;
                synchronized (indexWriter2) {
                    assert (this.pendingCommit == null);
                    assert (this.segmentInfos.getGeneration() == toSync.getGeneration());
                    toSync.prepareCommit(this.directory);
                    pendingCommitSet = true;
                    this.pendingCommit = toSync;
                }
                boolean success = false;
                try {
                    filesToSync = toSync.files(this.directory, false);
                    this.directory.sync(filesToSync);
                    success = true;
                }
                finally {
                    if (!success) {
                        pendingCommitSet = false;
                        this.pendingCommit = null;
                        toSync.rollbackCommit(this.directory);
                    }
                }
                if (this.infoStream.isEnabled("IW")) {
                    this.infoStream.message("IW", "done all syncs: " + filesToSync);
                }
                assert (this.testPoint("midStartCommitSuccess"));
            }
            finally {
                IndexWriter indexWriter3 = this;
                synchronized (indexWriter3) {
                    this.segmentInfos.updateGeneration(toSync);
                    if (!pendingCommitSet) {
                        if (this.infoStream.isEnabled("IW")) {
                            this.infoStream.message("IW", "hit exception committing segments file");
                        }
                        this.deleter.decRef(this.filesToCommit);
                        this.filesToCommit = null;
                    }
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

    private void handleOOM(OutOfMemoryError oom, String location) {
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "hit OutOfMemoryError inside " + location);
        }
        this.hitOOM = true;
        throw oom;
    }

    private final boolean testPoint(String message) {
        if (this.infoStream.isEnabled("TP")) {
            this.infoStream.message("TP", message);
        }
        return true;
    }

    synchronized boolean nrtIsCurrent(SegmentInfos infos) {
        this.ensureOpen();
        if (this.infoStream.isEnabled("IW")) {
            this.infoStream.message("IW", "nrtIsCurrent: infoVersion matches: " + (infos.version == this.segmentInfos.version) + "; DW changes: " + this.docWriter.anyChanges() + "; BD changes: " + this.bufferedDeletesStream.any());
        }
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    static final Collection<String> createCompoundFile(InfoStream infoStream, Directory directory, MergeState.CheckAbort checkAbort, SegmentInfo info, IOContext context) throws IOException {
        String fileName = IndexFileNames.segmentFileName(info.name, "", "cfs");
        if (infoStream.isEnabled("IW")) {
            infoStream.message("IW", "create compound file " + fileName);
        }
        assert (Lucene3xSegmentInfoFormat.getDocStoreOffset(info) == -1);
        Set<String> files = info.files();
        CompoundFileDirectory cfsDir = new CompoundFileDirectory(directory, fileName, context, true);
        IOException prior = null;
        for (String file : files) {
            directory.copy(cfsDir, file, file, context);
            checkAbort.work(directory.fileLength(file));
        }
        boolean success = false;
        try {
            IOUtils.closeWhileHandlingException(prior, cfsDir);
            success = true;
        }
        finally {
            if (!success) {
                try {
                    directory.deleteFile(fileName);
                }
                catch (Throwable throwable) {}
                try {
                    directory.deleteFile(IndexFileNames.segmentFileName(info.name, "", "cfe"));
                }
                catch (Throwable throwable) {}
            }
        }
        catch (IOException ex) {
            try {
                prior = ex;
                success = false;
            }
            catch (Throwable throwable) {
                boolean success2 = false;
                try {
                    IOUtils.closeWhileHandlingException(prior, cfsDir);
                    success2 = true;
                }
                finally {
                    if (!success2) {
                        try {
                            directory.deleteFile(fileName);
                        }
                        catch (Throwable throwable2) {}
                        try {
                            directory.deleteFile(IndexFileNames.segmentFileName(info.name, "", "cfe"));
                        }
                        catch (Throwable throwable3) {}
                    }
                }
                throw throwable;
            }
            try {
                IOUtils.closeWhileHandlingException(prior, cfsDir);
                success = true;
            }
            finally {
                if (!success) {
                    try {
                        directory.deleteFile(fileName);
                    }
                    catch (Throwable throwable) {}
                    try {
                        directory.deleteFile(IndexFileNames.segmentFileName(info.name, "", "cfe"));
                    }
                    catch (Throwable throwable) {}
                }
            }
        }
        HashSet<String> siFiles = new HashSet<String>();
        siFiles.add(fileName);
        siFiles.add(IndexFileNames.segmentFileName(info.name, "", "cfe"));
        info.setFiles(siFiles);
        return files;
    }

    final synchronized void deleteNewFiles(Collection<String> files) throws IOException {
        this.deleter.deleteNewFiles(files);
    }

    final synchronized void flushFailed(SegmentInfo info) throws IOException {
        this.deleter.refresh(info.name);
    }

    public static abstract class IndexReaderWarmer {
        protected IndexReaderWarmer() {
        }

        public abstract void warm(AtomicReader var1) throws IOException;
    }

    class ReaderPool {
        private final Map<SegmentInfoPerCommit, ReadersAndLiveDocs> readerMap = new HashMap<SegmentInfoPerCommit, ReadersAndLiveDocs>();

        ReaderPool() {
        }

        public synchronized boolean infoIsLive(SegmentInfoPerCommit info) {
            int idx = IndexWriter.this.segmentInfos.indexOf(info);
            assert (idx != -1) : "info=" + info + " isn't live";
            assert (IndexWriter.this.segmentInfos.info(idx) == info) : "info=" + info + " doesn't match live info in segmentInfos";
            return true;
        }

        public synchronized void drop(SegmentInfoPerCommit info) throws IOException {
            ReadersAndLiveDocs rld = this.readerMap.get(info);
            if (rld != null) {
                assert (info == rld.info);
                this.readerMap.remove(info);
                rld.dropReaders();
            }
        }

        public synchronized boolean anyPendingDeletes() {
            for (ReadersAndLiveDocs rld : this.readerMap.values()) {
                if (rld.getPendingDeleteCount() == 0) continue;
                return true;
            }
            return false;
        }

        public synchronized void release(ReadersAndLiveDocs rld) throws IOException {
            rld.decRef();
            assert (rld.refCount() >= 1);
            if (!IndexWriter.this.poolReaders && rld.refCount() == 1) {
                if (rld.writeLiveDocs(IndexWriter.this.directory)) {
                    assert (this.infoIsLive(rld.info));
                    IndexWriter.this.deleter.checkpoint(IndexWriter.this.segmentInfos, false);
                }
                rld.dropReaders();
                this.readerMap.remove(rld.info);
            }
        }

        synchronized void dropAll(boolean doSave) throws IOException {
            Throwable priorE = null;
            Iterator<Map.Entry<SegmentInfoPerCommit, ReadersAndLiveDocs>> it = this.readerMap.entrySet().iterator();
            while (it.hasNext()) {
                ReadersAndLiveDocs rld;
                block9: {
                    rld = it.next().getValue();
                    try {
                        if (doSave && rld.writeLiveDocs(IndexWriter.this.directory)) {
                            assert (this.infoIsLive(rld.info));
                            IndexWriter.this.deleter.checkpoint(IndexWriter.this.segmentInfos, false);
                        }
                    }
                    catch (Throwable t) {
                        if (priorE == null) break block9;
                        priorE = t;
                    }
                }
                it.remove();
                try {
                    rld.dropReaders();
                }
                catch (Throwable t) {
                    if (priorE == null) continue;
                    priorE = t;
                }
            }
            assert (this.readerMap.size() == 0);
            if (priorE != null) {
                throw new RuntimeException(priorE);
            }
        }

        public synchronized void commit(SegmentInfos infos) throws IOException {
            for (SegmentInfoPerCommit info : infos) {
                ReadersAndLiveDocs rld = this.readerMap.get(info);
                if (rld == null) continue;
                assert (rld.info == info);
                if (!rld.writeLiveDocs(IndexWriter.this.directory)) continue;
                assert (this.infoIsLive(info));
                IndexWriter.this.deleter.checkpoint(IndexWriter.this.segmentInfos, false);
            }
        }

        public synchronized ReadersAndLiveDocs get(SegmentInfoPerCommit info, boolean create) {
            assert (info.info.dir == IndexWriter.this.directory) : "info.dir=" + info.info.dir + " vs " + IndexWriter.access$100(IndexWriter.this);
            ReadersAndLiveDocs rld = this.readerMap.get(info);
            if (rld == null) {
                if (!create) {
                    return null;
                }
                rld = new ReadersAndLiveDocs(IndexWriter.this, info);
                this.readerMap.put(info, rld);
            } else assert (rld.info == info) : "rld.info=" + rld.info + " info=" + info + " isLive?=" + this.infoIsLive(rld.info) + " vs " + this.infoIsLive(info);
            if (create) {
                rld.incRef();
            }
            assert (this.noDups());
            return rld;
        }

        private boolean noDups() {
            HashSet<String> seen = new HashSet<String>();
            for (SegmentInfoPerCommit info : this.readerMap.keySet()) {
                assert (!seen.contains(info.info.name));
                seen.add(info.info.name);
            }
            return true;
        }
    }
}

