/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import java.util.Comparator;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldCacheDocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.OpenBitSet;

public final class FieldCacheRewriteMethod
extends MultiTermQuery.RewriteMethod {
    @Override
    public Query rewrite(IndexReader reader, MultiTermQuery query) {
        ConstantScoreQuery result = new ConstantScoreQuery(new MultiTermQueryFieldCacheWrapperFilter(query));
        result.setBoost(query.getBoost());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        return this.getClass() == obj.getClass();
    }

    public int hashCode() {
        return 641;
    }

    static class MultiTermQueryFieldCacheWrapperFilter
    extends Filter {
        protected final MultiTermQuery query;

        protected MultiTermQueryFieldCacheWrapperFilter(MultiTermQuery query) {
            this.query = query;
        }

        public String toString() {
            return this.query.toString();
        }

        public final boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (this.getClass().equals(o.getClass())) {
                return this.query.equals(((MultiTermQueryFieldCacheWrapperFilter)o).query);
            }
            return false;
        }

        public final int hashCode() {
            return this.query.hashCode();
        }

        public final String getField() {
            return this.query.getField();
        }

        @Override
        public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
            final SortedDocValues fcsi = FieldCache.DEFAULT.getTermsIndex(context.reader(), this.query.field);
            final OpenBitSet termSet = new OpenBitSet(fcsi.getValueCount());
            TermsEnum termsEnum = this.query.getTermsEnum(new Terms(){

                @Override
                public Comparator<BytesRef> getComparator() {
                    return BytesRef.getUTF8SortedAsUnicodeComparator();
                }

                @Override
                public TermsEnum iterator(TermsEnum reuse) {
                    return fcsi.termsEnum();
                }

                @Override
                public long getSumTotalTermFreq() {
                    return -1L;
                }

                @Override
                public long getSumDocFreq() {
                    return -1L;
                }

                @Override
                public int getDocCount() {
                    return -1;
                }

                @Override
                public long size() {
                    return -1L;
                }

                @Override
                public boolean hasOffsets() {
                    return false;
                }

                @Override
                public boolean hasPositions() {
                    return false;
                }

                @Override
                public boolean hasPayloads() {
                    return false;
                }
            });
            assert (termsEnum != null);
            if (termsEnum.next() != null) {
                do {
                    long ord;
                    if ((ord = termsEnum.ord()) < 0L) continue;
                    termSet.set(ord);
                } while (termsEnum.next() != null);
            } else {
                return null;
            }
            return new FieldCacheDocIdSet(context.reader().maxDoc(), acceptDocs){

                @Override
                protected final boolean matchDoc(int doc) throws ArrayIndexOutOfBoundsException {
                    int ord = fcsi.getOrd(doc);
                    if (ord == -1) {
                        return false;
                    }
                    return termSet.get(ord);
                }
            };
        }
    }
}

