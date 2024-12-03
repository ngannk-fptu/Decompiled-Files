/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.context.query;

import java.util.HashSet;
import java.util.Set;
import org.terracotta.context.TreeNode;
import org.terracotta.context.query.Children;
import org.terracotta.context.query.Query;

class Descendants
implements Query {
    static final Query INSTANCE = new Descendants();

    Descendants() {
    }

    @Override
    public Set<TreeNode> execute(Set<TreeNode> input) {
        HashSet<TreeNode> descendants = new HashSet<TreeNode>();
        Set<TreeNode> children = Children.INSTANCE.execute(input);
        while (!children.isEmpty() && descendants.addAll(children)) {
            children = Children.INSTANCE.execute(children);
        }
        return descendants;
    }

    public String toString() {
        return "descendants";
    }
}

