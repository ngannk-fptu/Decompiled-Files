/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldDoc;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SortField;
import org.apache.lucene.util.PriorityQueue;

public abstract class FieldValueHitQueue<T extends Entry>
extends PriorityQueue<T> {
    protected final SortField[] fields;
    protected final FieldComparator<?>[] comparators;
    protected FieldComparator<?> firstComparator;
    protected final int[] reverseMul;

    private FieldValueHitQueue(SortField[] fields, int size) {
        super(size);
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

    public void setComparator(int pos, FieldComparator<?> comparator) {
        if (pos == 0) {
            this.firstComparator = comparator;
        }
        this.comparators[pos] = comparator;
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

    private static final class MultiComparatorsFieldValueHitQueue<T extends Entry>
    extends FieldValueHitQueue<T> {
        public MultiComparatorsFieldValueHitQueue(SortField[] fields, int size) throws IOException {
            super(fields, size);
            int numComparators = this.comparators.length;
            for (int i = 0; i < numComparators; ++i) {
                SortField field = fields[i];
                this.reverseMul[i] = field.reverse ? -1 : 1;
                this.setComparator(i, field.getComparator(size, i));
            }
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

    private static final class OneComparatorFieldValueHitQueue<T extends Entry>
    extends FieldValueHitQueue<T> {
        private final int oneReverseMul;

        public OneComparatorFieldValueHitQueue(SortField[] fields, int size) throws IOException {
            super(fields, size);
            SortField field = fields[0];
            this.setComparator(0, field.getComparator(size, 0));
            this.reverseMul[0] = this.oneReverseMul = field.reverse ? -1 : 1;
        }

        @Override
        protected boolean lessThan(Entry hitA, Entry hitB) {
            assert (hitA != hitB);
            assert (hitA.slot != hitB.slot);
            int c = this.oneReverseMul * this.firstComparator.compare(hitA.slot, hitB.slot);
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

        @Override
        public String toString() {
            return "slot:" + this.slot + " " + super.toString();
        }
    }
}

