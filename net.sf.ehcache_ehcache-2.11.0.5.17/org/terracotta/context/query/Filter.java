/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.context.query;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.terracotta.context.TreeNode;
import org.terracotta.context.query.Matcher;
import org.terracotta.context.query.Query;

class Filter
implements Query {
    private final Matcher<? super TreeNode> filter;

    public Filter(Matcher<? super TreeNode> filter) {
        if (filter == null) {
            throw new NullPointerException("Cannot filter using a null matcher");
        }
        this.filter = filter;
    }

    @Override
    public Set<TreeNode> execute(Set<TreeNode> input) {
        HashSet<TreeNode> output = new HashSet<TreeNode>(input);
        Iterator it = output.iterator();
        while (it.hasNext()) {
            if (this.filter.matches(it.next())) continue;
            it.remove();
        }
        return output;
    }

    public String toString() {
        return "filter for nodes with " + this.filter;
    }
}

