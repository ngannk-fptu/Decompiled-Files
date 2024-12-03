/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.Bits;

public abstract class AtomicReader
extends IndexReader {
    private final AtomicReaderContext readerContext = new AtomicReaderContext(this);

    protected AtomicReader() {
    }

    @Override
    public final AtomicReaderContext getContext() {
        this.ensureOpen();
        return this.readerContext;
    }

    @Deprecated
    public final boolean hasNorms(String field) throws IOException {
        this.ensureOpen();
        FieldInfo fi = this.getFieldInfos().fieldInfo(field);
        return fi != null && fi.hasNorms();
    }

    public abstract Fields fields() throws IOException;

    @Override
    public final int docFreq(Term term) throws IOException {
        Fields fields = this.fields();
        if (fields == null) {
            return 0;
        }
        Terms terms = fields.terms(term.field());
        if (terms == null) {
            return 0;
        }
        TermsEnum termsEnum = terms.iterator(null);
        if (termsEnum.seekExact(term.bytes(), true)) {
            return termsEnum.docFreq();
        }
        return 0;
    }

    @Override
    public final long totalTermFreq(Term term) throws IOException {
        Fields fields = this.fields();
        if (fields == null) {
            return 0L;
        }
        Terms terms = fields.terms(term.field());
        if (terms == null) {
            return 0L;
        }
        TermsEnum termsEnum = terms.iterator(null);
        if (termsEnum.seekExact(term.bytes(), true)) {
            return termsEnum.totalTermFreq();
        }
        return 0L;
    }

    @Override
    public final long getSumDocFreq(String field) throws IOException {
        Terms terms = this.terms(field);
        if (terms == null) {
            return 0L;
        }
        return terms.getSumDocFreq();
    }

    @Override
    public final int getDocCount(String field) throws IOException {
        Terms terms = this.terms(field);
        if (terms == null) {
            return 0;
        }
        return terms.getDocCount();
    }

    @Override
    public final long getSumTotalTermFreq(String field) throws IOException {
        Terms terms = this.terms(field);
        if (terms == null) {
            return 0L;
        }
        return terms.getSumTotalTermFreq();
    }

    public final Terms terms(String field) throws IOException {
        Fields fields = this.fields();
        if (fields == null) {
            return null;
        }
        return fields.terms(field);
    }

    public final DocsEnum termDocsEnum(Term term) throws IOException {
        TermsEnum termsEnum;
        Terms terms;
        assert (term.field() != null);
        assert (term.bytes() != null);
        Fields fields = this.fields();
        if (fields != null && (terms = fields.terms(term.field())) != null && (termsEnum = terms.iterator(null)).seekExact(term.bytes(), true)) {
            return termsEnum.docs(this.getLiveDocs(), null);
        }
        return null;
    }

    public final DocsAndPositionsEnum termPositionsEnum(Term term) throws IOException {
        TermsEnum termsEnum;
        Terms terms;
        assert (term.field() != null);
        assert (term.bytes() != null);
        Fields fields = this.fields();
        if (fields != null && (terms = fields.terms(term.field())) != null && (termsEnum = terms.iterator(null)).seekExact(term.bytes(), true)) {
            return termsEnum.docsAndPositions(this.getLiveDocs(), null);
        }
        return null;
    }

    public abstract NumericDocValues getNumericDocValues(String var1) throws IOException;

    public abstract BinaryDocValues getBinaryDocValues(String var1) throws IOException;

    public abstract SortedDocValues getSortedDocValues(String var1) throws IOException;

    public abstract SortedSetDocValues getSortedSetDocValues(String var1) throws IOException;

    public abstract NumericDocValues getNormValues(String var1) throws IOException;

    public abstract FieldInfos getFieldInfos();

    public abstract Bits getLiveDocs();
}

