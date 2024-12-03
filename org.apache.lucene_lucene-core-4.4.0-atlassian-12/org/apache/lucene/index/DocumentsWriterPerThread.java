/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.index.BufferedDeletes;
import org.apache.lucene.index.DocConsumer;
import org.apache.lucene.index.DocFieldProcessor;
import org.apache.lucene.index.DocInverter;
import org.apache.lucene.index.DocValuesProcessor;
import org.apache.lucene.index.DocumentsWriter;
import org.apache.lucene.index.DocumentsWriterDeleteQueue;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.FreqProxTermsWriter;
import org.apache.lucene.index.FrozenBufferedDeletes;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.LiveIndexWriterConfig;
import org.apache.lucene.index.MergeState;
import org.apache.lucene.index.NormsConsumer;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.index.SegmentInfoPerCommit;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.index.StoredFieldsProcessor;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermVectorsConsumer;
import org.apache.lucene.index.TermsHash;
import org.apache.lucene.index.TwoStoredFieldsConsumers;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FlushInfo;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.TrackingDirectoryWrapper;
import org.apache.lucene.util.ByteBlockPool;
import org.apache.lucene.util.Constants;
import org.apache.lucene.util.Counter;
import org.apache.lucene.util.InfoStream;
import org.apache.lucene.util.IntBlockPool;
import org.apache.lucene.util.MutableBits;

class DocumentsWriterPerThread {
    static final IndexingChain defaultIndexingChain = new IndexingChain(){

        @Override
        DocConsumer getChain(DocumentsWriterPerThread documentsWriterPerThread) {
            TermVectorsConsumer termVectorsWriter = new TermVectorsConsumer(documentsWriterPerThread);
            FreqProxTermsWriter freqProxWriter = new FreqProxTermsWriter();
            TermsHash termsHash = new TermsHash(documentsWriterPerThread, freqProxWriter, true, new TermsHash(documentsWriterPerThread, termVectorsWriter, false, null));
            NormsConsumer normsWriter = new NormsConsumer();
            DocInverter docInverter = new DocInverter(documentsWriterPerThread.docState, termsHash, normsWriter);
            TwoStoredFieldsConsumers storedFields = new TwoStoredFieldsConsumers(new StoredFieldsProcessor(documentsWriterPerThread), new DocValuesProcessor(documentsWriterPerThread.bytesUsed));
            return new DocFieldProcessor(documentsWriterPerThread, docInverter, storedFields);
        }
    };
    private static final boolean INFO_VERBOSE = false;
    final DocumentsWriter parent;
    final Codec codec;
    final IndexWriter writer;
    final TrackingDirectoryWrapper directory;
    final Directory directoryOrig;
    final DocState docState;
    final DocConsumer consumer;
    final Counter bytesUsed;
    SegmentWriteState flushState;
    BufferedDeletes pendingDeletes;
    SegmentInfo segmentInfo;
    boolean aborting = false;
    boolean hasAborted = false;
    private FieldInfos.Builder fieldInfos;
    private final InfoStream infoStream;
    private int numDocsInRAM;
    private int flushedDocCount;
    DocumentsWriterDeleteQueue deleteQueue;
    DocumentsWriterDeleteQueue.DeleteSlice deleteSlice;
    private final NumberFormat nf = NumberFormat.getInstance(Locale.ROOT);
    final ByteBlockPool.Allocator byteBlockAllocator;
    final IntBlockPool.Allocator intBlockAllocator;
    private final LiveIndexWriterConfig indexWriterConfig;
    static final int BYTE_BLOCK_NOT_MASK = Short.MIN_VALUE;
    static final int MAX_TERM_LENGTH_UTF8 = 32766;

