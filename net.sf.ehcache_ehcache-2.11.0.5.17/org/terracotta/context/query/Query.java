/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.context.query;

import java.util.Set;
import org.terracotta.context.TreeNode;

public interface Query {
    public Set<TreeNode> execute(Set<TreeNode> var1);
}

