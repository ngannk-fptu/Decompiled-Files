/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.tree;

import org.apache.commons.configuration2.tree.NodeHandler;

public interface NodeMatcher<C> {
    public <T> boolean matches(T var1, NodeHandler<T> var2, C var3);
}

