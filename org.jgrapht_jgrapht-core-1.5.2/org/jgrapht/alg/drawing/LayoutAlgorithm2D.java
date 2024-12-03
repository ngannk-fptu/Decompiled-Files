/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.drawing;

import org.jgrapht.Graph;
import org.jgrapht.alg.drawing.model.LayoutModel2D;

public interface LayoutAlgorithm2D<V, E> {
    public void layout(Graph<V, E> var1, LayoutModel2D<V> var2);
}

