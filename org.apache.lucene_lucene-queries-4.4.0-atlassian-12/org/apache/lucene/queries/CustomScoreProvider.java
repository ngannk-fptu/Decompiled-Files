/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.search.Explanation
 */
package org.apache.lucene.queries;

import java.io.IOException;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.Explanation;

public class CustomScoreProvider {
    protected final AtomicReaderContext context;

    public CustomScoreProvider(AtomicReaderContext context) {
        this.context = context;
    }

    public float customScore(int doc, float subQueryScore, float[] valSrcScores) throws IOException {
        if (valSrcScores.length == 1) {
            return this.customScore(doc, subQueryScore, valSrcScores[0]);
        }
        if (valSrcScores.length == 0) {
            return this.customScore(doc, subQueryScore, 1.0f);
        }
        float score = subQueryScore;
        for (float valSrcScore : valSrcScores) {
            score *= valSrcScore;
        }
        return score;
    }

    public float customScore(int doc, float subQueryScore, float valSrcScore) throws IOException {
        return subQueryScore * valSrcScore;
    }

    public Explanation customExplain(int doc, Explanation subQueryExpl, Explanation[] valSrcExpls) throws IOException {
        if (valSrcExpls.length == 1) {
            return this.customExplain(doc, subQueryExpl, valSrcExpls[0]);
        }
        if (valSrcExpls.length == 0) {
            return subQueryExpl;
        }
        float valSrcScore = 1.0f;
        for (Explanation valSrcExpl : valSrcExpls) {
            valSrcScore *= valSrcExpl.getValue();
        }
        Explanation exp = new Explanation(valSrcScore * subQueryExpl.getValue(), "custom score: product of:");
        exp.addDetail(subQueryExpl);
        for (Explanation valSrcExpl : valSrcExpls) {
            exp.addDetail(valSrcExpl);
        }
        return exp;
    }

    public Explanation customExplain(int doc, Explanation subQueryExpl, Explanation valSrcExpl) throws IOException {
        float valSrcScore = 1.0f;
        if (valSrcExpl != null) {
            valSrcScore *= valSrcExpl.getValue();
        }
        Explanation exp = new Explanation(valSrcScore * subQueryExpl.getValue(), "custom score: product of:");
        exp.addDetail(subQueryExpl);
        exp.addDetail(valSrcExpl);
        return exp;
    }
}