    void abort() {
        this.aborting = true;
        this.hasAborted = true;
        try {
            if (this.infoStream.isEnabled("DWPT")) {
                this.infoStream.message("DWPT", "now abort");
            }
            try {
                this.consumer.abort();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            this.pendingDeletes.clear();
            this.deleteSlice = this.deleteQueue.newSlice();
            this.doAfterFlush();
        }
        finally {
            this.aborting = false;
            if (this.infoStream.isEnabled("DWPT")) {
                this.infoStream.message("DWPT", "done abort");
            }
        }
    }

    public DocumentsWriterPerThread(Directory directory, DocumentsWriter parent, FieldInfos.Builder fieldInfos, IndexingChain indexingChain) {
        this.directoryOrig = directory;
        this.directory = new TrackingDirectoryWrapper(directory);
        this.parent = parent;
        this.fieldInfos = fieldInfos;
        this.writer = parent.indexWriter;
        this.indexWriterConfig = parent.indexWriterConfig;
        this.infoStream = parent.infoStream;
        this.codec = parent.codec;
        this.docState = new DocState(this, this.infoStream);
        this.docState.similarity = parent.indexWriter.getConfig().getSimilarity();
        this.bytesUsed = Counter.newCounter();
        this.byteBlockAllocator = new ByteBlockPool.DirectTrackingAllocator(this.bytesUsed);
        this.pendingDeletes = new BufferedDeletes();
        this.intBlockAllocator = new IntBlockAllocator(this.bytesUsed);
        this.initialize();
        this.consumer = indexingChain.getChain(this);
    }

    public DocumentsWriterPerThread(DocumentsWriterPerThread other, FieldInfos.Builder fieldInfos) {
        this(other.directoryOrig, other.parent, fieldInfos, other.parent.chain);
    }

    void initialize() {
        this.deleteQueue = this.parent.deleteQueue;
        assert (this.numDocsInRAM == 0) : "num docs " + this.numDocsInRAM;
        this.pendingDeletes.clear();
        this.deleteSlice = null;
    }

    void setAborting() {
        this.aborting = true;
    }

    final boolean testPoint(String message) {
        if (this.infoStream.isEnabled("TP")) {
            this.infoStream.message("TP", message);
        }
        return true;
    }

    boolean checkAndResetHasAborted() {
        boolean retval = this.hasAborted;
        this.hasAborted = false;
        return retval;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateDocument(Iterable<? extends IndexableField> doc, Analyzer analyzer, Term delTerm) throws IOException {
        assert (this.testPoint("DocumentsWriterPerThread addDocument start"));
        assert (this.deleteQueue != null);
        this.docState.doc = doc;
        this.docState.analyzer = analyzer;
        this.docState.docID = this.numDocsInRAM;
        if (this.segmentInfo == null) {
            this.initSegmentInfo();
        }
        boolean success = false;
        try {
            try {
                this.consumer.processDocument(this.fieldInfos);
            }
            finally {
                this.docState.clear();
            }
            success = true;
        }
        finally {
            if (!success) {
                if (!this.aborting) {
                    this.deleteDocID(this.docState.docID);
                    ++this.numDocsInRAM;
                } else {
                    this.abort();
                }
            }
        }
        success = false;
        try {
            this.consumer.finishDocument();
            success = true;
        }
        finally {
            if (!success) {
                this.abort();
            }
        }
        this.finishDocument(delTerm);
    }

    private void initSegmentInfo() {
        String segment = this.writer.newSegmentName();
        this.segmentInfo = new SegmentInfo(this.directoryOrig, Constants.LUCENE_MAIN_VERSION, segment, -1, false, this.codec, null, null);
        assert (this.numDocsInRAM == 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int updateDocuments(Iterable<? extends Iterable<? extends IndexableField>> docs, Analyzer analyzer, Term delTerm) throws IOException {
        assert (this.testPoint("DocumentsWriterPerThread addDocuments start"));
        assert (this.deleteQueue != null);
        this.docState.analyzer = analyzer;
        if (this.segmentInfo == null) {
            this.initSegmentInfo();
        }
        int docCount = 0;
        boolean allDocsIndexed = false;
        try {
            for (Iterable<? extends IndexableField> iterable : docs) {
                this.docState.doc = iterable;
                this.docState.docID = this.numDocsInRAM++;
                ++docCount;
                boolean success = false;
                try {
                    this.consumer.processDocument(this.fieldInfos);
                    success = true;
                }
                finally {
                    if (!success && this.aborting) {
                        this.abort();
                    }
                }
                success = false;
                try {
                    this.consumer.finishDocument();
                    success = true;
                }
                finally {
                    if (!success) {
                        this.abort();
                    }
                }
                this.finishDocument(null);
            }
            allDocsIndexed = true;
            if (delTerm != null) {
                this.deleteQueue.add(delTerm, this.deleteSlice);
                assert (this.deleteSlice.isTailItem(delTerm)) : "expected the delete term as the tail item";
                this.deleteSlice.apply(this.pendingDeletes, this.numDocsInRAM - docCount);
            }
        }
        finally {
            if (!allDocsIndexed && !this.aborting) {
                int docID;
                int n = docID - docCount;
                for (docID = this.numDocsInRAM - 1; docID > n; --docID) {
                    this.deleteDocID(docID);
                }
            }
            this.docState.clear();
        }
        return docCount;
    }

    private void finishDocument(Term delTerm) {
        if (this.deleteSlice == null) {
            this.deleteSlice = this.deleteQueue.newSlice();
            if (delTerm != null) {
                this.deleteQueue.add(delTerm, this.deleteSlice);
                this.deleteSlice.reset();
            }
        } else if (delTerm != null) {
            this.deleteQueue.add(delTerm, this.deleteSlice);
            assert (this.deleteSlice.isTailItem(delTerm)) : "expected the delete term as the tail item";
            this.deleteSlice.apply(this.pendingDeletes, this.numDocsInRAM);
        } else if (this.deleteQueue.updateSlice(this.deleteSlice)) {
            this.deleteSlice.apply(this.pendingDeletes, this.numDocsInRAM);
        }
        ++this.numDocsInRAM;
    }

    void deleteDocID(int docIDUpto) {
        this.pendingDeletes.addDocID(docIDUpto);
    }

    public int numDeleteTerms() {
        return this.pendingDeletes.numTermDeletes.get();
    }

    public int getNumDocsInRAM() {
        return this.numDocsInRAM;
    }

    private void doAfterFlush() {
        this.segmentInfo = null;
        this.directory.getCreatedFiles().clear();
        this.fieldInfos = new FieldInfos.Builder(this.fieldInfos.globalFieldNumbers);
        this.parent.subtractFlushedNumDocs(this.numDocsInRAM);
        this.numDocsInRAM = 0;
    }

    FrozenBufferedDeletes prepareFlush() {
        assert (this.numDocsInRAM > 0);
        FrozenBufferedDeletes globalDeletes = this.deleteQueue.freezeGlobalBuffer(this.deleteSlice);
        if (this.deleteSlice != null) {
            this.deleteSlice.apply(this.pendingDeletes, this.numDocsInRAM);
            assert (this.deleteSlice.isEmpty());
            this.deleteSlice = null;
        }
        return globalDeletes;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    FlushedSegment flush() throws IOException {
        assert (this.numDocsInRAM > 0);
        assert (this.deleteSlice == null) : "all deletes must be applied in prepareFlush";
        this.segmentInfo.setDocCount(this.numDocsInRAM);
        this.flushState = new SegmentWriteState(this.infoStream, this.directory, this.segmentInfo, this.fieldInfos.finish(), this.writer.getConfig().getTermIndexInterval(), this.pendingDeletes, new IOContext(new FlushInfo(this.numDocsInRAM, this.bytesUsed())));
        double startMBUsed = (double)this.parent.flushControl.netBytes() / 1024.0 / 1024.0;
        if (this.pendingDeletes.docIDs.size() > 0) {
            this.flushState.liveDocs = this.codec.liveDocsFormat().newLiveDocs(this.numDocsInRAM);
            for (int delDocID : this.pendingDeletes.docIDs) {
                this.flushState.liveDocs.clear(delDocID);
            }
            this.flushState.delCountOnFlush = this.pendingDeletes.docIDs.size();
            this.pendingDeletes.bytesUsed.addAndGet(-this.pendingDeletes.docIDs.size() * BufferedDeletes.BYTES_PER_DEL_DOCID);
            this.pendingDeletes.docIDs.clear();
        }
        if (this.aborting) {
            if (this.infoStream.isEnabled("DWPT")) {
                this.infoStream.message("DWPT", "flush: skip because aborting is set");
            }
            return null;
        }
        if (this.infoStream.isEnabled("DWPT")) {
            this.infoStream.message("DWPT", "flush postings as segment " + this.flushState.segmentInfo.name + " numDocs=" + this.numDocsInRAM);
        }
        boolean success = false;
        try {
            BufferedDeletes segmentDeletes;
            this.consumer.flush(this.flushState);
            this.pendingDeletes.terms.clear();
            this.segmentInfo.setFiles(new HashSet<String>(this.directory.getCreatedFiles()));
            SegmentInfoPerCommit segmentInfoPerCommit = new SegmentInfoPerCommit(this.segmentInfo, 0, -1L);
            if (this.infoStream.isEnabled("DWPT")) {
                this.infoStream.message("DWPT", "new segment has " + (this.flushState.liveDocs == null ? 0 : this.flushState.segmentInfo.getDocCount() - this.flushState.delCountOnFlush) + " deleted docs");
                this.infoStream.message("DWPT", "new segment has " + (this.flushState.fieldInfos.hasVectors() ? "vectors" : "no vectors") + "; " + (this.flushState.fieldInfos.hasNorms() ? "norms" : "no norms") + "; " + (this.flushState.fieldInfos.hasDocValues() ? "docValues" : "no docValues") + "; " + (this.flushState.fieldInfos.hasProx() ? "prox" : "no prox") + "; " + (this.flushState.fieldInfos.hasFreq() ? "freqs" : "no freqs"));
                this.infoStream.message("DWPT", "flushedFiles=" + segmentInfoPerCommit.files());
                this.infoStream.message("DWPT", "flushed codec=" + this.codec);
            }
            this.flushedDocCount += this.flushState.segmentInfo.getDocCount();
            if (this.pendingDeletes.queries.isEmpty()) {
                this.pendingDeletes.clear();
                segmentDeletes = null;
            } else {
                segmentDeletes = this.pendingDeletes;
                this.pendingDeletes = new BufferedDeletes();
            }
            if (this.infoStream.isEnabled("DWPT")) {
                double newSegmentSize = (double)segmentInfoPerCommit.sizeInBytes() / 1024.0 / 1024.0;
                this.infoStream.message("DWPT", "flushed: segment=" + this.segmentInfo.name + " ramUsed=" + this.nf.format(startMBUsed) + " MB newFlushedSize(includes docstores)=" + this.nf.format(newSegmentSize) + " MB docs/MB=" + this.nf.format((double)this.flushedDocCount / newSegmentSize));
            }
            assert (this.segmentInfo != null);
            FlushedSegment fs = new FlushedSegment(segmentInfoPerCommit, this.flushState.fieldInfos, segmentDeletes, this.flushState.liveDocs, this.flushState.delCountOnFlush);
            this.sealFlushedSegment(fs);
            this.doAfterFlush();
            success = true;
            FlushedSegment flushedSegment = fs;
            return flushedSegment;
        }
        finally {
            if (!success) {
                if (this.segmentInfo != null) {
                    this.writer.flushFailed(this.segmentInfo);
                }
                this.abort();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void sealFlushedSegment(FlushedSegment flushedSegment) throws IOException {
        assert (flushedSegment != null);
        SegmentInfoPerCommit newSegment = flushedSegment.segmentInfo;
        IndexWriter.setDiagnostics(newSegment.info, "flush");
        IOContext context = new IOContext(new FlushInfo(newSegment.info.getDocCount(), newSegment.sizeInBytes()));
        boolean success = false;
        try {
            if (this.indexWriterConfig.getUseCompoundFile()) {
                Collection<String> oldFiles = IndexWriter.createCompoundFile(this.infoStream, this.directory, MergeState.CheckAbort.NONE, newSegment.info, context);
                newSegment.info.setUseCompoundFile(true);
                this.writer.deleteNewFiles(oldFiles);
            }
            this.codec.segmentInfoFormat().getSegmentInfoWriter().write(this.directory, newSegment.info, flushedSegment.fieldInfos, context);
            if (flushedSegment.liveDocs != null) {
                int delCount = flushedSegment.delCount;
                assert (delCount > 0);
                if (this.infoStream.isEnabled("DWPT")) {
                    this.infoStream.message("DWPT", "flush: write " + delCount + " deletes gen=" + flushedSegment.segmentInfo.getDelGen());
                }
                SegmentInfoPerCommit info = flushedSegment.segmentInfo;
                Codec codec = info.info.getCodec();
                codec.liveDocsFormat().writeLiveDocs(flushedSegment.liveDocs, this.directory, info, delCount, context);
                newSegment.setDelCount(delCount);
                newSegment.advanceDelGen();
            }
            success = true;
        }
        finally {
            if (!success) {
                if (this.infoStream.isEnabled("DWPT")) {
                    this.infoStream.message("DWPT", "hit exception reating compound file for newly flushed segment " + newSegment.info.name);
                }
                this.writer.flushFailed(newSegment.info);
            }
        }
    }

    SegmentInfo getSegmentInfo() {
        return this.segmentInfo;
    }

    long bytesUsed() {
        return this.bytesUsed.get() + this.pendingDeletes.bytesUsed.get();
    }

    public String toString() {
        return "DocumentsWriterPerThread [pendingDeletes=" + this.pendingDeletes + ", segment=" + (this.segmentInfo != null ? this.segmentInfo.name : "null") + ", aborting=" + this.aborting + ", numDocsInRAM=" + this.numDocsInRAM + ", deleteQueue=" + this.deleteQueue + "]";
    }

    private static class IntBlockAllocator
    extends IntBlockPool.Allocator {
        private final Counter bytesUsed;

        public IntBlockAllocator(Counter bytesUsed) {
            super(8192);
            this.bytesUsed = bytesUsed;
        }

        @Override
        public int[] getIntBlock() {
            int[] b = new int[8192];
            this.bytesUsed.addAndGet(32768L);
            return b;
        }

        @Override
        public void recycleIntBlocks(int[][] blocks, int offset, int length) {
            this.bytesUsed.addAndGet(-(length * 32768));
        }
    }

    static class FlushedSegment {
        final SegmentInfoPerCommit segmentInfo;
        final FieldInfos fieldInfos;
        final FrozenBufferedDeletes segmentDeletes;
        final MutableBits liveDocs;
        final int delCount;

        private FlushedSegment(SegmentInfoPerCommit segmentInfo, FieldInfos fieldInfos, BufferedDeletes segmentDeletes, MutableBits liveDocs, int delCount) {
            this.segmentInfo = segmentInfo;
            this.fieldInfos = fieldInfos;
            this.segmentDeletes = segmentDeletes != null && segmentDeletes.any() ? new FrozenBufferedDeletes(segmentDeletes, true) : null;
            this.liveDocs = liveDocs;
            this.delCount = delCount;
        }
    }

    static class DocState {
        final DocumentsWriterPerThread docWriter;
        Analyzer analyzer;
        InfoStream infoStream;
        Similarity similarity;
        int docID;
        Iterable<? extends IndexableField> doc;
        String maxTermPrefix;

        DocState(DocumentsWriterPerThread docWriter, InfoStream infoStream) {
            this.docWriter = docWriter;
            this.infoStream = infoStream;
        }

        public boolean testPoint(String name) {
            return this.docWriter.testPoint(name);
        }

        public void clear() {
            this.doc = null;
            this.analyzer = null;
        }
    }

    static abstract class IndexingChain {
        IndexingChain() {
        }

        abstract DocConsumer getChain(DocumentsWriterPerThread var1);
    }
}

