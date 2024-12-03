/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.search.FieldComparator;
import com.atlassian.lucene36.search.FieldDoc;
import com.atlassian.lucene36.search.ScoreDoc;
import com.atlassian.lucene36.search.SortField;
import com.atlassian.lucene36.util.PriorityQueue;
import java.io.IOException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class FieldValueHitQueue<T extends Entry>
extends PriorityQueue<T> {
    protected final SortField[] fields;
    protected final FieldComparator<?>[] comparators;
    protected final int[] reverseMul;

    private FieldValueHitQueue(SortField[] fields) {
        this.fields = fields;
        int numComparators = fields.length;
        this.comparators = new FieldComparator[numComparators];
        this.reverseMul = new int[numComparators];
    }

    public static <T extends Entry> FieldValueHitQueue<T> create(SortField[] fields, int size) throws IOException {
        if (fields.length == 0) {
            throw new IllegalArgumentException("Sort must contain at least one field");
        }
        if (fields.length == 1) {
            return new OneComparatorFieldValueHitQueue(fields, size);
        }
        return new MultiComparatorsFieldValueHitQueue(fields, size);
    }

    public FieldComparator<?>[] getComparators() {
        return this.comparators;
    }

    public int[] getReverseMul() {
        return this.reverseMul;
    }

    @Override
    protected abstract boolean lessThan(Entry var1, Entry var2);

    FieldDoc fillFields(Entry entry) {
        int n = this.comparators.length;
        Object[] fields = new Object[n];
        for (int i = 0; i < n; ++i) {
            fields[i] = this.comparators[i].value(entry.slot);
        }
        return new FieldDoc(entry.doc, entry.score, fields);
    }

    SortField[] getFields() {
        return this.fields;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class MultiComparatorsFieldValueHitQueue<T extends Entry>
    extends FieldValueHitQueue<T> {
        public MultiComparatorsFieldValueHitQueue(SortField[] fields, int size) throws IOException {
            super(fields);
            int numComparators = this.comparators.length;
            for (int i = 0; i < numComparators; ++i) {
                SortField field = fields[i];
                this.reverseMul[i] = field.reverse ? -1 : 1;
                this.comparators[i] = field.getComparator(size, i);
            }
            this.initialize(size);
        }

        @Override
        protected boolean lessThan(Entry hitA, Entry hitB) {
            assert (hitA != hitB);
            assert (hitA.slot != hitB.slot);
            int numComparators = this.comparators.length;
            for (int i = 0; i < numComparators; ++i) {
                int c = this.reverseMul[i] * this.comparators[i].compare(hitA.slot, hitB.slot);
                if (c == 0) continue;
                return c > 0;
            }
            return hitA.doc > hitB.doc;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class OneComparatorFieldValueHitQueue<T extends Entry>
    extends FieldValueHitQueue<T> {
        private final FieldComparator<?> comparator;
        private final int oneReverseMul;

        public OneComparatorFieldValueHitQueue(SortField[] fields, int size) throws IOException {
            super(fields);
            SortField field = fields[0];
            this.comparator = field.getComparator(size, 0);
            this.oneReverseMul = field.reverse ? -1 : 1;
            this.comparators[0] = this.comparator;
            this.reverseMul[0] = this.oneReverseMul;
            this.initialize(size);
        }

        @Override
        protected boolean lessThan(Entry hitA, Entry hitB) {
            assert (hitA != hitB);
            assert (hitA.slot != hitB.slot);
            int c = this.oneReverseMul * this.comparator.compare(hitA.slot, hitB.slot);
            if (c != 0) {
                return c > 0;
            }
            return hitA.doc > hitB.doc;
        }
    }

    public static class Entry
    extends ScoreDoc {
        public int slot;

        public Entry(int slot, int doc, float score) {
            super(doc, score);
            this.slot = slot;
        }

        public String toString() {
            return "slot:" + this.slot + " " + super.toString();
        }
    }
}

