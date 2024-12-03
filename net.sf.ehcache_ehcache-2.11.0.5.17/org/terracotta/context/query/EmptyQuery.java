/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.context.query;

import java.util.Collections;
import java.util.Set;
import org.terracotta.context.TreeNode;
import org.terracotta.context.query.Query;

class EmptyQuery
implements Query {
    static final Query INSTANCE = new EmptyQuery();

    private EmptyQuery() {
    }

    @Override
    public Set<TreeNode> execute(Set<TreeNode> input) {
        return Collections.emptySet();
    }

    public String toString() {
        return "<empty>";
    }
}

