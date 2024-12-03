/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.linkprediction;

import org.jgrapht.alg.util.Pair;

public class LinkPredictionIndexNotWellDefinedException
extends RuntimeException {
    private static final long serialVersionUID = -8832535053621910719L;
    private Pair<?, ?> vertexPair;

    public LinkPredictionIndexNotWellDefinedException() {
    }

    public LinkPredictionIndexNotWellDefinedException(String message) {
        super(message);
    }

    public LinkPredictionIndexNotWellDefinedException(String message, Pair<?, ?> vertexPair) {
        super(message);
        this.vertexPair = vertexPair;
    }

    public Pair<?, ?> getVertexPair() {
        return this.vertexPair;
    }

    public void setVertexPair(Pair<?, ?> vertexPair) {
        this.vertexPair = vertexPair;
    }
}

