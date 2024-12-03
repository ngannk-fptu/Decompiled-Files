/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.context.query;

import java.util.Set;
import org.terracotta.context.TreeNode;
import org.terracotta.context.query.Query;

class NullQuery
implements Query {
    static final Query INSTANCE = new NullQuery();

    private NullQuery() {
    }

    @Override
    public Set<TreeNode> execute(Set<TreeNode> input) {
        return input;
    }

    public String toString() {
        return "";
    }
}

