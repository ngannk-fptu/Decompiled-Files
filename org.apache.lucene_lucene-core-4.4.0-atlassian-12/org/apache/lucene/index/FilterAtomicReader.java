/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.index.StoredFieldVisitor;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;

public class FilterAtomicReader
extends AtomicReader {
    protected final AtomicReader in;

    public FilterAtomicReader(AtomicReader in) {
        this.in = in;
        in.registerParentReader(this);
    }

    @Override
    public Bits getLiveDocs() {
        this.ensureOpen();
        return this.in.getLiveDocs();
    }

    @Override
    public FieldInfos getFieldInfos() {
        return this.in.getFieldInfos();
    }

    @Override
    public Fields getTermVectors(int docID) throws IOException {
        this.ensureOpen();
        return this.in.getTermVectors(docID);
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
    public void document(int docID, StoredFieldVisitor visitor) throws IOException {
        this.ensureOpen();
        this.in.document(docID, visitor);
    }

    @Override
    protected void doClose() throws IOException {
        this.in.close();
    }

    @Override
    public Fields fields() throws IOException {
        this.ensureOpen();
        return this.in.fields();
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder("FilterAtomicReader(");
        buffer.append(this.in);
        buffer.append(')');
        return buffer.toString();
    }

    @Override
    public NumericDocValues getNumericDocValues(String field) throws IOException {
        this.ensureOpen();
        return this.in.getNumericDocValues(field);
    }

    @Override
    public BinaryDocValues getBinaryDocValues(String field) throws IOException {
        this.ensureOpen();
        return this.in.getBinaryDocValues(field);
    }

    @Override
    public SortedDocValues getSortedDocValues(String field) throws IOException {
        this.ensureOpen();
        return this.in.getSortedDocValues(field);
    }

    @Override
    public SortedSetDocValues getSortedSetDocValues(String field) throws IOException {
        this.ensureOpen();
        return this.in.getSortedSetDocValues(field);
    }

    @Override
    public NumericDocValues getNormValues(String field) throws IOException {
        this.ensureOpen();
        return this.in.getNormValues(field);
    }

    public static class FilterDocsAndPositionsEnum
    extends DocsAndPositionsEnum {
        protected final DocsAndPositionsEnum in;

        public FilterDocsAndPositionsEnum(DocsAndPositionsEnum in) {
            this.in = in;
        }

        @Override
        public AttributeSource attributes() {
            return this.in.attributes();
        }

        @Override
        public int docID() {
            return this.in.docID();
        }

        @Override
        public int freq() throws IOException {
            return this.in.freq();
        }

        @Override
        public int nextDoc() throws IOException {
            return this.in.nextDoc();
        }

        @Override
        public int advance(int target) throws IOException {
            return this.in.advance(target);
        }

        @Override
        public int nextPosition() throws IOException {
            return this.in.nextPosition();
        }

        @Override
        public int startOffset() throws IOException {
            return this.in.startOffset();
        }

        @Override
        public int endOffset() throws IOException {
            return this.in.endOffset();
        }

        @Override
        public BytesRef getPayload() throws IOException {
            return this.in.getPayload();
        }

        @Override
        public long cost() {
            return this.in.cost();
        }
    }

    public static class FilterDocsEnum
    extends DocsEnum {
        protected final DocsEnum in;

        public FilterDocsEnum(DocsEnum in) {
            this.in = in;
        }

        @Override
        public AttributeSource attributes() {
            return this.in.attributes();
        }

        @Override
        public int docID() {
            return this.in.docID();
        }

        @Override
        public int freq() throws IOException {
            return this.in.freq();
        }

        @Override
        public int nextDoc() throws IOException {
            return this.in.nextDoc();
        }

        @Override
        public int advance(int target) throws IOException {
            return this.in.advance(target);
        }

        @Override
        public long cost() {
            return this.in.cost();
        }
    }

    public static class FilterTermsEnum
    extends TermsEnum {
        protected final TermsEnum in;

        public FilterTermsEnum(TermsEnum in) {
            this.in = in;
        }

        @Override
        public AttributeSource attributes() {
            return this.in.attributes();
        }

        @Override
        public TermsEnum.SeekStatus seekCeil(BytesRef text, boolean useCache) throws IOException {
            return this.in.seekCeil(text, useCache);
        }

        @Override
        public void seekExact(long ord) throws IOException {
            this.in.seekExact(ord);
        }

        @Override
        public BytesRef next() throws IOException {
            return this.in.next();
        }

        @Override
        public BytesRef term() throws IOException {
            return this.in.term();
        }

        @Override
        public long ord() throws IOException {
            return this.in.ord();
        }

        @Override
        public int docFreq() throws IOException {
            return this.in.docFreq();
        }

        @Override
        public long totalTermFreq() throws IOException {
            return this.in.totalTermFreq();
        }

        @Override
        public DocsEnum docs(Bits liveDocs, DocsEnum reuse, int flags) throws IOException {
            return this.in.docs(liveDocs, reuse, flags);
        }

        @Override
        public DocsAndPositionsEnum docsAndPositions(Bits liveDocs, DocsAndPositionsEnum reuse, int flags) throws IOException {
            return this.in.docsAndPositions(liveDocs, reuse, flags);
        }

        @Override
        public Comparator<BytesRef> getComparator() {
            return this.in.getComparator();
        }
    }

    public static class FilterTerms
    extends Terms {
        protected final Terms in;

        public FilterTerms(Terms in) {
            this.in = in;
        }

        @Override
        public TermsEnum iterator(TermsEnum reuse) throws IOException {
            return this.in.iterator(reuse);
        }

        @Override
        public Comparator<BytesRef> getComparator() {
            return this.in.getComparator();
        }

        @Override
        public long size() throws IOException {
            return this.in.size();
        }

        @Override
        public long getSumTotalTermFreq() throws IOException {
            return this.in.getSumTotalTermFreq();
        }

        @Override
        public long getSumDocFreq() throws IOException {
            return this.in.getSumDocFreq();
        }

        @Override
        public int getDocCount() throws IOException {
            return this.in.getDocCount();
        }

        @Override
        public boolean hasOffsets() {
            return this.in.hasOffsets();
        }

        @Override
        public boolean hasPositions() {
            return this.in.hasPositions();
        }

        @Override
        public boolean hasPayloads() {
            return this.in.hasPayloads();
        }
    }

    public static class FilterFields
    extends Fields {
        protected final Fields in;

        public FilterFields(Fields in) {
            this.in = in;
        }

        @Override
        public Iterator<String> iterator() {
            return this.in.iterator();
        }

        @Override
        public Terms terms(String field) throws IOException {
            return this.in.terms(field);
        }

        @Override
        public int size() {
            return this.in.size();
        }
    }
}

