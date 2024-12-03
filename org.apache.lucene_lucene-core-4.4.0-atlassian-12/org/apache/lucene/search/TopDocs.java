/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldDoc;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.util.PriorityQueue;

public class TopDocs {
    public int totalHits;
    public ScoreDoc[] scoreDocs;
    private float maxScore;

    public float getMaxScore() {
        return this.maxScore;
    }

    public void setMaxScore(float maxScore) {
        this.maxScore = maxScore;
    }

    TopDocs(int totalHits, ScoreDoc[] scoreDocs) {
        this(totalHits, scoreDocs, Float.NaN);
    }

    public TopDocs(int totalHits, ScoreDoc[] scoreDocs, float maxScore) {
        this.totalHits = totalHits;
        this.scoreDocs = scoreDocs;
        this.maxScore = maxScore;
    }

    public static TopDocs merge(Sort sort, int topN, TopDocs[] shardHits) throws IOException {
        PriorityQueue queue = sort == null ? new ScoreMergeSortQueue(shardHits) : new MergeSortQueue(sort, shardHits);
        int totalHitCount = 0;
        int availHitCount = 0;
        float maxScore = Float.MIN_VALUE;
        for (int shardIDX = 0; shardIDX < shardHits.length; ++shardIDX) {
            TopDocs shard = shardHits[shardIDX];
            totalHitCount += shard.totalHits;
            if (shard.scoreDocs == null || shard.scoreDocs.length <= 0) continue;
            availHitCount += shard.scoreDocs.length;
            queue.add(new ShardRef(shardIDX));
            maxScore = Math.max(maxScore, shard.getMaxScore());
        }
        if (availHitCount == 0) {
            maxScore = Float.NaN;
        }
        ScoreDoc[] hits = new ScoreDoc[Math.min(topN, availHitCount)];
        for (int hitUpto = 0; hitUpto < hits.length; ++hitUpto) {
            assert (queue.size() > 0);
            ShardRef ref = (ShardRef)queue.pop();
            ScoreDoc hit = shardHits[ref.shardIndex].scoreDocs[ref.hitIndex++];
            hit.shardIndex = ref.shardIndex;
            hits[hitUpto] = hit;
            if (ref.hitIndex >= shardHits[ref.shardIndex].scoreDocs.length) continue;
            queue.add(ref);
        }
        if (sort == null) {
            return new TopDocs(totalHitCount, hits, maxScore);
        }
        return new TopFieldDocs(totalHitCount, hits, sort.getSort(), maxScore);
    }

    private static class MergeSortQueue
    extends PriorityQueue<ShardRef> {
        final ScoreDoc[][] shardHits;
        final FieldComparator<?>[] comparators;
        final int[] reverseMul;

        public MergeSortQueue(Sort sort, TopDocs[] shardHits) throws IOException {
            super(shardHits.length);
            this.shardHits = new ScoreDoc[shardHits.length][];
            for (int shardIDX = 0; shardIDX < shardHits.length; ++shardIDX) {
                ScoreDoc[] shard = shardHits[shardIDX].scoreDocs;
                if (shard == null) continue;
                this.shardHits[shardIDX] = shard;
                for (int hitIDX = 0; hitIDX < shard.length; ++hitIDX) {
                    ScoreDoc sd = shard[hitIDX];
                    if (!(sd instanceof FieldDoc)) {
                        throw new IllegalArgumentException("shard " + shardIDX + " was not sorted by the provided Sort (expected FieldDoc but got ScoreDoc)");
                    }
                    FieldDoc fd = (FieldDoc)sd;
                    if (fd.fields != null) continue;
                    throw new IllegalArgumentException("shard " + shardIDX + " did not set sort field values (FieldDoc.fields is null); you must pass fillFields=true to IndexSearcher.search on each shard");
                }
            }
            SortField[] sortFields = sort.getSort();
            this.comparators = new FieldComparator[sortFields.length];
            this.reverseMul = new int[sortFields.length];
            for (int compIDX = 0; compIDX < sortFields.length; ++compIDX) {
                SortField sortField = sortFields[compIDX];
                this.comparators[compIDX] = sortField.getComparator(1, compIDX);
                this.reverseMul[compIDX] = sortField.getReverse() ? -1 : 1;
            }
        }

        @Override
        public boolean lessThan(ShardRef first, ShardRef second) {
            assert (first != second);
            FieldDoc firstFD = (FieldDoc)this.shardHits[first.shardIndex][first.hitIndex];
            FieldDoc secondFD = (FieldDoc)this.shardHits[second.shardIndex][second.hitIndex];
            for (int compIDX = 0; compIDX < this.comparators.length; ++compIDX) {
                FieldComparator<?> comp = this.comparators[compIDX];
                int cmp = this.reverseMul[compIDX] * comp.compareValues(firstFD.fields[compIDX], secondFD.fields[compIDX]);
                if (cmp == 0) continue;
                return cmp < 0;
            }
            if (first.shardIndex < second.shardIndex) {
                return true;
            }
            if (first.shardIndex > second.shardIndex) {
                return false;
            }
            assert (first.hitIndex != second.hitIndex);
            return first.hitIndex < second.hitIndex;
        }
    }

    private static class ScoreMergeSortQueue
    extends PriorityQueue<ShardRef> {
        final ScoreDoc[][] shardHits;

        public ScoreMergeSortQueue(TopDocs[] shardHits) {
            super(shardHits.length);
            this.shardHits = new ScoreDoc[shardHits.length][];
            for (int shardIDX = 0; shardIDX < shardHits.length; ++shardIDX) {
                this.shardHits[shardIDX] = shardHits[shardIDX].scoreDocs;
            }
        }

        @Override
        public boolean lessThan(ShardRef first, ShardRef second) {
            assert (first != second);
            float firstScore = this.shardHits[first.shardIndex][first.hitIndex].score;
            float secondScore = this.shardHits[second.shardIndex][second.hitIndex].score;
            if (firstScore < secondScore) {
                return false;
            }
            if (firstScore > secondScore) {
                return true;
            }
            if (first.shardIndex < second.shardIndex) {
                return true;
            }
            if (first.shardIndex > second.shardIndex) {
                return false;
            }
            assert (first.hitIndex != second.hitIndex);
            return first.hitIndex < second.hitIndex;
        }
    }

    private static class ShardRef {
        final int shardIndex;
        int hitIndex;

        public ShardRef(int shardIndex) {
            this.shardIndex = shardIndex;
        }

        public String toString() {
            return "ShardRef(shardIndex=" + this.shardIndex + " hitIndex=" + this.hitIndex + ")";
        }
    }
}

