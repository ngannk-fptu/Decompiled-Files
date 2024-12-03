/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

public class ScoreDoc {
    public float score;
    public int doc;
    public int shardIndex;

    public ScoreDoc(int doc, float score) {
        this(doc, score, -1);
    }

    public ScoreDoc(int doc, float score, int shardIndex) {
        this.doc = doc;
        this.score = score;
        this.shardIndex = shardIndex;
    }

    public String toString() {
        return "doc=" + this.doc + " score=" + this.score + " shardIndex=" + this.shardIndex;
    }
}

