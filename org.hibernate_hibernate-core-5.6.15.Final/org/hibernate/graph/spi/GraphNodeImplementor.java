/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.graph.spi;

import org.hibernate.graph.GraphNode;

public interface GraphNodeImplementor<J>
extends GraphNode<J> {
    @Override
    public GraphNodeImplementor<J> makeCopy(boolean var1);
}

