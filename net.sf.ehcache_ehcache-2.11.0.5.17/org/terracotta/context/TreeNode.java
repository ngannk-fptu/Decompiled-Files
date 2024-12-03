/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.context;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.terracotta.context.ContextElement;
import org.terracotta.context.WeakIdentityHashMap;

public interface TreeNode
extends WeakIdentityHashMap.Cleanable {
    public Set<? extends TreeNode> getChildren();

    public List<? extends TreeNode> getPath() throws IllegalStateException;

    public Collection<List<? extends TreeNode>> getPaths();

    public ContextElement getContext();

    public String toTreeString();
}

