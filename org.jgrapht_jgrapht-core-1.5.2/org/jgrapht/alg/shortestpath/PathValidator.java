/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.shortestpath;

import org.jgrapht.GraphPath;

public interface PathValidator<V, E> {
    public boolean isValidPath(GraphPath<V, E> var1, E var2);
}

