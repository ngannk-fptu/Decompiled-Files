/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.search.DocIdSet;
import com.atlassian.lucene36.search.FieldCache;
import com.atlassian.lucene36.search.FieldCacheDocIdSet;
import com.atlassian.lucene36.search.Filter;
import com.atlassian.lucene36.search.FilteredDocIdSet;
import com.atlassian.lucene36.util.Bits;
import java.io.IOException;

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

    public DocIdSet getDocIdSet(final IndexReader reader) throws IOException {
        final Bits docsWithField = FieldCache.DEFAULT.getDocsWithField(reader, this.field);
        if (this.negate) {
            if (docsWithField instanceof Bits.MatchAllBits) {
                return null;
            }
            return new FieldCacheDocIdSet(reader){

                protected final boolean matchDoc(int doc) {
                    return !docsWithField.get(doc);
                }
            };
        }
        if (docsWithField instanceof Bits.MatchNoBits) {
            return null;
        }
        if (docsWithField instanceof DocIdSet) {
            DocIdSet dis = (DocIdSet)((Object)docsWithField);
            return reader.hasDeletions() ? new FilteredDocIdSet(dis){

                protected final boolean match(int doc) {
                    return !reader.isDeleted(doc);
                }
            } : dis;
        }
        return new FieldCacheDocIdSet(reader){

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

