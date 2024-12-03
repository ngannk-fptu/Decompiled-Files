/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.nio.csv;

public enum CSVFormat {
    EDGE_LIST,
    ADJACENCY_LIST,
    MATRIX;


    public static enum Parameter {
        EDGE_WEIGHTS,
        MATRIX_FORMAT_NODEID,
        MATRIX_FORMAT_ZERO_WHEN_NO_EDGE;

    }
}

