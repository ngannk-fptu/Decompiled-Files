/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search.function;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.search.Explanation;
import java.io.IOException;

public class CustomScoreProvider {
    protected final IndexReader reader;

    public CustomScoreProvider(IndexReader reader) {
        this.reader = reader;
    }

    public float customScore(int doc, float subQueryScore, float[] valSrcScores) throws IOException {
        if (valSrcScores.length == 1) {
            return this.customScore(doc, subQueryScore, valSrcScores[0]);
        }
        if (valSrcScores.length == 0) {
            return this.customScore(doc, subQueryScore, 1.0f);
        }
        float score = subQueryScore;
        for (int i = 0; i < valSrcScores.length; ++i) {
            score *= valSrcScores[i];
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
        for (int i = 0; i < valSrcExpls.length; ++i) {
            valSrcScore *= valSrcExpls[i].getValue();
        }
        Explanation exp = new Explanation(valSrcScore * subQueryExpl.getValue(), "custom score: product of:");
        exp.addDetail(subQueryExpl);
        for (int i = 0; i < valSrcExpls.length; ++i) {
            exp.addDetail(valSrcExpls[i]);
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

