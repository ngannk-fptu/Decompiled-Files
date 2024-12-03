/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import java.util.Comparator;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.SortedSetDocValues;
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

public final class DocTermOrdsRewriteMethod
extends MultiTermQuery.RewriteMethod {
    @Override
    public Query rewrite(IndexReader reader, MultiTermQuery query) {
        ConstantScoreQuery result = new ConstantScoreQuery(new MultiTermQueryDocTermOrdsWrapperFilter(query));
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
        return 877;
    }

    static class MultiTermQueryDocTermOrdsWrapperFilter
    extends Filter {
        protected final MultiTermQuery query;

        protected MultiTermQueryDocTermOrdsWrapperFilter(MultiTermQuery query) {
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
                return this.query.equals(((MultiTermQueryDocTermOrdsWrapperFilter)o).query);
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
            final SortedSetDocValues docTermOrds = FieldCache.DEFAULT.getDocTermOrds(context.reader(), this.query.field);
            final OpenBitSet termSet = new OpenBitSet(docTermOrds.getValueCount());
            TermsEnum termsEnum = this.query.getTermsEnum(new Terms(){

                @Override
                public Comparator<BytesRef> getComparator() {
                    return BytesRef.getUTF8SortedAsUnicodeComparator();
                }

                @Override
                public TermsEnum iterator(TermsEnum reuse) {
                    return docTermOrds.termsEnum();
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
                    termSet.set(termsEnum.ord());
                } while (termsEnum.next() != null);
            } else {
                return null;
            }
            return new FieldCacheDocIdSet(context.reader().maxDoc(), acceptDocs){

                @Override
                protected final boolean matchDoc(int doc) throws ArrayIndexOutOfBoundsException {
                    long ord;
                    docTermOrds.setDocument(doc);
                    while ((ord = docTermOrds.nextOrd()) != -1L) {
                        if (!termSet.get(ord)) continue;
                        return true;
                    }
                    return false;
                }
            };
        }
    }
}

