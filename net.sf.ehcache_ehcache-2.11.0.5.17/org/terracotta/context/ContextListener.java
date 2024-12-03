/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.context;

import org.terracotta.context.TreeNode;

public interface ContextListener {
    public void graphAdded(TreeNode var1, TreeNode var2);

    public void graphRemoved(TreeNode var1, TreeNode var2);
}

