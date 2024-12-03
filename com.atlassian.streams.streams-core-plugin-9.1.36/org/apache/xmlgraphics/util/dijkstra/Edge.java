/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.util.dijkstra;

import org.apache.xmlgraphics.util.dijkstra.Vertex;

public interface Edge {
    public Vertex getStart();

    public Vertex getEnd();

    public int getPenalty();
}

