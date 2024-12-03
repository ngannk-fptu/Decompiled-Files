/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.document.Document;
import com.atlassian.lucene36.document.FieldSelector;
import com.atlassian.lucene36.index.AllTermDocs;
import com.atlassian.lucene36.index.CorruptIndexException;
import com.atlassian.lucene36.index.DirectoryReader;
import com.atlassian.lucene36.index.FieldInfo;
import com.atlassian.lucene36.index.FieldInfos;
import com.atlassian.lucene36.index.FieldsReader;
import com.atlassian.lucene36.index.IndexFileNames;
import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.ReadOnlySegmentReader;
import com.atlassian.lucene36.index.SegmentCoreReaders;
import com.atlassian.lucene36.index.SegmentInfo;
import com.atlassian.lucene36.index.SegmentNorms;
import com.atlassian.lucene36.index.SegmentTermDocs;
import com.atlassian.lucene36.index.SegmentTermPositions;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.index.TermDocs;
import com.atlassian.lucene36.index.TermEnum;
import com.atlassian.lucene36.index.TermFreqVector;
import com.atlassian.lucene36.index.TermInfo;
import com.atlassian.lucene36.index.TermPositions;
import com.atlassian.lucene36.index.TermVectorMapper;
import com.atlassian.lucene36.index.TermVectorsReader;
import com.atlassian.lucene36.search.Similarity;
import com.atlassian.lucene36.store.Directory;
import com.atlassian.lucene36.store.IndexInput;
import com.atlassian.lucene36.util.BitVector;
import com.atlassian.lucene36.util.CloseableThreadLocal;
import com.atlassian.lucene36.util.StringHelper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SegmentReader
extends IndexReader
implements Cloneable {
    @Deprecated
    protected boolean readOnly;
    private SegmentInfo si;
    private int readBufferSize;
    CloseableThreadLocal<FieldsReader> fieldsReaderLocal = new FieldsReaderLocal();
    CloseableThreadLocal<TermVectorsReader> termVectorsLocal = new CloseableThreadLocal();
    BitVector deletedDocs = null;
    AtomicInteger deletedDocsRef = null;
    private boolean deletedDocsDirty = false;
    private boolean normsDirty = false;
    private int pendingDeleteCount;
    private boolean rollbackHasChanges = false;
    private boolean rollbackDeletedDocsDirty = false;
    private boolean rollbackNormsDirty = false;
    private SegmentInfo rollbackSegmentInfo;
    private int rollbackPendingDeleteCount;
    IndexInput singleNormStream;
    AtomicInteger singleNormRef;
    SegmentCoreReaders core;
    Map<String, SegmentNorms> norms = new HashMap<String, SegmentNorms>();

    public static SegmentReader get(boolean readOnly, SegmentInfo si, int termInfosIndexDivisor) throws CorruptIndexException, IOException {
        return SegmentReader.get(readOnly, si.dir, si, 1024, true, termInfosIndexDivisor);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static SegmentReader get(boolean readOnly, Directory dir, SegmentInfo si, int readBufferSize, boolean doOpenStores, int termInfosIndexDivisor) throws CorruptIndexException, IOException {
        SegmentReader instance = readOnly ? new ReadOnlySegmentReader() : new SegmentReader();
        instance.readOnly = readOnly;
        instance.si = si;
        instance.readBufferSize = readBufferSize;
        boolean success = false;
        try {
            instance.core = new SegmentCoreReaders(instance, dir, si, readBufferSize, termInfosIndexDivisor);
            if (doOpenStores) {
                instance.core.openDocStores(si);
            }
            instance.loadDeletedDocs();
            instance.openNorms(instance.core.cfsDir, readBufferSize);
            success = true;
        }
        finally {
            if (!success) {
                instance.doClose();
            }
        }
        return instance;
    }

    void openDocStores() throws IOException {
        this.core.openDocStores(this.si);
    }

    private boolean checkDeletedCounts() throws IOException {
        int recomputedCount = this.deletedDocs.getRecomputedCount();
        assert (this.deletedDocs.count() == recomputedCount) : "deleted count=" + this.deletedDocs.count() + " vs recomputed count=" + recomputedCount;
        assert (this.si.getDelCount() == recomputedCount) : "delete count mismatch: info=" + this.si.getDelCount() + " vs BitVector=" + recomputedCount;
        assert (this.si.getDelCount() <= this.maxDoc()) : "delete count mismatch: " + recomputedCount + ") exceeds max doc (" + this.maxDoc() + ") for segment " + this.si.name;
        return true;
    }

    private void loadDeletedDocs() throws IOException {
        if (SegmentReader.hasDeletions(this.si)) {
            this.deletedDocs = new BitVector(this.directory(), this.si.getDelFileName());
            this.deletedDocsRef = new AtomicInteger(1);
            assert (this.checkDeletedCounts());
            if (this.deletedDocs.size() != this.si.docCount) {
                throw new CorruptIndexException("document count mismatch: deleted docs count " + this.deletedDocs.size() + " vs segment doc count " + this.si.docCount + " segment=" + this.si.name);
            }
        } else assert (this.si.getDelCount() == 0);
    }

    @Deprecated
    protected byte[] cloneNormBytes(byte[] bytes) {
        byte[] cloneBytes = new byte[bytes.length];
        System.arraycopy(bytes, 0, cloneBytes, 0, bytes.length);
        return cloneBytes;
    }

    @Deprecated
    protected BitVector cloneDeletedDocs(BitVector bv) {
        this.ensureOpen();
        return (BitVector)bv.clone();
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
        return this.reopenSegment(this.si, true, openReadOnly);
    }

    @Override
    protected synchronized IndexReader doOpenIfChanged() throws CorruptIndexException, IOException {
        return this.reopenSegment(this.si, false, this.readOnly);
    }

    @Override
    @Deprecated
    protected synchronized IndexReader doOpenIfChanged(boolean openReadOnly) throws CorruptIndexException, IOException {
        return this.reopenSegment(this.si, false, openReadOnly);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    synchronized SegmentReader reopenSegment(SegmentInfo si, boolean doClone, boolean openReadOnly) throws CorruptIndexException, IOException {
        this.ensureOpen();
        boolean deletionsUpToDate = this.si.hasDeletions() == si.hasDeletions() && (!si.hasDeletions() || this.si.getDelFileName().equals(si.getDelFileName()));
        boolean normsUpToDate = true;
        boolean[] fieldNormsChanged = new boolean[this.core.fieldInfos.size()];
        int fieldCount = this.core.fieldInfos.size();
        for (int i = 0; i < fieldCount; ++i) {
            if (this.si.getNormFileName(i).equals(si.getNormFileName(i))) continue;
            normsUpToDate = false;
            fieldNormsChanged[i] = true;
        }
        if (normsUpToDate && deletionsUpToDate && !doClone && openReadOnly && this.readOnly) {
            return null;
        }
        assert (!doClone || normsUpToDate && deletionsUpToDate);
        SegmentReader clone = openReadOnly ? new ReadOnlySegmentReader() : new SegmentReader();
        boolean success = false;
        try {
            this.core.incRef();
            clone.core = this.core;
            clone.readOnly = openReadOnly;
            clone.si = si;
            clone.readBufferSize = this.readBufferSize;
            clone.pendingDeleteCount = this.pendingDeleteCount;
            if (!openReadOnly && this.hasChanges) {
                clone.deletedDocsDirty = this.deletedDocsDirty;
                clone.normsDirty = this.normsDirty;
                clone.hasChanges = this.hasChanges;
                this.hasChanges = false;
            }
            if (doClone) {
                if (this.deletedDocs != null) {
                    this.deletedDocsRef.incrementAndGet();
                    clone.deletedDocs = this.deletedDocs;
                    clone.deletedDocsRef = this.deletedDocsRef;
                }
            } else if (!deletionsUpToDate) {
                assert (clone.deletedDocs == null);
                clone.loadDeletedDocs();
            } else if (this.deletedDocs != null) {
                this.deletedDocsRef.incrementAndGet();
                clone.deletedDocs = this.deletedDocs;
                clone.deletedDocsRef = this.deletedDocsRef;
            }
            clone.norms = new HashMap<String, SegmentNorms>();
            for (int i = 0; i < fieldNormsChanged.length; ++i) {
                String curField;
                SegmentNorms norm;
                if (!doClone && fieldNormsChanged[i] || (norm = this.norms.get(curField = this.core.fieldInfos.fieldInfo((int)i).name)) == null) continue;
                clone.norms.put(curField, (SegmentNorms)norm.clone());
            }
            clone.openNorms(si.getUseCompoundFile() ? this.core.getCFSReader() : this.directory(), this.readBufferSize);
            success = true;
        }
        finally {
            if (!success) {
                clone.decRef();
            }
        }
        return clone;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Deprecated
    protected void doCommit(Map<String, String> commitUserData) throws IOException {
        if (this.hasChanges) {
            this.startCommit();
            boolean success = false;
            try {
                this.commitChanges(commitUserData);
                success = true;
            }
            finally {
                if (!success) {
                    this.rollbackCommit();
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private synchronized void commitChanges(Map<String, String> commitUserData) throws IOException {
        if (this.deletedDocsDirty) {
            this.si.advanceDelGen();
            assert (this.deletedDocs.size() == this.si.docCount);
            String delFileName = this.si.getDelFileName();
            boolean success = false;
            try {
                this.deletedDocs.write(this.directory(), delFileName);
                success = true;
            }
            finally {
                if (!success) {
                    try {
                        this.directory().deleteFile(delFileName);
                    }
                    catch (Throwable t) {}
                }
            }
            this.si.setDelCount(this.si.getDelCount() + this.pendingDeleteCount);
            this.pendingDeleteCount = 0;
            assert (this.deletedDocs.count() == this.si.getDelCount()) : "delete count mismatch during commit: info=" + this.si.getDelCount() + " vs BitVector=" + this.deletedDocs.count();
        } else assert (this.pendingDeleteCount == 0);
        if (this.normsDirty) {
            this.si.setNumFields(this.core.fieldInfos.size());
            for (SegmentNorms norm : this.norms.values()) {
                if (!norm.dirty) continue;
                norm.reWrite(this.si);
            }
        }
        this.deletedDocsDirty = false;
        this.normsDirty = false;
        this.hasChanges = false;
    }

    FieldsReader getFieldsReader() {
        return this.fieldsReaderLocal.get();
    }

    @Override
    protected void doClose() throws IOException {
        this.termVectorsLocal.close();
        this.fieldsReaderLocal.close();
        if (this.deletedDocs != null) {
            this.deletedDocsRef.decrementAndGet();
            this.deletedDocs = null;
        }
        for (SegmentNorms norm : this.norms.values()) {
            norm.decRef();
        }
        if (this.core != null) {
            this.core.decRef();
        }
    }

    static boolean hasDeletions(SegmentInfo si) throws IOException {
        return si.hasDeletions();
    }

    @Override
    public boolean hasDeletions() {
        return this.deletedDocs != null;
    }

    static boolean usesCompoundFile(SegmentInfo si) throws IOException {
        return si.getUseCompoundFile();
    }

    static boolean hasSeparateNorms(SegmentInfo si) throws IOException {
        return si.hasSeparateNorms();
    }

    @Override
    @Deprecated
    protected void doDelete(int docNum) {
        if (this.deletedDocs == null) {
            this.deletedDocs = new BitVector(this.maxDoc());
            this.deletedDocsRef = new AtomicInteger(1);
        }
        if (this.deletedDocsRef.get() > 1) {
            AtomicInteger oldRef = this.deletedDocsRef;
            this.deletedDocs = this.cloneDeletedDocs(this.deletedDocs);
            this.deletedDocsRef = new AtomicInteger(1);
            oldRef.decrementAndGet();
        }
        this.deletedDocsDirty = true;
        if (!this.deletedDocs.getAndSet(docNum)) {
            ++this.pendingDeleteCount;
        }
    }

    @Override
    @Deprecated
    protected void doUndeleteAll() {
        this.deletedDocsDirty = false;
        if (this.deletedDocs != null) {
            assert (this.deletedDocsRef != null);
            this.deletedDocsRef.decrementAndGet();
            this.deletedDocs = null;
            this.deletedDocsRef = null;
            this.pendingDeleteCount = 0;
            this.si.clearDelGen();
            this.si.setDelCount(0);
        } else {
            assert (this.deletedDocsRef == null);
            assert (this.pendingDeleteCount == 0);
        }
    }

    List<String> files() throws IOException {
        return new ArrayList<String>(this.si.files());
    }

    @Override
    public TermEnum terms() {
        this.ensureOpen();
        return this.core.getTermsReader().terms();
    }

    @Override
    public TermEnum terms(Term t) throws IOException {
        this.ensureOpen();
        return this.core.getTermsReader().terms(t);
    }

    @Override
    public FieldInfos getFieldInfos() {
        return this.core.fieldInfos;
    }

    FieldInfos fieldInfos() {
        return this.core.fieldInfos;
    }

    @Override
    public Document document(int n, FieldSelector fieldSelector) throws CorruptIndexException, IOException {
        this.ensureOpen();
        if (n < 0 || n >= this.maxDoc()) {
            throw new IllegalArgumentException("docID must be >= 0 and < maxDoc=" + this.maxDoc() + " (got docID=" + n + ")");
        }
        return this.getFieldsReader().doc(n, fieldSelector);
    }

    @Override
    public synchronized boolean isDeleted(int n) {
        return this.deletedDocs != null && this.deletedDocs.get(n);
    }

    @Override
    public TermDocs termDocs(Term term) throws IOException {
        if (term == null) {
            return new AllTermDocs(this);
        }
        return super.termDocs(term);
    }

    public TermDocs rawTermDocs(Term term) throws IOException {
        if (term == null) {
            throw new IllegalArgumentException("term must not be null");
        }
        SegmentTermDocs td = new SegmentTermDocs(this, true);
        td.seek(term);
        return td;
    }

    @Override
    public TermDocs termDocs() throws IOException {
        this.ensureOpen();
        return new SegmentTermDocs(this);
    }

    @Override
    public TermPositions termPositions() throws IOException {
        this.ensureOpen();
        return new SegmentTermPositions(this);
    }

    @Override
    public int docFreq(Term t) throws IOException {
        this.ensureOpen();
        TermInfo ti = this.core.getTermsReader().get(t);
        if (ti != null) {
            return ti.docFreq;
        }
        return 0;
    }

    @Override
    public int numDocs() {
        int n = this.maxDoc();
        if (this.deletedDocs != null) {
            n -= this.deletedDocs.count();
        }
        return n;
    }

    @Override
    public int maxDoc() {
        return this.si.docCount;
    }

    @Override
    public boolean hasNorms(String field) {
        this.ensureOpen();
        return this.norms.containsKey(field);
    }

    @Override
    public byte[] norms(String field) throws IOException {
        this.ensureOpen();
        SegmentNorms norm = this.norms.get(field);
        if (norm == null) {
            return null;
        }
        return norm.bytes();
    }

    @Override
    @Deprecated
    protected void doSetNorm(int doc, String field, byte value) throws IOException {
        SegmentNorms norm = this.norms.get(field);
        if (norm == null) {
            throw new IllegalStateException("Cannot setNorm for field " + field + ": norms were omitted");
        }
        this.normsDirty = true;
        norm.copyOnWrite()[doc] = value;
    }

    @Override
    public synchronized void norms(String field, byte[] bytes, int offset) throws IOException {
        this.ensureOpen();
        SegmentNorms norm = this.norms.get(field);
        if (norm == null) {
            Arrays.fill(bytes, offset, bytes.length, Similarity.getDefault().encodeNormValue(1.0f));
            return;
        }
        norm.bytes(bytes, offset, this.maxDoc());
    }

    int getPostingsSkipInterval() {
        return this.core.getTermsReader().getSkipInterval();
    }

    private void openNorms(Directory cfsDir, int readBufferSize) throws IOException {
        boolean normsInitiallyEmpty = this.norms.isEmpty();
        long nextNormSeek = SegmentNorms.NORMS_HEADER.length;
        int maxDoc = this.maxDoc();
        for (int i = 0; i < this.core.fieldInfos.size(); ++i) {
            long normSeek;
            FieldInfo fi = this.core.fieldInfos.fieldInfo(i);
            if (this.norms.containsKey(fi.name) || !fi.isIndexed || fi.omitNorms) continue;
            Directory d = this.directory();
            String fileName = this.si.getNormFileName(fi.number);
            if (!this.si.hasSeparateNorms(fi.number)) {
                d = cfsDir;
            }
            boolean singleNormFile = IndexFileNames.matchesExtension(fileName, "nrm");
            IndexInput normInput = null;
            if (singleNormFile) {
                normSeek = nextNormSeek;
                if (this.singleNormStream == null) {
                    this.singleNormStream = d.openInput(fileName, readBufferSize);
                    this.singleNormRef = new AtomicInteger(1);
                } else {
                    this.singleNormRef.incrementAndGet();
                }
                normInput = this.singleNormStream;
            } else {
                normInput = d.openInput(fileName);
                String version = this.si.getVersion();
                boolean isUnversioned = (version == null || StringHelper.getVersionComparator().compare(version, "3.2") < 0) && normInput.length() == (long)this.maxDoc();
                normSeek = isUnversioned ? 0L : (long)SegmentNorms.NORMS_HEADER.length;
            }
            this.norms.put(fi.name, new SegmentNorms(normInput, fi.number, normSeek, this));
            nextNormSeek += (long)maxDoc;
        }
        assert (this.singleNormStream == null || !normsInitiallyEmpty || nextNormSeek == this.singleNormStream.length());
    }

    boolean termsIndexLoaded() {
        return this.core.termsIndexIsLoaded();
    }

    void loadTermsIndex(int termsIndexDivisor) throws IOException {
        this.core.loadTermsIndex(this.si, termsIndexDivisor);
    }

    boolean normsClosed() {
        if (this.singleNormStream != null) {
            return false;
        }
        for (SegmentNorms norm : this.norms.values()) {
            if (norm.refCount <= 0) continue;
            return false;
        }
        return true;
    }

    boolean normsClosed(String field) {
        return this.norms.get((Object)field).refCount == 0;
    }

    TermVectorsReader getTermVectorsReader() {
        TermVectorsReader tvReader = this.termVectorsLocal.get();
        if (tvReader == null) {
            TermVectorsReader orig = this.core.getTermVectorsReaderOrig();
            if (orig == null) {
                return null;
            }
            try {
                tvReader = (TermVectorsReader)orig.clone();
            }
            catch (CloneNotSupportedException cnse) {
                return null;
            }
            this.termVectorsLocal.set(tvReader);
        }
        return tvReader;
    }

    TermVectorsReader getTermVectorsReaderOrig() {
        return this.core.getTermVectorsReaderOrig();
    }

    @Override
    public TermFreqVector getTermFreqVector(int docNumber, String field) throws IOException {
        this.ensureOpen();
        FieldInfo fi = this.core.fieldInfos.fieldInfo(field);
        if (fi == null || !fi.storeTermVector) {
            return null;
        }
        TermVectorsReader termVectorsReader = this.getTermVectorsReader();
        if (termVectorsReader == null) {
            return null;
        }
        return termVectorsReader.get(docNumber, field);
    }

    @Override
    public void getTermFreqVector(int docNumber, String field, TermVectorMapper mapper) throws IOException {
        this.ensureOpen();
        FieldInfo fi = this.core.fieldInfos.fieldInfo(field);
        if (fi == null || !fi.storeTermVector) {
            return;
        }
        TermVectorsReader termVectorsReader = this.getTermVectorsReader();
        if (termVectorsReader == null) {
            return;
        }
        termVectorsReader.get(docNumber, field, mapper);
    }

    @Override
    public void getTermFreqVector(int docNumber, TermVectorMapper mapper) throws IOException {
        this.ensureOpen();
        TermVectorsReader termVectorsReader = this.getTermVectorsReader();
        if (termVectorsReader == null) {
            return;
        }
        termVectorsReader.get(docNumber, mapper);
    }

    @Override
    public TermFreqVector[] getTermFreqVectors(int docNumber) throws IOException {
        this.ensureOpen();
        TermVectorsReader termVectorsReader = this.getTermVectorsReader();
        if (termVectorsReader == null) {
            return null;
        }
        return termVectorsReader.get(docNumber);
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        if (this.hasChanges) {
            buffer.append('*');
        }
        buffer.append(this.si.toString(this.core.dir, this.pendingDeleteCount));
        return buffer.toString();
    }

    public String getSegmentName() {
        return this.core.segment;
    }

    SegmentInfo getSegmentInfo() {
        return this.si;
    }

    void setSegmentInfo(SegmentInfo info) {
        this.si = info;
    }

    void startCommit() {
        this.rollbackSegmentInfo = (SegmentInfo)this.si.clone();
        this.rollbackHasChanges = this.hasChanges;
        this.rollbackDeletedDocsDirty = this.deletedDocsDirty;
        this.rollbackNormsDirty = this.normsDirty;
        this.rollbackPendingDeleteCount = this.pendingDeleteCount;
        for (SegmentNorms norm : this.norms.values()) {
            norm.rollbackDirty = norm.dirty;
        }
    }

    void rollbackCommit() {
        this.si.reset(this.rollbackSegmentInfo);
        this.hasChanges = this.rollbackHasChanges;
        this.deletedDocsDirty = this.rollbackDeletedDocsDirty;
        this.normsDirty = this.rollbackNormsDirty;
        this.pendingDeleteCount = this.rollbackPendingDeleteCount;
        for (SegmentNorms norm : this.norms.values()) {
            norm.dirty = norm.rollbackDirty;
        }
    }

    @Override
    public Directory directory() {
        return this.core.dir;
    }

    @Override
    public final Object getCoreCacheKey() {
        return this.core.freqStream;
    }

    @Override
    public Object getDeletesCacheKey() {
        return this.deletedDocs;
    }

    @Override
    public long getUniqueTermCount() {
        return this.core.getTermsReader().size();
    }

    @Deprecated
    static SegmentReader getOnlySegmentReader(Directory dir) throws IOException {
        return SegmentReader.getOnlySegmentReader(IndexReader.open(dir, false));
    }

    static SegmentReader getOnlySegmentReader(IndexReader reader) {
        if (reader instanceof SegmentReader) {
            return (SegmentReader)reader;
        }
        if (reader instanceof DirectoryReader) {
            IndexReader[] subReaders = reader.getSequentialSubReaders();
            if (subReaders.length != 1) {
                throw new IllegalArgumentException(reader + " has " + subReaders.length + " segments instead of exactly one");
            }
            return (SegmentReader)subReaders[0];
        }
        throw new IllegalArgumentException(reader + " is not a SegmentReader or a single-segment DirectoryReader");
    }

    @Override
    public int getTermInfosIndexDivisor() {
        return this.core.termsIndexDivisor;
    }

    public void addCoreClosedListener(CoreClosedListener listener) {
        this.ensureOpen();
        this.core.addCoreClosedListener(listener);
    }

    public void removeCoreClosedListener(CoreClosedListener listener) {
        this.ensureOpen();
        this.core.removeCoreClosedListener(listener);
    }

    public static interface CoreClosedListener {
        public void onClose(SegmentReader var1);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private class FieldsReaderLocal
    extends CloseableThreadLocal<FieldsReader> {
        private FieldsReaderLocal() {
        }

        @Override
        protected FieldsReader initialValue() {
            return (FieldsReader)SegmentReader.this.core.getFieldsReaderOrig().clone();
        }
    }
}

