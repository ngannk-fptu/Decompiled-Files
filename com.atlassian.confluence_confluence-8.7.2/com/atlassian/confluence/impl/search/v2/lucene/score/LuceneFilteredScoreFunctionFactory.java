/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReader
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.search.DocIdSetIterator
 *  org.apache.lucene.search.IndexSearcher
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.Scorer
 */
package com.atlassian.confluence.impl.search.v2.lucene.score;

import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneScoreFunction;
import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneScoreFunctionFactory;
import java.io.IOException;
import java.util.Optional;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;

public class LuceneFilteredScoreFunctionFactory
implements LuceneScoreFunctionFactory {
    private final Query filter;
    private final LuceneScoreFunctionFactory functionFactory;

    public LuceneFilteredScoreFunctionFactory(Query filter, LuceneScoreFunctionFactory functionFactory) {
        this.filter = filter;
        this.functionFactory = functionFactory;
    }

    @Override
    public LuceneScoreFunction create(AtomicReader reader) throws IOException {
        Scorer docIdIterator = new IndexSearcher((IndexReader)reader).createNormalizedWeight(this.filter).scorer(reader.getContext(), true, false, reader.getLiveDocs());
        LuceneScoreFunction function = this.functionFactory.create(reader);
        return new LuceneScoreFunction((DocIdSetIterator)docIdIterator, function){
            final /* synthetic */ DocIdSetIterator val$docIdIterator;
            final /* synthetic */ LuceneScoreFunction val$function;
            {
                this.val$docIdIterator = docIdSetIterator;
                this.val$function = luceneScoreFunction;
            }

            @Override
            public double apply(int docId) throws IOException {
                return this.applyOptional(docId).orElse(0.0);
            }

            @Override
            public Optional<Double> applyOptional(int docId) throws IOException {
                if (this.val$docIdIterator != null && (this.val$docIdIterator.docID() == docId || this.val$docIdIterator.advance(docId) == docId)) {
                    return Optional.of(this.val$function.apply(docId));
                }
                return Optional.empty();
            }
        };
    }
}

