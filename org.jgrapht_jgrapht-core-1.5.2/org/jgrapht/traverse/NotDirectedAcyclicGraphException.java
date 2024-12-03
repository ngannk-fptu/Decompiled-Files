/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.traverse;

public class NotDirectedAcyclicGraphException
extends IllegalArgumentException {
    private static final String GRAPH_IS_NOT_A_DAG = "Graph is not a DAG";
    private static final long serialVersionUID = 1L;

    public NotDirectedAcyclicGraphException() {
        super(GRAPH_IS_NOT_A_DAG);
    }
}

