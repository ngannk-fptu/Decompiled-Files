/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.graph.spi;

import org.hibernate.graph.GraphSemantic;
import org.hibernate.graph.spi.RootGraphImplementor;

public interface AppliedGraph {
    public RootGraphImplementor<?> getGraph();

    public GraphSemantic getSemantic();
}

