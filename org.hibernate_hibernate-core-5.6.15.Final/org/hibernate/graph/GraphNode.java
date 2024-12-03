/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.graph;

public interface GraphNode<J> {
    public boolean isMutable();

    public GraphNode<J> makeCopy(boolean var1);
}

