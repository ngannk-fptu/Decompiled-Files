/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReader
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.DocsEnum
 *  org.apache.lucene.index.Fields
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.index.Terms
 *  org.apache.lucene.index.TermsEnum
 *  org.apache.lucene.search.DocIdSet
 *  org.apache.lucene.search.Filter
 *  org.apache.lucene.util.ArrayUtil
 *  org.apache.lucene.util.Bits
 *  org.apache.lucene.util.BytesRef
 *  org.apache.lucene.util.FixedBitSet
 */
package org.apache.lucene.queries;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.FixedBitSet;

public final class TermsFilter
extends Filter {
    private final int[] offsets;
    private final byte[] termsBytes;
    private final TermsAndField[] termsAndFields;
    private final int hashCode;
    private static final int PRIME = 31;

    public TermsFilter(final List<Term> terms) {
        this(new FieldAndTermEnum(){
            final Iterator<Term> iter;
            {
                this.iter = TermsFilter.sort(terms).iterator();
            }

            @Override
            public BytesRef next() {
                if (this.iter.hasNext()) {
                    Term next = this.iter.next();
                    this.field = next.field();
                    return next.bytes();
                }
                return null;
            }
        }, terms.size());
    }

    public TermsFilter(String field, final List<BytesRef> terms) {
        this(new FieldAndTermEnum(field){
            final Iterator<BytesRef> iter;
            {
                super(field);
                this.iter = TermsFilter.sort(terms).iterator();
            }

            @Override
            public BytesRef next() {
                if (this.iter.hasNext()) {
                    return this.iter.next();
                }
                return null;
            }
        }, terms.size());
    }

    public TermsFilter(String field, BytesRef ... terms) {
        this(field, Arrays.asList(terms));
    }

    public TermsFilter(Term ... terms) {
        this(Arrays.asList(terms));
    }

    private TermsFilter(FieldAndTermEnum iter, int length) {
        int start;
        BytesRef currentTerm;
        int hash = 9;
        byte[] serializedTerms = new byte[]{};
        this.offsets = new int[length + 1];
        int lastEndOffset = 0;
        int index = 0;
        ArrayList<TermsAndField> termsAndFields = new ArrayList<TermsAndField>();
        TermsAndField lastTermsAndField = null;
        BytesRef previousTerm = null;
        String previousField = null;
        while ((currentTerm = iter.next()) != null) {
            String currentField = iter.field();
            if (currentField == null) {
                throw new IllegalArgumentException("Field must not be null");
            }
            if (previousField != null) {
                if (previousField.equals(currentField)) {
                    if (previousTerm.bytesEquals(currentTerm)) {
                        continue;
                    }
                } else {
                    start = lastTermsAndField == null ? 0 : lastTermsAndField.end;
                    lastTermsAndField = new TermsAndField(start, index, previousField);
                    termsAndFields.add(lastTermsAndField);
                }
            }
            hash = 31 * hash + currentField.hashCode();
            hash = 31 * hash + currentTerm.hashCode();
            if (serializedTerms.length < lastEndOffset + currentTerm.length) {
                serializedTerms = ArrayUtil.grow((byte[])serializedTerms, (int)(lastEndOffset + currentTerm.length));
            }
            System.arraycopy(currentTerm.bytes, currentTerm.offset, serializedTerms, lastEndOffset, currentTerm.length);
            this.offsets[index] = lastEndOffset;
            lastEndOffset += currentTerm.length;
            ++index;
            previousTerm = currentTerm;
            previousField = currentField;
        }
        this.offsets[index] = lastEndOffset;
        start = lastTermsAndField == null ? 0 : lastTermsAndField.end;
        lastTermsAndField = new TermsAndField(start, index, previousField);
        termsAndFields.add(lastTermsAndField);
        this.termsBytes = ArrayUtil.shrink((byte[])serializedTerms, (int)lastEndOffset);
        this.termsAndFields = termsAndFields.toArray(new TermsAndField[termsAndFields.size()]);
        this.hashCode = hash;
    }

    public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
        AtomicReader reader = context.reader();
        FixedBitSet result = null;
        Fields fields = reader.fields();
        BytesRef spare = new BytesRef(this.termsBytes);
        if (fields == null) {
            return result;
        }
        Terms terms = null;
        TermsEnum termsEnum = null;
        DocsEnum docs = null;
        for (TermsAndField termsAndField : this.termsAndFields) {
            terms = fields.terms(termsAndField.field);
            if (terms == null) continue;
            termsEnum = terms.iterator(termsEnum);
            for (int i = termsAndField.start; i < termsAndField.end; ++i) {
                spare.offset = this.offsets[i];
                spare.length = this.offsets[i + 1] - this.offsets[i];
                if (!termsEnum.seekExact(spare, false)) continue;
                docs = termsEnum.docs(acceptDocs, docs, 0);
                if (result == null && docs.nextDoc() != Integer.MAX_VALUE) {
                    result = new FixedBitSet(reader.maxDoc());
                    result.set(docs.docID());
                }
                while (docs.nextDoc() != Integer.MAX_VALUE) {
                    result.set(docs.docID());
                }
            }
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != ((Object)((Object)this)).getClass()) {
            return false;
        }
        TermsFilter test = (TermsFilter)((Object)obj);
        if (test.hashCode == this.hashCode && this.termsAndFields.length == test.termsAndFields.length) {
            for (int i = 0; i < this.termsAndFields.length; ++i) {
                TermsAndField current = this.termsAndFields[i];
                if (current.equals(test.termsAndFields[i])) continue;
                return false;
            }
            int end = this.offsets[this.termsAndFields.length];
            byte[] left = this.termsBytes;
            byte[] right = test.termsBytes;
            for (int i = 0; i < end; ++i) {
                if (left[i] == right[i]) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        BytesRef spare = new BytesRef(this.termsBytes);
        boolean first = true;
        for (int i = 0; i < this.termsAndFields.length; ++i) {
            TermsAndField current = this.termsAndFields[i];
            for (int j = current.start; j < current.end; ++j) {
                spare.offset = this.offsets[j];
                spare.length = this.offsets[j + 1] - this.offsets[j];
                if (!first) {
                    builder.append(' ');
                }
                first = false;
                builder.append(current.field).append(':');
                builder.append(spare.utf8ToString());
            }
        }
        return builder.toString();
    }

    private static <T extends Comparable<? super T>> List<T> sort(List<T> toSort) {
        if (toSort.isEmpty()) {
            throw new IllegalArgumentException("no terms provided");
        }
        Collections.sort(toSort);
        return toSort;
    }

    private static abstract class FieldAndTermEnum {
        protected String field;

        public abstract BytesRef next();

        public FieldAndTermEnum() {
        }

        public FieldAndTermEnum(String field) {
            this.field = field;
        }

        public String field() {
            return this.field;
        }
    }

    private static final class TermsAndField {
        final int start;
        final int end;
        final String field;

        TermsAndField(int start, int end, String field) {
            this.start = start;
            this.end = end;
            this.field = field;
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + (this.field == null ? 0 : this.field.hashCode());
            result = 31 * result + this.end;
            result = 31 * result + this.start;
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            TermsAndField other = (TermsAndField)obj;
            if (this.field == null ? other.field != null : !this.field.equals(other.field)) {
                return false;
            }
            if (this.end != other.end) {
                return false;
            }
            return this.start == other.start;
        }
    }
}

