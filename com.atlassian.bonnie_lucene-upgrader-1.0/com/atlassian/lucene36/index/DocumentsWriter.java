/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.analysis.Analyzer;
import com.atlassian.lucene36.document.Document;
import com.atlassian.lucene36.index.BufferedDeletes;
import com.atlassian.lucene36.index.BufferedDeletesStream;
import com.atlassian.lucene36.index.ByteBlockPool;
import com.atlassian.lucene36.index.CompoundFileWriter;
import com.atlassian.lucene36.index.CorruptIndexException;
import com.atlassian.lucene36.index.DocConsumer;
import com.atlassian.lucene36.index.DocConsumerPerThread;
import com.atlassian.lucene36.index.DocFieldProcessor;
import com.atlassian.lucene36.index.DocInverter;
import com.atlassian.lucene36.index.DocumentsWriterThreadState;
import com.atlassian.lucene36.index.FieldInfos;
import com.atlassian.lucene36.index.FreqProxTermsWriter;
import com.atlassian.lucene36.index.FrozenBufferedDeletes;
import com.atlassian.lucene36.index.IndexFileDeleter;
import com.atlassian.lucene36.index.IndexFileNames;
import com.atlassian.lucene36.index.IndexWriter;
import com.atlassian.lucene36.index.IndexWriterConfig;
import com.atlassian.lucene36.index.MergePolicy;
import com.atlassian.lucene36.index.NormsWriter;
import com.atlassian.lucene36.index.SegmentInfo;
import com.atlassian.lucene36.index.SegmentInfos;
import com.atlassian.lucene36.index.SegmentWriteState;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.index.TermVectorsTermsWriter;
import com.atlassian.lucene36.index.TermsHash;
import com.atlassian.lucene36.search.Query;
import com.atlassian.lucene36.search.Similarity;
import com.atlassian.lucene36.store.AlreadyClosedException;
import com.atlassian.lucene36.store.Directory;
import com.atlassian.lucene36.store.RAMFile;
import com.atlassian.lucene36.util.ArrayUtil;
import com.atlassian.lucene36.util.BitVector;
import com.atlassian.lucene36.util.RamUsageEstimator;
import com.atlassian.lucene36.util.ThreadInterruptedException;
import java.io.IOException;
import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class DocumentsWriter {
    final AtomicLong bytesUsed = new AtomicLong(0L);
    IndexWriter writer;
    Directory directory;
    String segment;
    private int nextDocID;
    private int numDocs;
    private DocumentsWriterThreadState[] threadStates = new DocumentsWriterThreadState[0];
    private final HashMap<Thread, DocumentsWriterThreadState> threadBindings = new HashMap();
    boolean bufferIsFull;
    private boolean aborting;
    PrintStream infoStream;
    int maxFieldLength = IndexWriter.DEFAULT_MAX_FIELD_LENGTH;
    Similarity similarity;
    private final int maxThreadStates;
    private BufferedDeletes pendingDeletes = new BufferedDeletes();
    static final IndexingChain defaultIndexingChain = new IndexingChain(){

        DocConsumer getChain(DocumentsWriter documentsWriter) {
            TermVectorsTermsWriter termVectorsWriter = new TermVectorsTermsWriter(documentsWriter);
            FreqProxTermsWriter freqProxWriter = new FreqProxTermsWriter();
            TermsHash termsHash = new TermsHash(documentsWriter, true, freqProxWriter, new TermsHash(documentsWriter, false, termVectorsWriter, null));
            NormsWriter normsWriter = new NormsWriter();
            DocInverter docInverter = new DocInverter(termsHash, normsWriter);
            return new DocFieldProcessor(documentsWriter, docInverter);
        }
    };
    final DocConsumer consumer;
    private final IndexWriterConfig config;
    private boolean closed;
    private final FieldInfos fieldInfos;
    private final BufferedDeletesStream bufferedDeletesStream;
    private final IndexWriter.FlushControl flushControl;
    final SkipDocWriter skipDocWriter = new SkipDocWriter();
    NumberFormat nf = NumberFormat.getInstance();
    static final int BYTE_BLOCK_SHIFT = 15;
    static final int BYTE_BLOCK_SIZE = 32768;
    static final int BYTE_BLOCK_MASK = Short.MAX_VALUE;
    static final int BYTE_BLOCK_NOT_MASK = Short.MIN_VALUE;
    static final int INT_BLOCK_SHIFT = 13;
    static final int INT_BLOCK_SIZE = 8192;
    static final int INT_BLOCK_MASK = 8191;
    private List<int[]> freeIntBlocks = new ArrayList<int[]>();
    ByteBlockAllocator byteBlockAllocator = new ByteBlockAllocator(32768);
    static final int PER_DOC_BLOCK_SIZE = 1024;
    final ByteBlockAllocator perDocAllocator = new ByteBlockAllocator(1024);
    static final int CHAR_BLOCK_SHIFT = 14;
    static final int CHAR_BLOCK_SIZE = 16384;
    static final int CHAR_BLOCK_MASK = 16383;
    static final int MAX_TERM_LENGTH = 16383;
    private ArrayList<char[]> freeCharBlocks = new ArrayList();
    final WaitQueue waitQueue = new WaitQueue();

    PerDocBuffer newPerDocBuffer() {
        return new PerDocBuffer();
    }

    DocumentsWriter(IndexWriterConfig config, Directory directory, IndexWriter writer, FieldInfos fieldInfos, BufferedDeletesStream bufferedDeletesStream) throws IOException {
        this.directory = directory;
        this.writer = writer;
        this.similarity = config.getSimilarity();
        this.maxThreadStates = config.getMaxThreadStates();
        this.fieldInfos = fieldInfos;
        this.bufferedDeletesStream = bufferedDeletesStream;
        this.flushControl = writer.flushControl;
        this.consumer = config.getIndexingChain().getChain(this);
        this.config = config;
    }

    synchronized void deleteDocID(int docIDUpto) {
        this.pendingDeletes.addDocID(docIDUpto);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    boolean deleteQueries(Query ... queries) {
        boolean doFlush = this.flushControl.waitUpdate(0, queries.length);
        DocumentsWriter documentsWriter = this;
        synchronized (documentsWriter) {
            for (Query query : queries) {
                this.pendingDeletes.addQuery(query, this.numDocs);
            }
        }
        return doFlush;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    boolean deleteQuery(Query query) {
        boolean doFlush = this.flushControl.waitUpdate(0, 1);
        DocumentsWriter documentsWriter = this;
        synchronized (documentsWriter) {
            this.pendingDeletes.addQuery(query, this.numDocs);
        }
        return doFlush;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    boolean deleteTerms(Term ... terms) {
        boolean doFlush = this.flushControl.waitUpdate(0, terms.length);
        DocumentsWriter documentsWriter = this;
        synchronized (documentsWriter) {
            for (Term term : terms) {
                this.pendingDeletes.addTerm(term, this.numDocs);
            }
        }
        return doFlush;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    boolean deleteTerm(Term term, boolean skipWait) {
        boolean doFlush = this.flushControl.waitUpdate(0, 1, skipWait);
        DocumentsWriter documentsWriter = this;
        synchronized (documentsWriter) {
            this.pendingDeletes.addTerm(term, this.numDocs);
        }
        return doFlush;
    }

    public FieldInfos getFieldInfos() {
        return this.fieldInfos;
    }

    synchronized void setInfoStream(PrintStream infoStream) {
        this.infoStream = infoStream;
        for (int i = 0; i < this.threadStates.length; ++i) {
            this.threadStates[i].docState.infoStream = infoStream;
        }
    }

    synchronized void setMaxFieldLength(int maxFieldLength) {
        this.maxFieldLength = maxFieldLength;
        for (int i = 0; i < this.threadStates.length; ++i) {
            this.threadStates[i].docState.maxFieldLength = maxFieldLength;
        }
    }

    synchronized void setSimilarity(Similarity similarity) {
        this.similarity = similarity;
        for (int i = 0; i < this.threadStates.length; ++i) {
            this.threadStates[i].docState.similarity = similarity;
        }
    }

    synchronized String getSegment() {
        return this.segment;
    }

    synchronized int getNumDocs() {
        return this.numDocs;
    }

    void message(String message) {
        if (this.infoStream != null) {
            this.writer.message("DW: " + message);
        }
    }

    synchronized void setAborting() {
        if (this.infoStream != null) {
            this.message("setAborting");
        }
        this.aborting = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    synchronized void abort() throws IOException {
        if (this.infoStream != null) {
            this.message("docWriter: abort");
        }
        boolean success = false;
        try {
            block21: {
                block20: {
                    try {
                        this.waitQueue.abort();
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    this.waitIdle();
                    Object var4_3 = null;
                    if (this.infoStream != null) {
                        this.message("docWriter: abort waitIdle done");
                    }
                    if ($assertionsDisabled || 0 == this.waitQueue.numWaiting) break block20;
                    throw new AssertionError((Object)("waitQueue.numWaiting=" + this.waitQueue.numWaiting));
                }
                this.waitQueue.waitingBytes = 0L;
                this.pendingDeletes.clear();
                for (DocumentsWriterThreadState threadState : this.threadStates) {
                    try {
                        threadState.consumer.abort();
                    }
                    catch (Throwable t) {
                        // empty catch block
                    }
                }
                try {
                    this.consumer.abort();
                    break block21;
                }
                catch (Throwable t) {
                    // empty catch block
                }
                {
                }
                catch (Throwable throwable) {
                    Object var4_4 = null;
                    if (this.infoStream != null) {
                        this.message("docWriter: abort waitIdle done");
                    }
                    assert (0 == this.waitQueue.numWaiting) : "waitQueue.numWaiting=" + this.waitQueue.numWaiting;
                    this.waitQueue.waitingBytes = 0L;
                    this.pendingDeletes.clear();
                    for (DocumentsWriterThreadState threadState : this.threadStates) {
                        try {
                            threadState.consumer.abort();
                        }
                        catch (Throwable t) {
                            // empty catch block
                        }
                    }
                    try {
                        this.consumer.abort();
                    }
                    catch (Throwable t) {
                        // empty catch block
                    }
                    this.doAfterFlush();
                    throw throwable;
                }
            }
            this.doAfterFlush();
            success = true;
            Object var11_16 = null;
            this.aborting = false;
            this.notifyAll();
            if (this.infoStream != null) {
                this.message("docWriter: done abort; success=" + success);
            }
        }
        catch (Throwable throwable) {
            Object var11_17 = null;
            this.aborting = false;
            this.notifyAll();
            if (this.infoStream != null) {
                this.message("docWriter: done abort; success=" + success);
            }
            throw throwable;
        }
    }

    private void doAfterFlush() throws IOException {
        assert (this.allThreadsIdle());
        this.threadBindings.clear();
        this.waitQueue.reset();
        this.segment = null;
        this.numDocs = 0;
        this.nextDocID = 0;
        this.bufferIsFull = false;
        for (int i = 0; i < this.threadStates.length; ++i) {
            this.threadStates[i].doAfterFlush();
        }
    }

    private synchronized boolean allThreadsIdle() {
        for (int i = 0; i < this.threadStates.length; ++i) {
            if (this.threadStates[i].isIdle) continue;
            return false;
        }
        return true;
    }

    synchronized boolean anyChanges() {
        return this.numDocs != 0 || this.pendingDeletes.any();
    }

    public BufferedDeletes getPendingDeletes() {
        return this.pendingDeletes;
    }

    private void pushDeletes(SegmentInfo newSegment, SegmentInfos segmentInfos) {
        long delGen = this.bufferedDeletesStream.getNextGen();
        if (this.pendingDeletes.any()) {
            if (segmentInfos.size() > 0 || newSegment != null) {
                FrozenBufferedDeletes packet = new FrozenBufferedDeletes(this.pendingDeletes, delGen);
                if (this.infoStream != null) {
                    this.message("flush: push buffered deletes startSize=" + this.pendingDeletes.bytesUsed.get() + " frozenSize=" + packet.bytesUsed);
                }
                this.bufferedDeletesStream.push(packet);
                if (this.infoStream != null) {
                    this.message("flush: delGen=" + packet.gen);
                }
                if (newSegment != null) {
                    newSegment.setBufferedDeletesGen(packet.gen);
                }
            } else if (this.infoStream != null) {
                this.message("flush: drop buffered deletes: no segments");
            }
            this.pendingDeletes.clear();
        } else if (newSegment != null) {
            newSegment.setBufferedDeletesGen(delGen);
        }
    }

    public boolean anyDeletions() {
        return this.pendingDeletes.any();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    synchronized SegmentInfo flush(IndexWriter writer, IndexFileDeleter deleter, MergePolicy mergePolicy, SegmentInfos segmentInfos) throws IOException {
        SegmentInfo newSegment;
        long startTime;
        block29: {
            block30: {
                startTime = System.currentTimeMillis();
                assert (Thread.holdsLock(writer));
                this.waitIdle();
                if (this.numDocs == 0) {
                    if (this.infoStream != null) {
                        this.message("flush: no docs; skipping");
                    }
                    this.pushDeletes(null, segmentInfos);
                    return null;
                }
                if (this.aborting) {
                    if (this.infoStream == null) return null;
                    this.message("flush: skip because aborting is set");
                    return null;
                }
                boolean success = false;
                try {
                    double startMBUsed;
                    block28: {
                        assert (this.nextDocID == this.numDocs) : "nextDocID=" + this.nextDocID + " numDocs=" + this.numDocs;
                        assert (this.waitQueue.numWaiting == 0) : "numWaiting=" + this.waitQueue.numWaiting;
                        assert (this.waitQueue.waitingBytes == 0L);
                        if (this.infoStream != null) {
                            this.message("flush postings as segment " + this.segment + " numDocs=" + this.numDocs);
                        }
                        SegmentWriteState flushState = new SegmentWriteState(this.infoStream, this.directory, this.segment, this.fieldInfos, this.numDocs, writer.getConfig().getTermIndexInterval(), this.pendingDeletes);
                        if (this.pendingDeletes.docIDs.size() > 0) {
                            flushState.deletedDocs = new BitVector(this.numDocs);
                            for (int delDocID : this.pendingDeletes.docIDs) {
                                flushState.deletedDocs.set(delDocID);
                            }
                            this.pendingDeletes.bytesUsed.addAndGet(-this.pendingDeletes.docIDs.size() * BufferedDeletes.BYTES_PER_DEL_DOCID);
                            this.pendingDeletes.docIDs.clear();
                        }
                        newSegment = new SegmentInfo(this.segment, this.numDocs, this.directory, false, true, this.fieldInfos.hasProx(), false);
                        HashSet<DocConsumerPerThread> threads = new HashSet<DocConsumerPerThread>();
                        for (DocumentsWriterThreadState threadState : this.threadStates) {
                            threads.add(threadState.consumer);
                        }
                        startMBUsed = (double)this.bytesUsed() / 1024.0 / 1024.0;
                        this.consumer.flush(threads, flushState);
                        newSegment.setHasVectors(flushState.hasVectors);
                        if (this.infoStream != null) {
                            this.message("new segment has " + (flushState.hasVectors ? "vectors" : "no vectors"));
                            if (flushState.deletedDocs != null) {
                                this.message("new segment has " + flushState.deletedDocs.count() + " deleted docs");
                            }
                            this.message("flushedFiles=" + newSegment.files());
                        }
                        if (mergePolicy.useCompoundFile(segmentInfos, newSegment)) {
                            String cfsFileName = IndexFileNames.segmentFileName(this.segment, "cfs");
                            if (this.infoStream != null) {
                                this.message("flush: create compound file \"" + cfsFileName + "\"");
                            }
                            CompoundFileWriter cfsWriter = new CompoundFileWriter(this.directory, cfsFileName);
                            for (String fileName : newSegment.files()) {
                                cfsWriter.addFile(fileName);
                            }
                            cfsWriter.close();
                            deleter.deleteNewFiles(newSegment.files());
                            newSegment.setUseCompoundFile(true);
                        }
                        if (flushState.deletedDocs != null) {
                            int delCount = flushState.deletedDocs.count();
                            assert (delCount > 0);
                            newSegment.setDelCount(delCount);
                            newSegment.advanceDelGen();
                            String delFileName = newSegment.getDelFileName();
                            if (this.infoStream != null) {
                                this.message("flush: write " + delCount + " deletes to " + delFileName);
                            }
                            boolean success2 = false;
                            try {
                                flushState.deletedDocs.write(this.directory, delFileName);
                                success2 = true;
                                Object var20_20 = null;
                                if (success2) break block28;
                            }
                            catch (Throwable throwable) {
                                Object var20_21 = null;
                                if (success2) throw throwable;
                                try {
                                    this.directory.deleteFile(delFileName);
                                    throw throwable;
                                }
                                catch (Throwable t) {
                                    // empty catch block
                                }
                                throw throwable;
                            }
                            try {}
                            catch (Throwable t) {}
                            this.directory.deleteFile(delFileName);
                        }
                    }
                    if (this.infoStream != null) {
                        this.message("flush: segment=" + newSegment);
                        double newSegmentSizeNoStore = (double)newSegment.sizeInBytes(false) / 1024.0 / 1024.0;
                        double newSegmentSize = (double)newSegment.sizeInBytes(true) / 1024.0 / 1024.0;
                        this.message("  ramUsed=" + this.nf.format(startMBUsed) + " MB" + " newFlushedSize=" + this.nf.format(newSegmentSize) + " MB" + " (" + this.nf.format(newSegmentSizeNoStore) + " MB w/o doc stores)" + " docs/MB=" + this.nf.format((double)this.numDocs / newSegmentSize) + " new/old=" + this.nf.format(100.0 * newSegmentSizeNoStore / startMBUsed) + "%");
                    }
                    success = true;
                    Object var27_27 = null;
                    this.notifyAll();
                    if (success) break block29;
                    if (this.segment == null) break block30;
                }
                catch (Throwable throwable) {
                    Object var27_28 = null;
                    this.notifyAll();
                    if (success) throw throwable;
                    if (this.segment != null) {
                        deleter.refresh(this.segment);
                    }
                    this.abort();
                    throw throwable;
                }
                deleter.refresh(this.segment);
            }
            this.abort();
        }
        this.doAfterFlush();
        this.pushDeletes(newSegment, segmentInfos);
        if (this.infoStream == null) return newSegment;
        this.message("flush time " + (System.currentTimeMillis() - startTime) + " msec");
        return newSegment;
    }

    synchronized void close() {
        this.closed = true;
        this.notifyAll();
    }

    synchronized DocumentsWriterThreadState getThreadState(Term delTerm, int docCount) throws IOException {
        Thread currentThread = Thread.currentThread();
        assert (!Thread.holdsLock(this.writer));
        DocumentsWriterThreadState state = this.threadBindings.get(currentThread);
        if (state == null) {
            DocumentsWriterThreadState minThreadState = null;
            for (int i = 0; i < this.threadStates.length; ++i) {
                DocumentsWriterThreadState ts = this.threadStates[i];
                if (minThreadState != null && ts.numThreads >= minThreadState.numThreads) continue;
                minThreadState = ts;
            }
            if (minThreadState != null && (minThreadState.numThreads == 0 || this.threadStates.length >= this.maxThreadStates)) {
                state = minThreadState;
                ++state.numThreads;
            } else {
                DocumentsWriterThreadState[] newArray = new DocumentsWriterThreadState[1 + this.threadStates.length];
                if (this.threadStates.length > 0) {
                    System.arraycopy(this.threadStates, 0, newArray, 0, this.threadStates.length);
                }
                DocumentsWriterThreadState documentsWriterThreadState = new DocumentsWriterThreadState(this);
                newArray[this.threadStates.length] = documentsWriterThreadState;
                state = documentsWriterThreadState;
                this.threadStates = newArray;
            }
            this.threadBindings.put(currentThread, state);
        }
        this.waitReady(state);
        if (this.segment == null) {
            this.segment = this.writer.newSegmentName();
            assert (this.numDocs == 0);
        }
        state.docState.docID = this.nextDocID;
        this.nextDocID += docCount;
        if (delTerm != null) {
            this.pendingDeletes.addTerm(delTerm, state.docState.docID);
        }
        this.numDocs += docCount;
        state.isIdle = false;
        return state;
    }

    boolean addDocument(Document doc, Analyzer analyzer) throws CorruptIndexException, IOException {
        return this.updateDocument(doc, analyzer, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    boolean updateDocument(Document doc, Analyzer analyzer, Term delTerm) throws CorruptIndexException, IOException {
        boolean doFlush = this.flushControl.waitUpdate(1, delTerm != null ? 1 : 0);
        DocumentsWriterThreadState state = this.getThreadState(delTerm, 1);
        DocState docState = state.docState;
        docState.doc = doc;
        docState.analyzer = analyzer;
        boolean success = false;
        try {
            DocWriter perDoc;
            try {
                perDoc = state.consumer.processDocument();
                Object var10_9 = null;
                docState.clear();
            }
            catch (Throwable throwable) {
                Object var10_10 = null;
                docState.clear();
                throw throwable;
            }
            this.finishDocument(state, perDoc);
            return doFlush |= this.flushControl.flushByRAMUsage("new document");
        }
        catch (Throwable throwable) {
            Object var12_13 = null;
            if (!success) {
                if (doFlush) {
                    this.flushControl.clearFlushPending();
                }
                if (this.infoStream != null) {
                    this.message("exception in updateDocument aborting=" + this.aborting);
                }
                DocumentsWriter documentsWriter = this;
                synchronized (documentsWriter) {
                    state.isIdle = true;
                    this.notifyAll();
                    if (this.aborting) {
                        this.abort();
                    } else {
                        block16: {
                            this.skipDocWriter.docID = docState.docID;
                            boolean success2 = false;
                            try {
                                this.waitQueue.add(this.skipDocWriter);
                                success2 = true;
                                Object var16_20 = null;
                                if (success2) break block16;
                            }
                            catch (Throwable throwable2) {
                                Object var16_21 = null;
                                if (!success2) {
                                    this.abort();
                                    return false;
                                }
                                throw throwable2;
                            }
                            this.abort();
                            return false;
                        }
                        this.deleteDocID(state.docState.docID);
                    }
                }
            }
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    boolean updateDocuments(Collection<Document> docs, Analyzer analyzer, Term delTerm) throws CorruptIndexException, IOException {
        doFlush = this.flushControl.waitUpdate(docs.size(), delTerm != null ? 1 : 0);
        docCount = docs.size();
        state = this.getThreadState(null, docCount);
        docState = state.docState;
        docID = startDocID = docState.docID;
        i$ = docs.iterator();
        while (i$.hasNext()) {
            docState.doc = doc = i$.next();
            docState.analyzer = analyzer;
            docState.docID = docID++;
            success = false;
            try {
                try {
                    perDoc = state.consumer.processDocument();
                    var15_16 = null;
                    docState.clear();
                }
                catch (Throwable var14_15) {
                    var15_16 = null;
                    docState.clear();
                    throw var14_15;
                }
                this.balanceRAM();
                var14_14 = this;
                synchronized (var14_14) {
                    if (!this.aborting) ** break block44
                }
                var18_17 = null;
                if (success) break;
                if (doFlush) {
                    this.message("clearFlushPending!");
                    this.flushControl.clearFlushPending();
                }
                if (this.infoStream != null) {
                    this.message("exception in updateDocuments aborting=" + this.aborting);
                }
                var19_18 = this;
                ** GOTO lbl71
                {
                    if (!DocumentsWriter.$assertionsDisabled && perDoc != null && perDoc.docID != docState.docID) {
                        throw new AssertionError();
                    }
                    if (perDoc != null) {
                        this.waitQueue.add(perDoc);
                    } else {
                        this.skipDocWriter.docID = docState.docID;
                        this.waitQueue.add(this.skipDocWriter);
                    }
                }
                success = true;
                ** GOTO lbl103
            }
            catch (Throwable var17_25) {
                block45: {
                    var18_17 = null;
                    if (success) break block45;
                    if (doFlush) {
                        this.message("clearFlushPending!");
                        this.flushControl.clearFlushPending();
                    }
                    if (this.infoStream != null) {
                        this.message("exception in updateDocuments aborting=" + this.aborting);
                    }
                    var19_18 = this;
                    synchronized (var19_18) {
                        block46: {
                            state.isIdle = true;
                            this.notifyAll();
                            if (!this.aborting) break block46;
                            this.abort();
                            break block45;
                        }
                        endDocID = startDocID + docCount;
                        docID = docState.docID;
                        ** break block47
                    }
lbl71:
                    // 1 sources

                    synchronized (var19_18) {
                        state.isIdle = true;
                        this.notifyAll();
                        if (this.aborting) {
                            this.abort();
                        } else {
                            endDocID = startDocID + docCount;
                            docID = docState.docID;
                            while (docID < endDocID) {
                                this.skipDocWriter.docID = docID++;
                                success2 = false;
                                try {
                                    this.waitQueue.add(this.skipDocWriter);
                                    success2 = true;
                                    var23_21 = null;
                                    if (success2) continue;
                                }
                                catch (Throwable var22_22) {
                                    var23_21 = null;
                                    if (!success2) {
                                        this.abort();
                                        return false;
                                    }
                                    throw var22_22;
                                }
                                this.abort();
                                return false;
                            }
                            docID = startDocID;
                            while (docID < startDocID + docs.size()) {
                                this.deleteDocID(docID++);
                            }
                        }
                        break;
                    }
lbl103:
                    // 1 sources

                    var18_17 = null;
                    if (success) continue;
                    if (doFlush) {
                        this.message("clearFlushPending!");
                        this.flushControl.clearFlushPending();
                    }
                    if (this.infoStream != null) {
                        this.message("exception in updateDocuments aborting=" + this.aborting);
                    }
                    var19_18 = this;
                    synchronized (var19_18) {
                        state.isIdle = true;
                        this.notifyAll();
                        if (this.aborting) {
                            this.abort();
                        } else {
                            endDocID = startDocID + docCount;
                            docID = docState.docID;
                            while (docID < endDocID) {
                                this.skipDocWriter.docID = docID++;
                                success2 = false;
                                try {}
                                catch (Throwable var22_23) {
                                    var23_21 = null;
                                    if (!success2) {
                                        this.abort();
                                        return false;
                                    }
                                    throw var22_23;
                                }
                                this.waitQueue.add(this.skipDocWriter);
                                success2 = true;
                                var23_21 = null;
                                if (success2) continue;
                                this.abort();
                                return false;
                            }
                            docID = startDocID;
                            while (docID < startDocID + docs.size()) {
                                this.deleteDocID(docID++);
                            }
                        }
                        continue;
                    }
lbl-1000:
                    // 1 sources

                    {
                        while (docID < endDocID) {
                            this.skipDocWriter.docID = docID++;
                            success2 = false;
                            try {}
                            catch (Throwable var22_24) {
                                var23_21 = null;
                                if (!success2) {
                                    this.abort();
                                    return false;
                                }
                                throw var22_24;
                            }
                            this.waitQueue.add(this.skipDocWriter);
                            success2 = true;
                            var23_21 = null;
                            if (success2) continue;
                            this.abort();
                            return false;
                        }
                        docID = startDocID;
                        while (docID < startDocID + docs.size()) {
                            this.deleteDocID(docID++);
                        }
                    }
                }
                throw var17_25;
            }
        }
        var10_10 = this;
        synchronized (var10_10) {
            if (this.waitQueue.doPause()) {
                this.waitForWaitQueue();
            }
            if (this.aborting) {
                state.isIdle = true;
                this.notifyAll();
                this.abort();
                if (doFlush) {
                    this.message("clearFlushPending!");
                    this.flushControl.clearFlushPending();
                }
                return false;
            }
            if (delTerm != null) {
                this.pendingDeletes.addTerm(delTerm, startDocID);
            }
            state.isIdle = true;
            this.notifyAll();
            return doFlush |= this.flushControl.flushByRAMUsage("new document");
        }
    }

    public synchronized void waitIdle() {
        while (!this.allThreadsIdle()) {
            try {
                this.wait();
            }
            catch (InterruptedException ie) {
                throw new ThreadInterruptedException(ie);
            }
        }
    }

    synchronized void waitReady(DocumentsWriterThreadState state) {
        while (!(this.closed || state.isIdle && !this.aborting)) {
            try {
                this.wait();
            }
            catch (InterruptedException ie) {
                throw new ThreadInterruptedException(ie);
            }
        }
        if (this.closed) {
            throw new AlreadyClosedException("this IndexWriter is closed");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void finishDocument(DocumentsWriterThreadState perThread, DocWriter docWriter) throws IOException {
        this.balanceRAM();
        DocumentsWriter documentsWriter = this;
        synchronized (documentsWriter) {
            boolean doPause;
            assert (docWriter == null || docWriter.docID == perThread.docState.docID);
            if (this.aborting) {
                if (docWriter != null) {
                    try {
                        docWriter.abort();
                    }
                    catch (Throwable t) {
                        // empty catch block
                    }
                }
                perThread.isIdle = true;
                this.notifyAll();
                return;
            }
            if (docWriter != null) {
                doPause = this.waitQueue.add(docWriter);
            } else {
                this.skipDocWriter.docID = perThread.docState.docID;
                doPause = this.waitQueue.add(this.skipDocWriter);
            }
            if (doPause) {
                this.waitForWaitQueue();
            }
            perThread.isIdle = true;
            this.notifyAll();
        }
    }

    synchronized void waitForWaitQueue() {
        do {
            try {
                this.wait();
            }
            catch (InterruptedException ie) {
                throw new ThreadInterruptedException(ie);
            }
        } while (!this.waitQueue.doResume());
    }

    synchronized int[] getIntBlock() {
        int[] b;
        int size = this.freeIntBlocks.size();
        if (0 == size) {
            b = new int[8192];
            this.bytesUsed.addAndGet(32768L);
        } else {
            b = this.freeIntBlocks.remove(size - 1);
        }
        return b;
    }

    synchronized void bytesUsed(long numBytes) {
        this.bytesUsed.addAndGet(numBytes);
    }

    long bytesUsed() {
        return this.bytesUsed.get() + this.pendingDeletes.bytesUsed.get();
    }

    synchronized void recycleIntBlocks(int[][] blocks, int start, int end) {
        for (int i = start; i < end; ++i) {
            this.freeIntBlocks.add(blocks[i]);
            blocks[i] = null;
        }
    }

    synchronized char[] getCharBlock() {
        char[] c;
        int size = this.freeCharBlocks.size();
        if (0 == size) {
            this.bytesUsed.addAndGet(32768L);
            c = new char[16384];
        } else {
            c = this.freeCharBlocks.remove(size - 1);
        }
        return c;
    }

    synchronized void recycleCharBlocks(char[][] blocks, int numBlocks) {
        for (int i = 0; i < numBlocks; ++i) {
            this.freeCharBlocks.add(blocks[i]);
            blocks[i] = null;
        }
    }

    String toMB(long v) {
        return this.nf.format((double)v / 1024.0 / 1024.0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void balanceRAM() {
        boolean doBalance;
        long deletesRAMUsed = this.bufferedDeletesStream.bytesUsed();
        double mb = this.config.getRAMBufferSizeMB();
        long ramBufferSize = mb == -1.0 ? -1L : (long)(mb * 1024.0 * 1024.0);
        DocumentsWriter documentsWriter = this;
        synchronized (documentsWriter) {
            if (ramBufferSize == -1L || this.bufferIsFull) {
                return;
            }
            doBalance = this.bytesUsed() + deletesRAMUsed >= ramBufferSize;
        }
        if (doBalance) {
            if (this.infoStream != null) {
                this.message("  RAM: balance allocations: usedMB=" + this.toMB(this.bytesUsed()) + " vs trigger=" + this.toMB(ramBufferSize) + " deletesMB=" + this.toMB(deletesRAMUsed) + " byteBlockFree=" + this.toMB(this.byteBlockAllocator.freeByteBlocks.size() * 32768) + " perDocFree=" + this.toMB(this.perDocAllocator.freeByteBlocks.size() * 1024) + " charBlockFree=" + this.toMB(this.freeCharBlocks.size() * 16384 * 2));
            }
            long startBytesUsed = this.bytesUsed() + deletesRAMUsed;
            int iter = 0;
            boolean any = true;
            long freeLevel = (long)(0.95 * (double)ramBufferSize);
            while (this.bytesUsed() + deletesRAMUsed > freeLevel) {
                DocumentsWriter documentsWriter2 = this;
                synchronized (documentsWriter2) {
                    if (0 == this.perDocAllocator.freeByteBlocks.size() && 0 == this.byteBlockAllocator.freeByteBlocks.size() && 0 == this.freeCharBlocks.size() && 0 == this.freeIntBlocks.size() && !any) {
                        boolean bl = this.bufferIsFull = this.bytesUsed() + deletesRAMUsed > ramBufferSize;
                        if (this.infoStream != null) {
                            if (this.bytesUsed() + deletesRAMUsed > ramBufferSize) {
                                this.message("    nothing to free; set bufferIsFull");
                            } else {
                                this.message("    nothing to free");
                            }
                        }
                        break;
                    }
                    if (0 == iter % 5 && this.byteBlockAllocator.freeByteBlocks.size() > 0) {
                        this.byteBlockAllocator.freeByteBlocks.remove(this.byteBlockAllocator.freeByteBlocks.size() - 1);
                        this.bytesUsed.addAndGet(-32768L);
                    }
                    if (1 == iter % 5 && this.freeCharBlocks.size() > 0) {
                        this.freeCharBlocks.remove(this.freeCharBlocks.size() - 1);
                        this.bytesUsed.addAndGet(-32768L);
                    }
                    if (2 == iter % 5 && this.freeIntBlocks.size() > 0) {
                        this.freeIntBlocks.remove(this.freeIntBlocks.size() - 1);
                        this.bytesUsed.addAndGet(-32768L);
                    }
                    if (3 == iter % 5 && this.perDocAllocator.freeByteBlocks.size() > 0) {
                        for (int i = 0; i < 32; ++i) {
                            this.perDocAllocator.freeByteBlocks.remove(this.perDocAllocator.freeByteBlocks.size() - 1);
                            this.bytesUsed.addAndGet(-1024L);
                            if (this.perDocAllocator.freeByteBlocks.size() == 0) break;
                        }
                    }
                }
                if (4 == iter % 5 && any) {
                    any = this.consumer.freeRAM();
                }
                ++iter;
            }
            if (this.infoStream != null) {
                this.message("    after free: freedMB=" + this.nf.format((double)(startBytesUsed - this.bytesUsed() - deletesRAMUsed) / 1024.0 / 1024.0) + " usedMB=" + this.nf.format((double)(this.bytesUsed() + deletesRAMUsed) / 1024.0 / 1024.0));
            }
        }
    }

    private class WaitQueue {
        DocWriter[] waiting = new DocWriter[10];
        int nextWriteDocID;
        int nextWriteLoc;
        int numWaiting;
        long waitingBytes;

        synchronized void reset() {
            assert (this.numWaiting == 0);
            assert (this.waitingBytes == 0L);
            this.nextWriteDocID = 0;
        }

        synchronized boolean doResume() {
            double mb = DocumentsWriter.this.config.getRAMBufferSizeMB();
            long waitQueueResumeBytes = mb == -1.0 ? 0x200000L : (long)(mb * 1024.0 * 1024.0 * 0.05);
            return this.waitingBytes <= waitQueueResumeBytes;
        }

        synchronized boolean doPause() {
            double mb = DocumentsWriter.this.config.getRAMBufferSizeMB();
            long waitQueuePauseBytes = mb == -1.0 ? 0x400000L : (long)(mb * 1024.0 * 1024.0 * 0.1);
            return this.waitingBytes > waitQueuePauseBytes;
        }

        synchronized void abort() {
            int count = 0;
            for (int i = 0; i < this.waiting.length; ++i) {
                DocWriter doc = this.waiting[i];
                if (doc == null) continue;
                doc.abort();
                this.waiting[i] = null;
                ++count;
            }
            this.waitingBytes = 0L;
            assert (count == this.numWaiting);
            this.numWaiting = 0;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void writeDocument(DocWriter doc) throws IOException {
            assert (doc == DocumentsWriter.this.skipDocWriter || this.nextWriteDocID == doc.docID);
            boolean success = false;
            try {
                doc.finish();
                ++this.nextWriteDocID;
                ++this.nextWriteLoc;
                assert (this.nextWriteLoc <= this.waiting.length);
                if (this.nextWriteLoc == this.waiting.length) {
                    this.nextWriteLoc = 0;
                }
                success = true;
            }
            finally {
                if (!success) {
                    DocumentsWriter.this.setAborting();
                }
            }
        }

        public synchronized boolean add(DocWriter doc) throws IOException {
            assert (doc.docID >= this.nextWriteDocID);
            if (doc.docID == this.nextWriteDocID) {
                this.writeDocument(doc);
                while ((doc = this.waiting[this.nextWriteLoc]) != null) {
                    --this.numWaiting;
                    this.waiting[this.nextWriteLoc] = null;
                    this.waitingBytes -= doc.sizeInBytes();
                    this.writeDocument(doc);
                }
            } else {
                int loc;
                int gap = doc.docID - this.nextWriteDocID;
                if (gap >= this.waiting.length) {
                    DocWriter[] newArray = new DocWriter[ArrayUtil.oversize(gap, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
                    assert (this.nextWriteLoc >= 0);
                    System.arraycopy(this.waiting, this.nextWriteLoc, newArray, 0, this.waiting.length - this.nextWriteLoc);
                    System.arraycopy(this.waiting, 0, newArray, this.waiting.length - this.nextWriteLoc, this.nextWriteLoc);
                    this.nextWriteLoc = 0;
                    this.waiting = newArray;
                    gap = doc.docID - this.nextWriteDocID;
                }
                if ((loc = this.nextWriteLoc + gap) >= this.waiting.length) {
                    loc -= this.waiting.length;
                }
                assert (loc < this.waiting.length);
                assert (this.waiting[loc] == null);
                this.waiting[loc] = doc;
                ++this.numWaiting;
                this.waitingBytes += doc.sizeInBytes();
            }
            return this.doPause();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private class ByteBlockAllocator
    extends ByteBlockPool.Allocator {
        final int blockSize;
        ArrayList<byte[]> freeByteBlocks = new ArrayList();

        ByteBlockAllocator(int blockSize) {
            this.blockSize = blockSize;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        byte[] getByteBlock() {
            DocumentsWriter documentsWriter = DocumentsWriter.this;
            synchronized (documentsWriter) {
                byte[] b;
                int size = this.freeByteBlocks.size();
                if (0 == size) {
                    b = new byte[this.blockSize];
                    DocumentsWriter.this.bytesUsed.addAndGet(this.blockSize);
                } else {
                    b = this.freeByteBlocks.remove(size - 1);
                }
                return b;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        void recycleByteBlocks(byte[][] blocks, int start, int end) {
            DocumentsWriter documentsWriter = DocumentsWriter.this;
            synchronized (documentsWriter) {
                for (int i = start; i < end; ++i) {
                    this.freeByteBlocks.add(blocks[i]);
                    blocks[i] = null;
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        void recycleByteBlocks(List<byte[]> blocks) {
            DocumentsWriter documentsWriter = DocumentsWriter.this;
            synchronized (documentsWriter) {
                int size = blocks.size();
                for (int i = 0; i < size; ++i) {
                    this.freeByteBlocks.add(blocks.get(i));
                    blocks.set(i, null);
                }
            }
        }
    }

    private static class SkipDocWriter
    extends DocWriter {
        private SkipDocWriter() {
        }

        void finish() {
        }

        void abort() {
        }

        long sizeInBytes() {
            return 0L;
        }
    }

    static abstract class IndexingChain {
        IndexingChain() {
        }

        abstract DocConsumer getChain(DocumentsWriter var1);
    }

    class PerDocBuffer
    extends RAMFile {
        PerDocBuffer() {
        }

        protected byte[] newBuffer(int size) {
            assert (size == 1024);
            return DocumentsWriter.this.perDocAllocator.getByteBlock();
        }

        synchronized void recycle() {
            if (this.buffers.size() > 0) {
                this.setLength(0L);
                DocumentsWriter.this.perDocAllocator.recycleByteBlocks(this.buffers);
                this.buffers.clear();
                this.sizeInBytes = 0L;
                assert (this.numBuffers() == 0);
            }
        }
    }

    static abstract class DocWriter {
        DocWriter next;
        int docID;

        DocWriter() {
        }

        abstract void finish() throws IOException;

        abstract void abort();

        abstract long sizeInBytes();

        void setNext(DocWriter next) {
            this.next = next;
        }
    }

    static class DocState {
        DocumentsWriter docWriter;
        Analyzer analyzer;
        int maxFieldLength;
        PrintStream infoStream;
        Similarity similarity;
        int docID;
        Document doc;
        String maxTermPrefix;

        DocState() {
        }

        public boolean testPoint(String name) {
            return this.docWriter.writer.testPoint(name);
        }

        public void clear() {
            this.doc = null;
            this.analyzer = null;
        }
    }
}

