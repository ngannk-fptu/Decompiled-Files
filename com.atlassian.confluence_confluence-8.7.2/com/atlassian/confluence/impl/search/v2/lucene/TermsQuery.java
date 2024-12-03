/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.FilteredTermsEnum
 *  org.apache.lucene.index.FilteredTermsEnum$AcceptStatus
 *  org.apache.lucene.index.Terms
 *  org.apache.lucene.index.TermsEnum
 *  org.apache.lucene.search.MultiTermQuery
 *  org.apache.lucene.util.AttributeSource
 *  org.apache.lucene.util.BytesRef
 *  org.apache.lucene.util.BytesRefHash
 */
package com.atlassian.confluence.impl.search.v2.lucene;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import org.apache.lucene.index.FilteredTermsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefHash;

public class TermsQuery
extends MultiTermQuery {
    private final BytesRefHash terms;
    private final int[] ords;

    public TermsQuery(String field, BytesRefHash terms) {
        super(field);
        this.terms = terms;
        this.ords = terms.sort(BytesRef.getUTF8SortedAsUnicodeComparator());
    }

    public TermsQuery(String field, Collection<String> terms) {
        super(field);
        BytesRefHash bytesRefHash = new BytesRefHash();
        terms.forEach(x -> bytesRefHash.add(new BytesRef((CharSequence)x)));
        this.terms = bytesRefHash;
        this.ords = bytesRefHash.sort(BytesRef.getUTF8SortedAsUnicodeComparator());
    }

    protected TermsEnum getTermsEnum(Terms terms, AttributeSource atts) throws IOException {
        if (this.terms.size() == 0) {
            return TermsEnum.EMPTY;
        }
        return new SeekingTermSetTermsEnum(terms.iterator(null), this.terms, this.ords);
    }

    public String toString(String string) {
        return "TermsQuery{field=" + this.field + "}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TermsQuery)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        TermsQuery that = (TermsQuery)((Object)o);
        return this.terms.equals(that.terms);
    }

    public int hashCode() {
        return Objects.hash(super.hashCode(), this.terms);
    }

    static class SeekingTermSetTermsEnum
    extends FilteredTermsEnum {
        private final BytesRefHash terms;
        private final int[] ords;
        private final int lastElement;
        private final BytesRef lastTerm;
        private final BytesRef spare = new BytesRef();
        private final Comparator<BytesRef> comparator;
        private BytesRef seekTerm;
        private int upto = 0;

        SeekingTermSetTermsEnum(TermsEnum tenum, BytesRefHash terms, int[] ords) {
            super(tenum);
            this.terms = terms;
            this.ords = ords;
            this.comparator = BytesRef.getUTF8SortedAsUnicodeComparator();
            this.lastElement = terms.size() - 1;
            this.lastTerm = terms.get(ords[this.lastElement], new BytesRef());
            this.seekTerm = terms.get(ords[this.upto], this.spare);
        }

        protected BytesRef nextSeekTerm(BytesRef currentTerm) throws IOException {
            BytesRef temp = this.seekTerm;
            this.seekTerm = null;
            return temp;
        }

        protected FilteredTermsEnum.AcceptStatus accept(BytesRef term) throws IOException {
            int cmp;
            if (this.comparator.compare(term, this.lastTerm) > 0) {
                return FilteredTermsEnum.AcceptStatus.END;
            }
            BytesRef currentTerm = this.terms.get(this.ords[this.upto], this.spare);
            if (this.comparator.compare(term, currentTerm) == 0) {
                if (this.upto == this.lastElement) {
                    return FilteredTermsEnum.AcceptStatus.YES;
                }
                this.seekTerm = this.terms.get(this.ords[++this.upto], this.spare);
                return FilteredTermsEnum.AcceptStatus.YES_AND_SEEK;
            }
            if (this.upto == this.lastElement) {
                return FilteredTermsEnum.AcceptStatus.NO;
            }
            do {
                if (this.upto == this.lastElement) {
                    return FilteredTermsEnum.AcceptStatus.NO;
                }
                this.seekTerm = this.terms.get(this.ords[++this.upto], this.spare);
            } while ((cmp = this.comparator.compare(this.seekTerm, term)) < 0);
            if (cmp == 0) {
                if (this.upto == this.lastElement) {
                    return FilteredTermsEnum.AcceptStatus.YES;
                }
                this.seekTerm = this.terms.get(this.ords[++this.upto], this.spare);
                return FilteredTermsEnum.AcceptStatus.YES_AND_SEEK;
            }
            return FilteredTermsEnum.AcceptStatus.NO_AND_SEEK;
        }
    }
}

