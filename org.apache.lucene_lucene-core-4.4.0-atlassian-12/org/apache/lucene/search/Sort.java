/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SortField;

public class Sort {
    public static final Sort RELEVANCE = new Sort();
    public static final Sort INDEXORDER = new Sort(SortField.FIELD_DOC);
    SortField[] fields;

    public Sort() {
        this(SortField.FIELD_SCORE);
    }

    public Sort(SortField field) {
        this.setSort(field);
    }

    public Sort(SortField ... fields) {
        this.setSort(fields);
    }

    public void setSort(SortField field) {
        this.fields = new SortField[]{field};
    }

    public void setSort(SortField ... fields) {
        this.fields = fields;
    }

    public SortField[] getSort() {
        return this.fields;
    }

    public Sort rewrite(IndexSearcher searcher) throws IOException {
        boolean changed = false;
        SortField[] rewrittenSortFields = new SortField[this.fields.length];
        for (int i = 0; i < this.fields.length; ++i) {
            rewrittenSortFields[i] = this.fields[i].rewrite(searcher);
            if (this.fields[i] == rewrittenSortFields[i]) continue;
            changed = true;
        }
        return changed ? new Sort(rewrittenSortFields) : this;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < this.fields.length; ++i) {
            buffer.append(this.fields[i].toString());
            if (i + 1 >= this.fields.length) continue;
            buffer.append(',');
        }
        return buffer.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sort)) {
            return false;
        }
        Sort other = (Sort)o;
        return Arrays.equals(this.fields, other.fields);
    }

    public int hashCode() {
        return 1168832101 + Arrays.hashCode(this.fields);
    }

    boolean needsScores() {
        for (SortField sortField : this.fields) {
            if (sortField.getType() != SortField.Type.SCORE) continue;
            return true;
        }
        return false;
    }
}

