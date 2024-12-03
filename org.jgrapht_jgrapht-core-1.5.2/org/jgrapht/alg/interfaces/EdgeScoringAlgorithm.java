/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.interfaces;

import java.util.Map;

public interface EdgeScoringAlgorithm<E, D> {
    public Map<E, D> getScores();

    public D getEdgeScore(E var1);
}

