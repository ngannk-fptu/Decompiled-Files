/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.search.FieldComparator
 *  org.apache.lucene.search.FieldComparatorSource
 *  org.apache.lucene.search.IndexSearcher
 *  org.apache.lucene.search.SortField
 *  org.apache.lucene.search.SortField$Type
 */
package org.apache.lucene.queries.function;

import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.Map;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldComparatorSource;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SortField;

public abstract class ValueSource {
    public abstract FunctionValues getValues(Map var1, AtomicReaderContext var2) throws IOException;

    public abstract boolean equals(Object var1);

    public abstract int hashCode();

    public abstract String description();

    public String toString() {
        return this.description();
    }

    public void createWeight(Map context, IndexSearcher searcher) throws IOException {
    }

    public static Map newContext(IndexSearcher searcher) {
        IdentityHashMap<String, IndexSearcher> context = new IdentityHashMap<String, IndexSearcher>();
        context.put("searcher", searcher);
        return context;
    }

    public SortField getSortField(boolean reverse) {
        return new ValueSourceSortField(reverse);
    }

    class ValueSourceComparator
    extends FieldComparator<Double> {
        private final double[] values;
        private FunctionValues docVals;
        private double bottom;
        private final Map fcontext;

        ValueSourceComparator(Map fcontext, int numHits) {
            this.fcontext = fcontext;
            this.values = new double[numHits];
        }

        public int compare(int slot1, int slot2) {
            return Double.compare(this.values[slot1], this.values[slot2]);
        }

        public int compareBottom(int doc) {
            return Double.compare(this.bottom, this.docVals.doubleVal(doc));
        }

        public void copy(int slot, int doc) {
            this.values[slot] = this.docVals.doubleVal(doc);
        }

        public FieldComparator setNextReader(AtomicReaderContext context) throws IOException {
            this.docVals = ValueSource.this.getValues(this.fcontext, context);
            return this;
        }

        public void setBottom(int bottom) {
            this.bottom = this.values[bottom];
        }

        public Double value(int slot) {
            return this.values[slot];
        }

        public int compareDocToValue(int doc, Double valueObj) {
            double value = valueObj;
            double docValue = this.docVals.doubleVal(doc);
            return Double.compare(docValue, value);
        }
    }

    class ValueSourceComparatorSource
    extends FieldComparatorSource {
        private final Map context;

        public ValueSourceComparatorSource(Map context) {
            this.context = context;
        }

        public FieldComparator<Double> newComparator(String fieldname, int numHits, int sortPos, boolean reversed) throws IOException {
            return new ValueSourceComparator(this.context, numHits);
        }
    }

    class ValueSourceSortField
    extends SortField {
        public ValueSourceSortField(boolean reverse) {
            super(ValueSource.this.description(), SortField.Type.REWRITEABLE, reverse);
        }

        public SortField rewrite(IndexSearcher searcher) throws IOException {
            Map context = ValueSource.newContext(searcher);
            ValueSource.this.createWeight(context, searcher);
            return new SortField(this.getField(), (FieldComparatorSource)new ValueSourceComparatorSource(context), this.getReverse());
        }
    }
}

