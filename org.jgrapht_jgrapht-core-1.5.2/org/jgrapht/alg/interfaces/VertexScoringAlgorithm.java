/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.interfaces;

import java.util.Map;

public interface VertexScoringAlgorithm<V, D> {
    public Map<V, D> getScores();

    public D getVertexScore(V var1);
}

