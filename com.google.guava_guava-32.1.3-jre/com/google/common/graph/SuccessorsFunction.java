/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.DoNotMock
 */
package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.graph.ElementTypesAreNonnullByDefault;
import com.google.errorprone.annotations.DoNotMock;

@DoNotMock(value="Implement with a lambda, or use GraphBuilder to build a Graph with the desired edges")
@ElementTypesAreNonnullByDefault
@Beta
public interface SuccessorsFunction<N> {
    public Iterable<? extends N> successors(N var1);
}

