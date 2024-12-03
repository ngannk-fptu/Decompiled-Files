/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs;

import java.io.IOException;
import java.util.Comparator;
import org.apache.lucene.codecs.MappingMultiDocsAndPositionsEnum;
import org.apache.lucene.codecs.MappingMultiDocsEnum;
import org.apache.lucene.codecs.PostingsConsumer;
import org.apache.lucene.codecs.TermStats;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.MergeState;
import org.apache.lucene.index.MultiDocsAndPositionsEnum;
import org.apache.lucene.index.MultiDocsEnum;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.FixedBitSet;

public abstract class TermsConsumer {
    private MappingMultiDocsEnum docsEnum;
    private MappingMultiDocsEnum docsAndFreqsEnum;
    private MappingMultiDocsAndPositionsEnum postingsEnum;

    protected TermsConsumer() {
    }

    public abstract PostingsConsumer startTerm(BytesRef var1) throws IOException;

    public abstract void finishTerm(BytesRef var1, TermStats var2) throws IOException;

    public abstract void finish(long var1, long var3, int var5) throws IOException;

    public abstract Comparator<BytesRef> getComparator() throws IOException;

    public void merge(MergeState mergeState, FieldInfo.IndexOptions indexOptions, TermsEnum termsEnum) throws IOException {
        assert (termsEnum != null);
        long sumTotalTermFreq = 0L;
        long sumDocFreq = 0L;
        long sumDFsinceLastAbortCheck = 0L;
        FixedBitSet visitedDocs = new FixedBitSet(mergeState.segmentInfo.getDocCount());
        if (indexOptions == FieldInfo.IndexOptions.DOCS_ONLY) {
            BytesRef term;
            if (this.docsEnum == null) {
                this.docsEnum = new MappingMultiDocsEnum();
            }
            this.docsEnum.setMergeState(mergeState);
            MultiDocsEnum docsEnumIn = null;
            while ((term = termsEnum.next()) != null) {
                if ((docsEnumIn = (MultiDocsEnum)termsEnum.docs(null, docsEnumIn, 0)) == null) continue;
                this.docsEnum.reset(docsEnumIn);
                PostingsConsumer postingsConsumer = this.startTerm(term);
                TermStats stats = postingsConsumer.merge(mergeState, indexOptions, this.docsEnum, visitedDocs);
                if (stats.docFreq <= 0) continue;
                this.finishTerm(term, stats);
                sumTotalTermFreq += (long)stats.docFreq;
                sumDocFreq += (long)stats.docFreq;
                if ((sumDFsinceLastAbortCheck += (long)stats.docFreq) <= 60000L) continue;
                mergeState.checkAbort.work((double)sumDFsinceLastAbortCheck / 5.0);
                sumDFsinceLastAbortCheck = 0L;
            }
        } else if (indexOptions == FieldInfo.IndexOptions.DOCS_AND_FREQS) {
            BytesRef term;
            if (this.docsAndFreqsEnum == null) {
                this.docsAndFreqsEnum = new MappingMultiDocsEnum();
            }
            this.docsAndFreqsEnum.setMergeState(mergeState);
            MultiDocsEnum docsAndFreqsEnumIn = null;
            while ((term = termsEnum.next()) != null) {
                docsAndFreqsEnumIn = (MultiDocsEnum)termsEnum.docs(null, docsAndFreqsEnumIn);
                assert (docsAndFreqsEnumIn != null);
                this.docsAndFreqsEnum.reset(docsAndFreqsEnumIn);
                PostingsConsumer postingsConsumer = this.startTerm(term);
                TermStats stats = postingsConsumer.merge(mergeState, indexOptions, this.docsAndFreqsEnum, visitedDocs);
                if (stats.docFreq <= 0) continue;
                this.finishTerm(term, stats);
                sumTotalTermFreq += stats.totalTermFreq;
                sumDocFreq += (long)stats.docFreq;
                if ((sumDFsinceLastAbortCheck += (long)stats.docFreq) <= 60000L) continue;
                mergeState.checkAbort.work((double)sumDFsinceLastAbortCheck / 5.0);
                sumDFsinceLastAbortCheck = 0L;
            }
        } else if (indexOptions == FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) {
            BytesRef term;
            if (this.postingsEnum == null) {
                this.postingsEnum = new MappingMultiDocsAndPositionsEnum();
            }
            this.postingsEnum.setMergeState(mergeState);
            MultiDocsAndPositionsEnum postingsEnumIn = null;
            while ((term = termsEnum.next()) != null) {
                postingsEnumIn = (MultiDocsAndPositionsEnum)termsEnum.docsAndPositions(null, postingsEnumIn, 2);
                assert (postingsEnumIn != null);
                this.postingsEnum.reset(postingsEnumIn);
                PostingsConsumer postingsConsumer = this.startTerm(term);
                TermStats stats = postingsConsumer.merge(mergeState, indexOptions, this.postingsEnum, visitedDocs);
                if (stats.docFreq <= 0) continue;
                this.finishTerm(term, stats);
                sumTotalTermFreq += stats.totalTermFreq;
                sumDocFreq += (long)stats.docFreq;
                if ((sumDFsinceLastAbortCheck += (long)stats.docFreq) <= 60000L) continue;
                mergeState.checkAbort.work((double)sumDFsinceLastAbortCheck / 5.0);
                sumDFsinceLastAbortCheck = 0L;
            }
        } else {
            BytesRef term;
            assert (indexOptions == FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
            if (this.postingsEnum == null) {
                this.postingsEnum = new MappingMultiDocsAndPositionsEnum();
            }
            this.postingsEnum.setMergeState(mergeState);
            MultiDocsAndPositionsEnum postingsEnumIn = null;
            while ((term = termsEnum.next()) != null) {
                postingsEnumIn = (MultiDocsAndPositionsEnum)termsEnum.docsAndPositions(null, postingsEnumIn);
                assert (postingsEnumIn != null);
                this.postingsEnum.reset(postingsEnumIn);
                PostingsConsumer postingsConsumer = this.startTerm(term);
                TermStats stats = postingsConsumer.merge(mergeState, indexOptions, this.postingsEnum, visitedDocs);
                if (stats.docFreq <= 0) continue;
                this.finishTerm(term, stats);
                sumTotalTermFreq += stats.totalTermFreq;
                sumDocFreq += (long)stats.docFreq;
                if ((sumDFsinceLastAbortCheck += (long)stats.docFreq) <= 60000L) continue;
                mergeState.checkAbort.work((double)sumDFsinceLastAbortCheck / 5.0);
                sumDFsinceLastAbortCheck = 0L;
            }
        }
        this.finish(indexOptions == FieldInfo.IndexOptions.DOCS_ONLY ? -1L : sumTotalTermFreq, sumDocFreq, visitedDocs.cardinality());
    }
}

