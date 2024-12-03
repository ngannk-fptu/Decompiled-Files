/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.AbstractIterator
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.SortedSetDocValues
 *  org.apache.lucene.search.FieldCache
 *  org.apache.lucene.search.FieldComparator
 *  org.apache.lucene.util.BytesRef
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import com.atlassian.confluence.internal.search.v2.lucene.LuceneTermFieldComparator;
import com.google.common.collect.AbstractIterator;
import java.io.IOException;
import java.util.Comparator;
import java.util.function.Function;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.util.BytesRef;

public class LuceneMultiTermFieldComparator<T>
extends LuceneTermFieldComparator<T> {
    private SortedSetDocValues currentDocs;
    private BytesRef copyBuffer = new BytesRef();
    private Function<Iterable<BytesRef>, T> parser;

    public LuceneMultiTermFieldComparator(String fieldName, Function<Iterable<BytesRef>, T> parser, Comparator<T> comparator, int numHits) {
        super(fieldName, null, comparator, numHits);
        this.parser = parser;
    }

    public Function<Iterable<BytesRef>, T> getParser() {
        return this.parser;
    }

    @Override
    public FieldComparator<T> setNextReader(AtomicReaderContext context) throws IOException {
        this.currentDocs = this.getDocTermOrds(context);
        return this;
    }

    protected SortedSetDocValues getDocTermOrds(AtomicReaderContext context) throws IOException {
        return FieldCache.DEFAULT.getDocTermOrds(context.reader(), this.getFieldName());
    }

    @Override
    protected T getSortValue(int doc) {
        return this.readAllTermValuesForDocument(doc);
    }

    private T readAllTermValuesForDocument(int doc) {
        Iterable termIterable = () -> {
            this.currentDocs.setDocument(doc);
            return new AbstractIterator<BytesRef>(){
                private long ord;
                {
                    this.ord = LuceneMultiTermFieldComparator.this.currentDocs.nextOrd();
                }

                public BytesRef computeNext() {
                    if (this.ord == -1L) {
                        return (BytesRef)this.endOfData();
                    }
                    LuceneMultiTermFieldComparator.this.currentDocs.lookupOrd(this.ord, LuceneMultiTermFieldComparator.this.copyBuffer);
                    this.ord = LuceneMultiTermFieldComparator.this.currentDocs.nextOrd();
                    return LuceneMultiTermFieldComparator.this.copyBuffer;
                }
            };
        };
        return this.parser.apply(termIterable);
    }
}

