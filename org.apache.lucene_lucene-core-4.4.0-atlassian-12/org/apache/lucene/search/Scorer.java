/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Weight;

public abstract class Scorer
extends DocsEnum {
    protected final Weight weight;

    protected Scorer(Weight weight) {
        this.weight = weight;
    }

    public void score(Collector collector) throws IOException {
        int doc;
        assert (this.docID() == -1);
        collector.setScorer(this);
        while ((doc = this.nextDoc()) != Integer.MAX_VALUE) {
            collector.collect(doc);
        }
    }

    public boolean score(Collector collector, int max, int firstDocID) throws IOException {
        assert (this.docID() == firstDocID);
        collector.setScorer(this);
        int doc = firstDocID;
        while (doc < max) {
            collector.collect(doc);
            doc = this.nextDoc();
        }
        return doc != Integer.MAX_VALUE;
    }

    public abstract float score() throws IOException;

    public Weight getWeight() {
        return this.weight;
    }

    public Collection<ChildScorer> getChildren() {
        return Collections.emptyList();
    }

    public static class ChildScorer {
        public final Scorer child;
        public final String relationship;

        public ChildScorer(Scorer child, String relationship) {
            this.child = child;
            this.relationship = relationship;
        }
    }
}

