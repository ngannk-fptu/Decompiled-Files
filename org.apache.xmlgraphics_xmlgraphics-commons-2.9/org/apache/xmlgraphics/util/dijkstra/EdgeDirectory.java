/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.util.dijkstra;

import java.util.Iterator;
import org.apache.xmlgraphics.util.dijkstra.Vertex;

public interface EdgeDirectory {
    public int getPenalty(Vertex var1, Vertex var2);

    public Iterator getDestinations(Vertex var1);
}

