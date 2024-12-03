/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.BinaryDocValues
 *  org.apache.lucene.search.FieldCache
 *  org.apache.lucene.search.FieldComparator
 *  org.apache.lucene.util.BytesRef
 */
package com.atlassian.confluence.impl.search.v2.lucene;

import java.io.IOException;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.util.BytesRef;

public final class CaseInsensitiveSortComparator
extends FieldComparator<BytesRef> {
    private BytesRef[] values;
    private BinaryDocValues docTerms;
    private final String field;
    private String bottom;
    private final BytesRef tempBR = new BytesRef();

    CaseInsensitiveSortComparator(int numHits, String field) {
        this.values = new BytesRef[numHits];
        this.field = field;
    }

    public int compare(int slot1, int slot2) {
        String val1 = CaseInsensitiveSortComparator.toLowercase(this.values[slot1]);
        String val2 = CaseInsensitiveSortComparator.toLowercase(this.values[slot2]);
        if (val1 == null) {
            if (val2 == null) {
                return 0;
            }
            return -1;
        }
        if (val2 == null) {
            return 1;
        }
        return val1.compareTo(val2);
    }

    public int compareBottom(int doc) {
        this.docTerms.get(doc, this.tempBR);
        String val2 = CaseInsensitiveSortComparator.toLowercase(this.tempBR);
        if (this.bottom == null) {
            if (val2 == null) {
                return 0;
            }
            return -1;
        }
        if (val2 == null) {
            return 1;
        }
        return this.bottom.compareTo(val2);
    }

    public void copy(int slot, int doc) {
        if (this.values[slot] == null) {
            this.values[slot] = new BytesRef();
        }
        this.docTerms.get(doc, this.values[slot]);
    }

    public FieldComparator<BytesRef> setNextReader(AtomicReaderContext context) throws IOException {
        this.docTerms = FieldCache.DEFAULT.getTerms(context.reader(), this.field);
        return this;
    }

    public void setBottom(int bottom) {
        this.bottom = CaseInsensitiveSortComparator.toLowercase(this.values[bottom]);
    }

    public BytesRef value(int slot) {
        return this.values[slot];
    }

    public int compareValues(BytesRef val1, BytesRef val2) {
        if (val1 == null) {
            if (val2 == null) {
                return 0;
            }
            return -1;
        }
        if (val2 == null) {
            return 1;
        }
        return val1.compareTo(val2);
    }

    public int compareDocToValue(int doc, BytesRef value) {
        this.docTerms.get(doc, this.tempBR);
        return CaseInsensitiveSortComparator.toLowercase(this.tempBR).compareTo(CaseInsensitiveSortComparator.toLowercase(value));
    }

    private static String toLowercase(BytesRef bytesRef) {
        return bytesRef.utf8ToString().toLowerCase();
    }
}

