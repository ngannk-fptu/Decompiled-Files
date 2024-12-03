/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.tree;

import org.apache.commons.configuration2.tree.NodeHandler;

public interface ConfigurationNodeVisitor<T> {
    public void visitBeforeChildren(T var1, NodeHandler<T> var2);

    public void visitAfterChildren(T var1, NodeHandler<T> var2);

    public boolean terminate();
}

