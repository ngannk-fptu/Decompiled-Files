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
import org.apache.lucene.search.BoostAttribute;
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

public abstract class ScoringRewrite<Q extends Query>
extends TermCollectingRewrite<Q> {
    public static final ScoringRewrite<BooleanQuery> SCORING_BOOLEAN_QUERY_REWRITE = new ScoringRewrite<BooleanQuery>(){

        @Override
        protected BooleanQuery getTopLevelQuery() {
            return new BooleanQuery(true);
        }

        @Override
        protected void addClause(BooleanQuery topLevel, Term term, int docCount, float boost, TermContext states) {
            TermQuery tq = new TermQuery(term, states);
            tq.setBoost(boost);
            topLevel.add(tq, BooleanClause.Occur.SHOULD);
        }

        @Override
        protected void checkMaxClauseCount(int count) {
            if (count > BooleanQuery.getMaxClauseCount()) {
                throw new BooleanQuery.TooManyClauses();
            }
        }
    };
    public static final MultiTermQuery.RewriteMethod CONSTANT_SCORE_BOOLEAN_QUERY_REWRITE = new MultiTermQuery.RewriteMethod(){

        @Override
        public Query rewrite(IndexReader reader, MultiTermQuery query) throws IOException {
            BooleanQuery bq = SCORING_BOOLEAN_QUERY_REWRITE.rewrite(reader, query);
            if (bq.clauses().isEmpty()) {
                return bq;
            }
            ConstantScoreQuery result = new ConstantScoreQuery(bq);
            result.setBoost(query.getBoost());
            return result;
        }
    };

    protected abstract void checkMaxClauseCount(int var1) throws IOException;

    public final Q rewrite(IndexReader reader, MultiTermQuery query) throws IOException {
        Object result = this.getTopLevelQuery();
        ParallelArraysTermCollector col = new ParallelArraysTermCollector();
        this.collectTerms(reader, query, col);
        int size = col.terms.size();
        if (size > 0) {
            int[] sort = col.terms.sort(col.termsEnum.getComparator());
            float[] boost = col.array.boost;
            TermContext[] termStates = col.array.termState;
            for (int i = 0; i < size; ++i) {
                int pos = sort[i];
                Term term = new Term(query.getField(), col.terms.get(pos, new BytesRef()));
                assert (reader.docFreq(term) == termStates[pos].docFreq());
                this.addClause(result, term, termStates[pos].docFreq(), query.getBoost() * boost[pos], termStates[pos]);
            }
        }
        return result;
    }

    static final class TermFreqBoostByteStart
    extends BytesRefHash.DirectBytesStartArray {
        float[] boost;
        TermContext[] termState;

        public TermFreqBoostByteStart(int initSize) {
            super(initSize);
        }

        @Override
        public int[] init() {
            int[] ord = super.init();
            this.boost = new float[ArrayUtil.oversize(ord.length, 4)];
            this.termState = new TermContext[ArrayUtil.oversize(ord.length, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
            assert (this.termState.length >= ord.length && this.boost.length >= ord.length);
            return ord;
        }

        @Override
        public int[] grow() {
            int[] ord = super.grow();
            this.boost = ArrayUtil.grow(this.boost, ord.length);
            if (this.termState.length < ord.length) {
                TermContext[] tmpTermState = new TermContext[ArrayUtil.oversize(ord.length, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
                System.arraycopy(this.termState, 0, tmpTermState, 0, this.termState.length);
                this.termState = tmpTermState;
            }
            assert (this.termState.length >= ord.length && this.boost.length >= ord.length);
            return ord;
        }

        @Override
        public int[] clear() {
            this.boost = null;
            this.termState = null;
            return super.clear();
        }
    }

    final class ParallelArraysTermCollector
    extends TermCollectingRewrite.TermCollector {
        final TermFreqBoostByteStart array = new TermFreqBoostByteStart(16);
        final BytesRefHash terms = new BytesRefHash(new ByteBlockPool(new ByteBlockPool.DirectAllocator()), 16, this.array);
        TermsEnum termsEnum;
        private BoostAttribute boostAtt;

        ParallelArraysTermCollector() {
        }

        @Override
        public void setNextEnum(TermsEnum termsEnum) {
            this.termsEnum = termsEnum;
            this.boostAtt = termsEnum.attributes().addAttribute(BoostAttribute.class);
        }

        @Override
        public boolean collect(BytesRef bytes) throws IOException {
            int e = this.terms.add(bytes);
            TermState state = this.termsEnum.termState();
            assert (state != null);
            if (e < 0) {
                int pos = -e - 1;
                this.array.termState[pos].register(state, this.readerContext.ord, this.termsEnum.docFreq(), this.termsEnum.totalTermFreq());
                assert (this.array.boost[pos] == this.boostAtt.getBoost()) : "boost should be equal in all segment TermsEnums";
            } else {
                this.array.boost[e] = this.boostAtt.getBoost();
                this.array.termState[e] = new TermContext(this.topReaderContext, state, this.readerContext.ord, this.termsEnum.docFreq(), this.termsEnum.totalTermFreq());
                ScoringRewrite.this.checkMaxClauseCount(this.terms.size());
            }
            return true;
        }
    }
}

