/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldCacheDocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;

public abstract class DocTermOrdsRangeFilter
extends Filter {
    final String field;
    final BytesRef lowerVal;
    final BytesRef upperVal;
    final boolean includeLower;
    final boolean includeUpper;

    private DocTermOrdsRangeFilter(String field, BytesRef lowerVal, BytesRef upperVal, boolean includeLower, boolean includeUpper) {
        this.field = field;
        this.lowerVal = lowerVal;
        this.upperVal = upperVal;
        this.includeLower = includeLower;
        this.includeUpper = includeUpper;
    }

    @Override
    public abstract DocIdSet getDocIdSet(AtomicReaderContext var1, Bits var2) throws IOException;

    public static DocTermOrdsRangeFilter newBytesRefRange(String field, BytesRef lowerVal, BytesRef upperVal, boolean includeLower, boolean includeUpper) {
        return new DocTermOrdsRangeFilter(field, lowerVal, upperVal, includeLower, includeUpper){

            @Override
            public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
                long upperPoint;
                final SortedSetDocValues docTermOrds = FieldCache.DEFAULT.getDocTermOrds(context.reader(), this.field);
                long lowerPoint = this.lowerVal == null ? -1L : docTermOrds.lookupTerm(this.lowerVal);
                long l = upperPoint = this.upperVal == null ? -1L : docTermOrds.lookupTerm(this.upperVal);
                final long inclusiveLowerPoint = lowerPoint == -1L && this.lowerVal == null ? 0L : (this.includeLower && lowerPoint >= 0L ? lowerPoint : (lowerPoint >= 0L ? lowerPoint + 1L : Math.max(0L, -lowerPoint - 1L)));
                final long inclusiveUpperPoint = upperPoint == -1L && this.upperVal == null ? Long.MAX_VALUE : (this.includeUpper && upperPoint >= 0L ? upperPoint : (upperPoint >= 0L ? upperPoint - 1L : -upperPoint - 2L));
                if (inclusiveUpperPoint < 0L || inclusiveLowerPoint > inclusiveUpperPoint) {
                    return null;
                }
                assert (inclusiveLowerPoint >= 0L && inclusiveUpperPoint >= 0L);
                return new FieldCacheDocIdSet(context.reader().maxDoc(), acceptDocs){

                    @Override
                    protected final boolean matchDoc(int doc) {
                        long ord;
                        docTermOrds.setDocument(doc);
                        while ((ord = docTermOrds.nextOrd()) != -1L) {
                            if (ord > inclusiveUpperPoint) {
                                return false;
                            }
                            if (ord < inclusiveLowerPoint) continue;
                            return true;
                        }
                        return false;
                    }
                };
            }
        };
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder(this.field).append(":");
        return sb.append(this.includeLower ? (char)'[' : '{').append(this.lowerVal == null ? "*" : this.lowerVal.toString()).append(" TO ").append(this.upperVal == null ? "*" : this.upperVal.toString()).append(this.includeUpper ? (char)']' : '}').toString();
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DocTermOrdsRangeFilter)) {
            return false;
        }
        DocTermOrdsRangeFilter other = (DocTermOrdsRangeFilter)o;
        if (!this.field.equals(other.field) || this.includeLower != other.includeLower || this.includeUpper != other.includeUpper) {
            return false;
        }
        if (this.lowerVal != null ? !this.lowerVal.equals(other.lowerVal) : other.lowerVal != null) {
            return false;
        }
        return !(this.upperVal != null ? !this.upperVal.equals(other.upperVal) : other.upperVal != null);
    }

    public final int hashCode() {
        int h = this.field.hashCode();
        h ^= this.lowerVal != null ? this.lowerVal.hashCode() : 550356204;
        h = h << 1 | h >>> 31;
        h ^= this.upperVal != null ? this.upperVal.hashCode() : -1674416163;
        return h ^= (this.includeLower ? 1549299360 : -365038026) ^ (this.includeUpper ? 1721088258 : 1948649653);
    }

    public String getField() {
        return this.field;
    }

    public boolean includesLower() {
        return this.includeLower;
    }

    public boolean includesUpper() {
        return this.includeUpper;
    }

    public BytesRef getLowerVal() {
        return this.lowerVal;
    }

    public BytesRef getUpperVal() {
        return this.upperVal;
    }
}

