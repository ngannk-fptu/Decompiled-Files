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
package com.atlassian.confluence.internal.search.v2.lucene;

import java.io.IOException;
import java.util.Comparator;
import java.util.function.Function;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.util.BytesRef;

public class LuceneTermFieldComparator<T>
extends FieldComparator<T> {
    private String fieldName;
    private BinaryDocValues currentDocs;
    private Object[] sortValues;
    private T bottomSortValue;
    private BytesRef copyBuffer = new BytesRef();
    private Function<BytesRef, T> parser;
    private Comparator<T> comparator;

    public LuceneTermFieldComparator(String fieldName, Function<BytesRef, T> parser, Comparator<T> comparator, int numHits) {
        if (fieldName == null) {
            throw new IllegalArgumentException("fieldName must not be null");
        }
        if (comparator == null) {
            throw new IllegalArgumentException("comparator must not be null");
        }
        this.fieldName = fieldName;
        this.parser = parser != null ? parser : bytes -> {
            throw new UnsupportedOperationException("A parser must be specified or getSortValue must be overridden");
        };
        this.comparator = comparator;
        this.sortValues = new Object[numHits];
    }

    protected String getFieldName() {
        return this.fieldName;
    }

    public int compareValues(T x, T y) {
        return this.comparator.compare(x, y);
    }

    public int compare(int slot1, int slot2) {
        return this.compareValues(this.sortValues[slot1], this.sortValues[slot2]);
    }

    public void setBottom(int slot) {
        this.bottomSortValue = this.sortValues[slot];
    }

    public int compareBottom(int doc) throws IOException {
        return this.compareValues(this.bottomSortValue, this.getSortValue(doc));
    }

    public void copy(int slot, int doc) throws IOException {
        this.sortValues[slot] = this.getSortValue(doc);
    }

    public FieldComparator<T> setNextReader(AtomicReaderContext context) throws IOException {
        this.currentDocs = this.getTerms(context);
        return this;
    }

    protected BinaryDocValues getTerms(AtomicReaderContext context) throws IOException {
        return FieldCache.DEFAULT.getTerms(context.reader(), this.fieldName);
    }

    public T value(int slot) {
        return (T)this.sortValues[slot];
    }

    public int compareDocToValue(int doc, T value) throws IOException {
        return this.compareValues(this.getSortValue(doc), value);
    }

    protected T getSortValue(int doc) {
        this.currentDocs.get(doc, this.copyBuffer);
        return this.parser.apply(this.copyBuffer);
    }
}

