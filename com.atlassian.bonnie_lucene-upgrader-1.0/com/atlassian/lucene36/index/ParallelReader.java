/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.document.Document;
import com.atlassian.lucene36.document.FieldSelector;
import com.atlassian.lucene36.document.FieldSelectorResult;
import com.atlassian.lucene36.document.Fieldable;
import com.atlassian.lucene36.index.CorruptIndexException;
import com.atlassian.lucene36.index.FieldInfo;
import com.atlassian.lucene36.index.FieldInfos;
import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.index.TermDocs;
import com.atlassian.lucene36.index.TermEnum;
import com.atlassian.lucene36.index.TermFreqVector;
import com.atlassian.lucene36.index.TermPositions;
import com.atlassian.lucene36.index.TermVectorMapper;
import com.atlassian.lucene36.util.ReaderUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ParallelReader
extends IndexReader {
    private List<IndexReader> readers = new ArrayList<IndexReader>();
    private List<Boolean> decrefOnClose = new ArrayList<Boolean>();
    boolean incRefReaders = false;
    private SortedMap<String, IndexReader> fieldToReader = new TreeMap<String, IndexReader>();
    private List<IndexReader> storedFieldReaders = new ArrayList<IndexReader>();
    private int maxDoc;
    private int numDocs;
    private boolean hasDeletions;
    private final FieldInfos fieldInfos;

    public ParallelReader() throws IOException {
        this(true);
    }

    public ParallelReader(boolean closeSubReaders) throws IOException {
        this.incRefReaders = !closeSubReaders;
        this.fieldInfos = new FieldInfos();
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder("ParallelReader(");
        Iterator<IndexReader> iter = this.readers.iterator();
        if (iter.hasNext()) {
            buffer.append(iter.next());
        }
        while (iter.hasNext()) {
            buffer.append(", ").append(iter.next());
        }
        buffer.append(')');
        return buffer.toString();
    }

    public void add(IndexReader reader) throws IOException {
        this.ensureOpen();
        this.add(reader, false);
    }

    public void add(IndexReader reader, boolean ignoreStoredFields) throws IOException {
        this.ensureOpen();
        if (this.readers.size() == 0) {
            this.maxDoc = reader.maxDoc();
            this.numDocs = reader.numDocs();
            this.hasDeletions = reader.hasDeletions();
        }
        if (reader.maxDoc() != this.maxDoc) {
            throw new IllegalArgumentException("All readers must have same maxDoc: " + this.maxDoc + "!=" + reader.maxDoc());
        }
        if (reader.numDocs() != this.numDocs) {
            throw new IllegalArgumentException("All readers must have same numDocs: " + this.numDocs + "!=" + reader.numDocs());
        }
        FieldInfos readerFieldInfos = ReaderUtil.getMergedFieldInfos(reader);
        for (FieldInfo fieldInfo : readerFieldInfos) {
            if (this.fieldToReader.get(fieldInfo.name) != null) continue;
            this.fieldInfos.add(fieldInfo);
            this.fieldToReader.put(fieldInfo.name, reader);
        }
        if (!ignoreStoredFields) {
            this.storedFieldReaders.add(reader);
        }
        this.readers.add(reader);
        if (this.incRefReaders) {
            reader.incRef();
        }
        this.decrefOnClose.add(this.incRefReaders);
    }

    @Override
    public FieldInfos getFieldInfos() {
        return this.fieldInfos;
    }

    @Override
    protected synchronized IndexReader doOpenIfChanged() throws CorruptIndexException, IOException {
        return this.doReopen(false);
    }

    @Override
    @Deprecated
    protected IndexReader doOpenIfChanged(boolean openReadOnly) throws CorruptIndexException, IOException {
        throw new UnsupportedOperationException("ParallelReader does not support reopening with changing readOnly flag. Use IndexReader.openIfChanged(IndexReader) instead.");
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
        throw new UnsupportedOperationException("ParallelReader does not support cloning with changing readOnly flag. Use IndexReader.clone() instead.");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    private IndexReader doReopen(boolean doClone) throws CorruptIndexException, IOException {
        this.ensureOpen();
        reopened = false;
        newReaders = new ArrayList<IndexReader>();
        success = false;
        try {
            for (IndexReader oldReader : this.readers) {
                newReader = null;
                if (doClone) {
                    newReader = (IndexReader)oldReader.clone();
                    reopened = true;
                } else {
                    newReader = IndexReader.openIfChanged(oldReader);
                    if (newReader != null) {
                        reopened = true;
                    } else {
                        newReader = oldReader;
                    }
                }
                newReaders.add(newReader);
            }
            success = true;
            var9_9 = null;
            ** if (success || !reopened) goto lbl-1000
        }
        catch (Throwable var8_17) {
            var9_10 = null;
            if (!success && reopened) {
                for (i = 0; i < newReaders.size(); ++i) {
                    r = (IndexReader)newReaders.get(i);
                    if (r == this.readers.get(i)) continue;
                    try {
                        r.close();
                        continue;
                    }
                    catch (IOException ignore) {
                        // empty catch block
                    }
                }
            }
            throw var8_17;
        }
lbl-1000:
        // 4 sources

        {
            for (i = 0; i < newReaders.size(); ++i) {
                r = (IndexReader)newReaders.get(i);
                if (r == this.readers.get(i)) continue;
                try {
                    r.close();
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
        if (reopened) {
            newDecrefOnClose = new ArrayList<Boolean>();
            pr = new ParallelReader();
            for (i = 0; i < this.readers.size(); ++i) {
                oldReader = this.readers.get(i);
                newReader = (IndexReader)newReaders.get(i);
                if (newReader == oldReader) {
                    newDecrefOnClose.add(Boolean.TRUE);
                    newReader.incRef();
                } else {
                    newDecrefOnClose.add(Boolean.FALSE);
                }
                pr.add(newReader, this.storedFieldReaders.contains(oldReader) == false);
            }
            pr.decrefOnClose = newDecrefOnClose;
            pr.incRefReaders = this.incRefReaders;
            return pr;
        }
        return null;
    }

    @Override
    public int numDocs() {
        return this.numDocs;
    }

    @Override
    public int maxDoc() {
        return this.maxDoc;
    }

    @Override
    public boolean hasDeletions() {
        this.ensureOpen();
        return this.hasDeletions;
    }

    @Override
    public boolean isDeleted(int n) {
        if (this.readers.size() > 0) {
            return this.readers.get(0).isDeleted(n);
        }
        return false;
    }

    @Override
    @Deprecated
    protected void doDelete(int n) throws CorruptIndexException, IOException {
        for (IndexReader reader : this.readers) {
            reader.deleteDocument(n);
        }
        this.hasDeletions = true;
    }

    @Override
    @Deprecated
    protected void doUndeleteAll() throws CorruptIndexException, IOException {
        for (IndexReader reader : this.readers) {
            reader.undeleteAll();
        }
        this.hasDeletions = false;
    }

    @Override
    public Document document(int n, FieldSelector fieldSelector) throws CorruptIndexException, IOException {
        this.ensureOpen();
        Document result = new Document();
        for (IndexReader reader : this.storedFieldReaders) {
            boolean include;
            boolean bl = include = fieldSelector == null;
            if (!include) {
                for (FieldInfo fieldInfo : this.fieldInfos) {
                    if (fieldSelector.accept(fieldInfo.name) == FieldSelectorResult.NO_LOAD) continue;
                    include = true;
                    break;
                }
            }
            if (!include) continue;
            List<Fieldable> fields = reader.document(n, fieldSelector).getFields();
            for (Fieldable field : fields) {
                result.add(field);
            }
        }
        return result;
    }

    @Override
    public TermFreqVector[] getTermFreqVectors(int n) throws IOException {
        this.ensureOpen();
        ArrayList<TermFreqVector> results = new ArrayList<TermFreqVector>();
        for (Map.Entry<String, IndexReader> e : this.fieldToReader.entrySet()) {
            String field = e.getKey();
            IndexReader reader = e.getValue();
            TermFreqVector vector = reader.getTermFreqVector(n, field);
            if (vector == null) continue;
            results.add(vector);
        }
        return results.toArray(new TermFreqVector[results.size()]);
    }

    @Override
    public TermFreqVector getTermFreqVector(int n, String field) throws IOException {
        this.ensureOpen();
        IndexReader reader = (IndexReader)this.fieldToReader.get(field);
        return reader == null ? null : reader.getTermFreqVector(n, field);
    }

    @Override
    public void getTermFreqVector(int docNumber, String field, TermVectorMapper mapper) throws IOException {
        this.ensureOpen();
        IndexReader reader = (IndexReader)this.fieldToReader.get(field);
        if (reader != null) {
            reader.getTermFreqVector(docNumber, field, mapper);
        }
    }

    @Override
    public void getTermFreqVector(int docNumber, TermVectorMapper mapper) throws IOException {
        this.ensureOpen();
        for (Map.Entry<String, IndexReader> e : this.fieldToReader.entrySet()) {
            String field = e.getKey();
            IndexReader reader = e.getValue();
            reader.getTermFreqVector(docNumber, field, mapper);
        }
    }

    @Override
    public boolean hasNorms(String field) throws IOException {
        this.ensureOpen();
        IndexReader reader = (IndexReader)this.fieldToReader.get(field);
        return reader == null ? false : reader.hasNorms(field);
    }

    @Override
    public byte[] norms(String field) throws IOException {
        this.ensureOpen();
        IndexReader reader = (IndexReader)this.fieldToReader.get(field);
        return reader == null ? null : reader.norms(field);
    }

    @Override
    public void norms(String field, byte[] result, int offset) throws IOException {
        this.ensureOpen();
        IndexReader reader = (IndexReader)this.fieldToReader.get(field);
        if (reader != null) {
            reader.norms(field, result, offset);
        }
    }

    @Override
    @Deprecated
    protected void doSetNorm(int n, String field, byte value) throws CorruptIndexException, IOException {
        IndexReader reader = (IndexReader)this.fieldToReader.get(field);
        if (reader != null) {
            reader.doSetNorm(n, field, value);
        }
    }

    @Override
    public TermEnum terms() throws IOException {
        this.ensureOpen();
        return new ParallelTermEnum();
    }

    @Override
    public TermEnum terms(Term term) throws IOException {
        this.ensureOpen();
        return new ParallelTermEnum(term);
    }

    @Override
    public int docFreq(Term term) throws IOException {
        this.ensureOpen();
        IndexReader reader = (IndexReader)this.fieldToReader.get(term.field());
        return reader == null ? 0 : reader.docFreq(term);
    }

    @Override
    public TermDocs termDocs(Term term) throws IOException {
        this.ensureOpen();
        return new ParallelTermDocs(term);
    }

    @Override
    public TermDocs termDocs() throws IOException {
        this.ensureOpen();
        return new ParallelTermDocs();
    }

    @Override
    public TermPositions termPositions() throws IOException {
        this.ensureOpen();
        return new ParallelTermPositions();
    }

    @Override
    public boolean isCurrent() throws CorruptIndexException, IOException {
        this.ensureOpen();
        for (IndexReader reader : this.readers) {
            if (reader.isCurrent()) continue;
            return false;
        }
        return true;
    }

    @Override
    @Deprecated
    public boolean isOptimized() {
        this.ensureOpen();
        for (IndexReader reader : this.readers) {
            if (reader.isOptimized()) continue;
            return false;
        }
        return true;
    }

    @Override
    public long getVersion() {
        throw new UnsupportedOperationException("ParallelReader does not support this method.");
    }

    IndexReader[] getSubReaders() {
        return this.readers.toArray(new IndexReader[this.readers.size()]);
    }

    @Override
    @Deprecated
    protected void doCommit(Map<String, String> commitUserData) throws IOException {
        for (IndexReader reader : this.readers) {
            reader.commit(commitUserData);
        }
    }

    @Override
    protected synchronized void doClose() throws IOException {
        for (int i = 0; i < this.readers.size(); ++i) {
            if (this.decrefOnClose.get(i).booleanValue()) {
                this.readers.get(i).decRef();
                continue;
            }
            this.readers.get(i).close();
        }
    }

    private class ParallelTermPositions
    extends ParallelTermDocs
    implements TermPositions {
        public void seek(Term term) throws IOException {
            IndexReader reader = (IndexReader)ParallelReader.this.fieldToReader.get(term.field());
            this.termDocs = reader != null ? reader.termPositions(term) : null;
        }

        public int nextPosition() throws IOException {
            return ((TermPositions)this.termDocs).nextPosition();
        }

        public int getPayloadLength() {
            return ((TermPositions)this.termDocs).getPayloadLength();
        }

        public byte[] getPayload(byte[] data, int offset) throws IOException {
            return ((TermPositions)this.termDocs).getPayload(data, offset);
        }

        public boolean isPayloadAvailable() {
            return ((TermPositions)this.termDocs).isPayloadAvailable();
        }
    }

    private class ParallelTermDocs
    implements TermDocs {
        protected TermDocs termDocs;

        public ParallelTermDocs() {
        }

        public ParallelTermDocs(Term term) throws IOException {
            if (term == null) {
                this.termDocs = ParallelReader.this.readers.isEmpty() ? null : ((IndexReader)ParallelReader.this.readers.get(0)).termDocs(null);
            } else {
                this.seek(term);
            }
        }

        public int doc() {
            return this.termDocs.doc();
        }

        public int freq() {
            return this.termDocs.freq();
        }

        public void seek(Term term) throws IOException {
            IndexReader reader = (IndexReader)ParallelReader.this.fieldToReader.get(term.field());
            this.termDocs = reader != null ? reader.termDocs(term) : null;
        }

        public void seek(TermEnum termEnum) throws IOException {
            this.seek(termEnum.term());
        }

        public boolean next() throws IOException {
            if (this.termDocs == null) {
                return false;
            }
            return this.termDocs.next();
        }

        public int read(int[] docs, int[] freqs) throws IOException {
            if (this.termDocs == null) {
                return 0;
            }
            return this.termDocs.read(docs, freqs);
        }

        public boolean skipTo(int target) throws IOException {
            if (this.termDocs == null) {
                return false;
            }
            return this.termDocs.skipTo(target);
        }

        public void close() throws IOException {
            if (this.termDocs != null) {
                this.termDocs.close();
            }
        }
    }

    private class ParallelTermEnum
    extends TermEnum {
        private String field;
        private Iterator<String> fieldIterator;
        private TermEnum termEnum;

        public ParallelTermEnum() throws IOException {
            try {
                this.field = (String)ParallelReader.this.fieldToReader.firstKey();
            }
            catch (NoSuchElementException e) {
                return;
            }
            if (this.field != null) {
                this.termEnum = ((IndexReader)ParallelReader.this.fieldToReader.get(this.field)).terms();
            }
        }

        public ParallelTermEnum(Term term) throws IOException {
            this.field = term.field();
            IndexReader reader = (IndexReader)ParallelReader.this.fieldToReader.get(this.field);
            if (reader != null) {
                this.termEnum = reader.terms(term);
            }
        }

        public boolean next() throws IOException {
            if (this.termEnum == null) {
                return false;
            }
            if (this.termEnum.next() && this.termEnum.term().field() == this.field) {
                return true;
            }
            this.termEnum.close();
            if (this.fieldIterator == null) {
                this.fieldIterator = ParallelReader.this.fieldToReader.tailMap(this.field).keySet().iterator();
                this.fieldIterator.next();
            }
            while (this.fieldIterator.hasNext()) {
                this.field = this.fieldIterator.next();
                this.termEnum = ((IndexReader)ParallelReader.this.fieldToReader.get(this.field)).terms(new Term(this.field));
                Term term = this.termEnum.term();
                if (term != null && term.field() == this.field) {
                    return true;
                }
                this.termEnum.close();
            }
            return false;
        }

        public Term term() {
            if (this.termEnum == null) {
                return null;
            }
            return this.termEnum.term();
        }

        public int docFreq() {
            if (this.termEnum == null) {
                return 0;
            }
            return this.termEnum.docFreq();
        }

        public void close() throws IOException {
            if (this.termEnum != null) {
                this.termEnum.close();
            }
        }
    }
}

