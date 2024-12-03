/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.context.query;

import java.util.Set;
import org.terracotta.context.TreeNode;
import org.terracotta.context.query.Query;

class ChainedQuery
implements Query {
    private final Query current;
    private final Query previous;

    public ChainedQuery(Query previous, Query current) {
        this.previous = previous;
        this.current = current;
    }

    @Override
    public final Set<TreeNode> execute(Set<TreeNode> input) {
        return this.current.execute(this.previous.execute(input));
    }

    public String toString() {
        return this.previous + " => " + this.current;
    }
}

