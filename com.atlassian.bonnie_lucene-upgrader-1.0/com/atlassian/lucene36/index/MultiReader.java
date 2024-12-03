/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.document.Document;
import com.atlassian.lucene36.document.FieldSelector;
import com.atlassian.lucene36.index.CorruptIndexException;
import com.atlassian.lucene36.index.DirectoryReader;
import com.atlassian.lucene36.index.FieldInfos;
import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.index.TermDocs;
import com.atlassian.lucene36.index.TermEnum;
import com.atlassian.lucene36.index.TermFreqVector;
import com.atlassian.lucene36.index.TermPositions;
import com.atlassian.lucene36.index.TermVectorMapper;
import com.atlassian.lucene36.search.Similarity;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MultiReader
extends IndexReader
implements Cloneable {
    protected final IndexReader[] subReaders;
    protected final int[] starts;
    private final boolean[] decrefOnClose;
    private final Map<String, byte[]> normsCache = new HashMap<String, byte[]>();
    private final int maxDoc;
    private int numDocs = -1;
    private boolean hasDeletions = false;

    public MultiReader(IndexReader ... subReaders) {
        this(subReaders, true);
    }

    public MultiReader(IndexReader[] subReaders, boolean closeSubReaders) {
        this((IndexReader[])subReaders.clone(), new boolean[subReaders.length]);
        for (int i = 0; i < subReaders.length; ++i) {
            if (!closeSubReaders) {
                subReaders[i].incRef();
                this.decrefOnClose[i] = true;
                continue;
            }
            this.decrefOnClose[i] = false;
        }
    }

    private MultiReader(IndexReader[] subReaders, boolean[] decrefOnClose) {
        this.subReaders = subReaders;
        this.decrefOnClose = decrefOnClose;
        this.starts = new int[subReaders.length + 1];
        int maxDoc = 0;
        for (int i = 0; i < subReaders.length; ++i) {
            IndexReader reader = subReaders[i];
            this.starts[i] = maxDoc;
            maxDoc += reader.maxDoc();
            if (!reader.hasDeletions()) continue;
            this.hasDeletions = true;
        }
        int n = maxDoc;
        this.starts[subReaders.length] = n;
        this.maxDoc = n;
    }

    @Override
    public FieldInfos getFieldInfos() {
        throw new UnsupportedOperationException("call getFieldInfos() on each sub reader, or use ReaderUtil.getMergedFieldInfos, instead");
    }

    @Override
    protected synchronized IndexReader doOpenIfChanged() throws CorruptIndexException, IOException {
        return this.doReopen(false);
    }

    @Override
    @Deprecated
    protected IndexReader doOpenIfChanged(boolean openReadOnly) throws CorruptIndexException, IOException {
        throw new UnsupportedOperationException("MultiReader does not support reopening with changing readOnly flag. Use IndexReader.openIfChanged(IndexReader) instead.");
    }

    @Override
    public synchronized Object clone() {
        try {
            return this.doReopen(true);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    @Deprecated
    public IndexReader clone(boolean openReadOnly) throws CorruptIndexException, IOException {
        throw new UnsupportedOperationException("MultiReader does not support cloning with changing readOnly flag. Use IndexReader.clone() instead.");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    private IndexReader doReopen(boolean doClone) throws CorruptIndexException, IOException {
        this.ensureOpen();
        changed = false;
        newSubReaders = new IndexReader[this.subReaders.length];
        success = false;
        try {
            for (i = 0; i < this.subReaders.length; ++i) {
                if (doClone) {
                    newSubReaders[i] = (IndexReader)this.subReaders[i].clone();
                    changed = true;
                    continue;
                }
                newSubReader = IndexReader.openIfChanged(this.subReaders[i]);
                if (newSubReader != null) {
                    newSubReaders[i] = newSubReader;
                    changed = true;
                    continue;
                }
                newSubReaders[i] = this.subReaders[i];
            }
            success = true;
            var8_9 = null;
            ** if (success || !changed) goto lbl-1000
        }
        catch (Throwable var7_15) {
            var8_10 = null;
            if (!success && changed) {
                for (i = 0; i < newSubReaders.length; ++i) {
                    if (newSubReaders[i] == this.subReaders[i]) continue;
                    try {
                        newSubReaders[i].close();
                        continue;
                    }
                    catch (IOException ignore) {
                        // empty catch block
                    }
                }
            }
            throw var7_15;
        }
lbl-1000:
        // 4 sources

        {
            for (i = 0; i < newSubReaders.length; ++i) {
                if (newSubReaders[i] == this.subReaders[i]) continue;
                try {
                    newSubReaders[i].close();
                    continue;
                }
                catch (IOException ignore) {
                    // empty catch block
                }
            }
        }
lbl-1000:
        // 2 sources

        {
        }
        if (changed) {
            newDecrefOnClose = new boolean[this.subReaders.length];
            for (i = 0; i < this.subReaders.length; ++i) {
                if (newSubReaders[i] != this.subReaders[i]) continue;
                newSubReaders[i].incRef();
                newDecrefOnClose[i] = true;
            }
            return new MultiReader(newSubReaders, newDecrefOnClose);
        }
        return null;
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
        return false;
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

    protected int readerIndex(int n) {
        return DirectoryReader.readerIndex(n, this.starts, this.subReaders.length);
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
        int i;
        this.ensureOpen();
        byte[] bytes = this.normsCache.get(field);
        for (i = 0; i < this.subReaders.length; ++i) {
            this.subReaders[i].norms(field, result, offset + this.starts[i]);
        }
        if (bytes == null && !this.hasNorms(field)) {
            Arrays.fill(result, offset, result.length, Similarity.getDefault().encodeNormValue(1.0f));
        } else if (bytes != null) {
            System.arraycopy(bytes, 0, result, offset, this.maxDoc());
        } else {
            for (i = 0; i < this.subReaders.length; ++i) {
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
        return new DirectoryReader.MultiTermEnum(this, this.subReaders, this.starts, null);
    }

    @Override
    public TermEnum terms(Term term) throws IOException {
        this.ensureOpen();
        if (this.subReaders.length == 1) {
            return this.subReaders[0].terms(term);
        }
        return new DirectoryReader.MultiTermEnum(this, this.subReaders, this.starts, term);
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
        return new DirectoryReader.MultiTermDocs(this, this.subReaders, this.starts);
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
        return new DirectoryReader.MultiTermPositions(this, this.subReaders, this.starts);
    }

    @Override
    @Deprecated
    protected void doCommit(Map<String, String> commitUserData) throws IOException {
        for (int i = 0; i < this.subReaders.length; ++i) {
            this.subReaders[i].commit(commitUserData);
        }
    }

    @Override
    protected synchronized void doClose() throws IOException {
        IOException ioe = null;
        for (int i = 0; i < this.subReaders.length; ++i) {
            try {
                if (this.decrefOnClose[i]) {
                    this.subReaders[i].decRef();
                    continue;
                }
                this.subReaders[i].close();
                continue;
            }
            catch (IOException e) {
                if (ioe != null) continue;
                ioe = e;
            }
        }
        if (ioe != null) {
            throw ioe;
        }
    }

    @Override
    public boolean isCurrent() throws CorruptIndexException, IOException {
        this.ensureOpen();
        for (int i = 0; i < this.subReaders.length; ++i) {
            if (this.subReaders[i].isCurrent()) continue;
            return false;
        }
        return true;
    }

    @Override
    public long getVersion() {
        throw new UnsupportedOperationException("MultiReader does not support this method.");
    }

    @Override
    public IndexReader[] getSequentialSubReaders() {
        return this.subReaders;
    }
}

