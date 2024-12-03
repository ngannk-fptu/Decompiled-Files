/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.context.query;

import java.util.HashSet;
import java.util.Set;
import org.terracotta.context.TreeNode;
import org.terracotta.context.query.Query;

class Children
implements Query {
    static final Query INSTANCE = new Children();

    Children() {
    }

    @Override
    public Set<TreeNode> execute(Set<TreeNode> input) {
        HashSet<TreeNode> output = new HashSet<TreeNode>();
        for (TreeNode node : input) {
            output.addAll(node.getChildren());
        }
        return output;
    }

    public String toString() {
        return "children";
    }
}

