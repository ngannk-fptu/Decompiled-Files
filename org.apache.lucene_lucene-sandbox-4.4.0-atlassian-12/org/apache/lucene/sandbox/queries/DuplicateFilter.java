/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReader
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.DocsEnum
 *  org.apache.lucene.index.Terms
 *  org.apache.lucene.index.TermsEnum
 *  org.apache.lucene.search.DocIdSet
 *  org.apache.lucene.search.Filter
 *  org.apache.lucene.util.Bits
 *  org.apache.lucene.util.BytesRef
 *  org.apache.lucene.util.FixedBitSet
 */
package org.apache.lucene.sandbox.queries;

import java.io.IOException;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.FixedBitSet;

public class DuplicateFilter
extends Filter {
    private KeepMode keepMode;
    private ProcessingMode processingMode;
    private String fieldName;

    public DuplicateFilter(String fieldName) {
        this(fieldName, KeepMode.KM_USE_LAST_OCCURRENCE, ProcessingMode.PM_FULL_VALIDATION);
    }

    public DuplicateFilter(String fieldName, KeepMode keepMode, ProcessingMode processingMode) {
        this.fieldName = fieldName;
        this.keepMode = keepMode;
        this.processingMode = processingMode;
    }

    public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
        if (this.processingMode == ProcessingMode.PM_FAST_INVALIDATION) {
            return this.fastBits(context.reader(), acceptDocs);
        }
        return this.correctBits(context.reader(), acceptDocs);
    }

    private FixedBitSet correctBits(AtomicReader reader, Bits acceptDocs) throws IOException {
        BytesRef currTerm;
        FixedBitSet bits = new FixedBitSet(reader.maxDoc());
        Terms terms = reader.fields().terms(this.fieldName);
        if (terms == null) {
            return bits;
        }
        TermsEnum termsEnum = terms.iterator(null);
        DocsEnum docs = null;
        while ((currTerm = termsEnum.next()) != null) {
            int doc = (docs = termsEnum.docs(acceptDocs, docs, 0)).nextDoc();
            if (doc == Integer.MAX_VALUE) continue;
            if (this.keepMode == KeepMode.KM_USE_FIRST_OCCURRENCE) {
                bits.set(doc);
                continue;
            }
            int lastDoc = doc;
            do {
                lastDoc = doc;
            } while ((doc = docs.nextDoc()) != Integer.MAX_VALUE);
            bits.set(lastDoc);
        }
        return bits;
    }

    private FixedBitSet fastBits(AtomicReader reader, Bits acceptDocs) throws IOException {
        BytesRef currTerm;
        FixedBitSet bits = new FixedBitSet(reader.maxDoc());
        bits.set(0, reader.maxDoc());
        Terms terms = reader.fields().terms(this.fieldName);
        if (terms == null) {
            return bits;
        }
        TermsEnum termsEnum = terms.iterator(null);
        DocsEnum docs = null;
        while ((currTerm = termsEnum.next()) != null) {
            if (termsEnum.docFreq() <= 1) continue;
            int doc = (docs = termsEnum.docs(acceptDocs, docs, 0)).nextDoc();
            if (doc != Integer.MAX_VALUE && this.keepMode == KeepMode.KM_USE_FIRST_OCCURRENCE) {
                doc = docs.nextDoc();
            }
            int lastDoc = -1;
            do {
                lastDoc = doc;
                bits.clear(lastDoc);
            } while ((doc = docs.nextDoc()) != Integer.MAX_VALUE);
            if (this.keepMode != KeepMode.KM_USE_LAST_OCCURRENCE) continue;
            bits.set(lastDoc);
        }
        return bits;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public KeepMode getKeepMode() {
        return this.keepMode;
    }

    public void setKeepMode(KeepMode keepMode) {
        this.keepMode = keepMode;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != ((Object)((Object)this)).getClass()) {
            return false;
        }
        DuplicateFilter other = (DuplicateFilter)((Object)obj);
        return this.keepMode == other.keepMode && this.processingMode == other.processingMode && this.fieldName != null && this.fieldName.equals(other.fieldName);
    }

    public int hashCode() {
        int hash = 217;
        hash = 31 * hash + this.keepMode.hashCode();
        hash = 31 * hash + this.processingMode.hashCode();
        hash = 31 * hash + this.fieldName.hashCode();
        return hash;
    }

    public ProcessingMode getProcessingMode() {
        return this.processingMode;
    }

    public void setProcessingMode(ProcessingMode processingMode) {
        this.processingMode = processingMode;
    }

    public static enum ProcessingMode {
        PM_FULL_VALIDATION,
        PM_FAST_INVALIDATION;

    }

    public static enum KeepMode {
        KM_USE_FIRST_OCCURRENCE,
        KM_USE_LAST_OCCURRENCE;

    }
}

