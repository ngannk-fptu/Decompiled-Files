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
package org.apache.lucene.sandbox.queries;

import java.io.IOException;
import java.text.Collator;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.util.BytesRef;

@Deprecated
public final class SlowCollatedStringComparator
extends FieldComparator<String> {
    private final String[] values;
    private BinaryDocValues currentDocTerms;
    private final String field;
    final Collator collator;
    private String bottom;
    private final BytesRef tempBR = new BytesRef();

    public SlowCollatedStringComparator(int numHits, String field, Collator collator) {
        this.values = new String[numHits];
        this.field = field;
        this.collator = collator;
    }

    public int compare(int slot1, int slot2) {
        String val1 = this.values[slot1];
        String val2 = this.values[slot2];
        if (val1 == null) {
            if (val2 == null) {
                return 0;
            }
            return -1;
        }
        if (val2 == null) {
            return 1;
        }
        return this.collator.compare(val1, val2);
    }

    public int compareBottom(int doc) {
        String val2;
        this.currentDocTerms.get(doc, this.tempBR);
        String string = val2 = this.tempBR.bytes == BinaryDocValues.MISSING ? null : this.tempBR.utf8ToString();
        if (this.bottom == null) {
            if (val2 == null) {
                return 0;
            }
            return -1;
        }
        if (val2 == null) {
            return 1;
        }
        return this.collator.compare(this.bottom, val2);
    }

    public void copy(int slot, int doc) {
        this.currentDocTerms.get(doc, this.tempBR);
        this.values[slot] = this.tempBR.bytes == BinaryDocValues.MISSING ? null : this.tempBR.utf8ToString();
    }

    public FieldComparator<String> setNextReader(AtomicReaderContext context) throws IOException {
        this.currentDocTerms = FieldCache.DEFAULT.getTerms(context.reader(), this.field);
        return this;
    }

    public void setBottom(int bottom) {
        this.bottom = this.values[bottom];
    }

    public String value(int slot) {
        return this.values[slot];
    }

    public int compareValues(String first, String second) {
        if (first == null) {
            if (second == null) {
                return 0;
            }
            return -1;
        }
        if (second == null) {
            return 1;
        }
        return this.collator.compare(first, second);
    }

    public int compareDocToValue(int doc, String value) {
        this.currentDocTerms.get(doc, this.tempBR);
        String docValue = this.tempBR.bytes == BinaryDocValues.MISSING ? null : this.tempBR.utf8ToString();
        return this.compareValues(docValue, value);
    }
}

