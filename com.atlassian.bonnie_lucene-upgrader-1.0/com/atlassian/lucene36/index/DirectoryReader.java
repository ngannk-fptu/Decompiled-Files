/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.document.Document;
import com.atlassian.lucene36.document.FieldSelector;
import com.atlassian.lucene36.index.CorruptIndexException;
import com.atlassian.lucene36.index.FieldInfos;
import com.atlassian.lucene36.index.IndexCommit;
import com.atlassian.lucene36.index.IndexDeletionPolicy;
import com.atlassian.lucene36.index.IndexFileDeleter;
import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.IndexWriter;
import com.atlassian.lucene36.index.IndexWriterConfig;
import com.atlassian.lucene36.index.KeepOnlyLastCommitDeletionPolicy;
import com.atlassian.lucene36.index.ReadOnlyDirectoryReader;
import com.atlassian.lucene36.index.ReadOnlySegmentReader;
import com.atlassian.lucene36.index.SegmentInfo;
import com.atlassian.lucene36.index.SegmentInfos;
import com.atlassian.lucene36.index.SegmentMergeInfo;
import com.atlassian.lucene36.index.SegmentMergeQueue;
import com.atlassian.lucene36.index.SegmentReader;
import com.atlassian.lucene36.index.StaleReaderException;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.index.TermDocs;
import com.atlassian.lucene36.index.TermEnum;
import com.atlassian.lucene36.index.TermFreqVector;
import com.atlassian.lucene36.index.TermPositions;
import com.atlassian.lucene36.index.TermVectorMapper;
import com.atlassian.lucene36.search.Similarity;
import com.atlassian.lucene36.store.Directory;
import com.atlassian.lucene36.store.Lock;
import com.atlassian.lucene36.store.LockObtainFailedException;
import com.atlassian.lucene36.util.IOUtils;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class DirectoryReader
extends IndexReader
implements Cloneable {
    protected Directory directory;
    protected boolean readOnly;
    IndexWriter writer;
    private IndexDeletionPolicy deletionPolicy;
    private Lock writeLock;
    private final SegmentInfos segmentInfos;
    private boolean stale;
    private final int termInfosIndexDivisor;
    private boolean rollbackHasChanges;
    private SegmentReader[] subReaders;
    private int[] starts;
    private Map<String, byte[]> normsCache = new HashMap<String, byte[]>();
    private int maxDoc = 0;
    private int numDocs = -1;
    private boolean hasDeletions = false;
    private long maxIndexVersion;
    private final boolean applyAllDeletes;

    static IndexReader open(Directory directory, final IndexDeletionPolicy deletionPolicy, IndexCommit commit, final boolean readOnly, final int termInfosIndexDivisor) throws CorruptIndexException, IOException {
        return (IndexReader)new SegmentInfos.FindSegmentsFile(directory){

            protected Object doBody(String segmentFileName) throws CorruptIndexException, IOException {
                SegmentInfos infos = new SegmentInfos();
                infos.read(this.directory, segmentFileName);
                if (readOnly) {
                    return new ReadOnlyDirectoryReader(this.directory, infos, deletionPolicy, termInfosIndexDivisor);
                }
                return new DirectoryReader(this.directory, infos, deletionPolicy, false, termInfosIndexDivisor);
            }
        }.run(commit);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    DirectoryReader(Directory directory, SegmentInfos sis, IndexDeletionPolicy deletionPolicy, boolean readOnly, int termInfosIndexDivisor) throws IOException {
        this.directory = directory;
        this.readOnly = readOnly;
        this.segmentInfos = sis;
        this.deletionPolicy = deletionPolicy;
        this.termInfosIndexDivisor = termInfosIndexDivisor;
        this.applyAllDeletes = false;
        Closeable[] readers = new SegmentReader[sis.size()];
        int i = sis.size() - 1;
        while (true) {
            block8: {
                Object var12_11;
                if (i < 0) {
                    this.initialize((SegmentReader[])readers);
                    return;
                }
                IOException prior = null;
                boolean success = false;
                try {
                    try {
                        readers[i] = SegmentReader.get(readOnly, sis.info(i), termInfosIndexDivisor);
                        success = true;
                    }
                    catch (IOException ex) {
                        prior = ex;
                        var12_11 = null;
                        if (!success) {
                            IOUtils.closeWhileHandlingException(prior, readers);
                        }
                        break block8;
                    }
                    var12_11 = null;
                    if (success) break block8;
                }
                catch (Throwable throwable) {
                    var12_11 = null;
                    if (!success) {
                        IOUtils.closeWhileHandlingException(prior, readers);
                    }
                    throw throwable;
                }
                IOUtils.closeWhileHandlingException(prior, readers);
            }
            --i;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    DirectoryReader(IndexWriter writer, SegmentInfos infos, int termInfosIndexDivisor, boolean applyAllDeletes) throws IOException {
        this.directory = writer.getDirectory();
        this.readOnly = true;
        this.applyAllDeletes = applyAllDeletes;
        this.termInfosIndexDivisor = termInfosIndexDivisor;
        int numSegments = infos.size();
        ArrayList<SegmentReader> readers = new ArrayList<SegmentReader>();
        Directory dir = writer.getDirectory();
        this.segmentInfos = (SegmentInfos)infos.clone();
        int infosUpto = 0;
        int i = 0;
        while (true) {
            block11: {
                Object var15_15;
                if (i >= numSegments) {
                    this.writer = writer;
                    this.initialize(readers.toArray(new SegmentReader[readers.size()]));
                    return;
                }
                IOException prior = null;
                boolean success = false;
                try {
                    try {
                        SegmentInfo info = infos.info(i);
                        assert (info.dir == dir);
                        SegmentReader reader = writer.readerPool.getReadOnlyClone(info, true, termInfosIndexDivisor);
                        if (reader.numDocs() > 0 || writer.getKeepFullyDeletedSegments()) {
                            readers.add(reader);
                            ++infosUpto;
                        } else {
                            reader.close();
                            this.segmentInfos.remove(infosUpto);
                        }
                        success = true;
                    }
                    catch (IOException ex) {
                        prior = ex;
                        var15_15 = null;
                        if (!success) {
                            IOUtils.closeWhileHandlingException(prior, readers);
                        }
                        break block11;
                    }
                    var15_15 = null;
                    if (success) break block11;
                }
                catch (Throwable throwable) {
                    var15_15 = null;
                    if (!success) {
                        IOUtils.closeWhileHandlingException(prior, readers);
                    }
                    throw throwable;
                }
                IOUtils.closeWhileHandlingException(prior, readers);
            }
            ++i;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    DirectoryReader(Directory directory, SegmentInfos infos, SegmentReader[] oldReaders, int[] oldStarts, Map<String, byte[]> oldNormsCache, boolean readOnly, boolean doClone, int termInfosIndexDivisor) throws IOException {
        this.directory = directory;
        this.readOnly = readOnly;
        this.segmentInfos = infos;
        this.termInfosIndexDivisor = termInfosIndexDivisor;
        this.applyAllDeletes = false;
        HashMap<String, Integer> segmentReaders = new HashMap<String, Integer>();
        if (oldReaders != null) {
            for (int i = 0; i < oldReaders.length; ++i) {
                segmentReaders.put(oldReaders[i].getSegmentName(), i);
            }
        }
        SegmentReader[] newReaders = new SegmentReader[infos.size()];
        boolean[] readerShared = new boolean[infos.size()];
        for (int i = infos.size() - 1; i >= 0; --i) {
            IOException ex2222;
            Object var18_22;
            boolean success;
            IOException prior;
            block35: {
                Integer oldReaderIndex = (Integer)segmentReaders.get(infos.info((int)i).name);
                newReaders[i] = oldReaderIndex == null ? null : oldReaders[oldReaderIndex];
                prior = null;
                success = false;
                try {
                    SegmentReader newReader;
                    if (newReaders[i] == null || infos.info(i).getUseCompoundFile() != newReaders[i].getSegmentInfo().getUseCompoundFile()) {
                        assert (!doClone);
                        newReader = SegmentReader.get(readOnly, infos.info(i), termInfosIndexDivisor);
                        readerShared[i] = false;
                        newReaders[i] = newReader;
                    } else {
                        newReader = newReaders[i].reopenSegment(infos.info(i), doClone, readOnly);
                        if (newReader == null) {
                            readerShared[i] = true;
                            newReaders[i].incRef();
                        } else {
                            readerShared[i] = false;
                            newReaders[i] = newReader;
                        }
                    }
                    success = true;
                    var18_22 = null;
                    if (success) break block35;
                    ++i;
                }
                catch (Throwable throwable) {
                    var18_22 = null;
                    if (!success) {
                        ++i;
                        while (i < infos.size()) {
                            block38: {
                                if (newReaders[i] != null) {
                                    try {
                                        if (!readerShared[i]) {
                                            newReaders[i].close();
                                        } else {
                                            newReaders[i].decRef();
                                        }
                                    }
                                    catch (IOException ex2222) {
                                        if (prior != null) break block38;
                                        prior = ex2222;
                                    }
                                }
                            }
                            ++i;
                        }
                    }
                    if (prior != null) {
                        throw prior;
                    }
                    throw throwable;
                }
                while (i < infos.size()) {
                    block36: {
                        if (newReaders[i] != null) {
                            try {
                                if (!readerShared[i]) {
                                    newReaders[i].close();
                                } else {
                                    newReaders[i].decRef();
                                }
                            }
                            catch (IOException ex2222) {
                                if (prior != null) break block36;
                                prior = ex2222;
                            }
                        }
                    }
                    ++i;
                }
            }
            if (prior == null) continue;
            throw prior;
            {
                catch (IOException ex3) {
                    prior = ex3;
                    var18_22 = null;
                    if (!success) {
                        ++i;
                        while (i < infos.size()) {
                            block37: {
                                if (newReaders[i] != null) {
                                    try {
                                        if (!readerShared[i]) {
                                            newReaders[i].close();
                                        } else {
                                            newReaders[i].decRef();
                                        }
                                    }
                                    catch (IOException ex2222) {
                                        if (prior != null) break block37;
                                        prior = ex2222;
                                    }
                                }
                            }
                            ++i;
                        }
                    }
                    if (prior == null) continue;
                    throw prior;
                }
            }
        }
        this.initialize(newReaders);
        if (oldNormsCache != null) {
            for (Map.Entry<String, byte[]> entry : oldNormsCache.entrySet()) {
                String field = entry.getKey();
                if (!this.hasNorms(field)) continue;
                byte[] oldBytes = entry.getValue();
                byte[] bytes = new byte[this.maxDoc()];
                for (int i = 0; i < this.subReaders.length; ++i) {
                    Integer oldReaderIndex = (Integer)segmentReaders.get(this.subReaders[i].getSegmentName());
                    if (oldReaderIndex != null && (oldReaders[oldReaderIndex] == this.subReaders[i] || oldReaders[oldReaderIndex.intValue()].norms.get(field) == this.subReaders[i].norms.get(field))) {
                        System.arraycopy(oldBytes, oldStarts[oldReaderIndex], bytes, this.starts[i], this.starts[i + 1] - this.starts[i]);
                        continue;
                    }
                    this.subReaders[i].norms(field, bytes, this.starts[i]);
                }
                this.normsCache.put(field, bytes);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        if (this.hasChanges) {
            buffer.append("*");
        }
        buffer.append(this.getClass().getSimpleName());
        buffer.append('(');
        String segmentsFile = this.segmentInfos.getSegmentsFileName();
        if (segmentsFile != null) {
            buffer.append(segmentsFile);
        }
        if (this.writer != null) {
            buffer.append(":nrt");
        }
        for (int i = 0; i < this.subReaders.length; ++i) {
            buffer.append(' ');
            buffer.append(this.subReaders[i]);
        }
        buffer.append(')');
        return buffer.toString();
    }

    @Override
    public FieldInfos getFieldInfos() {
        throw new UnsupportedOperationException("call getFieldInfos() on each sub reader, or use ReaderUtil.getMergedFieldInfos, instead");
    }

    private void initialize(SegmentReader[] subReaders) throws IOException {
        this.subReaders = subReaders;
        this.starts = new int[subReaders.length + 1];
        for (int i = 0; i < subReaders.length; ++i) {
            SegmentReader reader = subReaders[i];
            this.starts[i] = this.maxDoc;
            this.maxDoc += reader.maxDoc();
            if (!reader.hasDeletions()) continue;
            this.hasDeletions = true;
        }
        this.starts[subReaders.length] = this.maxDoc;
        if (!this.readOnly) {
            this.maxIndexVersion = SegmentInfos.readCurrentVersion(this.directory);
        }
    }

    @Override
    public final synchronized Object clone() {
        try {
            return this.clone(this.readOnly);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    @Deprecated
    public final synchronized IndexReader clone(boolean openReadOnly) throws CorruptIndexException, IOException {
        DirectoryReader newReader = this.doOpenIfChanged((SegmentInfos)this.segmentInfos.clone(), true, openReadOnly);
        if (this != newReader) {
            newReader.deletionPolicy = this.deletionPolicy;
        }
        newReader.writer = this.writer;
        if (!openReadOnly && this.writeLock != null) {
            assert (this.writer == null);
            newReader.writeLock = this.writeLock;
            newReader.hasChanges = this.hasChanges;
            newReader.hasDeletions = this.hasDeletions;
            this.writeLock = null;
            this.hasChanges = false;
        }
        return newReader;
    }

    @Override
    protected final IndexReader doOpenIfChanged() throws CorruptIndexException, IOException {
        return this.doOpenIfChanged(this.readOnly, null);
    }

    @Override
    @Deprecated
    protected final IndexReader doOpenIfChanged(boolean openReadOnly) throws CorruptIndexException, IOException {
        return this.doOpenIfChanged(openReadOnly, null);
    }

    @Override
    protected final IndexReader doOpenIfChanged(IndexCommit commit) throws CorruptIndexException, IOException {
        return this.doOpenIfChanged(true, commit);
    }

    @Override
    protected final IndexReader doOpenIfChanged(IndexWriter writer, boolean applyAllDeletes) throws CorruptIndexException, IOException {
        if (writer == this.writer && applyAllDeletes == this.applyAllDeletes) {
            return this.doOpenIfChanged();
        }
        return super.doOpenIfChanged(writer, applyAllDeletes);
    }

    private final IndexReader doOpenFromWriter(boolean openReadOnly, IndexCommit commit) throws CorruptIndexException, IOException {
        assert (this.readOnly);
        if (!openReadOnly) {
            throw new IllegalArgumentException("a reader obtained from IndexWriter.getReader() can only be reopened with openReadOnly=true (got false)");
        }
        if (commit != null) {
            throw new IllegalArgumentException("a reader obtained from IndexWriter.getReader() cannot currently accept a commit");
        }
        if (this.writer.nrtIsCurrent(this.segmentInfos)) {
            return null;
        }
        IndexReader reader = this.writer.getReader(this.applyAllDeletes);
        if (reader.getVersion() == this.segmentInfos.getVersion()) {
            reader.decRef();
            return null;
        }
        return reader;
    }

    private IndexReader doOpenIfChanged(boolean openReadOnly, IndexCommit commit) throws CorruptIndexException, IOException {
        this.ensureOpen();
        assert (commit == null || openReadOnly);
        if (this.writer != null) {
            return this.doOpenFromWriter(openReadOnly, commit);
        }
        return this.doOpenNoWriter(openReadOnly, commit);
    }

    private synchronized IndexReader doOpenNoWriter(final boolean openReadOnly, IndexCommit commit) throws CorruptIndexException, IOException {
        if (commit == null) {
            if (this.hasChanges) {
                assert (!this.readOnly);
                assert (this.writeLock != null);
                assert (this.isCurrent());
                if (openReadOnly) {
                    return this.clone(openReadOnly);
                }
                return null;
            }
            if (this.isCurrent()) {
                if (openReadOnly != this.readOnly) {
                    return this.clone(openReadOnly);
                }
                return null;
            }
        } else {
            if (this.directory != commit.getDirectory()) {
                throw new IOException("the specified commit does not match the specified Directory");
            }
            if (this.segmentInfos != null && commit.getSegmentsFileName().equals(this.segmentInfos.getSegmentsFileName())) {
                if (this.readOnly != openReadOnly) {
                    return this.clone(openReadOnly);
                }
                return null;
            }
        }
        return (IndexReader)new SegmentInfos.FindSegmentsFile(this.directory){

            protected Object doBody(String segmentFileName) throws CorruptIndexException, IOException {
                SegmentInfos infos = new SegmentInfos();
                infos.read(this.directory, segmentFileName);
                return DirectoryReader.this.doOpenIfChanged(infos, false, openReadOnly);
            }
        }.run(commit);
    }

    private synchronized DirectoryReader doOpenIfChanged(SegmentInfos infos, boolean doClone, boolean openReadOnly) throws CorruptIndexException, IOException {
        DirectoryReader reader = openReadOnly ? new ReadOnlyDirectoryReader(this.directory, infos, this.subReaders, this.starts, this.normsCache, doClone, this.termInfosIndexDivisor) : new DirectoryReader(this.directory, infos, this.subReaders, this.starts, this.normsCache, false, doClone, this.termInfosIndexDivisor);
        return reader;
    }

    @Override
    public long getVersion() {
        this.ensureOpen();
        return this.segmentInfos.getVersion();
    }

    @Override
    public TermFreqVector[] getTermFreqVectors(int n) throws IOException {
        this.ensureOpen();
        int i = this.readerIndex(n);
        return this.subReaders[i].getTermFreqVectors(n - this.starts[i]);
    }

    @Override
    public TermFreqVector getTermFreqVector(int n, String field) throws IOException {
        this.ensureOpen();
        int i = this.readerIndex(n);
        return this.subReaders[i].getTermFreqVector(n - this.starts[i], field);
    }

    @Override
    public void getTermFreqVector(int docNumber, String field, TermVectorMapper mapper) throws IOException {
        this.ensureOpen();
        int i = this.readerIndex(docNumber);
        this.subReaders[i].getTermFreqVector(docNumber - this.starts[i], field, mapper);
    }

    @Override
    public void getTermFreqVector(int docNumber, TermVectorMapper mapper) throws IOException {
        this.ensureOpen();
        int i = this.readerIndex(docNumber);
        this.subReaders[i].getTermFreqVector(docNumber - this.starts[i], mapper);
    }

    @Override
    @Deprecated
    public boolean isOptimized() {
        this.ensureOpen();
        return this.segmentInfos.size() == 1 && !this.hasDeletions();
    }

    @Override
    public int numDocs() {
        if (this.numDocs == -1) {
            int n = 0;
            for (int i = 0; i < this.subReaders.length; ++i) {
                n += this.subReaders[i].numDocs();
            }
            this.numDocs = n;
        }
        return this.numDocs;
    }

    @Override
    public int maxDoc() {
        return this.maxDoc;
    }

    @Override
    public Document document(int n, FieldSelector fieldSelector) throws CorruptIndexException, IOException {
        this.ensureOpen();
        int i = this.readerIndex(n);
        return this.subReaders[i].document(n - this.starts[i], fieldSelector);
    }

    @Override
    public boolean isDeleted(int n) {
        int i = this.readerIndex(n);
        return this.subReaders[i].isDeleted(n - this.starts[i]);
    }

    @Override
    public boolean hasDeletions() {
        this.ensureOpen();
        return this.hasDeletions;
    }

    @Override
    @Deprecated
    protected void doDelete(int n) throws CorruptIndexException, IOException {
        this.numDocs = -1;
        int i = this.readerIndex(n);
        this.subReaders[i].deleteDocument(n - this.starts[i]);
        this.hasDeletions = true;
    }

    @Override
    @Deprecated
    protected void doUndeleteAll() throws CorruptIndexException, IOException {
        for (int i = 0; i < this.subReaders.length; ++i) {
            this.subReaders[i].undeleteAll();
        }
        this.hasDeletions = false;
        this.numDocs = -1;
    }

    private int readerIndex(int n) {
        return DirectoryReader.readerIndex(n, this.starts, this.subReaders.length);
    }

    static final int readerIndex(int n, int[] starts, int numSubReaders) {
        int lo = 0;
        int hi = numSubReaders - 1;
        while (hi >= lo) {
            int mid = lo + hi >>> 1;
            int midValue = starts[mid];
            if (n < midValue) {
                hi = mid - 1;
                continue;
            }
            if (n > midValue) {
                lo = mid + 1;
                continue;
            }
            while (mid + 1 < numSubReaders && starts[mid + 1] == midValue) {
                ++mid;
            }
            return mid;
        }
        return hi;
    }

    @Override
    public boolean hasNorms(String field) throws IOException {
        this.ensureOpen();
        for (int i = 0; i < this.subReaders.length; ++i) {
            if (!this.subReaders[i].hasNorms(field)) continue;
            return true;
        }
        return false;
    }

    @Override
    public synchronized byte[] norms(String field) throws IOException {
        this.ensureOpen();
        byte[] bytes = this.normsCache.get(field);
        if (bytes != null) {
            return bytes;
        }
        if (!this.hasNorms(field)) {
            return null;
        }
        bytes = new byte[this.maxDoc()];
        for (int i = 0; i < this.subReaders.length; ++i) {
            this.subReaders[i].norms(field, bytes, this.starts[i]);
        }
        this.normsCache.put(field, bytes);
        return bytes;
    }

    @Override
    public synchronized void norms(String field, byte[] result, int offset) throws IOException {
        this.ensureOpen();
        byte[] bytes = this.normsCache.get(field);
        if (bytes == null && !this.hasNorms(field)) {
            Arrays.fill(result, offset, result.length, Similarity.getDefault().encodeNormValue(1.0f));
        } else if (bytes != null) {
            System.arraycopy(bytes, 0, result, offset, this.maxDoc());
        } else {
            for (int i = 0; i < this.subReaders.length; ++i) {
                this.subReaders[i].norms(field, result, offset + this.starts[i]);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Deprecated
    protected void doSetNorm(int n, String field, byte value) throws CorruptIndexException, IOException {
        Map<String, byte[]> map = this.normsCache;
        synchronized (map) {
            this.normsCache.remove(field);
        }
        int i = this.readerIndex(n);
        this.subReaders[i].setNorm(n - this.starts[i], field, value);
    }

    @Override
    public TermEnum terms() throws IOException {
        this.ensureOpen();
        if (this.subReaders.length == 1) {
            return this.subReaders[0].terms();
        }
        return new MultiTermEnum(this, this.subReaders, this.starts, null);
    }

    @Override
    public TermEnum terms(Term term) throws IOException {
        this.ensureOpen();
        if (this.subReaders.length == 1) {
            return this.subReaders[0].terms(term);
        }
        return new MultiTermEnum(this, this.subReaders, this.starts, term);
    }

    @Override
    public int docFreq(Term t) throws IOException {
        this.ensureOpen();
        int total = 0;
        for (int i = 0; i < this.subReaders.length; ++i) {
            total += this.subReaders[i].docFreq(t);
        }
        return total;
    }

    @Override
    public TermDocs termDocs() throws IOException {
        this.ensureOpen();
        if (this.subReaders.length == 1) {
            return this.subReaders[0].termDocs();
        }
        return new MultiTermDocs(this, this.subReaders, this.starts);
    }

    @Override
    public TermDocs termDocs(Term term) throws IOException {
        this.ensureOpen();
        if (this.subReaders.length == 1) {
            return this.subReaders[0].termDocs(term);
        }
        return super.termDocs(term);
    }

    @Override
    public TermPositions termPositions() throws IOException {
        this.ensureOpen();
        if (this.subReaders.length == 1) {
            return this.subReaders[0].termPositions();
        }
        return new MultiTermPositions(this, this.subReaders, this.starts);
    }

    @Override
    @Deprecated
    protected void acquireWriteLock() throws StaleReaderException, CorruptIndexException, LockObtainFailedException, IOException {
        if (this.readOnly) {
            ReadOnlySegmentReader.noWrite();
        }
        if (this.segmentInfos != null) {
            this.ensureOpen();
            if (this.stale) {
                throw new StaleReaderException("IndexReader out of date and no longer valid for delete, undelete, or setNorm operations");
            }
            if (this.writeLock == null) {
                Lock writeLock = this.directory.makeLock("write.lock");
                if (!writeLock.obtain(IndexWriterConfig.WRITE_LOCK_TIMEOUT)) {
                    throw new LockObtainFailedException("Index locked for write: " + writeLock);
                }
                this.writeLock = writeLock;
                if (SegmentInfos.readCurrentVersion(this.directory) > this.maxIndexVersion) {
                    this.stale = true;
                    this.writeLock.release();
                    this.writeLock = null;
                    throw new StaleReaderException("IndexReader out of date and no longer valid for delete, undelete, or setNorm operations");
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Deprecated
    protected void doCommit(Map<String, String> commitUserData) throws IOException {
        if (this.hasChanges) {
            IndexFileDeleter deleter;
            block6: {
                this.segmentInfos.setUserData(commitUserData);
                deleter = new IndexFileDeleter(this.directory, this.deletionPolicy == null ? new KeepOnlyLastCommitDeletionPolicy() : this.deletionPolicy, this.segmentInfos, null, null);
                this.segmentInfos.updateGeneration(deleter.getLastSegmentInfos());
                this.segmentInfos.changed();
                this.startCommit();
                List<SegmentInfo> rollbackSegments = this.segmentInfos.createBackupSegmentInfos(false);
                boolean success = false;
                try {
                    for (int i = 0; i < this.subReaders.length; ++i) {
                        this.subReaders[i].commit();
                    }
                    this.segmentInfos.pruneDeletedSegments();
                    this.directory.sync(this.segmentInfos.files(this.directory, false));
                    this.segmentInfos.commit(this.directory);
                    success = true;
                    Object var7_6 = null;
                    if (success) break block6;
                    this.rollbackCommit();
                }
                catch (Throwable throwable) {
                    Object var7_7 = null;
                    if (!success) {
                        this.rollbackCommit();
                        deleter.refresh();
                        this.segmentInfos.rollbackSegmentInfos(rollbackSegments);
                    }
                    throw throwable;
                }
                deleter.refresh();
                this.segmentInfos.rollbackSegmentInfos(rollbackSegments);
                {
                }
            }
            deleter.checkpoint(this.segmentInfos, true);
            deleter.close();
            this.maxIndexVersion = this.segmentInfos.getVersion();
            if (this.writeLock != null) {
                this.writeLock.release();
                this.writeLock = null;
            }
        }
        this.hasChanges = false;
    }

    void startCommit() {
        this.rollbackHasChanges = this.hasChanges;
        for (int i = 0; i < this.subReaders.length; ++i) {
            this.subReaders[i].startCommit();
        }
    }

    void rollbackCommit() {
        this.hasChanges = this.rollbackHasChanges;
        for (int i = 0; i < this.subReaders.length; ++i) {
            this.subReaders[i].rollbackCommit();
        }
    }

    @Override
    public Map<String, String> getCommitUserData() {
        this.ensureOpen();
        return this.segmentInfos.getUserData();
    }

    @Override
    public boolean isCurrent() throws CorruptIndexException, IOException {
        this.ensureOpen();
        if (this.writer == null || this.writer.isClosed()) {
            return SegmentInfos.readCurrentVersion(this.directory) == this.segmentInfos.getVersion();
        }
        return this.writer.nrtIsCurrent(this.segmentInfos);
    }

    @Override
    protected synchronized void doClose() throws IOException {
        IOException ioe = null;
        this.normsCache = null;
        for (int i = 0; i < this.subReaders.length; ++i) {
            try {
                this.subReaders[i].decRef();
                continue;
            }
            catch (IOException e) {
                if (ioe != null) continue;
                ioe = e;
            }
        }
        if (this.writer != null) {
            this.writer.deletePendingFiles();
        }
        if (ioe != null) {
            throw ioe;
        }
    }

    @Override
    public IndexReader[] getSequentialSubReaders() {
        return this.subReaders;
    }

    @Override
    public Directory directory() {
        return this.directory;
    }

    @Override
    public int getTermInfosIndexDivisor() {
        this.ensureOpen();
        return this.termInfosIndexDivisor;
    }

    @Override
    public IndexCommit getIndexCommit() throws IOException {
        this.ensureOpen();
        return new ReaderCommit(this.segmentInfos, this.directory);
    }

    public static Collection<IndexCommit> listCommits(Directory dir) throws IOException {
        String[] files = dir.listAll();
        ArrayList<IndexCommit> commits = new ArrayList<IndexCommit>();
        SegmentInfos latest = new SegmentInfos();
        latest.read(dir);
        long currentGen = latest.getGeneration();
        commits.add(new ReaderCommit(latest, dir));
        for (int i = 0; i < files.length; ++i) {
            String fileName = files[i];
            if (!fileName.startsWith("segments") || fileName.equals("segments.gen") || SegmentInfos.generationFromSegmentsFileName(fileName) >= currentGen) continue;
            SegmentInfos sis = new SegmentInfos();
            try {
                sis.read(dir, fileName);
            }
            catch (FileNotFoundException fnfe) {
                sis = null;
            }
            if (sis == null) continue;
            commits.add(new ReaderCommit(sis, dir));
        }
        Collections.sort(commits);
        return commits;
    }

    static class MultiTermPositions
    extends MultiTermDocs
    implements TermPositions {
        public MultiTermPositions(IndexReader topReader, IndexReader[] r, int[] s) {
            super(topReader, r, s);
        }

        protected TermDocs termDocs(IndexReader reader) throws IOException {
            return reader.termPositions();
        }

        public int nextPosition() throws IOException {
            return ((TermPositions)this.current).nextPosition();
        }

        public int getPayloadLength() {
            return ((TermPositions)this.current).getPayloadLength();
        }

        public byte[] getPayload(byte[] data, int offset) throws IOException {
            return ((TermPositions)this.current).getPayload(data, offset);
        }

        public boolean isPayloadAvailable() {
            return ((TermPositions)this.current).isPayloadAvailable();
        }
    }

    static class MultiTermDocs
    implements TermDocs {
        IndexReader topReader;
        protected IndexReader[] readers;
        protected int[] starts;
        protected Term term;
        protected int base = 0;
        protected int pointer = 0;
        private TermDocs[] readerTermDocs;
        protected TermDocs current;
        private MultiTermEnum tenum;
        int matchingSegmentPos;
        SegmentMergeInfo smi;

        public MultiTermDocs(IndexReader topReader, IndexReader[] r, int[] s) {
            this.topReader = topReader;
            this.readers = r;
            this.starts = s;
            this.readerTermDocs = new TermDocs[r.length];
        }

        public int doc() {
            return this.base + this.current.doc();
        }

        public int freq() {
            return this.current.freq();
        }

        public void seek(Term term) {
            this.term = term;
            this.base = 0;
            this.pointer = 0;
            this.current = null;
            this.tenum = null;
            this.smi = null;
            this.matchingSegmentPos = 0;
        }

        public void seek(TermEnum termEnum) throws IOException {
            this.seek(termEnum.term());
            if (termEnum instanceof MultiTermEnum) {
                this.tenum = (MultiTermEnum)termEnum;
                if (this.topReader != this.tenum.topReader) {
                    this.tenum = null;
                }
            }
        }

        public boolean next() throws IOException {
            while (true) {
                if (this.current != null && this.current.next()) {
                    return true;
                }
                if (this.pointer >= this.readers.length) break;
                if (this.tenum != null) {
                    this.smi = this.tenum.matchingSegments[this.matchingSegmentPos++];
                    if (this.smi == null) {
                        this.pointer = this.readers.length;
                        return false;
                    }
                    this.pointer = this.smi.ord;
                }
                this.base = this.starts[this.pointer];
                this.current = this.termDocs(this.pointer++);
            }
            return false;
        }

        public int read(int[] docs, int[] freqs) throws IOException {
            int end;
            while (true) {
                if (this.current == null) {
                    if (this.pointer < this.readers.length) {
                        if (this.tenum != null) {
                            this.smi = this.tenum.matchingSegments[this.matchingSegmentPos++];
                            if (this.smi == null) {
                                this.pointer = this.readers.length;
                                return 0;
                            }
                            this.pointer = this.smi.ord;
                        }
                        this.base = this.starts[this.pointer];
                        this.current = this.termDocs(this.pointer++);
                        continue;
                    }
                    return 0;
                }
                end = this.current.read(docs, freqs);
                if (end != 0) break;
                this.current = null;
            }
            int b = this.base;
            int i = 0;
            while (i < end) {
                int n = i++;
                docs[n] = docs[n] + b;
            }
            return end;
        }

        public boolean skipTo(int target) throws IOException {
            while (true) {
                if (this.current != null && this.current.skipTo(target - this.base)) {
                    return true;
                }
                if (this.pointer >= this.readers.length) break;
                if (this.tenum != null) {
                    SegmentMergeInfo smi;
                    if ((smi = this.tenum.matchingSegments[this.matchingSegmentPos++]) == null) {
                        this.pointer = this.readers.length;
                        return false;
                    }
                    this.pointer = smi.ord;
                }
                this.base = this.starts[this.pointer];
                this.current = this.termDocs(this.pointer++);
            }
            return false;
        }

        private TermDocs termDocs(int i) throws IOException {
            TermDocs result = this.readerTermDocs[i];
            if (result == null) {
                result = this.readerTermDocs[i] = this.termDocs(this.readers[i]);
            }
            if (this.smi != null) {
                assert (this.smi.ord == i);
                assert (this.smi.termEnum.term().equals(this.term));
                result.seek(this.smi.termEnum);
            } else {
                result.seek(this.term);
            }
            return result;
        }

        protected TermDocs termDocs(IndexReader reader) throws IOException {
            return this.term == null ? reader.termDocs(null) : reader.termDocs();
        }

        public void close() throws IOException {
            for (int i = 0; i < this.readerTermDocs.length; ++i) {
                if (this.readerTermDocs[i] == null) continue;
                this.readerTermDocs[i].close();
            }
        }
    }

    static class MultiTermEnum
    extends TermEnum {
        IndexReader topReader;
        private SegmentMergeQueue queue;
        private Term term;
        private int docFreq;
        final SegmentMergeInfo[] matchingSegments;

        public MultiTermEnum(IndexReader topReader, IndexReader[] readers, int[] starts, Term t) throws IOException {
            this.topReader = topReader;
            this.queue = new SegmentMergeQueue(readers.length);
            this.matchingSegments = new SegmentMergeInfo[readers.length + 1];
            for (int i = 0; i < readers.length; ++i) {
                IndexReader reader = readers[i];
                TermEnum termEnum = t != null ? reader.terms(t) : reader.terms();
                SegmentMergeInfo smi = new SegmentMergeInfo(starts[i], termEnum, reader);
                smi.ord = i;
                if (t == null ? smi.next() : termEnum.term() != null) {
                    this.queue.add(smi);
                    continue;
                }
                smi.close();
            }
            if (t != null && this.queue.size() > 0) {
                this.next();
            }
        }

        public boolean next() throws IOException {
            SegmentMergeInfo smi;
            for (int i = 0; i < this.matchingSegments.length && (smi = this.matchingSegments[i]) != null; ++i) {
                if (smi.next()) {
                    this.queue.add(smi);
                    continue;
                }
                smi.close();
            }
            int numMatchingSegments = 0;
            this.matchingSegments[0] = null;
            SegmentMergeInfo top = (SegmentMergeInfo)this.queue.top();
            if (top == null) {
                this.term = null;
                return false;
            }
            this.term = top.term;
            this.docFreq = 0;
            while (top != null && this.term.compareTo(top.term) == 0) {
                this.matchingSegments[numMatchingSegments++] = top;
                this.queue.pop();
                this.docFreq += top.termEnum.docFreq();
                top = (SegmentMergeInfo)this.queue.top();
            }
            this.matchingSegments[numMatchingSegments] = null;
            return true;
        }

        public Term term() {
            return this.term;
        }

        public int docFreq() {
            return this.docFreq;
        }

        public void close() throws IOException {
            this.queue.close();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class ReaderCommit
    extends IndexCommit {
        private String segmentsFileName;
        Collection<String> files;
        Directory dir;
        long generation;
        long version;
        final Map<String, String> userData;
        private final int segmentCount;

        ReaderCommit(SegmentInfos infos, Directory dir) throws IOException {
            this.segmentsFileName = infos.getSegmentsFileName();
            this.dir = dir;
            this.userData = infos.getUserData();
            this.files = Collections.unmodifiableCollection(infos.files(dir, true));
            this.version = infos.getVersion();
            this.generation = infos.getGeneration();
            this.segmentCount = infos.size();
        }

        public String toString() {
            return "DirectoryReader.ReaderCommit(" + this.segmentsFileName + ")";
        }

        @Override
        public int getSegmentCount() {
            return this.segmentCount;
        }

        @Override
        public String getSegmentsFileName() {
            return this.segmentsFileName;
        }

        @Override
        public Collection<String> getFileNames() {
            return this.files;
        }

        @Override
        public Directory getDirectory() {
            return this.dir;
        }

        @Override
        public long getVersion() {
            return this.version;
        }

        @Override
        public long getGeneration() {
            return this.generation;
        }

        @Override
        public boolean isDeleted() {
            return false;
        }

        @Override
        public Map<String, String> getUserData() {
            return this.userData;
        }

        @Override
        public void delete() {
            throw new UnsupportedOperationException("This IndexCommit does not support deletions");
        }
    }
}

