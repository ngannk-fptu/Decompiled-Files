/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.similarities;

import java.io.IOException;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.similarities.Similarity;

public abstract class PerFieldSimilarityWrapper
extends Similarity {
    @Override
    public final long computeNorm(FieldInvertState state) {
        return this.get(state.getName()).computeNorm(state);
    }

    @Override
    public final Similarity.SimWeight computeWeight(float queryBoost, CollectionStatistics collectionStats, TermStatistics ... termStats) {
        PerFieldSimWeight weight = new PerFieldSimWeight();
        weight.delegate = this.get(collectionStats.field());
        weight.delegateWeight = weight.delegate.computeWeight(queryBoost, collectionStats, termStats);
        return weight;
    }

    @Override
    public final Similarity.SimScorer simScorer(Similarity.SimWeight weight, AtomicReaderContext context) throws IOException {
        PerFieldSimWeight perFieldWeight = (PerFieldSimWeight)weight;
        return perFieldWeight.delegate.simScorer(perFieldWeight.delegateWeight, context);
    }

    public abstract Similarity get(String var1);

    static class PerFieldSimWeight
    extends Similarity.SimWeight {
        Similarity delegate;
        Similarity.SimWeight delegateWeight;

        PerFieldSimWeight() {
        }

        @Override
        public float getValueForNormalization() {
            return this.delegateWeight.getValueForNormalization();
        }

        @Override
        public void normalize(float queryNorm, float topLevelBoost) {
            this.delegateWeight.normalize(queryNorm, topLevelBoost);
        }
    }
}

