/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.google.common.base.Strings
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.BinaryDocValues
 *  org.apache.lucene.search.FieldCache
 *  org.apache.lucene.search.FieldComparator
 *  org.apache.lucene.search.FieldComparatorSource
 *  org.apache.lucene.search.Sort
 *  org.apache.lucene.search.SortField
 *  org.apache.lucene.util.BytesRef
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneSortMapper;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.sort.ContentIdSort;
import com.google.common.base.Strings;
import java.io.IOException;
import java.text.ParseException;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldComparatorSource;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.util.BytesRef;

@Internal
class ContentIdSortMapper
implements LuceneSortMapper<ContentIdSort> {
    ContentIdSortMapper() {
    }

    @Override
    public Sort convertToLuceneSort(ContentIdSort contentIdSort) {
        boolean reverse = SearchSort.Order.DESCENDING.equals((Object)contentIdSort.getOrder());
        return new Sort(new SortField(SearchFieldNames.HANDLE, this.getFieldComparator(), reverse));
    }

    private FieldComparatorSource getFieldComparator() {
        return new FieldComparatorSource(){

            public FieldComparator<?> newComparator(String fieldName, int numHits, int sortPos, boolean reversed) {
                return new LongFieldComparator(numHits, fieldName, reversed);
            }
        };
    }

    static class ContentIdSortException
    extends RuntimeException {
        public ContentIdSortException(Throwable cause) {
            super(cause);
        }
    }

    private static class LongFieldComparator
    extends FieldComparator<Long> {
        private final String fieldName;
        private final boolean reversed;
        private final long[] sortValues;
        private final BytesRef copyBuffer;
        private BinaryDocValues docs;
        private long bottomSortValue;

        public LongFieldComparator(int numHits, String fieldName, boolean reversed) {
            this.fieldName = fieldName;
            this.reversed = reversed;
            this.sortValues = new long[numHits];
            this.copyBuffer = new BytesRef();
        }

        public int compare(int slot1, int slot2) {
            return Long.compare(this.sortValues[slot1], this.sortValues[slot2]);
        }

        public void setBottom(int slot) {
            this.bottomSortValue = this.sortValues[slot];
        }

        public int compareBottom(int doc) {
            return Long.compare(this.bottomSortValue, this.getSortValue(doc));
        }

        public void copy(int slot, int doc) {
            this.sortValues[slot] = this.getSortValue(doc);
        }

        public FieldComparator<Long> setNextReader(AtomicReaderContext context) throws IOException {
            this.docs = FieldCache.DEFAULT.getTerms(context.reader(), this.fieldName);
            return this;
        }

        public Long value(int slot) {
            return this.sortValues[slot];
        }

        public int compareDocToValue(int doc, Long value) {
            return Long.compare(this.getSortValue(doc), value);
        }

        private long getSortValue(int doc) {
            this.docs.get(doc, this.copyBuffer);
            String utf8 = this.copyBuffer.utf8ToString();
            if (Strings.isNullOrEmpty((String)utf8)) {
                return this.reversed ? Long.MAX_VALUE : Long.MIN_VALUE;
            }
            try {
                HibernateHandle hibernateHandle = new HibernateHandle(utf8);
                return hibernateHandle.getId();
            }
            catch (ParseException e) {
                throw new ContentIdSortException(e);
            }
        }
    }
}

