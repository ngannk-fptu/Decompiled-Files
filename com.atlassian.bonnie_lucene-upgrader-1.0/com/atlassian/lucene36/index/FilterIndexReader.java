/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.document.Document;
import com.atlassian.lucene36.document.FieldSelector;
import com.atlassian.lucene36.index.CorruptIndexException;
import com.atlassian.lucene36.index.FieldInfos;
import com.atlassian.lucene36.index.IndexCommit;
import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.index.TermDocs;
import com.atlassian.lucene36.index.TermEnum;
import com.atlassian.lucene36.index.TermFreqVector;
import com.atlassian.lucene36.index.TermPositions;
import com.atlassian.lucene36.index.TermVectorMapper;
import com.atlassian.lucene36.store.Directory;
import java.io.IOException;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FilterIndexReader
extends IndexReader {
    protected IndexReader in;

    public FilterIndexReader(IndexReader in) {
        this.in = in;
    }

    @Override
    public Directory directory() {
        this.ensureOpen();
        return this.in.directory();
    }

    @Override
    public IndexCommit getIndexCommit() throws IOException {
        this.ensureOpen();
        return this.in.getIndexCommit();
    }

    @Override
    public FieldInfos getFieldInfos() {
        return this.in.getFieldInfos();
    }

    @Override
    public int getTermInfosIndexDivisor() {
        this.ensureOpen();
        return this.in.getTermInfosIndexDivisor();
    }

    @Override
    public TermFreqVector[] getTermFreqVectors(int docNumber) throws IOException {
        this.ensureOpen();
        return this.in.getTermFreqVectors(docNumber);
    }

    @Override
    public TermFreqVector getTermFreqVector(int docNumber, String field) throws IOException {
        this.ensureOpen();
        return this.in.getTermFreqVector(docNumber, field);
    }

    @Override
    public void getTermFreqVector(int docNumber, String field, TermVectorMapper mapper) throws IOException {
        this.ensureOpen();
        this.in.getTermFreqVector(docNumber, field, mapper);
    }

    @Override
    public void getTermFreqVector(int docNumber, TermVectorMapper mapper) throws IOException {
        this.ensureOpen();
        this.in.getTermFreqVector(docNumber, mapper);
    }

    @Override
    public long getUniqueTermCount() throws IOException {
        this.ensureOpen();
        return this.in.getUniqueTermCount();
    }

    @Override
    public int numDocs() {
        return this.in.numDocs();
    }

    @Override
    public int maxDoc() {
        return this.in.maxDoc();
    }

    @Override
    public Document document(int n, FieldSelector fieldSelector) throws CorruptIndexException, IOException {
        this.ensureOpen();
        return this.in.document(n, fieldSelector);
    }

    @Override
    public boolean isDeleted(int n) {
        return this.in.isDeleted(n);
    }

    @Override
    public boolean hasDeletions() {
        this.ensureOpen();
        return this.in.hasDeletions();
    }

    @Override
    @Deprecated
    protected void doUndeleteAll() throws CorruptIndexException, IOException {
        this.in.undeleteAll();
    }

    @Override
    public boolean hasNorms(String field) throws IOException {
        this.ensureOpen();
        return this.in.hasNorms(field);
    }

    @Override
    public byte[] norms(String f) throws IOException {
        this.ensureOpen();
        return this.in.norms(f);
    }

    @Override
    public void norms(String f, byte[] bytes, int offset) throws IOException {
        this.ensureOpen();
        this.in.norms(f, bytes, offset);
    }

    @Override
    @Deprecated
    protected void doSetNorm(int d, String f, byte b) throws CorruptIndexException, IOException {
        this.in.setNorm(d, f, b);
    }

    @Override
    public TermEnum terms() throws IOException {
        this.ensureOpen();
        return this.in.terms();
    }

    @Override
    public TermEnum terms(Term t) throws IOException {
        this.ensureOpen();
        return this.in.terms(t);
    }

    @Override
    public int docFreq(Term t) throws IOException {
        this.ensureOpen();
        return this.in.docFreq(t);
    }

    @Override
    public TermDocs termDocs() throws IOException {
        this.ensureOpen();
        return this.in.termDocs();
    }

    @Override
    public TermDocs termDocs(Term term) throws IOException {
        this.ensureOpen();
        return this.in.termDocs(term);
    }

    @Override
    public TermPositions termPositions() throws IOException {
        this.ensureOpen();
        return this.in.termPositions();
    }

    @Override
    @Deprecated
    protected void doDelete(int n) throws CorruptIndexException, IOException {
        this.in.deleteDocument(n);
    }

    @Override
    @Deprecated
    protected void doCommit(Map<String, String> commitUserData) throws IOException {
        this.in.commit(commitUserData);
    }

    @Override
    protected void doClose() throws IOException {
        this.in.close();
    }

    @Override
    public long getVersion() {
        this.ensureOpen();
        return this.in.getVersion();
    }

    @Override
    public boolean isCurrent() throws CorruptIndexException, IOException {
        this.ensureOpen();
        return this.in.isCurrent();
    }

    @Override
    @Deprecated
    public boolean isOptimized() {
        this.ensureOpen();
        return this.in.isOptimized();
    }

    @Override
    public IndexReader[] getSequentialSubReaders() {
        return this.in.getSequentialSubReaders();
    }

    @Override
    public Map<String, String> getCommitUserData() {
        return this.in.getCommitUserData();
    }

    @Override
    public Object getCoreCacheKey() {
        return this.in.getCoreCacheKey();
    }

    @Override
    public Object getDeletesCacheKey() {
        return this.in.getDeletesCacheKey();
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder("FilterReader(");
        buffer.append(this.in);
        buffer.append(')');
        return buffer.toString();
    }

    public static class FilterTermEnum
    extends TermEnum {
        protected TermEnum in;

        public FilterTermEnum(TermEnum in) {
            this.in = in;
        }

        public boolean next() throws IOException {
            return this.in.next();
        }

        public Term term() {
            return this.in.term();
        }

        public int docFreq() {
            return this.in.docFreq();
        }

        public void close() throws IOException {
            this.in.close();
        }
    }

    public static class FilterTermPositions
    extends FilterTermDocs
    implements TermPositions {
        public FilterTermPositions(TermPositions in) {
            super(in);
        }

        public int nextPosition() throws IOException {
            return ((TermPositions)this.in).nextPosition();
        }

        public int getPayloadLength() {
            return ((TermPositions)this.in).getPayloadLength();
        }

        public byte[] getPayload(byte[] data, int offset) throws IOException {
            return ((TermPositions)this.in).getPayload(data, offset);
        }

        public boolean isPayloadAvailable() {
            return ((TermPositions)this.in).isPayloadAvailable();
        }
    }

    public static class FilterTermDocs
    implements TermDocs {
        protected TermDocs in;

        public FilterTermDocs(TermDocs in) {
            this.in = in;
        }

        public void seek(Term term) throws IOException {
            this.in.seek(term);
        }

        public void seek(TermEnum termEnum) throws IOException {
            this.in.seek(termEnum);
        }

        public int doc() {
            return this.in.doc();
        }

        public int freq() {
            return this.in.freq();
        }

        public boolean next() throws IOException {
            return this.in.next();
        }

        public int read(int[] docs, int[] freqs) throws IOException {
            return this.in.read(docs, freqs);
        }

        public boolean skipTo(int i) throws IOException {
            return this.in.skipTo(i);
        }

        public void close() throws IOException {
            this.in.close();
        }
    }
}

