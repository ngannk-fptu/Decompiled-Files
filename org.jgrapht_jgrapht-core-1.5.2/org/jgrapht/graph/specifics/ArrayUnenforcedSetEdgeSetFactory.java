/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph.specifics;

import java.io.Serializable;
import java.util.Set;
import org.jgrapht.graph.EdgeSetFactory;
import org.jgrapht.util.ArrayUnenforcedSet;

public class ArrayUnenforcedSetEdgeSetFactory<V, E>
implements EdgeSetFactory<V, E>,
Serializable {
    private static final long serialVersionUID = 5936902837403445985L;

    @Override
    public Set<E> createEdgeSet(V vertex) {
        return new ArrayUnenforcedSet(1);
    }
}

