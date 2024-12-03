/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.search.BooleanClause;
import com.atlassian.lucene36.search.Collector;
import com.atlassian.lucene36.search.DocIdSetIterator;
import com.atlassian.lucene36.search.Query;
import com.atlassian.lucene36.search.Similarity;
import com.atlassian.lucene36.search.Weight;
import java.io.IOException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class Scorer
extends DocIdSetIterator {
    private final Similarity similarity;
    protected final Weight weight;

    protected Scorer(Weight weight) {
        this(null, weight);
    }

    @Deprecated
    protected Scorer(Similarity similarity) {
        this(similarity, null);
    }

    @Deprecated
    protected Scorer(Similarity similarity, Weight weight) {
        this.similarity = similarity;
        this.weight = weight;
    }

    @Deprecated
    public Similarity getSimilarity() {
        return this.similarity;
    }

    public void score(Collector collector) throws IOException {
        int doc;
        collector.setScorer(this);
        while ((doc = this.nextDoc()) != Integer.MAX_VALUE) {
            collector.collect(doc);
        }
    }

    protected boolean score(Collector collector, int max, int firstDocID) throws IOException {
        collector.setScorer(this);
        int doc = firstDocID;
        while (doc < max) {
            collector.collect(doc);
            doc = this.nextDoc();
        }
        return doc != Integer.MAX_VALUE;
    }

    public abstract float score() throws IOException;

    public float freq() throws IOException {
        throw new UnsupportedOperationException(this + " does not implement freq()");
    }

    public void visitScorers(ScorerVisitor<Query, Query, Scorer> visitor) {
        this.visitSubScorers(null, BooleanClause.Occur.MUST, visitor);
    }

    public void visitSubScorers(Query parent, BooleanClause.Occur relationship, ScorerVisitor<Query, Query, Scorer> visitor) {
        if (this.weight == null) {
            throw new UnsupportedOperationException();
        }
        Query q = this.weight.getQuery();
        switch (relationship) {
            case MUST: {
                visitor.visitRequired(parent, q, this);
                break;
            }
            case MUST_NOT: {
                visitor.visitProhibited(parent, q, this);
                break;
            }
            case SHOULD: {
                visitor.visitOptional(parent, q, this);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class ScorerVisitor<P extends Query, C extends Query, S extends Scorer> {
        public void visitOptional(P parent, C child, S scorer) {
        }

        public void visitRequired(P parent, C child, S scorer) {
        }

        public void visitProhibited(P parent, C child, S scorer) {
        }
    }
}

