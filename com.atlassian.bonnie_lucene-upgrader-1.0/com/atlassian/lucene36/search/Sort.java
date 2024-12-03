/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.search.SortField;
import java.io.Serializable;
import java.util.Arrays;

public class Sort
implements Serializable {
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
}

