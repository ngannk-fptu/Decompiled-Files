/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.TermState;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermCollectingRewrite;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.ByteBlockPool;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefHash;
import org.apache.lucene.util.RamUsageEstimator;

class ConstantScoreAutoRewrite
extends TermCollectingRewrite<BooleanQuery> {
    public static int DEFAULT_TERM_COUNT_CUTOFF = 350;
    public static double DEFAULT_DOC_COUNT_PERCENT = 0.1;
    private int termCountCutoff = DEFAULT_TERM_COUNT_CUTOFF;
    private double docCountPercent = DEFAULT_DOC_COUNT_PERCENT;

    ConstantScoreAutoRewrite() {
    }

    public void setTermCountCutoff(int count) {
        this.termCountCutoff = count;
    }

    public int getTermCountCutoff() {
        return this.termCountCutoff;
    }

    public void setDocCountPercent(double percent) {
        this.docCountPercent = percent;
    }

    public double getDocCountPercent() {
        return this.docCountPercent;
    }

    @Override
    protected BooleanQuery getTopLevelQuery() {
        return new BooleanQuery(true);
    }

    @Override
    protected void addClause(BooleanQuery topLevel, Term term, int docFreq, float boost, TermContext states) {
        topLevel.add(new TermQuery(term, states), BooleanClause.Occur.SHOULD);
    }

    @Override
    public Query rewrite(IndexReader reader, MultiTermQuery query) throws IOException {
        int docCountCutoff = (int)(this.docCountPercent / 100.0 * (double)reader.maxDoc());
        int termCountLimit = Math.min(BooleanQuery.getMaxClauseCount(), this.termCountCutoff);
        CutOffTermCollector col = new CutOffTermCollector(docCountCutoff, termCountLimit);
        this.collectTerms(reader, query, col);
        int size = col.pendingTerms.size();
        if (col.hasCutOff) {
            return MultiTermQuery.CONSTANT_SCORE_FILTER_REWRITE.rewrite(reader, query);
        }
        if (size == 0) {
            return this.getTopLevelQuery();
        }
        BooleanQuery bq = this.getTopLevelQuery();
        BytesRefHash pendingTerms = col.pendingTerms;
        int[] sort = pendingTerms.sort(col.termsEnum.getComparator());
        for (int i = 0; i < size; ++i) {
            int pos = sort[i];
            this.addClause(bq, new Term(query.field, pendingTerms.get(pos, new BytesRef())), 1, 1.0f, col.array.termState[pos]);
        }
        ConstantScoreQuery result = new ConstantScoreQuery(bq);
        result.setBoost(query.getBoost());
        return result;
    }

    public int hashCode() {
        int prime = 1279;
        return (int)((long)(1279 * this.termCountCutoff) + Double.doubleToLongBits(this.docCountPercent));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ConstantScoreAutoRewrite other = (ConstantScoreAutoRewrite)obj;
        if (other.termCountCutoff != this.termCountCutoff) {
            return false;
        }
        return Double.doubleToLongBits(other.docCountPercent) == Double.doubleToLongBits(this.docCountPercent);
    }

    static final class TermStateByteStart
    extends BytesRefHash.DirectBytesStartArray {
        TermContext[] termState;

        public TermStateByteStart(int initSize) {
            super(initSize);
        }

        @Override
        public int[] init() {
            int[] ord = super.init();
            this.termState = new TermContext[ArrayUtil.oversize(ord.length, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
            assert (this.termState.length >= ord.length);
            return ord;
        }

        @Override
        public int[] grow() {
            int[] ord = super.grow();
            if (this.termState.length < ord.length) {
                TermContext[] tmpTermState = new TermContext[ArrayUtil.oversize(ord.length, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
                System.arraycopy(this.termState, 0, tmpTermState, 0, this.termState.length);
                this.termState = tmpTermState;
            }
            assert (this.termState.length >= ord.length);
            return ord;
        }

        @Override
        public int[] clear() {
            this.termState = null;
            return super.clear();
        }
    }

    static final class CutOffTermCollector
    extends TermCollectingRewrite.TermCollector {
        int docVisitCount = 0;
        boolean hasCutOff = false;
        TermsEnum termsEnum;
        final int docCountCutoff;
        final int termCountLimit;
        final TermStateByteStart array = new TermStateByteStart(16);
        final BytesRefHash pendingTerms = new BytesRefHash(new ByteBlockPool(new ByteBlockPool.DirectAllocator()), 16, this.array);

        CutOffTermCollector(int docCountCutoff, int termCountLimit) {
            this.docCountCutoff = docCountCutoff;
            this.termCountLimit = termCountLimit;
        }

        @Override
        public void setNextEnum(TermsEnum termsEnum) {
            this.termsEnum = termsEnum;
        }

        @Override
        public boolean collect(BytesRef bytes) throws IOException {
            int pos = this.pendingTerms.add(bytes);
            this.docVisitCount += this.termsEnum.docFreq();
            if (this.pendingTerms.size() >= this.termCountLimit || this.docVisitCount >= this.docCountCutoff) {
                this.hasCutOff = true;
                return false;
            }
            TermState termState = this.termsEnum.termState();
            assert (termState != null);
            if (pos < 0) {
                pos = -pos - 1;
                this.array.termState[pos].register(termState, this.readerContext.ord, this.termsEnum.docFreq(), this.termsEnum.totalTermFreq());
            } else {
                this.array.termState[pos] = new TermContext(this.topReaderContext, termState, this.readerContext.ord, this.termsEnum.docFreq(), this.termsEnum.totalTermFreq());
            }
            return true;
        }
    }
}

