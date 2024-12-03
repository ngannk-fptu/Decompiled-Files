/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht;

public interface GraphMapping<V, E> {
    public V getVertexCorrespondence(V var1, boolean var2);

    public E getEdgeCorrespondence(E var1, boolean var2);
}

