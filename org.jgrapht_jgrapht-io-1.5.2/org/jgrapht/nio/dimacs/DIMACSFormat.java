/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.nio.dimacs;

public enum DIMACSFormat {
    SHORTEST_PATH("sp", "a"),
    MAX_CLIQUE("edge", "e"),
    COLORING("col", "e");

    private final String problem;
    private final String edge;

    private DIMACSFormat(String problem, String edge) {
        this.problem = problem;
        this.edge = edge;
    }

    public String getProblem() {
        return this.problem;
    }

    public String getEdgeDescriptor() {
        return this.edge;
    }
}

