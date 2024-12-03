/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.context.query;

import java.util.Set;
import org.terracotta.context.TreeNode;
import org.terracotta.context.query.Query;

class EnsureUnique
implements Query {
    static Query INSTANCE = new EnsureUnique();

    private EnsureUnique() {
    }

    @Override
    public Set<TreeNode> execute(Set<TreeNode> input) {
        if (input.size() == 1) {
            return input;
        }
        throw new IllegalStateException("Expected a uniquely identified node: found " + input.size());
    }
}

