/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReader
 *  org.apache.lucene.index.NumericDocValues
 */
package org.apache.lucene.index.sorter;

import java.io.IOException;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.sorter.Sorter;

public class NumericDocValuesSorter
extends Sorter {
    private final String fieldName;
    private final boolean ascending;

    public NumericDocValuesSorter(String fieldName) {
        this(fieldName, true);
    }

    public NumericDocValuesSorter(String fieldName, boolean ascending) {
        this.fieldName = fieldName;
        this.ascending = ascending;
    }

    @Override
    public Sorter.DocMap sort(AtomicReader reader) throws IOException {
        final NumericDocValues ndv = reader.getNumericDocValues(this.fieldName);
        Sorter.DocComparator comparator = this.ascending ? new Sorter.DocComparator(){

            @Override
            public int compare(int docID1, int docID2) {
                long v2;
                long v1 = ndv.get(docID1);
                return v1 < (v2 = ndv.get(docID2)) ? -1 : (v1 == v2 ? 0 : 1);
            }
        } : new Sorter.DocComparator(){

            @Override
            public int compare(int docID1, int docID2) {
                long v2;
                long v1 = ndv.get(docID1);
                return v1 > (v2 = ndv.get(docID2)) ? -1 : (v1 == v2 ? 0 : 1);
            }
        };
        return NumericDocValuesSorter.sort(reader.maxDoc(), comparator);
    }

    @Override
    public String getID() {
        return "DocValues(" + this.fieldName + "," + (this.ascending ? "ascending" : "descending") + ")";
    }
}

