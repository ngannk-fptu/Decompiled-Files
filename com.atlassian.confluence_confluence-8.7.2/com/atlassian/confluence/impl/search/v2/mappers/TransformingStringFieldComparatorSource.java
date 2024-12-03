/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Strings
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.Ordering
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.BinaryDocValues
 *  org.apache.lucene.search.FieldCache
 *  org.apache.lucene.search.FieldComparator
 *  org.apache.lucene.search.FieldComparatorSource
 *  org.apache.lucene.util.BytesRef
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Ordering;
import java.io.IOException;
import java.text.CollationKey;
import java.text.Collator;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldComparatorSource;
import org.apache.lucene.util.BytesRef;

class TransformingStringFieldComparatorSource<T>
extends FieldComparatorSource {
    private final Function<T, CollationKey> stringValueFunction;
    private final LoadingCache<String, T> valueLoader;

    public TransformingStringFieldComparatorSource(Locale locale, CacheLoader<String, T> loader, Function<T, String> stringValueFunction) {
        this(Collator.getInstance((Locale)Preconditions.checkNotNull((Object)locale, (Object)"locale is required")), loader, stringValueFunction);
    }

    public TransformingStringFieldComparatorSource(Collator collator, CacheLoader<String, T> loader, Function<T, String> stringValueFunction) {
        this.valueLoader = CacheBuilder.newBuilder().build(loader);
        this.stringValueFunction = stringValueFunction.andThen(collator::getCollationKey);
    }

    public FieldComparator<?> newComparator(final String fieldname, final int numHits, int sortPos, boolean reversed) {
        return new FieldComparator<Object>(){
            private final CollationKey[] sortValues;
            private final Ordering<CollationKey> ordering;
            private final BytesRef copyBuffer;
            private BinaryDocValues docs;
            private CollationKey bottomSortValue;
            {
                this.sortValues = new CollationKey[numHits];
                this.ordering = Ordering.natural().nullsLast();
                this.copyBuffer = new BytesRef();
            }

            public int compare(int slot1, int slot2) {
                return this.ordering.compare((Object)this.sortValues[slot1], (Object)this.sortValues[slot2]);
            }

            public void setBottom(int slot) {
                this.bottomSortValue = this.sortValues[slot];
            }

            public int compareBottom(int doc) {
                return this.ordering.compare((Object)this.bottomSortValue, (Object)this.getSortValue(doc));
            }

            public void copy(int slot, int doc) {
                this.sortValues[slot] = this.getSortValue(doc);
            }

            public FieldComparator<Object> setNextReader(AtomicReaderContext context) throws IOException {
                this.docs = FieldCache.DEFAULT.getTerms(context.reader(), fieldname);
                return this;
            }

            public Object value(int slot) {
                return this.sortValues[slot];
            }

            public int compareDocToValue(int doc, Object value) {
                return this.ordering.compare((Object)this.getSortValue(doc), (Object)((CollationKey)value));
            }

            private CollationKey getSortValue(int doc) {
                this.docs.get(doc, this.copyBuffer);
                String userKey = this.copyBuffer.utf8ToString();
                if (Strings.isNullOrEmpty((String)userKey)) {
                    return null;
                }
                try {
                    Object user = TransformingStringFieldComparatorSource.this.valueLoader.get((Object)userKey);
                    return TransformingStringFieldComparatorSource.this.stringValueFunction.apply(user);
                }
                catch (ExecutionException ex) {
                    throw new TransformingStringFieldComparatorException(ex.getCause());
                }
            }
        };
    }

    private static class TransformingStringFieldComparatorException
    extends RuntimeException {
        private static final long serialVersionUID = 7687685505989835363L;

        public TransformingStringFieldComparatorException(Throwable cause) {
            super(cause);
        }
    }
}

