/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.search.FieldComparator;
import com.atlassian.lucene36.search.FieldDoc;
import com.atlassian.lucene36.search.SortField;
import com.atlassian.lucene36.util.PriorityQueue;
import java.io.IOException;
import java.text.Collator;
import java.util.Locale;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class FieldDocSortedHitQueue
extends PriorityQueue<FieldDoc> {
    volatile SortField[] fields = null;
    volatile Collator[] collators = null;
    volatile FieldComparator<?>[] comparators = null;

    FieldDocSortedHitQueue(int size) {
        this.initialize(size);
    }

    void setFields(SortField[] fields) throws IOException {
        this.fields = fields;
        this.collators = this.hasCollators(fields);
        this.comparators = new FieldComparator[fields.length];
        for (int fieldIDX = 0; fieldIDX < fields.length; ++fieldIDX) {
            this.comparators[fieldIDX] = fields[fieldIDX].getComparator(1, fieldIDX);
        }
    }

    SortField[] getFields() {
        return this.fields;
    }

    private Collator[] hasCollators(SortField[] fields) {
        if (fields == null) {
            return null;
        }
        Collator[] ret = new Collator[fields.length];
        for (int i = 0; i < fields.length; ++i) {
            Locale locale = fields[i].getLocale();
            if (locale == null) continue;
            ret[i] = Collator.getInstance(locale);
        }
        return ret;
    }

    @Override
    protected final boolean lessThan(FieldDoc docA, FieldDoc docB) {
        int n = this.fields.length;
        int c = 0;
        for (int i = 0; i < n && c == 0; ++i) {
            int type = this.fields[i].getType();
            if (type == 3) {
                String s1 = (String)docA.fields[i];
                String s2 = (String)docB.fields[i];
                c = s1 == null ? (s2 == null ? 0 : -1) : (s2 == null ? 1 : (this.fields[i].getLocale() == null ? s1.compareTo(s2) : this.collators[i].compare(s1, s2)));
            } else {
                FieldComparator<?> comp = this.comparators[i];
                c = comp.compareValues(docA.fields[i], docB.fields[i]);
            }
            if (!this.fields[i].getReverse()) continue;
            c = -c;
        }
        if (c == 0) {
            return docA.doc > docB.doc;
        }
        return c > 0;
    }
}

