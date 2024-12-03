/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.search.FieldComparator;
import com.atlassian.lucene36.search.FieldDoc;
import com.atlassian.lucene36.search.FieldValueHitQueue;
import com.atlassian.lucene36.search.ScoreDoc;
import com.atlassian.lucene36.search.Scorer;
import com.atlassian.lucene36.search.Sort;
import com.atlassian.lucene36.search.TopDocs;
import com.atlassian.lucene36.search.TopDocsCollector;
import com.atlassian.lucene36.search.TopFieldDocs;
import com.atlassian.lucene36.util.PriorityQueue;
import java.io.IOException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class TopFieldCollector
extends TopDocsCollector<FieldValueHitQueue.Entry> {
    private static final ScoreDoc[] EMPTY_SCOREDOCS = new ScoreDoc[0];
    private final boolean fillFields;
    float maxScore = Float.NaN;
    final int numHits;
    FieldValueHitQueue.Entry bottom = null;
    boolean queueFull;
    int docBase;

    private TopFieldCollector(PriorityQueue<FieldValueHitQueue.Entry> pq, int numHits, boolean fillFields) {
        super(pq);
        this.numHits = numHits;
        this.fillFields = fillFields;
    }

    public static TopFieldCollector create(Sort sort, int numHits, boolean fillFields, boolean trackDocScores, boolean trackMaxScore, boolean docsScoredInOrder) throws IOException {
        if (sort.fields.length == 0) {
            throw new IllegalArgumentException("Sort must contain at least one field");
        }
        if (numHits <= 0) {
            throw new IllegalArgumentException("numHits must be > 0; please use TotalHitCountCollector if you just need the total hit count");
        }
        FieldValueHitQueue<FieldValueHitQueue.Entry> queue = FieldValueHitQueue.create(sort.fields, numHits);
        if (queue.getComparators().length == 1) {
            if (docsScoredInOrder) {
                if (trackMaxScore) {
                    return new OneComparatorScoringMaxScoreCollector(queue, numHits, fillFields);
                }
                if (trackDocScores) {
                    return new OneComparatorScoringNoMaxScoreCollector(queue, numHits, fillFields);
                }
                return new OneComparatorNonScoringCollector(queue, numHits, fillFields);
            }
            if (trackMaxScore) {
                return new OutOfOrderOneComparatorScoringMaxScoreCollector(queue, numHits, fillFields);
            }
            if (trackDocScores) {
                return new OutOfOrderOneComparatorScoringNoMaxScoreCollector(queue, numHits, fillFields);
            }
            return new OutOfOrderOneComparatorNonScoringCollector(queue, numHits, fillFields);
        }
        if (docsScoredInOrder) {
            if (trackMaxScore) {
                return new MultiComparatorScoringMaxScoreCollector(queue, numHits, fillFields);
            }
            if (trackDocScores) {
                return new MultiComparatorScoringNoMaxScoreCollector(queue, numHits, fillFields);
            }
            return new MultiComparatorNonScoringCollector(queue, numHits, fillFields);
        }
        if (trackMaxScore) {
            return new OutOfOrderMultiComparatorScoringMaxScoreCollector(queue, numHits, fillFields);
        }
        if (trackDocScores) {
            return new OutOfOrderMultiComparatorScoringNoMaxScoreCollector(queue, numHits, fillFields);
        }
        return new OutOfOrderMultiComparatorNonScoringCollector(queue, numHits, fillFields);
    }

    final void add(int slot, int doc, float score) {
        this.bottom = this.pq.add(new FieldValueHitQueue.Entry(slot, this.docBase + doc, score));
        this.queueFull = this.totalHits == this.numHits;
    }

    @Override
    protected void populateResults(ScoreDoc[] results, int howMany) {
        if (this.fillFields) {
            FieldValueHitQueue queue = (FieldValueHitQueue)this.pq;
            for (int i = howMany - 1; i >= 0; --i) {
                results[i] = queue.fillFields((FieldValueHitQueue.Entry)queue.pop());
            }
        } else {
            for (int i = howMany - 1; i >= 0; --i) {
                FieldValueHitQueue.Entry entry = (FieldValueHitQueue.Entry)this.pq.pop();
                results[i] = new FieldDoc(entry.doc, entry.score);
            }
        }
    }

    @Override
    protected TopDocs newTopDocs(ScoreDoc[] results, int start) {
        if (results == null) {
            results = EMPTY_SCOREDOCS;
            this.maxScore = Float.NaN;
        }
        return new TopFieldDocs(this.totalHits, results, ((FieldValueHitQueue)this.pq).getFields(), this.maxScore);
    }

    @Override
    public boolean acceptsDocsOutOfOrder() {
        return false;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class OutOfOrderMultiComparatorScoringNoMaxScoreCollector
    extends MultiComparatorScoringNoMaxScoreCollector {
        public OutOfOrderMultiComparatorScoringNoMaxScoreCollector(FieldValueHitQueue<FieldValueHitQueue.Entry> queue, int numHits, boolean fillFields) throws IOException {
            super(queue, numHits, fillFields);
        }

        @Override
        public void collect(int doc) throws IOException {
            block8: {
                block7: {
                    ++this.totalHits;
                    if (!this.queueFull) break block7;
                    int i = 0;
                    while (true) {
                        int c;
                        if ((c = this.reverseMul[i] * this.comparators[i].compareBottom(doc)) < 0) {
                            return;
                        }
                        if (c > 0) break;
                        if (i == this.comparators.length - 1) {
                            if (doc + this.docBase <= this.bottom.doc) break;
                            return;
                        }
                        ++i;
                    }
                    for (i = 0; i < this.comparators.length; ++i) {
                        this.comparators[i].copy(this.bottom.slot, doc);
                    }
                    float score = this.scorer.score();
                    this.updateBottom(doc, score);
                    for (int i2 = 0; i2 < this.comparators.length; ++i2) {
                        this.comparators[i2].setBottom(this.bottom.slot);
                    }
                    break block8;
                }
                int slot = this.totalHits - 1;
                for (int i = 0; i < this.comparators.length; ++i) {
                    this.comparators[i].copy(slot, doc);
                }
                float score = this.scorer.score();
                this.add(slot, doc, score);
                if (!this.queueFull) break block8;
                for (int i = 0; i < this.comparators.length; ++i) {
                    this.comparators[i].setBottom(this.bottom.slot);
                }
            }
        }

        @Override
        public void setScorer(Scorer scorer) throws IOException {
            this.scorer = scorer;
            super.setScorer(scorer);
        }

        @Override
        public boolean acceptsDocsOutOfOrder() {
            return true;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class MultiComparatorScoringNoMaxScoreCollector
    extends MultiComparatorNonScoringCollector {
        Scorer scorer;

        public MultiComparatorScoringNoMaxScoreCollector(FieldValueHitQueue<FieldValueHitQueue.Entry> queue, int numHits, boolean fillFields) throws IOException {
            super(queue, numHits, fillFields);
        }

        final void updateBottom(int doc, float score) {
            this.bottom.doc = this.docBase + doc;
            this.bottom.score = score;
            this.bottom = (FieldValueHitQueue.Entry)this.pq.updateTop();
        }

        @Override
        public void collect(int doc) throws IOException {
            block8: {
                block7: {
                    ++this.totalHits;
                    if (!this.queueFull) break block7;
                    int i = 0;
                    while (true) {
                        int c;
                        if ((c = this.reverseMul[i] * this.comparators[i].compareBottom(doc)) < 0) {
                            return;
                        }
                        if (c > 0) break;
                        if (i == this.comparators.length - 1) {
                            return;
                        }
                        ++i;
                    }
                    for (i = 0; i < this.comparators.length; ++i) {
                        this.comparators[i].copy(this.bottom.slot, doc);
                    }
                    float score = this.scorer.score();
                    this.updateBottom(doc, score);
                    for (int i2 = 0; i2 < this.comparators.length; ++i2) {
                        this.comparators[i2].setBottom(this.bottom.slot);
                    }
                    break block8;
                }
                int slot = this.totalHits - 1;
                for (int i = 0; i < this.comparators.length; ++i) {
                    this.comparators[i].copy(slot, doc);
                }
                float score = this.scorer.score();
                this.add(slot, doc, score);
                if (!this.queueFull) break block8;
                for (int i = 0; i < this.comparators.length; ++i) {
                    this.comparators[i].setBottom(this.bottom.slot);
                }
            }
        }

        @Override
        public void setScorer(Scorer scorer) throws IOException {
            this.scorer = scorer;
            super.setScorer(scorer);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class OutOfOrderMultiComparatorScoringMaxScoreCollector
    extends MultiComparatorScoringMaxScoreCollector {
        public OutOfOrderMultiComparatorScoringMaxScoreCollector(FieldValueHitQueue<FieldValueHitQueue.Entry> queue, int numHits, boolean fillFields) throws IOException {
            super(queue, numHits, fillFields);
        }

        @Override
        public void collect(int doc) throws IOException {
            block9: {
                int i;
                float score;
                block8: {
                    score = this.scorer.score();
                    if (score > this.maxScore) {
                        this.maxScore = score;
                    }
                    ++this.totalHits;
                    if (!this.queueFull) break block8;
                    int i2 = 0;
                    while (true) {
                        int c;
                        if ((c = this.reverseMul[i2] * this.comparators[i2].compareBottom(doc)) < 0) {
                            return;
                        }
                        if (c > 0) break;
                        if (i2 == this.comparators.length - 1) {
                            if (doc + this.docBase <= this.bottom.doc) break;
                            return;
                        }
                        ++i2;
                    }
                    for (i2 = 0; i2 < this.comparators.length; ++i2) {
                        this.comparators[i2].copy(this.bottom.slot, doc);
                    }
                    this.updateBottom(doc, score);
                    for (i2 = 0; i2 < this.comparators.length; ++i2) {
                        this.comparators[i2].setBottom(this.bottom.slot);
                    }
                    break block9;
                }
                int slot = this.totalHits - 1;
                for (i = 0; i < this.comparators.length; ++i) {
                    this.comparators[i].copy(slot, doc);
                }
                this.add(slot, doc, score);
                if (!this.queueFull) break block9;
                for (i = 0; i < this.comparators.length; ++i) {
                    this.comparators[i].setBottom(this.bottom.slot);
                }
            }
        }

        @Override
        public boolean acceptsDocsOutOfOrder() {
            return true;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class MultiComparatorScoringMaxScoreCollector
    extends MultiComparatorNonScoringCollector {
        Scorer scorer;

        public MultiComparatorScoringMaxScoreCollector(FieldValueHitQueue<FieldValueHitQueue.Entry> queue, int numHits, boolean fillFields) throws IOException {
            super(queue, numHits, fillFields);
            this.maxScore = Float.NEGATIVE_INFINITY;
        }

        final void updateBottom(int doc, float score) {
            this.bottom.doc = this.docBase + doc;
            this.bottom.score = score;
            this.bottom = (FieldValueHitQueue.Entry)this.pq.updateTop();
        }

        @Override
        public void collect(int doc) throws IOException {
            block9: {
                int i;
                float score;
                block8: {
                    score = this.scorer.score();
                    if (score > this.maxScore) {
                        this.maxScore = score;
                    }
                    ++this.totalHits;
                    if (!this.queueFull) break block8;
                    int i2 = 0;
                    while (true) {
                        int c;
                        if ((c = this.reverseMul[i2] * this.comparators[i2].compareBottom(doc)) < 0) {
                            return;
                        }
                        if (c > 0) break;
                        if (i2 == this.comparators.length - 1) {
                            return;
                        }
                        ++i2;
                    }
                    for (i2 = 0; i2 < this.comparators.length; ++i2) {
                        this.comparators[i2].copy(this.bottom.slot, doc);
                    }
                    this.updateBottom(doc, score);
                    for (i2 = 0; i2 < this.comparators.length; ++i2) {
                        this.comparators[i2].setBottom(this.bottom.slot);
                    }
                    break block9;
                }
                int slot = this.totalHits - 1;
                for (i = 0; i < this.comparators.length; ++i) {
                    this.comparators[i].copy(slot, doc);
                }
                this.add(slot, doc, score);
                if (!this.queueFull) break block9;
                for (i = 0; i < this.comparators.length; ++i) {
                    this.comparators[i].setBottom(this.bottom.slot);
                }
            }
        }

        @Override
        public void setScorer(Scorer scorer) throws IOException {
            this.scorer = scorer;
            super.setScorer(scorer);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class OutOfOrderMultiComparatorNonScoringCollector
    extends MultiComparatorNonScoringCollector {
        public OutOfOrderMultiComparatorNonScoringCollector(FieldValueHitQueue<FieldValueHitQueue.Entry> queue, int numHits, boolean fillFields) throws IOException {
            super(queue, numHits, fillFields);
        }

        @Override
        public void collect(int doc) throws IOException {
            block8: {
                int i;
                block7: {
                    ++this.totalHits;
                    if (!this.queueFull) break block7;
                    int i2 = 0;
                    while (true) {
                        int c;
                        if ((c = this.reverseMul[i2] * this.comparators[i2].compareBottom(doc)) < 0) {
                            return;
                        }
                        if (c > 0) break;
                        if (i2 == this.comparators.length - 1) {
                            if (doc + this.docBase <= this.bottom.doc) break;
                            return;
                        }
                        ++i2;
                    }
                    for (i2 = 0; i2 < this.comparators.length; ++i2) {
                        this.comparators[i2].copy(this.bottom.slot, doc);
                    }
                    this.updateBottom(doc);
                    for (i2 = 0; i2 < this.comparators.length; ++i2) {
                        this.comparators[i2].setBottom(this.bottom.slot);
                    }
                    break block8;
                }
                int slot = this.totalHits - 1;
                for (i = 0; i < this.comparators.length; ++i) {
                    this.comparators[i].copy(slot, doc);
                }
                this.add(slot, doc, Float.NaN);
                if (!this.queueFull) break block8;
                for (i = 0; i < this.comparators.length; ++i) {
                    this.comparators[i].setBottom(this.bottom.slot);
                }
            }
        }

        @Override
        public boolean acceptsDocsOutOfOrder() {
            return true;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class MultiComparatorNonScoringCollector
    extends TopFieldCollector {
        final FieldComparator<?>[] comparators;
        final int[] reverseMul;

        public MultiComparatorNonScoringCollector(FieldValueHitQueue<FieldValueHitQueue.Entry> queue, int numHits, boolean fillFields) throws IOException {
            super(queue, numHits, fillFields);
            this.comparators = queue.getComparators();
            this.reverseMul = queue.getReverseMul();
        }

        final void updateBottom(int doc) {
            this.bottom.doc = this.docBase + doc;
            this.bottom = (FieldValueHitQueue.Entry)this.pq.updateTop();
        }

        @Override
        public void collect(int doc) throws IOException {
            block8: {
                int i;
                block7: {
                    ++this.totalHits;
                    if (!this.queueFull) break block7;
                    int i2 = 0;
                    while (true) {
                        int c;
                        if ((c = this.reverseMul[i2] * this.comparators[i2].compareBottom(doc)) < 0) {
                            return;
                        }
                        if (c > 0) break;
                        if (i2 == this.comparators.length - 1) {
                            return;
                        }
                        ++i2;
                    }
                    for (i2 = 0; i2 < this.comparators.length; ++i2) {
                        this.comparators[i2].copy(this.bottom.slot, doc);
                    }
                    this.updateBottom(doc);
                    for (i2 = 0; i2 < this.comparators.length; ++i2) {
                        this.comparators[i2].setBottom(this.bottom.slot);
                    }
                    break block8;
                }
                int slot = this.totalHits - 1;
                for (i = 0; i < this.comparators.length; ++i) {
                    this.comparators[i].copy(slot, doc);
                }
                this.add(slot, doc, Float.NaN);
                if (!this.queueFull) break block8;
                for (i = 0; i < this.comparators.length; ++i) {
                    this.comparators[i].setBottom(this.bottom.slot);
                }
            }
        }

        @Override
        public void setNextReader(IndexReader reader, int docBase) throws IOException {
            this.docBase = docBase;
            for (int i = 0; i < this.comparators.length; ++i) {
                this.comparators[i].setNextReader(reader, docBase);
            }
        }

        @Override
        public void setScorer(Scorer scorer) throws IOException {
            for (int i = 0; i < this.comparators.length; ++i) {
                this.comparators[i].setScorer(scorer);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class OutOfOrderOneComparatorScoringMaxScoreCollector
    extends OneComparatorScoringMaxScoreCollector {
        public OutOfOrderOneComparatorScoringMaxScoreCollector(FieldValueHitQueue<FieldValueHitQueue.Entry> queue, int numHits, boolean fillFields) throws IOException {
            super(queue, numHits, fillFields);
        }

        @Override
        public void collect(int doc) throws IOException {
            float score = this.scorer.score();
            if (score > this.maxScore) {
                this.maxScore = score;
            }
            ++this.totalHits;
            if (this.queueFull) {
                int cmp = this.reverseMul * this.comparator.compareBottom(doc);
                if (cmp < 0 || cmp == 0 && doc + this.docBase > this.bottom.doc) {
                    return;
                }
                this.comparator.copy(this.bottom.slot, doc);
                this.updateBottom(doc, score);
                this.comparator.setBottom(this.bottom.slot);
            } else {
                int slot = this.totalHits - 1;
                this.comparator.copy(slot, doc);
                this.add(slot, doc, score);
                if (this.queueFull) {
                    this.comparator.setBottom(this.bottom.slot);
                }
            }
        }

        @Override
        public boolean acceptsDocsOutOfOrder() {
            return true;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class OneComparatorScoringMaxScoreCollector
    extends OneComparatorNonScoringCollector {
        Scorer scorer;

        public OneComparatorScoringMaxScoreCollector(FieldValueHitQueue<FieldValueHitQueue.Entry> queue, int numHits, boolean fillFields) throws IOException {
            super(queue, numHits, fillFields);
            this.maxScore = Float.NEGATIVE_INFINITY;
        }

        final void updateBottom(int doc, float score) {
            this.bottom.doc = this.docBase + doc;
            this.bottom.score = score;
            this.bottom = (FieldValueHitQueue.Entry)this.pq.updateTop();
        }

        @Override
        public void collect(int doc) throws IOException {
            float score = this.scorer.score();
            if (score > this.maxScore) {
                this.maxScore = score;
            }
            ++this.totalHits;
            if (this.queueFull) {
                if (this.reverseMul * this.comparator.compareBottom(doc) <= 0) {
                    return;
                }
                this.comparator.copy(this.bottom.slot, doc);
                this.updateBottom(doc, score);
                this.comparator.setBottom(this.bottom.slot);
            } else {
                int slot = this.totalHits - 1;
                this.comparator.copy(slot, doc);
                this.add(slot, doc, score);
                if (this.queueFull) {
                    this.comparator.setBottom(this.bottom.slot);
                }
            }
        }

        @Override
        public void setScorer(Scorer scorer) throws IOException {
            this.scorer = scorer;
            super.setScorer(scorer);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class OutOfOrderOneComparatorScoringNoMaxScoreCollector
    extends OneComparatorScoringNoMaxScoreCollector {
        public OutOfOrderOneComparatorScoringNoMaxScoreCollector(FieldValueHitQueue<FieldValueHitQueue.Entry> queue, int numHits, boolean fillFields) throws IOException {
            super(queue, numHits, fillFields);
        }

        @Override
        public void collect(int doc) throws IOException {
            ++this.totalHits;
            if (this.queueFull) {
                int cmp = this.reverseMul * this.comparator.compareBottom(doc);
                if (cmp < 0 || cmp == 0 && doc + this.docBase > this.bottom.doc) {
                    return;
                }
                float score = this.scorer.score();
                this.comparator.copy(this.bottom.slot, doc);
                this.updateBottom(doc, score);
                this.comparator.setBottom(this.bottom.slot);
            } else {
                float score = this.scorer.score();
                int slot = this.totalHits - 1;
                this.comparator.copy(slot, doc);
                this.add(slot, doc, score);
                if (this.queueFull) {
                    this.comparator.setBottom(this.bottom.slot);
                }
            }
        }

        @Override
        public boolean acceptsDocsOutOfOrder() {
            return true;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class OneComparatorScoringNoMaxScoreCollector
    extends OneComparatorNonScoringCollector {
        Scorer scorer;

        public OneComparatorScoringNoMaxScoreCollector(FieldValueHitQueue<FieldValueHitQueue.Entry> queue, int numHits, boolean fillFields) throws IOException {
            super(queue, numHits, fillFields);
        }

        final void updateBottom(int doc, float score) {
            this.bottom.doc = this.docBase + doc;
            this.bottom.score = score;
            this.bottom = (FieldValueHitQueue.Entry)this.pq.updateTop();
        }

        @Override
        public void collect(int doc) throws IOException {
            ++this.totalHits;
            if (this.queueFull) {
                if (this.reverseMul * this.comparator.compareBottom(doc) <= 0) {
                    return;
                }
                float score = this.scorer.score();
                this.comparator.copy(this.bottom.slot, doc);
                this.updateBottom(doc, score);
                this.comparator.setBottom(this.bottom.slot);
            } else {
                float score = this.scorer.score();
                int slot = this.totalHits - 1;
                this.comparator.copy(slot, doc);
                this.add(slot, doc, score);
                if (this.queueFull) {
                    this.comparator.setBottom(this.bottom.slot);
                }
            }
        }

        @Override
        public void setScorer(Scorer scorer) throws IOException {
            this.scorer = scorer;
            this.comparator.setScorer(scorer);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class OutOfOrderOneComparatorNonScoringCollector
    extends OneComparatorNonScoringCollector {
        public OutOfOrderOneComparatorNonScoringCollector(FieldValueHitQueue<FieldValueHitQueue.Entry> queue, int numHits, boolean fillFields) throws IOException {
            super(queue, numHits, fillFields);
        }

        @Override
        public void collect(int doc) throws IOException {
            ++this.totalHits;
            if (this.queueFull) {
                int cmp = this.reverseMul * this.comparator.compareBottom(doc);
                if (cmp < 0 || cmp == 0 && doc + this.docBase > this.bottom.doc) {
                    return;
                }
                this.comparator.copy(this.bottom.slot, doc);
                this.updateBottom(doc);
                this.comparator.setBottom(this.bottom.slot);
            } else {
                int slot = this.totalHits - 1;
                this.comparator.copy(slot, doc);
                this.add(slot, doc, Float.NaN);
                if (this.queueFull) {
                    this.comparator.setBottom(this.bottom.slot);
                }
            }
        }

        @Override
        public boolean acceptsDocsOutOfOrder() {
            return true;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class OneComparatorNonScoringCollector
    extends TopFieldCollector {
        final FieldComparator<?> comparator;
        final int reverseMul;

        public OneComparatorNonScoringCollector(FieldValueHitQueue<FieldValueHitQueue.Entry> queue, int numHits, boolean fillFields) throws IOException {
            super(queue, numHits, fillFields);
            this.comparator = queue.getComparators()[0];
            this.reverseMul = queue.getReverseMul()[0];
        }

        final void updateBottom(int doc) {
            this.bottom.doc = this.docBase + doc;
            this.bottom = (FieldValueHitQueue.Entry)this.pq.updateTop();
        }

        @Override
        public void collect(int doc) throws IOException {
            ++this.totalHits;
            if (this.queueFull) {
                if (this.reverseMul * this.comparator.compareBottom(doc) <= 0) {
                    return;
                }
                this.comparator.copy(this.bottom.slot, doc);
                this.updateBottom(doc);
                this.comparator.setBottom(this.bottom.slot);
            } else {
                int slot = this.totalHits - 1;
                this.comparator.copy(slot, doc);
                this.add(slot, doc, Float.NaN);
                if (this.queueFull) {
                    this.comparator.setBottom(this.bottom.slot);
                }
            }
        }

        @Override
        public void setNextReader(IndexReader reader, int docBase) throws IOException {
            this.docBase = docBase;
            this.comparator.setNextReader(reader, docBase);
        }

        @Override
        public void setScorer(Scorer scorer) throws IOException {
            this.comparator.setScorer(scorer);
        }
    }
}

