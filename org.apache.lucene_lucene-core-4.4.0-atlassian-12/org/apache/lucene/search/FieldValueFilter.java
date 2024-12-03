/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.BitsFilteredDocIdSet;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldCacheDocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.Bits;

public class FieldValueFilter
extends Filter {
    private final String field;
    private final boolean negate;

    public FieldValueFilter(String field) {
        this(field, false);
    }

    public FieldValueFilter(String field, boolean negate) {
        this.field = field;
        this.negate = negate;
    }

    public String field() {
        return this.field;
    }

    public boolean negate() {
        return this.negate;
    }

    @Override
    public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
        final Bits docsWithField = FieldCache.DEFAULT.getDocsWithField(context.reader(), this.field);
        if (this.negate) {
            if (docsWithField instanceof Bits.MatchAllBits) {
                return null;
            }
            return new FieldCacheDocIdSet(context.reader().maxDoc(), acceptDocs){

                @Override
                protected final boolean matchDoc(int doc) {
                    return !docsWithField.get(doc);
                }
            };
        }
        if (docsWithField instanceof Bits.MatchNoBits) {
            return null;
        }
        if (docsWithField instanceof DocIdSet) {
            return BitsFilteredDocIdSet.wrap((DocIdSet)((Object)docsWithField), acceptDocs);
        }
        return new FieldCacheDocIdSet(context.reader().maxDoc(), acceptDocs){

            @Override
            protected final boolean matchDoc(int doc) {
                return docsWithField.get(doc);
            }
        };
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.field == null ? 0 : this.field.hashCode());
        result = 31 * result + (this.negate ? 1231 : 1237);
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
        FieldValueFilter other = (FieldValueFilter)obj;
        if (this.field == null ? other.field != null : !this.field.equals(other.field)) {
            return false;
        }
        return this.negate == other.negate;
    }

    public String toString() {
        return "FieldValueFilter [field=" + this.field + ", negate=" + this.negate + "]";
    }
}

